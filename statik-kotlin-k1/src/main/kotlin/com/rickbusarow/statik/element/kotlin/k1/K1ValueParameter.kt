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
import com.rickbusarow.statik.element.kotlin.StatikKotlinValueParameter
import com.rickbusarow.statik.element.kotlin.k1.compiler.HasStatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.getStrictParentOfType
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.requireReferenceName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.StatikName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import com.rickbusarow.statik.utils.lazy.lazySet
import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.resolve.BindingContext

@Poko
@InternalStatikApi
public class K1ValueParameter<out PARENT : K1ElementWithPackageName>(
  override val context: StatikKotlinElementContext,
  override val node: KtParameter,
  override val parent: PARENT
) : DefaultK1DeclaredElement<PARENT>(node, parent),
  StatikKotlinValueParameter<PARENT>,
  HasStatikKotlinElementContext {

  override val text: String
    get() = node.text

  override val name: StatikName
    get() = simplestName

  override val type: LazyDeferred<ReferenceName> = lazyDeferred {
    bindingContext(BindingContext.VALUE_PARAMETER, node)?.type
      .requireReferenceName()
  }

  override val index: Int
    get() = node.getStrictParentOfType<KtParameterList>()?.parameters?.indexOf(node) ?: -1

  override val annotations: LazySet<K1Annotation<*>> = lazySet {
    node.annotations(context, this@K1ValueParameter)
  }
}
