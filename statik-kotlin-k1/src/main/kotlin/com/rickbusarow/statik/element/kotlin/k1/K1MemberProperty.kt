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

package com.rickbusarow.statik.element.kotlin.k1

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.element.StatikAnnotation
import com.rickbusarow.statik.element.kotlin.StatikKotlinDeclaredElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinMemberProperty
import com.rickbusarow.statik.element.kotlin.k1.compiler.HasStatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.requireReferenceName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import com.rickbusarow.statik.utils.stdlib.requireNotNull
import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext

@Poko
@InternalStatikApi
public class K1MemberProperty<out PARENT : StatikKotlinDeclaredElement<*>>(
  override val context: StatikKotlinElementContext,
  override val psi: KtProperty,
  override val parent: PARENT
) : StatikKotlinMemberProperty<PARENT>,
  StatikKotlinDeclaredElement<PARENT> by StatikKotlinDeclaredElementDelegate(psi, parent),
  HasStatikKotlinElementContext {

  override val typeReferenceName: LazyDeferred<ReferenceName> = lazyDeferred {
    bindingContext(BindingContext.VARIABLE, psi)
      .requireNotNull()
      .type
      .requireReferenceName()
  }

  override val annotations: LazySet<StatikAnnotation<*>> = lazySet {
    psi.annotations(context, parent = this)
  }
  override val isMutable: Boolean
    get() = psi.isVar
}
