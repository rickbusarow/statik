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
import com.rickbusarow.statik.element.internal.HasChildrenInternal
import com.rickbusarow.statik.element.internal.HasChildrenInternalDelegate
import com.rickbusarow.statik.element.kotlin.StatikKotlinDeclaredElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinDeclaredFunction
import com.rickbusarow.statik.element.kotlin.StatikKotlinElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinElementWithPackageName
import com.rickbusarow.statik.element.kotlin.StatikKotlinFunction
import com.rickbusarow.statik.element.kotlin.StatikKotlinProperty
import com.rickbusarow.statik.element.kotlin.StatikKotlinTypeParameter
import com.rickbusarow.statik.element.kotlin.StatikKotlinValueParameter
import com.rickbusarow.statik.element.kotlin.k1.compiler.HasStatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.getStrictParentOfType
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.requireReferenceName
import com.rickbusarow.statik.name.HasPackageName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.SimpleName
import com.rickbusarow.statik.name.StatikName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.emptyLazySet
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import com.rickbusarow.statik.utils.stdlib.mapToSet
import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeParameterList
import org.jetbrains.kotlin.resolve.BindingContext

@Poko
@InternalStatikApi
public class StatikKotlinFunctionImpl<out PARENT>(
  override val context: StatikKotlinElementContext,
  override val psi: KtFunction,
  override val parent: PARENT
) : StatikKotlinFunction<PARENT>,
  HasStatikKotlinElementContext,
  HasChildrenInternal by HasChildrenInternalDelegate()
  where PARENT : StatikKotlinElementWithPackageName,
        PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  override val valueParameters: LazySet<StatikKotlinValueParameter<*>>
    get() = lazySet { TODO("Not yet implemented") }

  override val properties: LazySet<StatikKotlinProperty<*>>
    get() = lazySet { TODO("Not yet implemented") }

  override val returnType: LazyDeferred<ReferenceName> = lazyDeferred {
    bindingContext(BindingContext.FUNCTION, psi)
      ?.returnType
      .requireReferenceName()
  }

  override val annotations: LazySet<StatikAnnotation<*>>
    get() = lazySet { TODO("Not yet implemented") }
  override val typeParameters: LazySet<StatikKotlinTypeParameter<*>>
    get() = lazySet { TODO("Not yet implemented") }
}

@Poko
@InternalStatikApi
public class StatikKotlinDeclaredFunctionImpl<out PARENT>(
  override val context: StatikKotlinElementContext,
  override val psi: KtFunction,
  override val parent: PARENT
) : StatikKotlinDeclaredFunction<PARENT>,
  HasStatikKotlinElementContext,
  StatikKotlinDeclaredElement<PARENT> by StatikKotlinDeclaredElementDelegate(psi, parent)
  where PARENT : StatikKotlinElementWithPackageName,
        PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  override val valueParameters: LazySet<StatikKotlinValueParameter<*>> = lazySet {
    psi.valueParameters.mapToSet { StatikKotlinValueParameterImpl(context, it, this) }
  }
  override val properties: LazySet<StatikKotlinProperty<*>>
    get() = TODO("Not yet implemented")
  override val returnType: LazyDeferred<ReferenceName> = lazyDeferred {
    bindingContext(BindingContext.FUNCTION, psi)
      ?.returnType
      .requireReferenceName()
  }
  override val annotations: LazySet<StatikAnnotation<*>> = lazySet {
    psi.annotations(context, this@StatikKotlinDeclaredFunctionImpl)
  }
  override val typeParameters: LazySet<StatikKotlinTypeParameter<*>> = lazySet {
    psi.typeParameters.mapToSet { StatikKotlinTypeParameterImpl(context, it, this) }
  }
}

@Poko
@InternalStatikApi
public class StatikKotlinTypeParameterImpl<out PARENT>(
  override val context: StatikKotlinElementContext,
  override val psi: KtTypeParameter,
  override val parent: PARENT
) : StatikKotlinTypeParameter<PARENT>,
  HasStatikKotlinElementContext,
  StatikKotlinDeclaredElement<PARENT> by StatikKotlinDeclaredElementDelegate(psi, parent)
  where PARENT : StatikKotlinElementWithPackageName,
        PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  override val index: Int
    get() = psi.getStrictParentOfType<KtTypeParameterList>()?.parameters?.indexOf(psi) ?: -1

  override val superTypes: LazySet<StatikTypeReference<*>> = lazySet {
    emptySet()
  }
  override val typeParameters: LazySet<StatikTypeParameter<*>>
    get() = emptyLazySet()

  override val annotations: LazySet<StatikAnnotation<*>> = lazySet {
    psi.annotations(context, this@StatikKotlinTypeParameterImpl)
  }
}

@Poko
@InternalStatikApi
public class StatikKotlinValueParameterImpl<out PARENT>(
  override val context: StatikKotlinElementContext,
  override val psi: KtParameter,
  override val parent: PARENT
) : StatikKotlinValueParameter<PARENT>,
  HasStatikKotlinElementContext,
  StatikKotlinDeclaredElement<PARENT> by StatikKotlinDeclaredElementDelegate(psi, parent)
  where PARENT : StatikKotlinElementWithPackageName,
        PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  override val name: StatikName
    get() = simplestName

  override val type: LazyDeferred<ReferenceName> = lazyDeferred {
    bindingContext(BindingContext.VALUE_PARAMETER, psi)?.type
      .requireReferenceName()
  }

  override val index: Int
    get() = psi.getStrictParentOfType<KtParameterList>()?.parameters?.indexOf(psi) ?: -1

  override val annotations: LazySet<StatikAnnotation<*>> = lazySet {
    psi.annotations(context, this@StatikKotlinValueParameterImpl)
  }
}

public val SimpleName.Companion.underscore: SimpleName
  get() = SimpleName("_")
