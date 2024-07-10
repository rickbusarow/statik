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
import com.rickbusarow.statik.element.internal.HasChildrenInternal
import com.rickbusarow.statik.element.internal.HasChildrenInternalDelegate
import com.rickbusarow.statik.element.kotlin.StatikKotlinAnonymousFunction
import com.rickbusarow.statik.element.kotlin.StatikKotlinDeclaredFunction
import com.rickbusarow.statik.element.kotlin.k1.compiler.HasStatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.requireReferenceName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import com.rickbusarow.statik.utils.lazy.lazySet
import com.rickbusarow.statik.utils.stdlib.mapToSet
import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext

@Poko
@InternalStatikApi
public class K1AnonymousFunction<out PARENT : K1ElementWithPackageName>(
  override val context: StatikKotlinElementContext,
  override val node: KtFunction,
  override val parent: PARENT
) : K1ElementWithParent<PARENT>,
  StatikKotlinAnonymousFunction<PARENT>,
  HasStatikKotlinElementContext,
  HasChildrenInternal by HasChildrenInternalDelegate() {

  override val valueParameters: LazySet<K1ValueParameter<*>>
    get() = lazySet { TODO("Not yet implemented") }

  override val properties: LazySet<K1Property<*>>
    get() = lazySet { TODO("Not yet implemented") }

  override val returnType: LazyDeferred<ReferenceName> = lazyDeferred {
    bindingContext(BindingContext.FUNCTION, node)
      ?.returnType
      .requireReferenceName()
  }

  override val annotations: LazySet<StatikAnnotation<*>>
    get() = lazySet { TODO("Not yet implemented") }
  override val typeParameters: LazySet<K1TypeParameter<*>>
    get() = lazySet { TODO("Not yet implemented") }
}

@Poko
@InternalStatikApi
public class K1DeclaredFunction<out PARENT : K1ElementWithPackageName>(
  override val context: StatikKotlinElementContext,
  override val node: KtNamedFunction,
  override val parent: PARENT
) : StatikKotlinDeclaredFunction<PARENT>,
  HasStatikKotlinElementContext,
  K1DeclaredElement<PARENT> by K1DeclaredElementDelegate(node, parent) {

  override val text: String
    get() = node.text

  override val valueParameters: LazySet<K1ValueParameter<*>> = lazySet {
    node.valueParameters.mapToSet { K1ValueParameter(context, it, this) }
  }
  override val properties: LazySet<K1Property<*>>
    get() = TODO("Not yet implemented")
  override val returnType: LazyDeferred<ReferenceName> = lazyDeferred {
    bindingContext(BindingContext.FUNCTION, node)
      ?.returnType
      .requireReferenceName()
  }
  override val annotations: LazySet<K1Annotation<*>> = lazySet {
    node.annotations(context, this@K1DeclaredFunction)
  }
  override val typeParameters: LazySet<K1TypeParameter<*>> = lazySet {
    node.typeParameters.mapToSet { K1TypeParameter(context, it, this) }
  }
}
