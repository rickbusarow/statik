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

package com.rickbusarow.statik.element.kotlin.psi.utils.traversal

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.element.StatikElement
import com.rickbusarow.statik.element.StatikElementWithParent
import com.rickbusarow.statik.utils.traversal.AbstractTreePrinter
import kotlinx.coroutines.flow.Flow

/**
 * prints a tree starting at any arbitrary psi element,
 * showing all its children types and their text
 */
@InternalStatikApi
public class StatikTreePrinter(
  whitespaceChar: Char = ' '
) : AbstractTreePrinter<StatikElement>(whitespaceChar) {

  override fun StatikElement.text(): String = text
  override fun StatikElement.typeName(): String = javaClass.simpleName
  override fun StatikElement.parent(): StatikElement? =
    (this as? StatikElementWithParent<*>)?.parent

  override fun StatikElement.simpleClassName(): String = this::class.java.simpleName
  override fun StatikElement.children(): Flow<StatikElement> = children

  @InternalStatikApi
  public companion object {

    @InternalStatikApi
    public fun <T : StatikElement> T.printEverything(whitespaceChar: Char = ' '): T =
      apply { StatikTreePrinter(whitespaceChar).printTreeString(this) }

    @InternalStatikApi
    public fun <T : StatikElement> T.printEverythingFromPSINode(whitespaceChar: Char = ' '): T =
      apply { PsiTreePrinter(whitespaceChar).printTreeString(this.psi) }

    @InternalStatikApi
    public fun <T : StatikElement> T.printEverythingFromASTNode(whitespaceChar: Char = ' '): T =
      apply { ASTTreePrinter(whitespaceChar).printTreeString(this.psi.node) }
  }
}