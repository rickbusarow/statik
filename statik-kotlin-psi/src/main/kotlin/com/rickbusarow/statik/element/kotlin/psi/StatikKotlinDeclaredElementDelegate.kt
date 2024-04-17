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

package com.rickbusarow.statik.element.kotlin.psi

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.element.kotlin.StatikKotlinDeclaredElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinFile
import com.rickbusarow.statik.element.kotlin.psi.resolve.requireSimpleName
import com.rickbusarow.statik.name.DeclaredName
import com.rickbusarow.statik.name.HasPackageName
import com.rickbusarow.statik.name.HasSimpleNames
import com.rickbusarow.statik.name.PackageName
import com.rickbusarow.statik.name.SimpleName
import com.rickbusarow.statik.name.asDeclaredName
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import com.rickbusarow.statik.utils.stdlib.singletonList
import org.jetbrains.kotlin.psi.KtModifierListOwner

@InternalStatikApi
public class StatikKotlinDeclaredElementDelegate<T : KtModifierListOwner, PARENT>(
  override val psi: T,
  override val parent: PARENT
) : StatikKotlinDeclaredElement<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  override val containingFile: StatikKotlinFile by unsafeLazy { parent.containingFile }
  override val packageName: PackageName by unsafeLazy { parent.packageName }
  override val simpleNames: List<SimpleName> by unsafeLazy {
    when (parent) {
      is HasSimpleNames -> parent.simpleNames + psi.requireSimpleName()
      else -> psi.requireSimpleName().singletonList()
    }
  }
  override val declaredName: DeclaredName by unsafeLazy {
    simpleNames.asDeclaredName(packageName)
  }
}
