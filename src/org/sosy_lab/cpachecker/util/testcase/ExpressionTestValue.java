/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2019  Dirk Beyer
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
package org.sosy_lab.cpachecker.util.testcase;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.sosy_lab.cpachecker.cfa.ast.AAstNode;
import org.sosy_lab.cpachecker.cfa.ast.AExpression;

public class ExpressionTestValue extends TestValue {

  private final AExpression value;

  private ExpressionTestValue(ImmutableList<AAstNode> pAuxiliaryStatements, AExpression pValue) {
    super(pAuxiliaryStatements, pValue);
    value = pValue;
  }

  @Override
  public AExpression getValue() {
    return value;
  }

  public static ExpressionTestValue of(AExpression pValue) {
    return of(ImmutableList.of(), pValue);
  }

  public static ExpressionTestValue of(List<AAstNode> pAuxiliaryStatments, AExpression pValue) {
    return new ExpressionTestValue(ImmutableList.copyOf(pAuxiliaryStatments), pValue);
  }
}
