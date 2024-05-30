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

package com.rickbusarow.statik.element.kotlin

import com.rickbusarow.statik.element.StatikHasTypeParameters
import com.rickbusarow.statik.element.StatikHasValueParameters
import com.rickbusarow.statik.element.StatikTypeArgument
import com.rickbusarow.statik.element.StatikTypeParameter
import com.rickbusarow.statik.element.StatikValueArgument
import com.rickbusarow.statik.element.StatikValueParameter
import com.rickbusarow.statik.utils.lazy.LazySet

public interface StatikKotlinValueParameter<out PARENT : StatikKotlinElementWithPackageName<*>, NODE : Any> :
  StatikValueParameter<PARENT, NODE>,
  StatikKotlinDeclaredElement<PARENT, NODE>,
  StatikKotlinCallable<PARENT, NODE>

public interface StatikKotlinValueArgument<out PARENT : StatikKotlinElementWithPackageName<*>, NODE : Any> :
  StatikValueArgument<PARENT, NODE>,
  StatikKotlinDeclaredElement<PARENT, NODE>,
  StatikKotlinCallable<PARENT, NODE>

public interface StatikKotlinHasValueParameters<out PARENT : StatikKotlinElement<*>, NODE : Any> :
  StatikHasValueParameters<PARENT, NODE>,
  StatikKotlinElementWithParent<PARENT, NODE> {
  override val valueParameters: LazySet<StatikKotlinValueParameter<*, *>>
}

public interface StatikKotlinTypeParameter<out PARENT : StatikKotlinElementWithPackageName<*>, NODE : Any> :
  StatikTypeParameter<PARENT, NODE>,
  StatikKotlinTypeDeclaration<PARENT, NODE>

public interface StatikKotlinTypeArgument<out PARENT : StatikKotlinElementWithPackageName<*>, NODE : Any> :
  StatikTypeArgument<PARENT, NODE>,
  StatikKotlinTypeDeclaration<PARENT, NODE>

public interface StatikKotlinHasTypeParameters<out PARENT : StatikKotlinElement<*>, NODE : Any> :
  StatikHasTypeParameters<PARENT, NODE>,
  StatikKotlinElementWithParent<PARENT, NODE> {
  override val typeParameters: LazySet<StatikKotlinTypeParameter<*, *>>
}
