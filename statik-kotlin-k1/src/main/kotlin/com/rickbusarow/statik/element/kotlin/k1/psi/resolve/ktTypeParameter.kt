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
import com.rickbusarow.statik.utils.stdlib.singletonSet
import org.jetbrains.kotlin.psi.KtTypeConstraint
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeParameterList
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

@InternalStatikApi
public fun KtTypeParameter.upperBounds(): Set<KtTypeReference> {
  return extendsBound?.singletonSet()
    ?: typeParameterListOwner()
      ?.typeConstraintList
      ?.getChildrenOfType<KtTypeConstraint>()
      ?.mapNotNullTo(mutableSetOf()) {
        if (it.subjectTypeParameterName?.getReferencedName() == name) {
          it.boundTypeReference
        } else {
          null
        }
      }
      .orEmpty()
}

@InternalStatikApi
public fun KtTypeParameter.typeParameterListOwner(): KtTypeParameterListOwner? {
  return getStrictParentOfType<KtTypeParameterList>()
    ?.getStrictParentOfType<KtTypeParameterListOwner>()
}
