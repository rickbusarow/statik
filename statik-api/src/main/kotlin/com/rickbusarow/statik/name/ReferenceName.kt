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
import com.rickbusarow.statik.name.StatikLanguage.JAVA
import com.rickbusarow.statik.name.StatikLanguage.KOTLIN
import com.rickbusarow.statik.name.StatikLanguage.XML
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import com.rickbusarow.statik.utils.stdlib.trimSegments

/** Trait interface for providing [ReferenceName]s. */
public interface HasReferences {

  /** The references in this object, calculated lazily */
  public val references: LazySet<ReferenceName>
}

/** Represents a name -- fully qualified or not -- which references a declaration somewhere else */
public sealed class ReferenceName(name: String) : StatikName, ResolvableStatikName {

  final override val asString: String by unsafeLazy { name.trimSegments(".") }

  /** The [language][StatikLanguage] of the file making this reference */
  public abstract val language: StatikLanguage

  override val segments: List<String> by unsafeLazy { this.asString.split('.') }

  /** This reference is from a Java source file */
  public fun isJava(): Boolean = language == JAVA

  /** This reference is from a Kotlin source file */
  public fun isKotlin(): Boolean = language == KOTLIN

  /** This reference is from an xml source file */
  @Suppress("GrazieInspection")
  public fun isXml(): Boolean = language == XML

  override fun equals(other: Any?): Boolean {
    if (this === other) return true

    when (other) {
      is ReferenceName -> {

        if (asString != other.asString) return false
        if (language != other.language) return false
      }

      is QualifiedDeclaredName -> {

        if (asString != other.asString) return false
        if (!other.languages.contains(language)) return false
      }

      else -> return false
    }
    return true
  }

  final override fun hashCode(): Int = asString.hashCode()

  override fun toString(): String {
    @Suppress("UnsafeCallOnNullableType")
    return "${this::class.simpleName!!}(name='$asString'  language=$language)"
  }

  public companion object {
    /** @return a basic [ReferenceName] for this name and language. */
    public operator fun invoke(name: String, language: StatikLanguage): ReferenceName =
      DefaultReferenceName(
        name = name,
        language = language
      )

    /** @return a basic [ReferenceName] for this name and language. */
    public fun String.asReferenceName(language: StatikLanguage): ReferenceName =
      ReferenceName(this, language)
  }
}

private class DefaultReferenceName(
  name: String,
  override val language: StatikLanguage
) : ReferenceName(name), StatikName {

  override val simpleName by unsafeLazy {
    name.split('.').last().asSimpleName()
  }
}
