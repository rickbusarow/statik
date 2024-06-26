/*
 * Copyright (C) 2024 Rick Busarow
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rickbusarow.statik.element.kotlin.k1.psi.traversal

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.utils.traversal.AbstractTreePrinter
import org.jetbrains.kotlin.com.intellij.psi.PsiElement

/**
 * prints a tree starting at any arbitrary psi element,
 * showing all its children types and their text
 */
@InternalStatikApi
public class PsiTreePrinter(
  whitespaceChar: Char = ' '
) : AbstractTreePrinter<PsiElement>(whitespaceChar) {

  override fun PsiElement.text(): String = text
  override fun PsiElement.typeName(): String = node.elementType.toString()
  override fun PsiElement.parent(): PsiElement? = parent
  override fun PsiElement.simpleClassName(): String = this::class.java.simpleName
  override fun PsiElement.children(): Sequence<PsiElement> = children.asSequence()

  @InternalStatikApi
  public companion object {

    @InternalStatikApi
    public fun <T : PsiElement> T.printEverything(whitespaceChar: Char = ' '): T =
      apply { PsiTreePrinter(whitespaceChar).printTreeString(this) }

    @InternalStatikApi
    public fun <T : PsiElement> T.printEverythingFromASTNode(whitespaceChar: Char = ' '): T =
      apply { ASTTreePrinter(whitespaceChar).printTreeString(this.node) }
  }
}
