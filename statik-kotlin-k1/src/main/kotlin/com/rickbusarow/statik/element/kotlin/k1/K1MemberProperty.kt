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
import com.rickbusarow.statik.element.kotlin.StatikKotlinHasTypeParameters
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
public open class K1MemberProperty<out PARENT : K1DeclaredElement<*>>(
  override val context: StatikKotlinElementContext,
  override val node: KtProperty,
  override val parent: PARENT
) : DefaultK1DeclaredElement<PARENT>(node, parent),
  StatikKotlinMemberProperty<PARENT>,
  HasStatikKotlinElementContext {

  override val typeReferenceName: LazyDeferred<ReferenceName> = lazyDeferred {
    bindingContext(BindingContext.VARIABLE, node)
      .requireNotNull()
      .type
      .requireReferenceName()
  }

  override val annotations: LazySet<StatikAnnotation<*>> = lazySet {
    node.annotations(context, parent = this)
  }
  override val isMutable: Boolean
    get() = node.isVar
}

@InternalStatikApi
public class K1MemberExtensionProperty<out PARENT : K1DeclaredElement<*>>(
  context: StatikKotlinElementContext,
  node: KtProperty,
  parent: PARENT
) : K1MemberProperty<PARENT>(context, node, parent),
  K1HasTypeParameters<PARENT> {
  override val typeParameters: LazySet<K1TypeParameter<*>> = lazySet { TODO() }
}

public interface K1HasTypeParameters<out PARENT : K1Element> :
  StatikKotlinHasTypeParameters<PARENT>,
  K1ElementWithParent<PARENT> {
  override val typeParameters: LazySet<K1TypeParameter<*>>
}
