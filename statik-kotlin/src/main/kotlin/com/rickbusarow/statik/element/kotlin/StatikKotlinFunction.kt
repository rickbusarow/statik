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

import com.rickbusarow.statik.element.StatikFunction
import com.rickbusarow.statik.name.HasPackageName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet

/** Represents a Kotlin function element. */
public sealed interface StatikKotlinFunction<out PARENT, NODE : Any> :
  StatikFunction<PARENT>,
  StatikKotlinCallable<PARENT, NODE>,
  StatikKotlinHasValueParameters<PARENT, NODE>,
  StatikKotlinHasTypeParameters<PARENT, NODE>
  where PARENT : StatikKotlinElementWithPackageName<*>,
        PARENT : StatikKotlinElement<*>,
        PARENT : HasPackageName {

  override val valueParameters: LazySet<StatikKotlinValueParameter<*, *>>
  override val properties: LazySet<StatikKotlinProperty<*, *>>
  override val returnType: LazyDeferred<ReferenceName>
}

/** Represents a Kotlin function element. */
public interface StatikKotlinAnonymousFunction<out PARENT : StatikKotlinElementWithPackageName<*>, NODE : Any> :
  StatikKotlinFunction<PARENT, NODE>

/** Represents a Kotlin function element. */
public interface StatikKotlinDeclaredFunction<out PARENT : StatikKotlinElementWithPackageName<*>, NODE : Any> :
  StatikKotlinFunction<PARENT, NODE>,
  StatikKotlinDeclaredElement<PARENT, NODE>

/** A Kotlin extension function. */
public interface StatikKotlinExtensionFunction<out PARENT : StatikKotlinElementWithPackageName<*>, NODE : Any> :
  StatikKotlinExtensionElement<PARENT, NODE>,
  StatikKotlinFunction<PARENT, NODE>

/** A Kotlin extension function. */
public interface StatikKotlinDeclaredExtensionFunction<out PARENT : StatikKotlinElementWithPackageName<*>, NODE : Any> :
  StatikKotlinExtensionElement<PARENT, NODE>,
  StatikKotlinFunction<PARENT, NODE>,
  StatikKotlinDeclaredFunction<PARENT, NODE>