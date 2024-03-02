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

package com.rickbusarow.statik.element.kotlin.impl

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.compiler.StatikElementContext
import com.rickbusarow.statik.element.HasStatikElementContext
import com.rickbusarow.statik.element.StatikAnnotation
import com.rickbusarow.statik.element.impl.kotlin.psi.internal.requireReferenceName
import com.rickbusarow.statik.element.kotlin.HasKotlinVisibility
import com.rickbusarow.statik.element.kotlin.StatikKotlinDeclaredElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinElementWithPackageName
import com.rickbusarow.statik.element.kotlin.StatikKotlinFunction
import com.rickbusarow.statik.element.kotlin.StatikKotlinParameter
import com.rickbusarow.statik.element.kotlin.StatikKotlinProperty
import com.rickbusarow.statik.element.kotlin.StatikKotlinTypeParameter
import com.rickbusarow.statik.name.HasPackageName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import com.rickbusarow.statik.utils.lazy.lazySet
import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.resolve.BindingContext

@Poko
@InternalStatikApi
public class StatikKotlinFunctionImpl<out PARENT>(
  override val context: StatikElementContext<PsiElement>,
  override val psi: KtFunction,
  override val parent: PARENT
) : StatikKotlinFunction<PARENT>,
  StatikKotlinDeclaredElement<PARENT> by StatikKotlinDeclaredElementDelegate(psi, parent),
  HasKotlinVisibility by StatikKotlinVisibilityDelegate(psi),
  HasStatikElementContext
  where PARENT : StatikKotlinElementWithPackageName,
        PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  override val parameters: LazySet<StatikKotlinParameter<*>> =
    lazySet { TODO("Not yet implemented") }
  override val properties: LazySet<StatikKotlinProperty<*>> =
    lazySet { TODO("Not yet implemented") }
  override val returnType: LazyDeferred<ReferenceName> = lazyDeferred {
    bindingContext(BindingContext.FUNCTION, psi)
      ?.returnType
      .requireReferenceName()
  }
  override val annotations: LazySet<StatikAnnotation<*>> = lazySet { TODO("Not yet implemented") }
  override val typeParameters: LazySet<StatikKotlinTypeParameter<*>> =
    lazySet { TODO("Not yet implemented") }
}
