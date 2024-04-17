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
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isObjectLiteral
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@InternalStatikApi
public fun KtDeclaration.isInObject(): Boolean = containingClassOrObject?.isObjectLiteral() ?: false

/** @return true if the receiver declaration is inside a companion object */
@InternalStatikApi
public fun KtDeclaration.isInCompanionObject(): Boolean {
  return containingClassOrObject?.isCompanionObject() ?: false
}

@InternalStatikApi
public fun KtDeclaration.isInObjectOrCompanionObject(): Boolean = isInObject() || isInCompanionObject()

/** @return true if the receiver declaration is a companion object */
@OptIn(ExperimentalContracts::class)
@InternalStatikApi
public fun KtDeclaration.isCompanionObject(): Boolean {
  contract {
    returns(true) implies (this@isCompanionObject is KtObjectDeclaration)
  }
  return this is KtObjectDeclaration && isCompanion()
}
