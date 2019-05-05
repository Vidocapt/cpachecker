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
package org.sosy_lab.cpachecker.cfa.parser.eclipse.js;

import static org.mockito.Mockito.mock;

import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;
import java.util.List;
import org.eclipse.wst.jsdt.core.dom.Expression;
import org.eclipse.wst.jsdt.core.dom.PrefixExpression;
import org.eclipse.wst.jsdt.core.dom.SimpleName;
import org.junit.Test;
import org.sosy_lab.cpachecker.cfa.ast.FileLocation;
import org.sosy_lab.cpachecker.cfa.ast.js.JSBinaryExpression;
import org.sosy_lab.cpachecker.cfa.ast.js.JSExpression;
import org.sosy_lab.cpachecker.cfa.ast.js.JSIdExpression;
import org.sosy_lab.cpachecker.cfa.ast.js.JSInitializerExpression;
import org.sosy_lab.cpachecker.cfa.ast.js.JSVariableDeclaration;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.js.JSDeclarationEdge;
import org.sosy_lab.cpachecker.util.test.ReturnValueCaptor;

public class ExpressionListCFABuilderTest extends CFABuilderTestBase {

  private ReturnValueCaptor<CFAEdge> sideEffectEdgeCaptor;
  private ReturnValueCaptor<CFAEdge> secondSideEffectEdgeCaptor;

  @Override
  public void init() {
    super.init();
    sideEffectEdgeCaptor = new ReturnValueCaptor<>();
    secondSideEffectEdgeCaptor = new ReturnValueCaptor<>();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void appendSideEffectEdge(final JavaScriptCFABuilder pBuilder) {
    pBuilder.appendEdge(
        sideEffectEdgeCaptor.captureReturn(
            DummyEdge.withDescription("side effect " + sideEffectEdgeCaptor.getTimesCalled())));
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void appendSecondSideEffectEdge(final JavaScriptCFABuilder pBuilder) {
    pBuilder.appendEdge(
        secondSideEffectEdgeCaptor.captureReturn(
            DummyEdge.withDescription(
                "second side effect " + secondSideEffectEdgeCaptor.getTimesCalled())));
  }

  @Test
  public final void testSingleExpression() {
    // single expression should be appended without temporary assignment
    final Expression expression = parseExpression(PrefixExpression.class, "++x");
    final JSIdExpression x = mock(JSIdExpression.class, "x");
    builder.setExpressionAppendable(
        (pBuilder, pExpression) -> {
          appendSideEffectEdge(pBuilder);
          return x;
        });

    final List<JSExpression> result =
        new ExpressionListCFABuilder().append(builder, ImmutableList.of(expression));

    Truth.assertThat(result).isEqualTo(ImmutableList.of(x));
    Truth.assertThat(entryNode.getNumLeavingEdges()).isEqualTo(1);
    Truth.assertThat(sideEffectEdgeCaptor.getValues()).contains(entryNode.getLeavingEdge(0));
    Truth.assertThat(entryNode.getLeavingEdge(0).getSuccessor()).isEqualTo(builder.getExitNode());
  }

  @Test
  public final void testLastExpressionHasSideEffect() {
    final Expression first = parseExpression(SimpleName.class, "x");
    final Expression second = parseExpression(PrefixExpression.class, "++x");
    final JSIdExpression x = mock(JSIdExpression.class, "x");
    builder.setExpressionAppendable(
        (pBuilder, pExpression) -> {
          if (pExpression == first) {
            return x;
          } else if (pExpression == second) {
            appendSideEffectEdge(pBuilder);
            return x;
          } else {
            throw new RuntimeException("unexpected expression");
          }
        });

    final List<JSExpression> result =
        new ExpressionListCFABuilder().append(builder, ImmutableList.of(first, second));

    Truth.assertThat(sideEffectEdgeCaptor.getTimesCalled()).isGreaterThan(0);

    Truth.assertThat(entryNode.getNumLeavingEdges()).isEqualTo(1);
    Truth.assertThat(entryNode.getLeavingEdge(0)).isInstanceOf(JSDeclarationEdge.class);
    final JSDeclarationEdge tmpAssignmentEdge = (JSDeclarationEdge) entryNode.getLeavingEdge(0);
    final JSVariableDeclaration tmpVariableDeclaration =
        (JSVariableDeclaration) tmpAssignmentEdge.getDeclaration();
    Truth.assertThat(getAssignedExpression(tmpVariableDeclaration)).isEqualTo(x);

    Truth.assertThat(tmpAssignmentEdge.getSuccessor().getNumLeavingEdges()).isEqualTo(1);
    final CFAEdge sideEffectEdge = tmpAssignmentEdge.getSuccessor().getLeavingEdge(0);
    Truth.assertThat(sideEffectEdgeCaptor.getValues()).contains(sideEffectEdge);
    // no temporary variable assignment is required for the last expression.
    // That's why there should be no further (declaration) edge.
    Truth.assertThat(sideEffectEdge.getSuccessor()).isEqualTo(builder.getExitNode());

    // check result
    Truth.assertThat(result)
        .isEqualTo(
            ImmutableList.of(new JSIdExpression(FileLocation.DUMMY, tmpVariableDeclaration), x));
  }

  @Test
  public final void testExpressionsWithoutSideEffectAreNotTemporaryAssigned() {
    final Expression first = parseExpression(SimpleName.class, "x");
    final Expression second = parseExpression(SimpleName.class, "y");
    final Expression third = parseExpression(SimpleName.class, "z");
    final JSExpression firstResult = mock(JSIdExpression.class, "x");
    final JSExpression secondResult = mock(JSIdExpression.class, "y");
    final JSExpression thirdResult = mock(JSIdExpression.class, "z");
    builder.setExpressionAppendable(
        (pBuilder, pExpression) -> {
          if (pExpression == first) {
            return firstResult;
          } else if (pExpression == second) {
            return secondResult;
          } else if (pExpression == third) {
            return thirdResult;
          } else {
            throw new RuntimeException("unexpected expression");
          }
        });

    final List<JSExpression> result =
        new ExpressionListCFABuilder().append(builder, ImmutableList.of(first, second, third));

    Truth.assertThat(result).isEqualTo(ImmutableList.of(firstResult, secondResult, thirdResult));
    Truth.assertThat(builder.getExitNode()).isEqualTo(entryNode);
  }

  @Test
  public final void testNonLastExpressionHasSideEffect() {
    final Expression first = parseExpression(SimpleName.class, "x");
    final Expression second = parseExpression(PrefixExpression.class, "++x");
    final Expression third = parseExpression(PrefixExpression.class, "x + 1");
    final JSExpression firstResult = mock(JSIdExpression.class, "x");
    final JSExpression secondResult = mock(JSIdExpression.class, "x");
    final JSExpression thirdResult = mock(JSBinaryExpression.class, "x + 1");
    builder.setExpressionAppendable(
        (pBuilder, pExpression) -> {
          if (pExpression == first) {
            return firstResult;
          } else if (pExpression == second) {
            appendSideEffectEdge(pBuilder);
            return secondResult;
          } else if (pExpression == third) {
            return thirdResult;
          } else {
            throw new RuntimeException("unexpected expression");
          }
        });

    final List<JSExpression> result =
        new ExpressionListCFABuilder().append(builder, ImmutableList.of(first, second, third));

    Truth.assertThat(entryNode.getNumLeavingEdges()).isEqualTo(1);
    Truth.assertThat(entryNode.getLeavingEdge(0)).isInstanceOf(JSDeclarationEdge.class);
    final JSDeclarationEdge tmpAssignmentEdge = (JSDeclarationEdge) entryNode.getLeavingEdge(0);
    Truth.assertThat(tmpAssignmentEdge.getDeclaration()).isInstanceOf(JSVariableDeclaration.class);
    final JSVariableDeclaration tmpVariableDeclaration =
        (JSVariableDeclaration) tmpAssignmentEdge.getDeclaration();
    Truth.assertThat(getAssignedExpression(tmpVariableDeclaration)).isEqualTo(firstResult);

    Truth.assertThat(tmpAssignmentEdge.getSuccessor().getNumLeavingEdges()).isEqualTo(1);
    final CFAEdge sideEffectEdge = tmpAssignmentEdge.getSuccessor().getLeavingEdge(0);
    Truth.assertThat(sideEffectEdgeCaptor.getValues()).contains(sideEffectEdge);
    // no temporary variable assignment is required for the last expression.
    // That's why there should be no further (declaration) edge.
    Truth.assertThat(sideEffectEdge.getSuccessor()).isEqualTo(builder.getExitNode());

    // check result
    Truth.assertThat(result)
        .isEqualTo(
            ImmutableList.of(
                new JSIdExpression(FileLocation.DUMMY, tmpVariableDeclaration),
                secondResult,
                thirdResult));
  }

  @Test
  public final void testMultipleExpressionsWithSideEffect() {
    final Expression first = parseExpression(SimpleName.class, "x");
    final Expression second = parseExpression(PrefixExpression.class, "++x");
    final Expression third = parseExpression(PrefixExpression.class, "x + 1");
    final Expression fourth = parseExpression(PrefixExpression.class, "++x");
    final JSExpression firstResult = mock(JSIdExpression.class, "x");
    final JSExpression secondResult = mock(JSIdExpression.class, "x");
    final JSExpression thirdResult = mock(JSBinaryExpression.class, "x + 1");
    final JSExpression fourthResult = mock(JSIdExpression.class, "x");
    builder.setExpressionAppendable(
        (pBuilder, pExpression) -> {
          if (pExpression == first) {
            return firstResult;
          } else if (pExpression == second) {
            appendSideEffectEdge(pBuilder);
            return secondResult;
          } else if (pExpression == third) {
            return thirdResult;
          } else if (pExpression == fourth) {
            appendSecondSideEffectEdge(pBuilder);
            return fourthResult;
          } else {
            throw new RuntimeException("unexpected expression");
          }
        });

    final List<JSExpression> result =
        new ExpressionListCFABuilder()
            .append(builder, ImmutableList.of(first, second, third, fourth));

    Truth.assertThat(entryNode.getNumLeavingEdges()).isEqualTo(1);
    Truth.assertThat(entryNode.getLeavingEdge(0)).isInstanceOf(JSDeclarationEdge.class);
    final JSDeclarationEdge tmpAssignmentEdgeOfFirstExpr =
        (JSDeclarationEdge) entryNode.getLeavingEdge(0);
    Truth.assertThat(tmpAssignmentEdgeOfFirstExpr.getDeclaration())
        .isInstanceOf(JSVariableDeclaration.class);
    final JSVariableDeclaration tmpVariableDeclarationOfFirstExpr =
        (JSVariableDeclaration) tmpAssignmentEdgeOfFirstExpr.getDeclaration();
    Truth.assertThat(getAssignedExpression(tmpVariableDeclarationOfFirstExpr))
        .isEqualTo(firstResult);

    final CFANode afterFirstExpr = tmpAssignmentEdgeOfFirstExpr.getSuccessor();
    Truth.assertThat(afterFirstExpr.getNumLeavingEdges()).isEqualTo(1);
    final CFAEdge firstSideEffectEdge = afterFirstExpr.getLeavingEdge(0);
    Truth.assertThat(sideEffectEdgeCaptor.getValues()).contains(firstSideEffectEdge);
    Truth.assertThat(firstSideEffectEdge.getSuccessor().getNumLeavingEdges()).isEqualTo(1);
    Truth.assertThat(firstSideEffectEdge.getSuccessor().getLeavingEdge(0))
        .isInstanceOf(JSDeclarationEdge.class);
    final JSDeclarationEdge tmpAssignmentEdgeOfSecondExpr =
        (JSDeclarationEdge) firstSideEffectEdge.getSuccessor().getLeavingEdge(0);
    Truth.assertThat(tmpAssignmentEdgeOfSecondExpr.getDeclaration())
        .isInstanceOf(JSVariableDeclaration.class);
    final JSVariableDeclaration tmpVariableDeclarationOfSecondExpr =
        (JSVariableDeclaration) tmpAssignmentEdgeOfSecondExpr.getDeclaration();
    Truth.assertThat(getAssignedExpression(tmpVariableDeclarationOfSecondExpr))
        .isEqualTo(secondResult);

    final CFANode afterSecondExpr = tmpAssignmentEdgeOfSecondExpr.getSuccessor();
    Truth.assertThat(afterSecondExpr.getNumLeavingEdges()).isEqualTo(1);
    Truth.assertThat(afterSecondExpr.getLeavingEdge(0)).isInstanceOf(JSDeclarationEdge.class);
    final JSDeclarationEdge tmpAssignmentEdgeOfThirdExpr =
        (JSDeclarationEdge) afterSecondExpr.getLeavingEdge(0);
    Truth.assertThat(tmpAssignmentEdgeOfThirdExpr.getDeclaration())
        .isInstanceOf(JSVariableDeclaration.class);
    final JSVariableDeclaration tmpVariableDeclarationOfThirdExpr =
        (JSVariableDeclaration) tmpAssignmentEdgeOfThirdExpr.getDeclaration();
    Truth.assertThat(getAssignedExpression(tmpVariableDeclarationOfThirdExpr))
        .isEqualTo(thirdResult);

    final CFANode afterThirdExpr = tmpAssignmentEdgeOfThirdExpr.getSuccessor();
    Truth.assertThat(afterThirdExpr.getNumLeavingEdges()).isEqualTo(1);
    final CFAEdge secondSideEffectEdge = afterThirdExpr.getLeavingEdge(0);
    Truth.assertThat(secondSideEffectEdgeCaptor.getValues()).contains(secondSideEffectEdge);
    // no temporary variable assignment is required for the last expression.
    // That's why there should be no further (declaration) edge.
    Truth.assertThat(secondSideEffectEdge.getSuccessor()).isEqualTo(builder.getExitNode());

    // check result
    Truth.assertThat(result)
        .isEqualTo(
            ImmutableList.of(
                new JSIdExpression(FileLocation.DUMMY, tmpVariableDeclarationOfFirstExpr),
                new JSIdExpression(FileLocation.DUMMY, tmpVariableDeclarationOfSecondExpr),
                new JSIdExpression(FileLocation.DUMMY, tmpVariableDeclarationOfThirdExpr),
                fourthResult));
  }

  private static JSExpression getAssignedExpression(
      final JSVariableDeclaration pTmpVariableDeclarationOfFirstExpr) {
    return ((JSInitializerExpression) pTmpVariableDeclarationOfFirstExpr.getInitializer())
        .getExpression();
  }
}