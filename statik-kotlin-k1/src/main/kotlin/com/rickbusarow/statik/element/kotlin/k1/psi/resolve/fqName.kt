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

import com.rickbusarow.statik.name.PackageName
import com.rickbusarow.statik.name.QualifiedDeclaredName
import com.rickbusarow.statik.name.SimpleName
import com.rickbusarow.statik.name.SimpleName.Companion.stripPackageNameFromFqName
import com.rickbusarow.statik.name.StatikLanguage
import com.rickbusarow.statik.name.asDeclaredName
import org.jetbrains.kotlin.name.FqName

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
