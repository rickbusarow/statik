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
import com.rickbusarow.statik.element.internal.HasChildrenInternal
import com.rickbusarow.statik.element.kotlin.StatikKotlinElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinElementWithPackageName
import com.rickbusarow.statik.element.kotlin.StatikKotlinElementWithParent
import com.rickbusarow.statik.element.kotlin.k1.compiler.HasStatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikKotlinElementContext
import org.jetbrains.kotlin.psi.KtElement

@InternalStatikApi
public interface K1ElementSource2<P : KtElement> {

  context(HasStatikKotlinElementContext, PARENT)
  public fun <PARENT : StatikKotlinElement, K : StatikKotlinElementWithParent<PARENT>> P.statik(
    context: StatikKotlinElementContext = this@HasStatikKotlinElementContext.context,
    parent: PARENT = this@PARENT
  ): K
}

@InternalStatikApi
public abstract class K1ElementSource2Delegate<PB : StatikKotlinElementWithPackageName, K1, PSI : KtElement>(
  private val constructor: (StatikKotlinElementContext, PSI, PB) -> K1
) where K1 : StatikKotlinElement,
        K1 : StatikKotlinElementWithParent<PB>,
        K1 : HasStatikKotlinElementContext,
        K1 : HasChildrenInternal {

  context(HasStatikKotlinElementContext, PARENT)
  public fun <PARENT : PB> PSI.statik(
    context: StatikKotlinElementContext,
    parent: PARENT
  ): K1 = constructor(context, this, parent)
}
