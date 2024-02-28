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
import com.rickbusarow.statik.name.SimpleName.Companion.stripPackageNameFromFqName
import com.rickbusarow.statik.stdlib.asList
import com.rickbusarow.statik.stdlib.singletonList
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.name.FqName

/** Represents a "declaration" -- a named object which can be referenced elsewhere. */
sealed interface NameWithPackageName :
  HasPackageName,
  HasSimpleNames,
  ResolvableName {

  override val asString: String
    get() = packageName.appendAsString(simpleNames)

  override val segments: List<String>
    get() = asString.split('.')

  /**
   * `true` if a declaration is top-level in a file, otherwise `false`
   * such as if the declaration is a nested type or a member declaration
   */
  val isTopLevel: Boolean
    get() = simpleNames.size == 1

  companion object {
    /** */
    operator fun invoke(
      packageName: PackageName,
      simpleNames: List<SimpleName>
    ): NameWithPackageName = NameWithPackageNameImpl(packageName, simpleNames)
  }
}

@Poko
internal class NameWithPackageNameImpl(
  override val packageName: PackageName,
  override val simpleNames: List<SimpleName>
) : NameWithPackageName {
  init {
    checkSimpleNames()
  }

  override val asString: String by unsafeLazy { packageName.appendAsString(simpleNames) }

  override val segments: List<String> by unsafeLazy { asString.split('.') }
}

/**
 * @return a [NameWithPackageName], where the String after [packageName]
 *   is split and treated as the collection of [SimpleNames][SimpleName].
 */
fun FqName.asNameWithPackageName(packageName: PackageName): NameWithPackageName = asString()
  .stripPackageNameFromFqName(packageName)
  .asNameWithPackageName(packageName)

/**
 * @return a [NameWithPackageName] from the [packageName]
 *   argument, appending the receiver [SimpleNames][SimpleName]
 */
fun Iterable<SimpleName>.asNameWithPackageName(packageName: PackageName): NameWithPackageName {
  return NameWithPackageNameImpl(packageName, this.asList())
}

/**
 * @return a [NameWithPackageName] from the [packageName]
 *   argument, appending the receiver [SimpleNames][SimpleName]
 */
fun SimpleName.asNameWithPackageName(packageName: PackageName): NameWithPackageName {
  return singletonList().asNameWithPackageName(packageName)
}
