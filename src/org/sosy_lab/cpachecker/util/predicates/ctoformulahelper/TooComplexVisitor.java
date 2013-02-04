/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2013  Dirk Beyer
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
package org.sosy_lab.cpachecker.util.predicates.ctoformulahelper;

import org.sosy_lab.cpachecker.cfa.ast.c.CArraySubscriptExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CBinaryExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CCastExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CCharLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CExpressionVisitor;
import org.sosy_lab.cpachecker.cfa.ast.c.CFieldReference;
import org.sosy_lab.cpachecker.cfa.ast.c.CFloatLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CIdExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CIntegerLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CStringLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CTypeIdExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CTypeIdInitializerExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CUnaryExpression;

public class TooComplexVisitor implements CExpressionVisitor<Boolean, Exception> {

  private final boolean handleFieldAccess;

  public TooComplexVisitor(boolean pHandleFieldAccess) {
    this.handleFieldAccess = pHandleFieldAccess;
  }

  @Override
  public Boolean visit(CArraySubscriptExpression pIastArraySubscriptExpression) throws Exception {
    return true;
  }

  @Override
  public Boolean visit(CBinaryExpression pIastBinaryExpression) throws Exception {
    return
        pIastBinaryExpression.getOperand1().accept(this) ||
        pIastBinaryExpression.getOperand2().accept(this);
  }

  @Override
  public Boolean visit(CCastExpression pIastCastExpression) throws Exception {
    return pIastCastExpression.getOperand().accept(this);
  }

  @Override
  public Boolean visit(CFieldReference pIastFieldReference) throws Exception {
    return
        handleFieldAccess &&
        CtoFormulaTypeUtils.getRealFieldOwner(pIastFieldReference).accept(this);
  }

  @Override
  public Boolean visit(CIdExpression pIastIdExpression) throws Exception {
    return false;
  }

  @Override
  public Boolean visit(CCharLiteralExpression pIastCharLiteralExpression) throws Exception {
    return false;
  }

  @Override
  public Boolean visit(CFloatLiteralExpression pIastFloatLiteralExpression) throws Exception {
    return false;
  }

  @Override
  public Boolean visit(CIntegerLiteralExpression pIastIntegerLiteralExpression) throws Exception {
    return false;
  }

  @Override
  public Boolean visit(CStringLiteralExpression pIastStringLiteralExpression) throws Exception {
    return false;
  }

  @Override
  public Boolean visit(CTypeIdExpression pIastTypeIdExpression) throws Exception {
    return false;
  }

  @Override
  public Boolean visit(CTypeIdInitializerExpression pCTypeIdInitializerExpression) throws Exception {
    return false;
  }

  @Override
  public Boolean visit(CUnaryExpression pIastUnaryExpression) throws Exception {
    return pIastUnaryExpression.getOperand().accept(this);
  }

}