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

package com.rickbusarow.statik.element.impl.kotlin.psi.internal

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotated

@InternalStatikApi
internal fun KtAnnotated.hasAnnotation(annotationFqName: FqName): Boolean {

  if (
    annotationEntries.any { it.typeReference?.typeElement?.text == annotationFqName.asString() }
  ) {
    return true
  }

  val file = containingKtFile

  val samePackage = annotationFqName.parent() == file.packageFqName

  // The annotation doesn't need to be imported if it's defined in the same package,
  // or if it's from the Kotlin stdlib.
  val needsImport = !samePackage && !setOf("kotlin", "kotlin.jvm")
    .contains(annotationFqName.parent().asString())

  val isImported by unsafeLazy {
    file.importDirectives.map { it.importPath?.pathStr }
      .contains(annotationFqName.asString())
  }

  if (needsImport && !isImported) {
    return false
  }

  return annotationEntries
    .mapNotNull { it.typeReference?.typeElement?.text }
    .any { it == annotationFqName.shortName().asString() }
}
