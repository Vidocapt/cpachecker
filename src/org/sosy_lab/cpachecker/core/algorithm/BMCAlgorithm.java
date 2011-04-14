/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2010  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.core.algorithm;

import static com.google.common.collect.Iterables.*;
import static org.sosy_lab.cpachecker.cpa.predicate.PredicateAbstractElement.FILTER_ABSTRACTION_ELEMENTS;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.sosy_lab.common.LogManager;
import org.sosy_lab.common.Timer;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.cpachecker.core.CPAcheckerResult.Result;
import org.sosy_lab.cpachecker.core.interfaces.AbstractElement;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysis;
import org.sosy_lab.cpachecker.core.interfaces.Statistics;
import org.sosy_lab.cpachecker.core.interfaces.StatisticsProvider;
import org.sosy_lab.cpachecker.core.interfaces.WrapperCPA;
import org.sosy_lab.cpachecker.core.reachedset.ReachedSet;
import org.sosy_lab.cpachecker.cpa.assumptions.storage.AssumptionStorageElement;
import org.sosy_lab.cpachecker.cpa.predicate.PredicateAbstractElement;
import org.sosy_lab.cpachecker.cpa.predicate.PredicateCPA;
import org.sosy_lab.cpachecker.exceptions.CPAException;
import org.sosy_lab.cpachecker.util.AbstractElements;
import org.sosy_lab.cpachecker.util.predicates.interfaces.Formula;
import org.sosy_lab.cpachecker.util.predicates.interfaces.FormulaManager;
import org.sosy_lab.cpachecker.util.predicates.interfaces.TheoremProver;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Options(prefix="bmc")
public class BMCAlgorithm implements Algorithm, StatisticsProvider {

  private static final Function<AbstractElement, PredicateAbstractElement> EXTRACT_PREDICATE_ELEMENT
      = AbstractElements.extractElementByTypeFunction(PredicateAbstractElement.class);

  private static class BMCStatistics implements Statistics {

    private final Timer satCheck = new Timer();
    private final Timer assertionsCheck = new Timer();
    
    @Override
    public void printStatistics(PrintStream out, Result pResult, ReachedSet pReached) {
      if (satCheck.getNumberOfIntervals() > 0) {
        out.println("Time for final sat check:           " + satCheck);
      }
      if (assertionsCheck.getNumberOfIntervals() > 0) {
        out.println("Time for bounding assertions check: " + assertionsCheck);
      }
    }

    @Override
    public String getName() {
      return "BMC algorithm";
    }
  }
  
  @Option
  private boolean boundingAssertions = true;
  
  @Option
  private boolean checkTargetStates = true;
  
  private final BMCStatistics stats = new BMCStatistics();
  private final Algorithm algorithm;
  private final PredicateCPA cpa;
  private final LogManager logger;
  
  public BMCAlgorithm(Algorithm algorithm, Configuration config, LogManager logger) throws InvalidConfigurationException, CPAException {
    config.inject(this);
    this.algorithm = algorithm;
    this.logger = logger;
    
    cpa = ((WrapperCPA)getCPA()).retrieveWrappedCpa(PredicateCPA.class);
    if (cpa == null) {
      throw new InvalidConfigurationException("PredicateCPA needed for BMCAlgorithm");
    }
  }

  @Override
  public boolean run(ReachedSet pReachedSet) throws CPAException {
    boolean sound = algorithm.run(pReachedSet);
    
    if (any(transform(skip(pReachedSet, 1), EXTRACT_PREDICATE_ELEMENT), FILTER_ABSTRACTION_ELEMENTS)) {
      // first element of reached is always an abstraction element, so skip it
      logger.log(Level.WARNING, "BMC algorithm does not work with abstractions. Could not check for satisfiability!");
      return sound;
    }
    
    FormulaManager fmgr = cpa.getFormulaManager();
    List<AbstractElement> targetElements = Lists.newArrayList(AbstractElements.filterTargetElements(pReachedSet));
    logger.log(Level.FINER, "Found", targetElements.size(), "potential target elements");

    TheoremProver prover = cpa.getTheoremProver();
    prover.init();

    boolean safe = true;
    if (checkTargetStates) {
      Formula program = fmgr.makeFalse();
      for (PredicateAbstractElement e : transform(targetElements, EXTRACT_PREDICATE_ELEMENT)) {
        assert e != null : "PredicateCPA exists but did not produce elements!";
        program = fmgr.makeOr(program, e.getPathFormula().getFormula());
      }
      
      logger.log(Level.INFO, "Starting satisfiability check...");
      stats.satCheck.start();
      safe = prover.isUnsat(program);
      stats.satCheck.stop();

    } else {
      safe = targetElements.isEmpty();
    }
    
    logger.log(Level.FINER, "Program is safe?:", safe);
    
    if (safe) {
      pReachedSet.removeAll(targetElements);
    }
    
    // check loop unwinding assertions, but don't bother if we are unsound anyway
    // or we have found a bug
    if (sound && safe && boundingAssertions) {
      Formula assertions = fmgr.makeFalse();
      
      // create formula for unwinding assertions
      for (AbstractElement e : pReachedSet) {
        AssumptionStorageElement asmpt = AbstractElements.extractElementByType(e, AssumptionStorageElement.class);
        if (asmpt.isStop()) {
          PredicateAbstractElement pred = AbstractElements.extractElementByType(e, PredicateAbstractElement.class);
          assertions = fmgr.makeOr(assertions, pred.getPathFormula().getFormula());
        }
      }
      
      stats.assertionsCheck.start();
      sound = prover.isUnsat(assertions);
      stats.assertionsCheck.stop();

    } else {
      sound = false; // signal that this is unsound
    }
    
    prover.reset();
    return sound;
  }

  @Override
  public ConfigurableProgramAnalysis getCPA() {
    return algorithm.getCPA();
  }

  @Override
  public void collectStatistics(Collection<Statistics> pStatsCollection) {
    if (algorithm instanceof StatisticsProvider) {
      ((StatisticsProvider)algorithm).collectStatistics(pStatsCollection);
    }
    pStatsCollection.add(stats);
  }  
}