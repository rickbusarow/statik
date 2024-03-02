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

import com.rickbusarow.statik.utils.stdlib.joinToStringDot
import com.rickbusarow.statik.utils.stdlib.regex

/**
 * Trait interface for providing a split list of name segments.
 *
 * ex: 'com.example.Subject' has the segments ['com', 'example', 'Subject']
 */
public interface HasNameSegments {

  /** ex: 'com.example.Subject' has the segments ['com', 'example', 'Subject'] */
  public val segments: List<String>
}

/**
 * A name which is not fully qualified, like `Foo` in `com.example.Foo`
 *
 * @property asString the string value of this name
 */
@JvmInline
public value class SimpleName(override val asString: String) : StatikName {

  init {
    require(asString.matches(SIMPLE_NAME_REGEX)) {
      "SimpleName names must be valid Java identifier " +
        "without a dot qualifier.  This name was: `$asString`"
    }
  }

  override val simpleName: SimpleName
    get() = this

  override val segments: List<String>
    get() = listOf(asString)

  public companion object {

    /** matches simple type or member names */
    public val JAVA_IDENTIFIER_REGEX: Regex = regex {

      appendWithoutInjection("(?:")

      // matches any alphabet character or an underscore at the start of a word
      append("""\b[_a-zA-Z]""")

      or()

      // matches the literal '$' character anywhere but the start of a word
      append("""\B\$""")

      appendWithoutInjection(")")

      // matches any alphabet character, digit, underscore, or literal '$' character
      append("""[_a-zA-Z0-9$]*+""")
    }

    /** Basic validation for simple names. They must not include any periods. */
    public val SIMPLE_NAME_REGEX: Regex = regex {

      append("^")

      // any normal identifier not wrapped in backticks
      append(JAVA_IDENTIFIER_REGEX.pattern)

      or()

      // matches names wrapped in backticks, stopping at the first backtick character.
      // This is intentionally lenient,
      // because the set of characters Kotlin allows is dependent upon the platform.
      append("""`[^\n`]+`""")

      append("$")
    }

    /** shorthand for `joinToStringDot { it.name.trim() }` */
    public fun Iterable<SimpleName>.asString(): String = joinToStringDot { it.asString.trim() }

    /** wraps this String in a [SimpleName] */
    public fun String.asSimpleName(): SimpleName = SimpleName(this)

    /**
     * Removes the prefix of [packageName]'s value and a subsequent period,
     * then splits the remainder by dots and returns that list as [SimpleName]
     *
     * example: `com.example.Outer.Inner` becomes `[Outer, Inner]`
     */
    public fun String.stripPackageNameFromFqName(packageName: PackageName): List<SimpleName> {
      return removePrefix("${packageName.asString}.").split('.')
        .map { it.asSimpleName() }
    }
  }
}

/** Trait interface for providing a [SimpleName]. */
public interface HasSimpleNames : HasNameSegments {
  /** The contained [SimpleNames][SimpleName] */
  public val simpleNames: List<SimpleName>

  override val segments: List<String>
    get() = simpleNames.map { it.asString }

  /**
   * If the collection in [simpleNames] has more than one name, this value will be the last.
   *
   * example: Given a full name of `com.example.Outer.Inner`, with
   * the [simpleNames] `[Outer, Inner]`, this value will be `Inner`.
   */
  public val simplestName: SimpleName
    get() = simpleNames.last()

  public companion object {

    internal fun HasSimpleNames.checkSimpleNames() {
      check(simpleNames.isNotEmpty()) {
        "`simpleNames` must have at least one name, but this list is empty."
      }
    }
  }
}
