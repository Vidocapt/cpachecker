/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
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
package org.sosy_lab.cpachecker.cfa.ast;

import java.io.Serializable;
import org.sosy_lab.cpachecker.cfa.ast.c.CAstNodeVisitor;
import org.sosy_lab.cpachecker.cfa.ast.java.JAstNodeVisitor;

@SuppressWarnings("serial") // we cannot set a UID for an interface
public interface AAstNode extends Serializable {

  FileLocation getFileLocation();

  /**
   * Constructs a String representation of the AST represented by this node. Depending on the
   * parameter value different representations for local variables are used. Typically, you want to
   * call this method with a fixed value for the parameter. In these cases, we highly recommend to
   * either use {@link #toASTString()} (fixed parameter value false) or {@link
   * #toQualifiedASTString()} (fixed parameter value true).
   *
   * @param pQualified - if true use qualified variable names, i.e., add prefix functionname__ to
   *     local variable names, where functionname is the name of the function that declared the
   *     local variable
   * @return AST string either using qualified names or pure names for local variables
   */
  String toASTString(boolean pQualified);

  String toParenthesizedASTString(boolean pQualified);

  default String toASTString() {
    return toASTString(false);
  }

  default String toParenthesizedASTString() {
    return toParenthesizedASTString(false);
  }

  default String toQualifiedASTString() {
    return toASTString(true);
  }

  /**
   * Accept methods for visitors that works with AST nodes of all languages. It requires a visitor
   * that implements the respective visitor interfaces for all languages. If you can, do not call
   * this method but one of the normal "accept" methods.
   *
   * @param v The visitor.
   * @return Returns the object returned by the visit method.
   */
  <
          R,
          R1 extends R,
          R2 extends R,
          X1 extends Exception,
          X2 extends Exception,
          V extends CAstNodeVisitor<R1, X1> & JAstNodeVisitor<R2, X2>>
      R accept_(V v) throws X1, X2;
}
