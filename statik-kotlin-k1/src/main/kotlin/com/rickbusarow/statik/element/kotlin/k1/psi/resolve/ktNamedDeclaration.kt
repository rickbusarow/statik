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

import com.rickbusarow.statik.InternalStatikApi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.parents

@InternalStatikApi
public fun KtNamedDeclaration.isConst(): Boolean = (this as? KtProperty)?.isConstant() ?: false

/** Basically the same as `name`, but if the name has backticks, this will include it. */
@InternalStatikApi
public fun KtNamedDeclaration.identifierName(): String? = nameIdentifier?.text

/**
 * For a declaration with a name wrapped in backticks, this returns a
 * name with those backticks. The regular `fqName` property does not.
 */
@InternalStatikApi
public fun KtNamedDeclaration.fqNameSafe(): FqName? {
  val base = fqName ?: return null

  if (!base.asString().contains("\\s+".toRegex())) {
    return base
  }

  val packageOffset = containingKtFile.packageFqName.pathSegments().size

  val parentsList = listOf(this@fqNameSafe)
    .plus(parents.filterIsInstance<KtNamedDeclaration>())
    .distinct()
    .reversed()

  return FqName.fromSegments(
    base.pathSegments()
      .mapIndexed { index, name ->

        name.asString()
          .takeIf { !it.contains("\\s+".toRegex()) }
          ?: parentsList[index - packageOffset].identifierName()
      }
  )
}
