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

package com.rickbusarow.statik.element.kotlin.psi.resolve

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.element.kotlin.psi.resolve.ElementType.KDOC_TEXT
import com.rickbusarow.statik.utils.stdlib.removeRegex
import com.rickbusarow.statik.utils.stdlib.requireNotNull
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.kdoc.parser.KDocKnownTag
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtPsiFactory

/** */
internal val KDocKnownTag.Companion.AT_PARAM get() = "@param"

/** */
internal val KDocKnownTag.Companion.AT_PROPERTY get() = "@property"

/** */
internal fun ASTNode?.isKDocText(): Boolean = this != null && elementType == ElementType.KDOC_TEXT

/** */
internal fun ASTNode?.isKDocTag(): Boolean = this != null && elementType == ElementType.KDOC_TAG

/** */
internal fun ASTNode?.isKDocSectionWithTagChildren(): Boolean =
  this != null && elementType == ElementType.KDOC_SECTION && children().any { it.isKDocTag() }

/** */
internal fun ASTNode?.isKDocTagWithTagChildren(): Boolean =
  this != null && elementType == ElementType.KDOC_TAG && children().any { it.isKDocTag() }

/** */
internal fun ASTNode?.isKDocSection(): Boolean =
  this != null && elementType == ElementType.KDOC_SECTION

/** */
internal fun ASTNode?.isKDocTagOrSection(): Boolean = isKDocSection() || isKDocTag()

/** @return true if this is a KDoc tag "type" name, like `@param`, `@property`, `@throws`, etc. */
internal fun ASTNode?.isKDocTagName(): Boolean {
  return this != null && elementType == ElementType.KDOC_TAG_NAME
}

/**
 * @return true if this is a KDoc tag identifier/link,
 *   like `myParameter` from `@param myParameter [...]`
 */
internal fun ASTNode?.isKDocTagMarkdownLink(): Boolean {
  return isKDocMarkdownLink() && this?.parent.isKDocTag()
}

/**
 * @return true if this is a KDoc tag identifier/link name,
 *   like `myParameter` from `@param myParameter [...]`
 */
internal fun ASTNode?.isKDocTagLinkName(): Boolean {
  return this != null && elementType == ElementType.KDOC_NAME && parent.isKDocTagMarkdownLink()
}

/**
 * @return true if this is a KDoc tag identifier/link name
 *   identifier, like `myParameter` from `@param myParameter [...]`
 */
internal fun ASTNode?.isKDocTagLinkNameIdentifier(): Boolean {
  return this != null && elementType == ElementType.IDENTIFIER && parent.isKDocTagLinkName()
}

/** */
internal fun ASTNode?.isKDocMarkdownLink(): Boolean =
  this != null && elementType == ElementType.KDOC_MARKDOWN_LINK

/** */
internal fun ASTNode?.isKDocDefaultSection(): Boolean = this?.psi?.isKDocDefaultSection() == true

/** */
internal fun ASTNode?.isKDocFirstSectionAfterDefault(): Boolean {
  return when {
    this == null -> false
    isKDocTag() -> parent?.prevSibling().isKDocDefaultSection()
    isKDocSection() -> prevSibling().isKDocDefaultSection()
    else -> false
  }
}

/** */
internal fun ASTNode?.isKDocLastSection(): Boolean {

  return when {
    this == null -> false
    isKDocTag() -> parent?.nextSibling().isKDocDefaultSection()
    isKDocSection() -> nextSibling().isKDocDefaultSection()
    else -> false
  }
}

/** */
internal fun ASTNode?.getKDocSection(): ASTNode? {
  return this?.parents()?.firstOrNull { it.isKDocSection() }
}

/** */
internal fun ASTNode?.isInKDocTag(): Boolean {
  return this != null && parents().any { it.isKDocTag() }
}

/** */
internal fun ASTNode?.isInKDocDefaultSection(): Boolean {
  if (this == null) return false

  val kdoc = psi.getNonStrictParentOfType<KDoc>() ?: return false

  val defaultSection = kdoc.getDefaultSection().node

  return this == defaultSection || parents().any { it == defaultSection }
}

/** */
internal fun ASTNode?.isKDocWhitespaceAfterKDocStart(): Boolean {

  if (!isWhiteSpaceOrBlank()) return false
  return prevLeaf(true).isKDocStart()
}

/** */
internal fun ASTNode?.isKDocWhitespaceAfterLeadingAsterisk(): Boolean {

  if (!isWhiteSpaceOrBlank()) return false
  return prevLeaf(true).isKDocLeadingAsterisk()
}

/** */
internal fun ASTNode?.isKDocWhitespaceBeforeLeadingAsterisk(): Boolean =
  this != null && elementType == ElementType.WHITE_SPACE && nextSibling().isKDocLeadingAsterisk()

/** */
internal fun ASTNode?.isKDocLeadingAsterisk(): Boolean =
  this != null && elementType == ElementType.KDOC_LEADING_ASTERISK

/** */
internal fun ASTNode?.isKDocEnd(): Boolean = this != null && elementType == ElementType.KDOC_END

/** */
internal fun ASTNode?.isKDocStart(): Boolean = this != null && elementType == ElementType.KDOC_START

/** */
internal fun ASTNode?.isKDoc(): Boolean = this != null && elementType == ElementType.KDOC

/** */
internal fun ASTNode?.isFirstAfterKDocStart(): Boolean =
  this != null && prevSibling()?.isKDocStart() == true

/** */
internal fun ASTNode?.isKDocCodeBlockText(): Boolean =
  this != null && elementType == ElementType.KDOC_CODE_BLOCK_TEXT

/** */
internal fun ASTNode?.prevKDocLeadingAsterisk(): ASTNode? =
  this?.prevLeaf { it.isKDocLeadingAsterisk() }

/**
 * @return true if this node is the opening backticks of a code block, with or without a language.
 */
internal fun ASTNode.isKDocCodeBlockStartText(): Boolean {
  if (elementType != ElementType.KDOC_TEXT) return false

  return nextSibling { !it.isWhiteSpace() && !it.isKDocLeadingAsterisk() }
    .isKDocCodeBlockText()
}

/** @return true if this node is closing backticks after a code block. */
internal fun ASTNode.isKDocCodeBlockEndText(): Boolean {
  if (elementType != ElementType.KDOC_TEXT) return false

  return prevSibling { !it.isWhiteSpace() && !it.isKDocLeadingAsterisk() }
    .isKDocCodeBlockText()
}

/** */
internal fun ASTNode.getKDocSections(): Sequence<ASTNode> {
  check(isKDoc()) { "Only call `getKDocSections()` from the KDoc root element." }
  return childrenDepthFirst { !it.parent.isKDocSection() }
    .filter { it.isKDocSection() }
}

/** */
internal fun ASTNode.getTagTextWithoutLeadingAsterisks(): String {
  check(isKDocTag() || isKDocSection()) {
    "Only call `getTagTextWithoutLeadingAsterisks()` from a KDOC_TAG or KDOC_SECTION."
  }
  return (psi as KDocTag).tagTextWithoutLeadingAsterisks()
}

internal fun ASTNode.getKDocTextWithoutLeadingAsterisks(): String {
  return childrenDepthFirst()
    .filter { it.isLeaf() }
    .toList()
    .dropLastWhile { it.isKDocWhitespaceAfterLeadingAsterisk() }
    .joinToString("") { it.text }
    .replaceIndentByMargin(newIndent = "", marginPrefix = "*")
}

/**
 * The spaces to indent lines after the first line. This is
 * the indentation of the KDOC_START plus one more space.
 */
@InternalStatikApi
public val KDoc.leadingAteriskIndent: String get() = fileIndent(additionalOffset = 1)

/**
 * The spaces to indent lines after the first line. This is
 * the indentation of the KDOC_START plus one more space.
 */
@InternalStatikApi
public val KDoc.starIndent: String get() = fileIndent(additionalOffset = 1)

/** */
@InternalStatikApi
public fun KDoc.makeMultiline(): KDoc = apply {

  require(text.count { it == '\n' } == 0) { "KDoc is already multi-line:\n---\n$text\n---" }

  val starIndent = fileIndent(additionalOffset = 1)

  val kdocStart = node.firstChildNode
  val afterStart = kdocStart.nextSibling()

  node.addChild(PsiWhiteSpaceImpl("\n$starIndent"), afterStart)

  node.addChild(LeafPsiElement(ElementType.KDOC_LEADING_ASTERISK, "*"), afterStart)

  val ds = getDefaultSection()

  ds.node
    .childrenDepthFirst()
    .singleOrNull { it.isKDocText() }
    ?.let { old ->
      old.parent
        .requireNotNull()
        .replaceChild(old, LeafPsiElement(KDOC_TEXT, old.text.trimEnd()))
    }

  // The newline after the KDoc content is always the last child of the last tag
  getAllTags().last().node.addChild(PsiWhiteSpaceImpl("\n$starIndent"), null)
}

/** */
@InternalStatikApi
public fun KDocTag.replaceContentWithNewPsiFromText(newText: String): KDocTag = apply {
  val tagNode = this@replaceContentWithNewPsiFromText.node

  val toDelete = tagNode.children()
    .takeWhile { !it.isKDocTag() }
    .toList()
    .dropLastWhile { it.isKDocLeadingAsterisk() || it.isBlank() }

  val anchor = toDelete.firstOrNull()

  val newTag = ktPsiFactory().createKDocTagFromText(
    newText = newText,
    removeLeadingAsterisk = !toDelete.firstOrNull().isKDocLeadingAsterisk()
  )

  newTag.node
    .children()
    .forEach { newTagChild ->
      tagNode.addChild(newTagChild.clone() as ASTNode, anchor)
    }

  toDelete.forEach { tagNode.removeChild(it) }
}

/** */
@InternalStatikApi
public fun KtPsiFactory.createKDocTagFromText(
  newText: String,
  removeLeadingAsterisk: Boolean
): KDocTag {
  return createFileFromText(newText.removeRegex("""\*/\S*$""").plus("\n*\n*/"))
    .childrenBreadthFirst()
    .filterIsInstance<KDocTag>()
    .toList()
    .last()
    .also { tag ->
      tag.node.removeLastChildrenWhile { it.isKDocLeadingAsterisk() || it.isBlank() }

      if (removeLeadingAsterisk) {
        tag.node.removeFirstChildrenWhile { it.isKDocLeadingAsterisk() }
      }
    }
}

/** */
@InternalStatikApi
public fun KtPsiFactory.createKDoc(
  sections: List<String>,
  startIndent: String
): KDoc {

  val newText = buildString {

    val makeCollapsed = sections.singleOrNull()?.count { it == '\n' } == 0

    if (makeCollapsed) {
      append("$startIndent/**")
    } else {
      appendLine("$startIndent/**")
    }

    if (makeCollapsed) {
      append("${sections.first()} */")
    } else {

      val leadingAsteriskIndent = "$startIndent "

      sections.forEach { section ->
        section.lineSequence().forEach { line ->
          appendLine("$leadingAsteriskIndent*$line")
        }
      }

      appendLine("$leadingAsteriskIndent*/")
    }
  }

  return createFileFromText(newText).requireChildOfType<KDoc>()
}
