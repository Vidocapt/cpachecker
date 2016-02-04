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
package org.sosy_lab.cpachecker.core.algorithm.mpa.partitioning;

import java.util.Comparator;
import java.util.Set;

import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.core.algorithm.mpa.interfaces.Partitioning;
import org.sosy_lab.cpachecker.core.algorithm.mpa.interfaces.Partitioning.PartitioningStatus;
import org.sosy_lab.cpachecker.core.interfaces.Property;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Options
public class DefaultOperator extends PartitioningBudgetOperator {

  public DefaultOperator(Configuration pConfig, LogManager pLogger) throws InvalidConfigurationException {
    super(pConfig, pLogger);
  }

  @Override
  public Partitioning partition(
      Partitioning pLastCheckedPartitioning,
      Set<Property> pToCheck, Set<Property> pExpensiveProperties,
      Comparator<Property> pPropertyExpenseComparator) throws PartitioningException {

    Set<Property> cheapProperties = Sets.difference(pToCheck, pExpensiveProperties);

    ImmutableList<ImmutableSet<Property>> partitions;
    PartitioningStatus status;

    if (cheapProperties.size() > 0) {
      partitions = ImmutableList.of(ImmutableSet.copyOf(cheapProperties));
      status = PartitioningStatus.NOT_EXHAUSTED_ONLY;
    } else {
      partitions = ImmutableList.of(ImmutableSet.copyOf(pToCheck));
      status = PartitioningStatus.ALL_IN_ONE;
    }

    // At least as much partitions as in pLastCheckedPartitioning
    if (partitions.size() < pLastCheckedPartitioning.partitionCount()) {
      partitions = bisectPartitons(partitions, pPropertyExpenseComparator);
      status = PartitioningStatus.MORE_PARTITIONS;
    }

    // If possible not equal to the last partitioning
    if (pLastCheckedPartitioning.getPartitions().equals(partitions)) {
      // Divide the partitioning into two halfs...
      return create(PartitioningStatus.CHEAPEST_BISECT,
          getPropertyBudgetingOperator(),
          getPartitionBudgetingOperator(),
          bisectPartitons(partitions, pPropertyExpenseComparator));
    }

    return create(status, getPropertyBudgetingOperator(), getPartitionBudgetingOperator(), partitions);
  }

}
