/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2018  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.threadmodular;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import java.util.Optional;
import org.sosy_lab.cpachecker.core.defaults.AbstractSingleWrapperState;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.interfaces.PrecisionAdjustment;
import org.sosy_lab.cpachecker.core.interfaces.PrecisionAdjustmentResult;
import org.sosy_lab.cpachecker.core.interfaces.PrecisionAdjustmentResult.Action;
import org.sosy_lab.cpachecker.core.reachedset.UnmodifiableReachedSet;
import org.sosy_lab.cpachecker.exceptions.CPAException;

public class ThreadModularPrecisionAdjustment implements PrecisionAdjustment {

  private final PrecisionAdjustment wrappedPrec;

  public ThreadModularPrecisionAdjustment(PrecisionAdjustment pWrappedPrec) {
    wrappedPrec = pWrappedPrec;
  }

  @Override
  public Optional<PrecisionAdjustmentResult> prec(
      AbstractState pState,
      Precision pPrecision,
      UnmodifiableReachedSet pStates,
      Function<AbstractState, AbstractState> pStateProjection,
      AbstractState pFullState)
      throws CPAException, InterruptedException {

    ThreadModularState state = (ThreadModularState) pState;

    Optional<PrecisionAdjustmentResult> optionalUnwrappedResult =
        wrappedPrec.prec(
            state.getWrappedState(),
            pPrecision,
            pStates,
            Functions.compose(AbstractSingleWrapperState.getUnwrapFunction(), pStateProjection),
            pFullState);

    if (!optionalUnwrappedResult.isPresent()) {
      return Optional.empty();
    }

    PrecisionAdjustmentResult unwrappedResult = optionalUnwrappedResult.get();

    AbstractState newElement = unwrappedResult.abstractState();
    Precision newPrecision = unwrappedResult.precision();
    Action action = unwrappedResult.action();

    if ((state.getWrappedState() == newElement)) {
      // nothing has changed
      return Optional.of(PrecisionAdjustmentResult.create(pState, newPrecision, action));
    }

    ThreadModularState resultElement =
        new ThreadModularState(newElement, state.getInferenceObject());

    return Optional.of(PrecisionAdjustmentResult.create(resultElement, newPrecision, action));
  }
}
