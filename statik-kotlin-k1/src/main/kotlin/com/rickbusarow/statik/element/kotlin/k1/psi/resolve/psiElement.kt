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

package com.rickbusarow.statik.element.kotlin.k1.psi.resolve

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.stdlib.requireNotNull
import com.rickbusarow.statik.utils.traversal.Traversals
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.types.KotlinType

/** @return `true` if this [PsiElement] is a descendant of [T], `false` otherwise. */
@InternalStatikApi
public inline fun <reified T : PsiElement> PsiElement.isPartOf(): Boolean =
  getNonStrictParentOfType<T>() != null

/**
 * @return a sequence of child nodes of this [PsiElement] in depth-first
 *   order. The sequence starts with the first child node of this [PsiElement],
 *   followed by the first child node of the first child node, and so on.
 */
@InternalStatikApi
public fun PsiElement.childrenDepthFirst(): Sequence<PsiElement> {
  return Traversals.depthFirstTraversal(this) { children.toList() }
}

/**
 * @return a sequence of child nodes of type [T] of this [PsiElement] in depth-first
 *   order. The sequence starts with the first child node of this [PsiElement],
 *   followed by the first child node of the first child node, and so on.
 */
@InternalStatikApi
public inline fun <reified T : PsiElement> PsiElement.childrenOfTypeDepthFirst(): Sequence<T> {
  return Traversals.depthFirstTraversal(this) { children.toList() }
    .filterIsInstance<T>()
}

/**
 * @param predicate stops visiting child nodes of the given node once this predicate returns false
 * @return a sequence of child nodes of this [PsiElement] in depth-first order that
 *   satisfy the given [predicate]. The sequence starts with the first child node of this
 *   [PsiElement], followed by the first child node of the first child node, and so on.
 */
@InternalStatikApi
public inline fun PsiElement.childrenDepthFirst(
  crossinline predicate: (PsiElement) -> Boolean
): Sequence<PsiElement> = Traversals.depthFirstTraversal(this) { children.filter(predicate) }

/**
 * @return a sequence of child nodes of type [T] of this [PsiElement] in breadth-first
 *   order. The sequence starts with the first child node of this [PsiElement],
 *   followed by the first child node of the second child node, and so on.
 */
@InternalStatikApi
public inline fun <reified T : PsiElement> PsiElement.childrenOfTypeBreadthFirst(): Sequence<T> {
  return Traversals.breadthFirstTraversal(this) { children.toList() }
    .filterIsInstance<T>()
}

/**
 * @return a sequence of child nodes of this [PsiElement] in breadth-first
 *   order. The sequence starts with all the child nodes of this [PsiElement],
 *   followed by all the child nodes of the first child node, and so on.
 */
@InternalStatikApi
public fun PsiElement.childrenBreadthFirst(): Sequence<PsiElement> {
  return Traversals.breadthFirstTraversal(this) { children.toList() }
}

/**
 * @param [predicate] stops visiting child nodes of the parent
 *   of the given node once this predicate returns false
 * @return a sequence of child nodes of this [PsiElement] in breadth-first order that
 *   satisfy the given [predicate]. The sequence starts with all the child nodes of this
 *   [PsiElement], followed by all the child nodes of the first child node, and so on.
 */
@InternalStatikApi
public inline fun PsiElement.childrenBreadthFirst(
  crossinline predicate: (PsiElement) -> Boolean
): Sequence<PsiElement> {
  return Traversals.breadthFirstTraversal(this) { children.filter(predicate) }
}

@InternalStatikApi
public fun KotlinType?.requireReferenceName(): ReferenceName {
  return requireNotNull { "The receiver type is null" }.asReferenceName()
}

@InternalStatikApi
public fun PsiElement.isQualifiedPropertyOrCallExpression(): Boolean {
  // properties which are qualified have a direct parent of `KtQualifiedExpression`
  if (parent is KtQualifiedExpression) return true

  // A qualified function is actually a NamedReferencedExpression (`foo`)
  // with a KtCallExpression (`foo()`) for a parent,
  // and a qualified grandparent (`com.foo()`).
  return parent is KtCallExpression && parent.parent is KtQualifiedExpression
}

/**
 * This poorly-named function will return the most-qualified name available for a given
 * [PsiElement] from the snippet of code where it's being called, without looking at imports.
 */
@InternalStatikApi
public fun PsiElement.callSiteName(): String {
  // If a qualified expression is a function call, then the selector expression is the full
  // function call (`KtCallExpression`).
  // For example, `com.example.foo(...)` has a selector of `foo(...)`.
  // to get just the qualified name, we have to get the `calleeExpression` of the
  // function, then append that to the parent qualified expression's receiver expression.
  return (this as? KtDotQualifiedExpression)
    ?.selectorExpression
    ?.let { it as? KtCallExpression }
    ?.calleeExpression
    ?.let {
      val receiver = receiverExpression.text
      val selectorCallText = it.text

      "$receiver.$selectorCallText"
    }
    ?: text
}
