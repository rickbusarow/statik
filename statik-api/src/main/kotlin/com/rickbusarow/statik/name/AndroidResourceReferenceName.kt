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

import com.rickbusarow.statik.name.McName.CompatibleLanguage
import com.rickbusarow.statik.name.SimpleName.Companion.asSimpleName
import com.rickbusarow.statik.utils.lazy.unsafeLazy

/** Any reference to an Android resource */
sealed class AndroidResourceReferenceName(name: String) : ReferenceName(name)

/**
 * example: `com.example.R`
 *
 * @property packageName the package of this reference (which is just the full string, minus `.R`)
 * @property language the language making this reference
 */
class AndroidRReferenceName(
  val packageName: PackageName,
  override val language: CompatibleLanguage
) : AndroidResourceReferenceName(packageName.append("R"))

/**
 * example: `R.string.app_name`
 *
 * @param name `R.string.____`
 * @property language the language making this reference
 */
// hashcode behavior is intentionally handled by super
@Suppress("EqualsWithHashCodeExist", "EqualsOrHashCode")
class UnqualifiedAndroidResourceReferenceName(
  name: String,
  override val language: CompatibleLanguage
) : AndroidResourceReferenceName(name),
  HasSimpleNames {

  private val split by unsafeLazy {
    name.split('.').also { segments ->
      @Suppress("MagicNumber")
      require(segments.size == 3) {
        "The name `$name` must follow the format `R.<prefix>.<identifier>`, " +
          "such as `R.string.app_name`."
      }
    }
  }

  /** example: 'string' in `R.string.app_name` */
  val prefix: SimpleName by unsafeLazy { split[1].asSimpleName() }

  /** example: 'app_name' in `R.string.app_name` */
  val identifier: SimpleName by unsafeLazy { split[2].asSimpleName() }

  override val simpleNames: List<SimpleName> by unsafeLazy {
    listOf("R".asSimpleName(), prefix, identifier)
  }

  override val segments: List<String> by unsafeLazy { simpleNames.map { it.asString } }

  override fun equals(other: Any?): Boolean {
    if (other is UnqualifiedAndroidResource) {
      return asString == other.asString
    }

    return super.equals(other)
  }
}

/**
 * example: `com.example.databinding.FragmentViewBinding`
 *
 * @param name `com.example.databinding.FragmentViewBinding`
 * @property language the language making this reference
 */
class AndroidDataBindingReferenceName(
  name: String,
  override val language: CompatibleLanguage
) : AndroidResourceReferenceName(name)

/**
 * example: `com.example.R.string.app_name`
 *
 * @param name `com.example.R.string.app_name`
 * @property language the language making this reference
 */
class QualifiedAndroidResourceReferenceName(
  name: String,
  override val language: CompatibleLanguage
) : AndroidResourceReferenceName(name)
