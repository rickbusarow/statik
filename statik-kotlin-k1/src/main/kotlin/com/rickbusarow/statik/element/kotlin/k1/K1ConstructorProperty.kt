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
import com.rickbusarow.statik.element.StatikType
import com.rickbusarow.statik.element.kotlin.StatikKotlinCallable
import com.rickbusarow.statik.element.kotlin.StatikKotlinConstructorProperty
import com.rickbusarow.statik.element.kotlin.StatikKotlinDeclaredElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinProperty
import com.rickbusarow.statik.element.kotlin.StatikKotlinVisibility
import com.rickbusarow.statik.element.kotlin.k1.compiler.HasStatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.requireReferenceName
import com.rickbusarow.statik.name.HasPackageName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import com.rickbusarow.statik.utils.lazy.lazySet
import com.rickbusarow.statik.utils.stdlib.requireNotNull
import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.BindingContext

/** Represents a declared Kotlin element in the source code. */
public interface K1DeclaredElement<out PARENT> :
  K1ElementWithParent<PARENT>,
  K1ElementWithPackageName,
  StatikKotlinDeclaredElement<PARENT>
  where PARENT : K1Element,
        PARENT : HasPackageName {

  override val visibility: StatikKotlinVisibility
}

@Poko
@InternalStatikApi
public class K1ConstructorProperty<out PARENT : K1DeclaredElement<*>>(
  override val context: StatikKotlinElementContext,
  override val node: KtParameter,
  override val parent: PARENT
) : StatikKotlinConstructorProperty<PARENT>,
  K1DeclaredElement<PARENT> by K1DeclaredElementDelegate(node, parent),
  HasStatikKotlinElementContext {

  override val typeReferenceName: LazyDeferred<ReferenceName> = lazyDeferred {
    bindingContext(BindingContext.VALUE_PARAMETER, node)
      .requireNotNull()
      .type
      .requireReferenceName()
  }

  override val annotations: LazySet<StatikAnnotation<*>> = lazySet {
    node.annotations(context, parent = this)
  }

  override val isMutable: Boolean
    get() = node.isMutable
}

public interface K1Callable<out PARENT : K1Element> : StatikKotlinCallable<PARENT>

/** A Kotlin property element. */
public sealed interface K1Property<out PARENT : K1ElementWithPackageName> :
  StatikKotlinProperty<PARENT>,
  K1Callable<PARENT>

/** An extension element. */
public sealed interface K1ExtensionElement<out PARENT : K1ElementWithPackageName> :
  K1Callable<PARENT>,
  K1Element {
  /** The receiver type. */
  public val receiver: StatikType<*>
}

/** A Kotlin extension property. */
public interface K1ExtensionProperty<out PARENT : K1ElementWithPackageName> :
  K1ExtensionElement<PARENT>,
  K1Property<PARENT>
