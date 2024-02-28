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

package com.rickbusarow.kase

import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import modulecheck.parsing.source.PackageName.DEFAULT
import modulecheck.utils.lazy.LazySet
import modulecheck.utils.lazy.unsafeLazy

/**
 * Represents a package name.
 *
 * Note that a java/kotlin file without a package declaration will have a `null` _declaration_, but
 * it still has a "default" package. Files with a default package should use [PackageName.DEFAULT].
 *
 * @see McName
 * @see DEFAULT
 */
sealed interface PackageName : McName {
  /** the full name of this package */
  override val name: String

  /**
   * Represents a [PackageName] when there isn't actually a package name, meaning that
   * top-level declarations in that file are at the root of source without qualifiers.
   *
   * @see McName
   * @see DEFAULT
   */
  object DEFAULT : PackageName {
    override val name: String = ""
    override val segments: List<String>
      get() = emptyList()

    override fun append(simpleNames: Iterable<String>): String = simpleNames.joinToString(".")
  }

  /**
   * Safe function for appending a simple name to the "end" of a package name.
   *
   * If the package name is default/empty, this function will
   * return just the simple name without a preceding period.
   *
   * If the package name is not blank, this function will append
   * a period to the package name, then add the simple name.
   */
  fun append(simpleNames: Iterable<String>): String

  companion object {
    /** Shorthand for calling [PackageName.invoke] in-line. */
    fun String?.asPackageName(): PackageName = PackageName(this)

    /**
     * Shorthand for calling [PackageName.invoke] in-line.
     *
     * @return A `PackageName` wrapper around [nameOrNull]. If [nameOrNull]
     *   is null or blank, this will return [PackageName.DEFAULT].
     */
    operator fun invoke(nameOrNull: String?): PackageName {
      return when {
        nameOrNull.isNullOrBlank() -> DEFAULT
        else -> PackageNameImpl(nameOrNull)
      }
    }
  }
}

/**
 * Safe function for appending a simple name to the "end" of a package name.
 *
 * If the package name is default/empty, this function will
 * return just the simple name without a preceding period.
 *
 * If the package name is not blank, this function will append
 * a period to the package name, then add the simple name.
 */
fun PackageName.append(vararg simpleNames: String): String = append(simpleNames.toList())

/**
 * @property name the full name of this package
 * @see McName
 * @throws IllegalArgumentException if the [name] parameter is empty or blank
 */
data class PackageNameImpl internal constructor(override val name: String) : PackageName {
  init {
    require(name.isNotBlank()) {
      "A ${this.javaClass.canonicalName} must be a non-empty, non-blank String.  " +
        "Represent an empty/blank or missing package name as ${DEFAULT::class.qualifiedName}.  " +
        "This name argument, wrapped in single quotes: '$name'"
    }
  }

  override val segments: List<String> by unsafeLazy { name.split('.') }

  override fun append(simpleNames: Iterable<String>): String =
    "$name.${simpleNames.joinToString(".")}"
}

/** Convenience interface for providing a [PackageName]. */
interface HasPackageName {
  val packageName: PackageName
}

/**
 * Fundamentally, this is a version of `FqName` (such as
 * Kotlin's [FqName][org.jetbrains.kotlin.name.FqName])
 * with syntactic sugar for complex matching requirements.
 *
 * @see QualifiedDeclaredName
 * @see ReferenceName
 * @since 0.12.0
 */
sealed interface McName : Comparable<McName> {
  /**
   * The raw String value of this name, such as `com.example.lib1.Lib1Class`.
   *
   * @since 0.12.0
   */
  val name: String

  /**
   * ex: 'com.example.Subject' has the segments ['com', 'example', 'Subject']
   *
   * @since 0.12.0
   */
  val segments: List<String>

  /**
   * The simplest name. For an inner class like `com.example.Outer.Inner`, this will be 'Inner'.
   *
   * @since 0.12.0
   */
  val simpleName: String
    get() = segments.last()

  /**
   * @return true if this [name] value with the name string of [other], otherwise false
   * @since 0.12.0
   */
  fun startsWith(other: McName): Boolean {
    return name.startsWith(other.name)
  }

  /**
   * @return true if this [name] value ends with the [str] parameter, otherwise false
   * @since 0.12.0
   */
  fun endsWith(str: String): Boolean {
    return name.endsWith(str)
  }

  /**
   * @return true if the last segment of this name matches [str], otherwise false
   * @since 0.12.0
   */
  fun endsWithSimpleName(str: String): Boolean {
    return name.split('.').last() == str
  }

  /** @return true if the last segment of this name matches [simpleName], otherwise false */
  fun endsWithSimpleName(simpleName: SimpleName): Boolean {
    return name.split('.').last() == simpleName.name
  }

  /** @return true if the last segment of this name matches [other], otherwise false
   * @since 0.12.0
   */
  fun endsWith(other: McName): Boolean {
    return name.endsWith(other.name)
  }

  override fun compareTo(other: McName): Int {
    // sort by name first, then by type.
    return compareValuesBy(
      this,
      other,
      { it.name },
      { it::class.java.simpleName }
    )
  }

  /**
   * The language which contains a given [ReferenceName],
   * or the language which can access a given [DeclaredName]
   *
   * @since 0.12.0
   */
  sealed interface CompatibleLanguage {
    /**
     * Java
     *
     * @since 0.12.0
     */
    object JAVA : CompatibleLanguage {
      override fun toString(): String = this::class.java.simpleName
    }

    /**
     * Kotlin
     *
     * @since 0.12.0
     */
    object KOTLIN : CompatibleLanguage {
      override fun toString(): String = this::class.java.simpleName
    }

    /**
     * Xml, which is treated the same as [JAVA]
     *
     * @since 0.12.0
     */
    object XML : CompatibleLanguage {
      override fun toString(): String = this::class.java.simpleName
    }
  }
}

/**
 * An [McName] which has the potential to be resolved --
 * meaning any [ReferenceName], or a [QualifiedDeclaredName]
 */
sealed interface ResolvableMcName : McName

suspend inline fun <reified T : McName> LazySet<McName>.getNameOrNull(element: McName): T? {
  return takeIf { it.contains(element) }
    ?.filterIsInstance<T>()
    ?.firstOrNull { it == element }
}
