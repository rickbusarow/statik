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
import com.rickbusarow.statik.name.SimpleName
import com.rickbusarow.statik.name.SimpleName.Companion.asSimpleName
import com.rickbusarow.statik.utils.stdlib.letIf
import com.rickbusarow.statik.utils.stdlib.prefix
import com.rickbusarow.statik.utils.stdlib.requireNotNull
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import kotlin.LazyThreadSafetyMode.NONE

@InternalStatikApi
public fun KtValueArgumentList.getByNameOrIndex(index: Int, name: String): KtValueArgument? {
  return arguments
    .firstOrNull { it.getArgumentName()?.text == name }
    ?: arguments
      .getOrNull(index)
}

@InternalStatikApi
public fun KtElement.requireSimpleName(): SimpleName = name.requireNotNull().asSimpleName()

/** @return a [KtPsiFactory] instance for the project of this [PsiElement]. */
public fun PsiElement.ktPsiFactory(): KtPsiFactory {
  return KtPsiFactory(project, markGenerated = true)
}

/**
 * @param text the text of the new [KtFile].
 * @return a new [KtFile] instance created from the given text.
 */
public fun PsiElement.createFileFromText(text: String): KtFile =
  ktPsiFactory().createFileFromText(text)

/**
 * @param text the text of the new [KtFile].
 * @return a new [KtFile] instance created from the given text.
 */
public fun KtPsiFactory.createFileFromText(text: String): KtFile = createFile("tmp.kt", text)

/** @return a list of all [KDocTag]s in this [KDoc]. */
public fun KDoc.getAllTags(): List<KDocTag> {
  return childrenDepthFirst()
    .filterIsInstance<KDocTag>()
    .filterNot { it.node.isKDocSection() && it.node.children().firstOrNull().isKDocTag() }
    .toList()
}

/**
 * @return `true` if this [PsiElement] is a [KDocTag] and its
 *   [node][PsiElement.getNode] is a KDoc tag node, `false` otherwise.
 */
public fun PsiElement?.isKDocTag(): Boolean {
  return this != null && node.isKDocTag()
}

/** @return `true` if this [PsiElement] is a KDoc section with tag children, `false` otherwise. */
public fun PsiElement?.isKDocSectionWithTagChildren(): Boolean {
  return this != null && node.isKDocSectionWithTagChildren()
}

/** @return `true` if this [PsiElement] is a KDoc tag with tag children, `false` otherwise. */
public fun PsiElement?.isKDocTagWithTagChildren(): Boolean {
  return this != null && node.isKDocTagWithTagChildren()
}

/**
 * @return `true` if this [PsiElement] is in the default
 *   section of a KDoc comment, `false` otherwise.
 */
public fun PsiElement?.isKDocDefaultSection(): Boolean {
  if (this !is KDocTag) return false

  val kdoc = getNonStrictParentOfType<KDoc>() ?: return false

  return this == kdoc.getDefaultSection()
}

/**
 * @return `true` if this [PsiElement] is the first section after
 *   the default section in a KDoc comment, `false` otherwise.
 */
public fun PsiElement?.isKDocFirstSectionAfterDefault(): Boolean {
  return this?.node.isKDocFirstSectionAfterDefault()
}

/**
 * @return `true` if this [PsiElement] is in the default
 *   section of a KDoc comment, `false` otherwise.
 */
public fun PsiElement?.isInKDocDefaultSection(): Boolean {
  if (this == null) return false

  val tag = this as? KDocTag
    ?: getNonStrictParentOfType<KDocTag>()
    ?: return false

  return tag.parent is KDoc
}

/**
 * Returns the text content of this [KDocTag] without any leading asterisks, optionally
 * trimming the trailing whitespace if this tag is in a KDoc section that has tags after it.
 *
 * @return the text content of this [KDocTag] without leading asterisks.
 */
public fun <T : KDocTag> T.tagTextWithoutLeadingAsterisks(): String {
  val shouldTrim = node.nextSibling().isKDocSection()

  val previousWhiteSpace by lazy(NONE) {

    node.prevLeaf(true)
      ?.takeIf { it.isKDocWhitespaceAfterLeadingAsterisk() }
      ?.text
      .orEmpty()
  }

  return node.childrenDepthFirst()
    .filter { it.psi != this@tagTextWithoutLeadingAsterisks }
    .filter { it.isKDocTag() || it.isLeaf() }
    .takeWhile { !it.isKDocTag() }
    .toList()
    .dropLastWhile { shouldTrim && it.isKDocWhitespaceAfterLeadingAsterisk() }
    .joinToString("") { it.text }
    .replaceIndentByMargin("", "*")
    .letIf(shouldTrim) { it.removeSuffix("\n") }
    .prefix(previousWhiteSpace)
}

/**
 * @return the [KDocSection] of this [KDocTag]. If the
 *   receiver tag is a [KDocSection], it will return itself.
 * @throws IllegalArgumentException if this [KDocTag] doesn't have a [KDocSection] parent.
 */
public fun KDocTag.getKDocSection(): KDocSection {
  return this as? KDocSection ?: getStrictParentOfType<KDocSection>()
    .requireNotNull {
      "The receiver KDocTag element ${this@getKDocSection} does not have a KDocSection parent."
    }
}

/** @return `true` if this [KDocTag] is blank, `false` otherwise. */
public fun KDocTag.isBlank(): Boolean {
  return node.children()
    .filter { !it.isKDocLeadingAsterisk() }
    .singleOrNull()
    .isBlank()
}

/** @return the start offset of this [PsiElement]. */
public val PsiElement.startOffset: Int get() = textRange.startOffset

/**
 * Returns the indentation string of this [PsiElement]'s containing file,
 * up to this element's start offset plus the specified additional offset.
 *
 * @param additionalOffset the additional offset to add to this
 *   element's start offset when computing the indentation.
 * @return the indentation string of this element's containing file.
 */
public fun PsiElement.fileIndent(additionalOffset: Int): String {

  val lineText = node.prevLeaves()
    .firstOrNull { it.isWhiteSpaceWithNewline() }
    ?.text
    ?.substringAfterLast('\n')
    ?: return " ".repeat(additionalOffset)

  val leadingSpaces = lineText.length

  return " ".repeat(leadingSpaces + additionalOffset)
}

/** @return the [KtSimpleNameExpression] of the call name, or `null`. */
public fun KtCallElement.getCallNameExpression(): KtSimpleNameExpression? {
  val calleeExpression = calleeExpression ?: return null

  return when (calleeExpression) {
    is KtSimpleNameExpression -> calleeExpression
    is KtConstructorCalleeExpression -> calleeExpression.constructorReferenceExpression
    else -> null
  }
}

/** @return the strict parent of type [T], or `null`. */
public inline fun <reified T : PsiElement> PsiElement.getStrictParentOfType(): T? {
  return PsiTreeUtil.getParentOfType(this, T::class.java, true)
}

/** @return the non-strict parent of type [T], or `null`. */
public inline fun <reified T : PsiElement> PsiElement.getNonStrictParentOfType(): T? {
  return PsiTreeUtil.getParentOfType(this, T::class.java, false)
}

/** @return the list of [KtParameter]s representing the value parameters. */
public fun KtNamedDeclaration.getValueParameters(): List<KtParameter> {
  return getValueParameterList()?.parameters.orEmpty()
}

/** @return the [KtParameterList] of the value parameters, or `null`. */
public fun KtNamedDeclaration.getValueParameterList(): KtParameterList? {
  return when (this) {
    is KtCallableDeclaration -> valueParameterList
    is KtClass -> getPrimaryConstructorParameterList()
    else -> null
  }
}

/**
 * @return the first child of type [T]
 * @throws NoSuchElementException if no child of type [T] is found.
 */
public inline fun <reified T : PsiElement> PsiElement.requireChildOfType(): T {
  return PsiTreeUtil.getChildOfType(this, T::class.java)
    ?: throw NoSuchElementException("No child of type ${T::class.java.name} found.")
}

/** @return the first child of type [T], or `null`. */
public inline fun <reified T : PsiElement> PsiElement.getChildOfTypeOrNull(): T? {
  return PsiTreeUtil.getChildOfType(this, T::class.java)
}

/** */
public inline fun <T : PsiElement> T.removeAllChildren(
  shouldRemove: (PsiElement) -> Boolean = { true }
): T {
  return apply {
    children
      .filter(shouldRemove)
      .forEach { it.delete() }
  }
}

/** */
public fun PsiElement.replaceChild(newChild: PsiElement, oldChild: PsiElement) {
  node.addChild(newChild.node, oldChild.node)
  node.removeChild(oldChild.node)
}
