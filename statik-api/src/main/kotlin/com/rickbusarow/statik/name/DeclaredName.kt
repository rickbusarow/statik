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

package com.rickbusarow.statik.name

import com.rickbusarow.statik.name.HasSimpleNames.Companion.checkSimpleNames
import com.rickbusarow.statik.name.ReferenceName.Companion.asReferenceName
import com.rickbusarow.statik.name.SimpleName.Companion.stripPackageNameFromFqName
import com.rickbusarow.statik.name.StatikLanguage.JAVA
import com.rickbusarow.statik.name.StatikLanguage.KOTLIN
import com.rickbusarow.statik.name.StatikLanguage.XML
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import com.rickbusarow.statik.utils.stdlib.singletonList
import org.jetbrains.kotlin.name.FqName

/** Represents a "declaration" -- a named object which can be referenced elsewhere. */
public sealed interface DeclaredName : StatikName, HasSimpleNames {

  /**
   * The languages with which this declaration is compatible. For instance, a member
   * property will typically have a [KOTLIN] declaration using property access syntax,
   * but will also have a [JAVA]/[XML] declaration for setter and getter functions.
   */
  public val languages: Set<StatikLanguage> get() = setOf(KOTLIN, JAVA, XML)

  public companion object {

    /**
     * Shorthand for creating a [QualifiedDeclaredName] which is only accessible from Kotlin files.
     *
     * @see StatikLanguage.KOTLIN
     */
    public fun kotlin(
      packageName: PackageName,
      simpleNames: Iterable<SimpleName>
    ): QualifiedDeclaredName =
      QualifiedDeclaredNameImpl(
        packageName = packageName,
        simpleNames = simpleNames.toList(),
        languages = setOf(KOTLIN)
      )

    /**
     * Shorthand for creating a [QualifiedDeclaredName]
     * which is only accessible from Java or XML files.
     *
     * @see StatikLanguage.JAVA
     * @see StatikLanguage.XML
     */
    public fun java(
      packageName: PackageName,
      simpleNames: Iterable<SimpleName>
    ): QualifiedDeclaredName =
      QualifiedDeclaredNameImpl(
        packageName = packageName,
        simpleNames = simpleNames.toList(),
        languages = setOf(JAVA, XML)
      )

    /**
     * Shorthand for creating a [QualifiedDeclaredName]
     * which is accessible from files in any language.
     *
     * @see StatikLanguage.JAVA
     * @see StatikLanguage.KOTLIN
     * @see StatikLanguage.XML
     */
    public fun agnostic(
      packageName: PackageName,
      simpleNames: Iterable<SimpleName>
    ): QualifiedDeclaredName = QualifiedDeclaredNameImpl(
      packageName = packageName,
      simpleNames = simpleNames.toList(),
      languages = setOf(KOTLIN, JAVA, XML)
    )
  }
}

/** Represents a "declaration" -- a named object which can be referenced elsewhere. */
public sealed class QualifiedDeclaredName :
  DeclaredName,
  StatikName,
  HasPackageName,
  HasSimpleNames,
  ResolvableStatikName {

  override val asString: String by unsafeLazy {
    packageName.appendAsString(simpleNames)
  }

  override val segments: List<String> by unsafeLazy { asString.split('.') }

  /**
   * `true` if a declaration is top-level in a file, otherwise `false`
   * such as if the declaration is a nested type or a member declaration
   */
  public val isTopLevel: Boolean by unsafeLazy { simpleNames.size == 1 }

  public open fun asReferenceName(language: StatikLanguage): ReferenceName {
    return asString.asReferenceName(language)
  }

  final override fun equals(other: Any?): Boolean {
    if (this === other) return true

    when (other) {
      is ReferenceName -> {

        if (asString != other.asString) return false
        if (!languages.contains(other.language)) return false
      }

      is QualifiedDeclaredName -> {

        if (asString != other.asString) return false
        if (languages != other.languages) return false
      }

      else -> return false
    }
    return true
  }

  final override fun hashCode(): Int = asString.hashCode()

  final override fun toString(): String =
    "(${this::class.java.simpleName}) `$asString`  language=$languages"
}

internal class QualifiedDeclaredNameImpl(
  override val packageName: PackageName,
  override val simpleNames: List<SimpleName>,
  override val languages: Set<StatikLanguage>
) : QualifiedDeclaredName() {
  init {
    checkSimpleNames()
  }
}

/**
 * @return a [QualifiedDeclaredName], where the String after [packageName]
 *   is split and treated as the collection of [SimpleNames][SimpleName].
 */
public fun FqName.asDeclaredName(
  packageName: PackageName,
  vararg languages: StatikLanguage
): QualifiedDeclaredName {
  return asString().stripPackageNameFromFqName(packageName).asDeclaredName(packageName, *languages)
}

/**
 * @return a [QualifiedDeclaredName] from the [packageName]
 *   argument, appending the receiver [SimpleNames][SimpleName]
 */
public fun Iterable<SimpleName>.asDeclaredName(
  packageName: PackageName,
  vararg languages: StatikLanguage
): QualifiedDeclaredName {
  return when {
    languages.isEmpty() -> DeclaredName.agnostic(packageName, this)
    !languages.contains(JAVA) -> DeclaredName.kotlin(packageName, this)
    !languages.contains(KOTLIN) -> DeclaredName.java(packageName, this)
    else -> DeclaredName.agnostic(packageName, this)
  }
}

/**
 * @return a [QualifiedDeclaredName] from the [packageName]
 *   argument, appending the receiver [SimpleNames][SimpleName]
 */
public fun SimpleName.asDeclaredName(
  packageName: PackageName,
  vararg languages: StatikLanguage
): QualifiedDeclaredName {
  return singletonList().asDeclaredName(packageName, *languages)
}
