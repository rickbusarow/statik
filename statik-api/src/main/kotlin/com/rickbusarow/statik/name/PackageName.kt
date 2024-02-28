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

import com.rickbusarow.statik.name.PackageName.DEFAULT
import com.rickbusarow.statik.name.SimpleName.Companion.asSimpleName
import com.rickbusarow.statik.stdlib.joinToStringDot
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import dev.drewhamilton.poko.Poko

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
  override val asString: String

  /**
   * Represents a [PackageName] when there isn't actually a package name, meaning that
   * top-level declarations in that file are at the root of source without qualifiers.
   *
   * @see McName
   * @see DEFAULT
   */
  object DEFAULT : PackageName {
    private fun readResolve(): Any = DEFAULT
    override val asString: String = ""
    override val segments: List<String>
      get() = emptyList()

    override fun append(simpleNames: Iterable<String>): String = simpleNames.joinToStringDot()
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
  fun appendAsString(simpleNames: Iterable<SimpleName>): String {
    return "$asString.${simpleNames.joinToStringDot { it.asString }}"
  }

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
fun PackageName.appendAsString(simpleNames: Iterable<String>): String {
  return appendAsString(simpleNames.map { it.asSimpleName() })
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
fun PackageName.appendAsString(vararg simpleNames: String): String {
  return appendAsString(simpleNames.toList())
}

/**
 * @see McName
 * @throws IllegalArgumentException if the [asString] parameter is empty or blank
 */
@Poko
class PackageNameImpl internal constructor(override val asString: String) : PackageName {
  init {
    require(asString.isNotBlank()) {
      "A ${this.javaClass.canonicalName} must be a non-empty, non-blank String.  " +
        "Represent an empty/blank or missing package name as ${DEFAULT::class.qualifiedName}.  " +
        "This name argument, wrapped in single quotes: '$asString'"
    }
  }

  override val segments: List<String> by unsafeLazy { asString.split('.') }

  override fun append(simpleNames: Iterable<String>): String =
    "$asString.${simpleNames.joinToStringDot()}"
}

/** Convenience interface for providing a [PackageName]. */
interface HasPackageName {
  val packageName: PackageName
}
