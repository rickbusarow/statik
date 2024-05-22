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
import com.rickbusarow.statik.utils.traversal.Traversals.breadthFirstTraversal
import com.rickbusarow.statik.utils.traversal.Traversals.depthFirstTraversal
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/** @return a sequence of the children of this [ASTNode]. */
@InternalStatikApi
public fun ASTNode.children(): Sequence<ASTNode> =
  generateSequence(firstChildNode) { node -> node.treeNext }

/**
 * Finds the previous leaf [ASTNode] relative to the receiver, optionally including empty nodes.
 *
 * @param includeEmpty If true, includes empty nodes in the search; otherwise, skips them.
 * @return The previous leaf [ASTNode] or null if there is none.
 */
@InternalStatikApi
public fun ASTNode.prevLeaf(includeEmpty: Boolean = false): ASTNode? {
  var n = this.prevLeafAny()
  if (!includeEmpty) {
    while (n != null && n.textLength == 0) {
      n = n.prevLeafAny()
    }
  }
  return n
}

/**
 * Finds the previous leaf [ASTNode] relative to the
 * receiver that satisfies the given predicate [p].
 *
 * @param p The predicate that the resulting [ASTNode] must satisfy.
 * @return The previous leaf [ASTNode] or null if there is none.
 */
@InternalStatikApi
public fun ASTNode.prevLeaf(p: (ASTNode) -> Boolean): ASTNode? {
  var n = this.prevLeafAny()
  while (n != null && !p(n)) {
    n = n.prevLeafAny()
  }
  return n
}

/**
 * Finds the previous leaf [ASTNode] relative to the receiver without any conditions.
 *
 * @return The previous leaf [ASTNode] or null if there is none.
 */
@InternalStatikApi
public fun ASTNode.prevLeafAny(): ASTNode? {
  val prevSibling = treePrev
  if (prevSibling != null) {
    return treePrev.lastChildLeafOrSelf()
  }
  return treeParent?.prevLeafAny()
}

/**
 * Finds the last child leaf of this [ASTNode] or the node itself if it has no children.
 *
 * @return The last child leaf [ASTNode] or the node itself.
 */
@InternalStatikApi
public fun ASTNode.lastChildLeafOrSelf(): ASTNode {
  return childrenDepthFirst().lastOrNull { it.isLeaf() } ?: this
}

/**
 * Finds the previous code leaf [ASTNode] relative
 * to the receiver, optionally including empty nodes.
 *
 * @param includeEmpty If true, includes empty nodes in the search; otherwise, skips them.
 * @return The previous code leaf [ASTNode] or null if there is none.
 */
@InternalStatikApi
public fun ASTNode.prevCodeLeaf(includeEmpty: Boolean = false): ASTNode? {
  var n = prevLeaf(includeEmpty)
  while (n != null && (n.elementType == ElementType.WHITE_SPACE || n.isPartOfComment())) {
    n = n.prevLeaf(includeEmpty)
  }
  return n
}

/** @return the previous code sibling [ASTNode] by filtering out white spaces and comments. */
@InternalStatikApi
public fun ASTNode.prevCodeSibling(): ASTNode? =
  prevSibling { it.elementType != ElementType.WHITE_SPACE && !it.isPartOfComment() }

/** @return the previous sibling [ASTNode] that satisfies the given [predicate]. */
@InternalStatikApi
public inline fun ASTNode.prevSibling(predicate: (ASTNode) -> Boolean = { true }): ASTNode? {
  var n = this.treePrev
  while (n != null) {
    if (predicate(n)) {
      return n
    }
    n = n.treePrev
  }
  return null
}

/**
 * @param includeEmpty If true, includes empty leaf nodes in the search.
 * @param skipSubtree If true, skips the subtree during the search.
 * @return the next code leaf [ASTNode] by filtering out white spaces and comments.
 */
@InternalStatikApi
public fun ASTNode.nextCodeLeaf(
  includeEmpty: Boolean = false,
  skipSubtree: Boolean = false
): ASTNode? {
  var n = nextLeaf(includeEmpty, skipSubtree)
  while (n != null && (n.elementType == ElementType.WHITE_SPACE || n.isPartOfComment())) {
    n = n.nextLeaf(includeEmpty, skipSubtree)
  }
  return n
}

/** @return the next code sibling [ASTNode] by filtering out white spaces and comments. */
@InternalStatikApi
public fun ASTNode.nextCodeSibling(): ASTNode? =
  nextSibling { it.elementType != ElementType.WHITE_SPACE && !it.isPartOfComment() }

/** @return the next sibling [ASTNode] that satisfies the given [predicate]. */
@InternalStatikApi
public inline fun ASTNode.nextSibling(predicate: (ASTNode) -> Boolean = { true }): ASTNode? {
  var n = this.treeNext
  while (n != null) {
    if (predicate(n)) {
      return n
    }
    n = n.treeNext
  }
  return null
}

/**
 * @param strict If true, the search starts from the receiver
 *   node's parent; otherwise, it starts from the receiver node.
 * @param predicate returns true for the matching predicate
 * @return the parent [ASTNode] that satisfies the given [predicate].
 */
@InternalStatikApi
public fun ASTNode.parent(
  strict: Boolean = true,
  predicate: (ASTNode) -> Boolean
): ASTNode? {
  var n: ASTNode? = if (strict) this.treeParent else this
  while (n != null) {
    if (predicate(n)) {
      return n
    }
    n = n.treeParent
  }
  return null
}

/**
 * @param elementType the requested type of node
 * @param strict If true, the search starts from the receiver
 *   node's parent; otherwise, it starts from the receiver node.
 * @return the parent [ASTNode] with the given [elementType].
 */
@InternalStatikApi
public fun ASTNode.parent(
  elementType: IElementType,
  strict: Boolean = true
): ASTNode? {
  var n: ASTNode? = if (strict) this.treeParent else this
  while (n != null) {
    if (n.elementType == elementType) {
      return n
    }
    n = n.treeParent
  }
  return null
}

/**
 * @param includeEmpty If true, includes empty leaf nodes in the search.
 * @param skipSubtree If true, skips the subtree during the search.
 * @return the next leaf [ASTNode] based on the given parameters.
 */
@InternalStatikApi
public fun ASTNode.nextLeaf(
  includeEmpty: Boolean = false,
  skipSubtree: Boolean = false
): ASTNode? {
  var n = if (skipSubtree) this.lastChildLeafOrSelf().nextLeafAny() else this.nextLeafAny()
  if (!includeEmpty) {
    while (n != null && n.textLength == 0) {
      n = n.nextLeafAny()
    }
  }
  return n
}

/** @return the next leaf [ASTNode] that satisfies the given predicate [p]. */
@InternalStatikApi
public fun ASTNode.nextLeaf(p: (ASTNode) -> Boolean): ASTNode? {
  var n = this.nextLeafAny()
  while (n != null && !p(n)) {
    n = n.nextLeafAny()
  }
  return n
}

/** @return the next leaf [ASTNode] without any specific conditions. */
@InternalStatikApi
public fun ASTNode.nextLeafAny(): ASTNode? {
  var n = this
  if (n.firstChildNode != null) {
    do {
      n = n.firstChildNode
    } while (n.firstChildNode != null)
    return n
  }
  return n.nextLeafStrict()
}

/** @return the next leaf [ASTNode] by traversing the tree strictly. */
@InternalStatikApi
public fun ASTNode.nextLeafStrict(): ASTNode? {
  val nextSibling: ASTNode? = treeNext
  if (nextSibling != null) {
    return nextSibling.firstChildLeafOrSelf()
  }
  return treeParent?.nextLeafStrict()
}

/** @return the first child leaf [ASTNode] or the receiver node if it has no children. */
@InternalStatikApi
public fun ASTNode.firstChildLeafOrSelf(): ASTNode {
  var n = this
  if (n.firstChildNode != null) {
    do {
      n = n.firstChildNode
    } while (n.firstChildNode != null)
    return n
  }
  return n
}

/**
 * Check if the given [ASTNode] is a code leaf. E.g. it must be a leaf and
 * may not be a whitespace or be part of a comment. @return true if the
 * receiver node is a leaf, not a whitespace, and not part of a comment
 */
@InternalStatikApi
public fun ASTNode.isCodeLeaf(): Boolean = isLeaf() && !isWhiteSpace() && !isPartOfComment()

/** @return true if the node is part of a KDoc comment, block comment, or end-of-line comment */
@InternalStatikApi
public fun ASTNode.isPartOfComment(): Boolean = parent(strict = false) {
  it.psi is PsiComment
} != null

/**
 * Updates or inserts a new whitespace element with [text] before the given node. If the
 * node itself is a whitespace then its contents is replaced with [text]. If the node is a
 * (nested) composite element, the whitespace element is added after the previous leaf node.
 */
@InternalStatikApi
public fun ASTNode.upsertWhitespaceBeforeMe(text: String) {
  if (this is LeafElement) {
    if (this.elementType == ElementType.WHITE_SPACE) {
      return replaceWhitespaceWith(text)
    }
    val previous = treePrev ?: prevLeaf()
    if (previous != null && previous.elementType == ElementType.WHITE_SPACE) {
      previous.replaceWhitespaceWith(text)
    } else {
      PsiWhiteSpaceImpl(text).also { psiWhiteSpace ->
        (psi as LeafElement).rawInsertBeforeMe(psiWhiteSpace)
      }
    }
  } else {
    val prevLeaf =
      requireNotNull(prevLeaf()) {
        "Can not upsert a whitespace if the first node is a non-leaf node"
      }
    prevLeaf.upsertWhitespaceAfterMe(text)
  }
}

/**
 * Updates or inserts a new whitespace element with [text] after the given node. If the
 * node itself is a whitespace then its contents is replaced with [text]. If the node is a
 * (nested) composite element, the whitespace element is added after the last child leaf.
 */
@InternalStatikApi
public fun ASTNode.upsertWhitespaceAfterMe(text: String) {
  if (this is LeafElement) {
    if (this.elementType == ElementType.WHITE_SPACE) {
      return replaceWhitespaceWith(text)
    }
    val next = treeNext ?: nextLeaf()
    if (next != null && next.elementType == ElementType.WHITE_SPACE) {
      next.replaceWhitespaceWith(text)
    } else {
      PsiWhiteSpaceImpl(text).also { psiWhiteSpace ->
        (psi as LeafElement).rawInsertAfterMe(psiWhiteSpace)
      }
    }
  } else {
    lastChildLeafOrSelf().upsertWhitespaceAfterMe(text)
  }
}

/**
 * Replaces the receiver [ASTNode] white space with the given [text].
 *
 * @throws IllegalArgumentException if the receiver
 *   [ASTNode] does not have a WHITE_SPACE element type.
 */
@InternalStatikApi
public fun ASTNode.replaceWhitespaceWith(text: String) {
  require(this.elementType == ElementType.WHITE_SPACE)
  if (this.text != text) {
    (this.psi as LeafElement).rawReplaceWithText(text)
  }
}

/** @return the column number of the receiver [ASTNode]. */
@InternalStatikApi
public val ASTNode.column: Int
  get() {
    var leaf = this.prevLeaf()
    var offsetToTheLeft = 0
    while (leaf != null) {
      if (leaf.isWhiteSpaceWithNewline() || leaf.isRegularStringWithNewline()) {
        offsetToTheLeft += leaf.textLength - 1 - leaf.text.lastIndexOf('\n')
      } else {
        offsetToTheLeft += leaf.textLength
        leaf = leaf.prevLeaf()
      }
    }
    return offsetToTheLeft + 1
  }

/** @return true if the [ASTNode] is part of a string, false otherwise. */
@InternalStatikApi
public fun ASTNode.isPartOfString(): Boolean = parent(
  ElementType.STRING_TEMPLATE,
  strict = false
) != null

/** @return true if the [ASTNode] is white space, false otherwise. */
@InternalStatikApi
public fun ASTNode?.isWhiteSpace(): Boolean = this != null && elementType == ElementType.WHITE_SPACE

/** @return true if the [ASTNode] is white space with a newline, false otherwise. */
@InternalStatikApi
public fun ASTNode?.isWhiteSpaceWithNewline(): Boolean =
  this != null && elementType == ElementType.WHITE_SPACE && textContains('\n')

/** @return true if the [ASTNode] is white space with a newline, false otherwise. */
@InternalStatikApi
public fun ASTNode?.isRegularStringWithNewline(): Boolean =
  this != null && elementType == ElementType.REGULAR_STRING_PART && textContains('\n')

/** @return true if the [ASTNode] is white space without a newline, false otherwise. */
@InternalStatikApi
public fun ASTNode?.isWhiteSpaceWithoutNewline(): Boolean =
  this != null && elementType == ElementType.WHITE_SPACE && !textContains('\n')

/** @return true if the [ASTNode] is a root, false otherwise. */
@InternalStatikApi
public fun ASTNode.isRoot(): Boolean = elementType == ElementType.FILE

/** @return true if the [ASTNode] is a leaf (has no children), false otherwise. */
@InternalStatikApi
public fun ASTNode.isLeaf(): Boolean = firstChildNode == null

/** */
@InternalStatikApi
public fun ASTNode?.isBlank(): Boolean = this != null && text.isBlank()

/** */
private val COPYRIGHT_COMMENT_START = Regex(
  """(?:/\*{1,2}\s+(?:\*\s)?|// *)Copyright [\s\S]*"""
)

/** */
@InternalStatikApi
public fun ASTNode.isCopyrightHeader(): Boolean {
  if (elementType != ElementType.BLOCK_COMMENT) return false

  return text.matches(COPYRIGHT_COMMENT_START)
}

/** */
@InternalStatikApi
public fun ASTNode?.isFile(): Boolean = this?.elementType == ElementType.FILE

/** */
@InternalStatikApi
public fun ASTNode?.isTopLevel(): Boolean = this?.parent.isFile()

/** */
@InternalStatikApi
@OptIn(ExperimentalContracts::class)
public fun ASTNode?.isWhiteSpaceOrBlank(): Boolean {

  contract {
    returns(true) implies (this@isWhiteSpaceOrBlank != null)
  }

  if (this == null) return false
  return isWhiteSpace() || isBlank()
}

/** */
@InternalStatikApi
public fun ASTNode?.isScript(): Boolean = this?.parent?.elementType == ElementType.SCRIPT

/** */
@InternalStatikApi
public val ASTNode.parent: ASTNode? get() = treeParent

/** */
@InternalStatikApi
public fun ASTNode.isFirstChild(): Boolean = prevSibling() == null

/** */
@InternalStatikApi
public fun ASTNode.prevSibling(): ASTNode? = prevSibling { true }

/** */
@InternalStatikApi
public fun ASTNode.prevSiblings(): Sequence<ASTNode> = generateSequence(prevSibling()) {
  it.prevSibling()
}

/** */
@InternalStatikApi
public fun ASTNode.prevLeaves(includeEmpty: Boolean = true): Sequence<ASTNode> =
  generateSequence(prevLeaf(includeEmpty = includeEmpty)) {
    it.prevLeaf(includeEmpty = includeEmpty)
  }

/** */
@InternalStatikApi
public fun ASTNode.nextSibling(): ASTNode? = nextSibling { true }

/** */
@InternalStatikApi
public fun ASTNode.nextSiblings(): Sequence<ASTNode> = generateSequence(nextSibling()) {
  it.nextSibling()
}

/** */
@InternalStatikApi
public fun ASTNode.nextLeaves(includeEmpty: Boolean = true): Sequence<ASTNode> =
  generateSequence(nextLeaf(includeEmpty = includeEmpty)) {
    it.nextLeaf(includeEmpty = includeEmpty)
  }

/** */
@InternalStatikApi
public fun ASTNode.childrenDepthFirst(): Sequence<ASTNode> {
  return depthFirstTraversal(this) { children().toList() }
}

/** @return a depth-first [Sequence] of this [ASTNode]'s descendants. */
@InternalStatikApi
public fun ASTNode.parentsWithSelf(): Sequence<ASTNode> {
  return generateSequence(this) { it.parent }
}

/**
 * Returns a depth-first [Sequence] of all of this [ASTNode]'s
 * descendants that satisfy the specified [predicate].
 *
 * @param predicate the predicate that each descendant
 *   must satisfy to be included in the [Sequence].
 * @return a depth-first [Sequence] of this [ASTNode]'s descendants that satisfy the [predicate].
 */
@InternalStatikApi
public fun ASTNode.childrenDepthFirst(predicate: (ASTNode) -> Boolean): Sequence<ASTNode> =
  depthFirstTraversal(this) {
    children()
      .filter(predicate)
      .toList()
  }

/** @return a breadth-first [Sequence] of this [ASTNode]'s descendants. */
@InternalStatikApi
public fun ASTNode.childrenBreadthFirst(): Sequence<ASTNode> {
  return breadthFirstTraversal(this) { children().toList() }
}

/**
 * Returns a breadth-first [Sequence] of all of this [ASTNode]'s
 * descendants that satisfy the specified [predicate].
 *
 * @param predicate the predicate that each descendant
 *   must satisfy to be included in the [Sequence].
 * @return a breadth-first [Sequence] of this [ASTNode]'s descendants that satisfy the [predicate].
 */
@InternalStatikApi
public fun ASTNode.childrenBreadthFirst(predicate: (ASTNode) -> Boolean): Sequence<ASTNode> =
  breadthFirstTraversal(this) {
    children()
      .filter(predicate)
      .toList()
  }

/** */
@InternalStatikApi
public fun ASTNode.fileIndent(additionalOffset: Int): String {
  return psi.fileIndent(additionalOffset = additionalOffset)
}

/** @return all ancestors of the receiver node, starting with the immediate parent */
@InternalStatikApi
public fun ASTNode.parents(): Sequence<ASTNode> = generateSequence(
  treeParent
) { node -> node.treeParent }

/** */
@InternalStatikApi
public inline fun <T : ASTNode> T.removeFirstChildrenWhile(shouldRemove: (ASTNode) -> Boolean): T {
  return apply {
    children()
      .toList()
      .takeWhile(shouldRemove)
      .forEach { removeChild(it) }
  }
}

/** */
@InternalStatikApi
public inline fun <T : ASTNode> T.removeLastChildrenWhile(shouldRemove: (ASTNode) -> Boolean): T {
  return apply {
    children()
      .toList()
      .takeLastWhile(shouldRemove)
      .forEach { removeChild(it) }
  }
}

/** */
@InternalStatikApi
public inline fun <T : ASTNode> T.removeAllChildren(
  shouldRemove: (ASTNode) -> Boolean = { true }
): T {
  return apply {
    children()
      .toList()
      .filter(shouldRemove)
      .forEach { removeChild(it) }
  }
}

/** */
@InternalStatikApi
public fun <T : ASTNode> T.removeAllChildrenRecursive(shouldRemove: (ASTNode) -> Boolean): T {
  return apply {
    removeAllChildren(shouldRemove)
    children()
      .toList()
      .forEach { it.removeAllChildrenRecursive(shouldRemove) }
  }
}
