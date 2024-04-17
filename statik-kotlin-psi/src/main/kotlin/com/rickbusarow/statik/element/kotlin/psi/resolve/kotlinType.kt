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

package com.rickbusarow.statik.element.kotlin.psi.resolve

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.name.ParameterizedReferenceName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.ReferenceName.Companion.asReferenceName
import com.rickbusarow.statik.name.StatikLanguage.KOTLIN
import org.jetbrains.kotlin.js.descriptorUtils.getKotlinTypeFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.getAbbreviatedType

@InternalStatikApi
public fun KotlinType.asReferenceName(): ReferenceName {

  // handle type aliases first.  An AbbreviatedType is an aliased type.  If it's an import alias,
  // the abbreviation is null.  If it's a typealias, the abbreviation is the type of the typealias
  // and that's what we want.
  val abbreviatedReferenceOrNull = getAbbreviatedType()?.abbreviation
    ?.asReferenceName()
  if (abbreviatedReferenceOrNull != null) {
    return abbreviatedReferenceOrNull
  }

  val rawType = getKotlinTypeFqName(false).asReferenceName(KOTLIN)

  return when {
    arguments.isNotEmpty() -> ParameterizedReferenceName(
      rawTypeName = rawType,
      typeParams = arguments.map { it.type.asReferenceName() }
    )

    else -> rawType
  }
}
