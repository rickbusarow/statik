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
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.stdlib.joinToStringDot
import kotlinx.coroutines.flow.firstOrNull
import java.io.Serializable

/**
 * Fundamentally, this is a version of `Name` (such as Kotlin's
 * [Name][org.jetbrains.kotlin.name.Name]) with syntactic sugar for complex matching requirements.
 *
 * @see QualifiedDeclaredName
 * @see ReferenceName
 */
public interface StatikName : HasNameSegments, Comparable<StatikName>, Serializable {

  /** The raw String value of this name, such as `com.example.lib1.Lib1Class`. */
  public val asString: String

  /** The simplest name. For an inner class like `com.example.Outer.Inner`, this will be 'Inner'. */
  public val simpleName: SimpleName
    get() = segments.last().asSimpleName()

  /** @return true if this [asString] value starts with the name string of [other], otherwise false */
  public fun startsWith(other: StatikName): Boolean {
    return asString.startsWith(other.asString)
  }

  /** @return true if this [asString] value ends with the [str] parameter, otherwise false */
  public fun endsWith(str: String): Boolean {
    return asString.endsWith(str)
  }

  /** @return true if the last segment of this name matches [str], otherwise false */
  public fun endsWithSimpleName(str: String): Boolean {
    return asString.split('.').last() == str
  }

  /** @return true if the last segment of this name matches [simpleName], otherwise false */
  public fun endsWithSimpleName(simpleName: SimpleName): Boolean {
    return asString.split('.').last() == simpleName.asString
  }

  /** @return true if the last segment of this name matches [other], otherwise false */
  public fun endsWith(other: StatikName): Boolean {
    return asString.endsWith(other.asString)
  }

  /** @return `true` if this string is empty or consists solely of whitespace characters. */
  public fun isBlank(): Boolean = asString.isBlank()

  /** @return true if this char sequence is empty (contains no characters). */
  public fun isEmpty(): Boolean = asString.isEmpty()

  public fun append(vararg simpleNames: String): String {
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
  public fun append(simpleNames: Iterable<String>): String {
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
  public fun append(simpleName: SimpleName): String {
    return if (isBlank()) {
      simpleName.asString
    } else {
      "$asString.${simpleName.asString}"
    }
  }

  override fun compareTo(other: StatikName): Int {
    // sort by name first, then by type.
    return compareValuesBy(
      this,
      other,
      { it.asString },
      { it::class.java.simpleName }
    )
  }
}

/**
 * The language which contains a given [ReferenceName],
 * or the language which can access a given [DeclaredName]
 */
public sealed interface StatikLanguage {
  /** Java */
  public object JAVA : StatikLanguage {
    override fun toString(): String = this::class.java.simpleName
  }

  /** Kotlin */
  public object KOTLIN : StatikLanguage {
    override fun toString(): String = this::class.java.simpleName
  }

  /** Xml, which is treated the same as [JAVA] */
  public object XML : StatikLanguage {
    override fun toString(): String = this::class.java.simpleName
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
public fun StatikName.append(
  simpleNames: Iterable<SimpleName>
): String {
  return if (isBlank()) {
    simpleNames.asString()
  } else {
    "$asString.${simpleNames.asString()}"
  }
}

/**
 * A [StatikName] which has the potential to be resolved,
 * meaning any [ReferenceName], or a [QualifiedDeclaredName]
 */
public sealed interface ResolvableStatikName : StatikName

public suspend inline fun <reified T : StatikName> LazySet<StatikName>.getNameOrNull(
  element: StatikName
): T? {
  return firstOrNull { it.asString == element.asString && it is T } as? T?
}
