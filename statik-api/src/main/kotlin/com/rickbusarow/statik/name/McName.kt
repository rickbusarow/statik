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

import com.rickbusarow.statik.name.SimpleName.Companion.asSimpleName
import com.rickbusarow.statik.name.SimpleName.Companion.asString
import com.rickbusarow.statik.stdlib.joinToStringDot
import com.rickbusarow.statik.utils.lazy.LazySet
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import java.io.Serializable

/**
 * Fundamentally, this is a version of `Name` (such as Kotlin's
 * [Name][org.jetbrains.kotlin.name.Name]) with syntactic sugar for complex matching requirements.
 *
 * @see QualifiedDeclaredName
 * @see ReferenceName
 */
interface McName : HasNameSegments, Comparable<McName>, Serializable {

  /** The raw String value of this name, such as `com.example.lib1.Lib1Class`. */
  val asString: String

  /** The simplest name. For an inner class like `com.example.Outer.Inner`, this will be 'Inner'. */
  val simpleName: SimpleName
    get() = segments.last().asSimpleName()

  /** @return true if this [asString] value starts with the name string of [other], otherwise false */
  fun startsWith(other: McName): Boolean {
    return asString.startsWith(other.asString)
  }

  /** @return true if this [asString] value ends with the [str] parameter, otherwise false */
  fun endsWith(str: String): Boolean {
    return asString.endsWith(str)
  }

  /** @return true if the last segment of this name matches [str], otherwise false */
  fun endsWithSimpleName(str: String): Boolean {
    return asString.split('.').last() == str
  }

  /** @return true if the last segment of this name matches [simpleName], otherwise false */
  fun endsWithSimpleName(simpleName: SimpleName): Boolean {
    return asString.split('.').last() == simpleName.asString
  }

  /** @return true if the last segment of this name matches [other], otherwise false */
  fun endsWith(other: McName): Boolean {
    return asString.endsWith(other.asString)
  }

  /** @return `true` if this string is empty or consists solely of whitespace characters. */
  fun isBlank(): Boolean = asString.isBlank()

  /** @return true if this char sequence is empty (contains no characters). */
  fun isEmpty(): Boolean = asString.isEmpty()

  fun append(vararg simpleNames: String): String {
    return if (isBlank()) {
      simpleNames.joinToStringDot()
    } else {
      "$asString.${simpleNames.joinToStringDot()}"
    }
  }

  /**
   * Safe function for appending a simple name to the end of a name.
   *
   * If the receiver name is empty or blank, this function
   * will return the simple name without a preceding period.
   *
   * If the receiver name is not empty or blank, this function will
   * append a period to the receiver name, then add the simple name.
   */
  fun append(simpleNames: Iterable<String>): String {
    return if (isBlank()) {
      simpleNames.joinToStringDot { it.trim() }
    } else {
      "$asString.${simpleNames.joinToString(".") { it.trim() }}"
    }
  }

  /**
   * Safe function for appending a simple name to the end of a name.
   *
   * If the receiver name is empty or blank, this function
   * will return the simple name without a preceding period.
   *
   * If the receiver name is not empty or blank, this function will
   * append a period to the receiver name, then add the simple name.
   */
  fun append(simpleName: SimpleName): String {
    return if (isBlank()) {
      simpleName.asString
    } else {
      "$asString.${simpleName.asString}"
    }
  }

  override fun compareTo(other: McName): Int {
    // sort by name first, then by type.
    return compareValuesBy(
      this,
      other,
      { it.asString },
      { it::class.java.simpleName }
    )
  }

  /**
   * The language which contains a given [ReferenceName],
   * or the language which can access a given [DeclaredName]
   */
  sealed interface CompatibleLanguage {
    /** Java */
    object JAVA : CompatibleLanguage {
      override fun toString(): String = this::class.java.simpleName
    }

    /** Kotlin */
    object KOTLIN : CompatibleLanguage {
      override fun toString(): String = this::class.java.simpleName
    }

    /** Xml, which is treated the same as [JAVA] */
    object XML : CompatibleLanguage {
      override fun toString(): String = this::class.java.simpleName
    }
  }
}

/**
 * Safe function for appending a simple name to the end of a name.
 *
 * If the receiver name is empty or blank, this function
 * will return the simple name without a preceding period.
 *
 * If the receiver name is not empty or blank, this function will
 * append a period to the receiver name, then add the simple name.
 */
@JvmName("appendSimpleNames")
fun McName.append(simpleNames: Iterable<SimpleName>): String {
  return if (isBlank()) {
    simpleNames.asString()
  } else {
    "$asString.${simpleNames.asString()}"
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
