/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2015  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.usage.refinement;

import com.google.errorprone.annotations.ForOverride;
import java.util.Collection;
import org.sosy_lab.cpachecker.core.interfaces.Statistics;
import org.sosy_lab.cpachecker.core.interfaces.StatisticsProvider;
import org.sosy_lab.cpachecker.exceptions.CPAException;



public abstract class WrappedConfigurableRefinementBlock<I, O> implements ConfigurableRefinementBlock<I>, StatisticsProvider {
  protected ConfigurableRefinementBlock<O> wrappedRefiner;

  @ForOverride
  protected void handleStartSignal(
      @SuppressWarnings("unused") Class<? extends RefinementInterface> callerClass) {}

  @ForOverride
  protected void handleFinishSignal(
      @SuppressWarnings("unused") Class<? extends RefinementInterface> callerClass) {}

  @ForOverride
  protected void handleUpdateSignal(
      @SuppressWarnings("unused") Class<? extends RefinementInterface> callerClass,
      @SuppressWarnings("unused") Object data) {}

  @ForOverride
  protected void handleSignal(
      @SuppressWarnings("unused") Class<? extends RefinementInterface> callerClass,
      @SuppressWarnings("unused") Object data) {}

  protected void sendStartSignal() {
    wrappedRefiner.start(getClass());
  }

  protected void sendFinishSignal() throws CPAException, InterruptedException {
    wrappedRefiner.finish(getClass());
  }

  protected void sendUpdateSignal(Class<? extends RefinementInterface> dstClass, Object data) {
    wrappedRefiner.update(getClass(), dstClass, data);
  }

  public WrappedConfigurableRefinementBlock(ConfigurableRefinementBlock<O> wrapper) {
    wrappedRefiner = wrapper;
  }

  @Override
  public final void update(Class<? extends RefinementInterface> callerClass, Class<? extends RefinementInterface> dstClass, Object data) {
    if (getClass().equals(dstClass)) {
      handleUpdateSignal(callerClass, data);
    } else {
      wrappedRefiner.update(callerClass, dstClass, data);
    }
  }

  @Override
  public final void start(Class<? extends RefinementInterface> callerClass) {
    handleStartSignal(callerClass);
    wrappedRefiner.start(callerClass);
  }

  @Override
  public final void finish(Class<? extends RefinementInterface> callerClass) throws CPAException, InterruptedException {
    handleFinishSignal(callerClass);
    wrappedRefiner.finish(callerClass);
  }

  @Override
  public void collectStatistics(Collection<Statistics> statsCollection) {
    if (wrappedRefiner instanceof StatisticsProvider) {
      ((StatisticsProvider)wrappedRefiner).collectStatistics(statsCollection);
    }
  }
}
