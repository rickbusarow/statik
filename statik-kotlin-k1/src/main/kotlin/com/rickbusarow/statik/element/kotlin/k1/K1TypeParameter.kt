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
import com.rickbusarow.statik.element.StatikTypeParameter
import com.rickbusarow.statik.element.StatikTypeReference
import com.rickbusarow.statik.element.kotlin.StatikKotlinTypeParameter
import com.rickbusarow.statik.element.kotlin.k1.compiler.HasStatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.getStrictParentOfType
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.emptyLazySet
import com.rickbusarow.statik.utils.lazy.lazySet
import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeParameterList

@Poko
@InternalStatikApi
public class K1TypeParameter<out PARENT : K1ElementWithPackageName>(
  override val context: StatikKotlinElementContext,
  override val node: KtTypeParameter,
  override val parent: PARENT
) : DefaultK1DeclaredElement<PARENT>(node, parent),
  StatikKotlinTypeParameter<PARENT>,
  HasStatikKotlinElementContext {

  override val index: Int
    get() = node.getStrictParentOfType<KtTypeParameterList>()?.parameters?.indexOf(node) ?: -1

  override val superTypes: LazySet<StatikTypeReference<*>> = lazySet {
    TODO("Not yet implemented")
  }

  override val typeParameters: LazySet<StatikTypeParameter<*>> get() = emptyLazySet()

  override val annotations: LazySet<StatikAnnotation<*>> = lazySet {
    node.annotations(context, this@K1TypeParameter)
  }
}
