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

import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.parent
import com.rickbusarow.statik.utils.traversal.AbstractTreePrinter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.children as realChildren

/**
 * prints a tree starting at any arbitrary psi element,
 * showing all its children types and their text
 */
internal class ASTTreePrinter(
  whitespaceChar: Char = ' '
) : AbstractTreePrinter<ASTNode>(whitespaceChar) {

  override fun ASTNode.text(): String = text
  override fun ASTNode.typeName(): String = elementType.toString()
  override fun ASTNode.parent(): ASTNode? = parent
  override fun ASTNode.simpleClassName(): String = this::class.java.simpleName
  override fun ASTNode.children(): Flow<ASTNode> = realChildren().asFlow()

  companion object {

    internal fun ASTNode.printEverything(whitespaceChar: Char = ' ') = apply {
      ASTTreePrinter(whitespaceChar).printTreeString(this)
    }
  }
}
