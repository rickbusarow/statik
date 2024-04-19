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

import com.rickbusarow.statik.element.StatikCallable
import com.rickbusarow.statik.element.StatikFunction
import com.rickbusarow.statik.element.StatikProperty
import com.rickbusarow.statik.element.StatikType
import com.rickbusarow.statik.name.HasPackageName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

/** A Kotlin callable element. */
public interface StatikKotlinCallable<out PARENT : StatikKotlinElement> :
  StatikCallable<PARENT>,
  StatikKotlinElementWithParent<PARENT> {
  override val psi: KtCallableDeclaration
}

/** A Kotlin property element. */
public sealed interface StatikKotlinProperty<out PARENT : StatikKotlinElementWithPackageName> :
  StatikProperty<PARENT>,
  StatikKotlinCallable<PARENT>

/** A Kotlin member property element. */
public interface StatikKotlinMemberProperty<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinProperty<PARENT> {
  override val psi: KtProperty
}

/** A Kotlin extension property element. */
public interface StatikKotlinMemberExtensionProperty<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinMemberProperty<PARENT>,
  StatikKotlinHasTypeParameters<PARENT> {
  override val psi: KtProperty
}

/** A Kotlin constructor property element. */
public interface StatikKotlinConstructorProperty<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinProperty<PARENT> {
  override val psi: KtParameter
}

/** Represents a Kotlin function element. */
public interface StatikKotlinFunction<out PARENT> :
  StatikFunction<PARENT>,
  StatikKotlinCallable<PARENT>,
  StatikKotlinHasValueParameters<PARENT>,
  StatikKotlinHasTypeParameters<PARENT>
  where PARENT : StatikKotlinElementWithPackageName,
        PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  override val psi: KtFunction
  override val valueParameters: LazySet<StatikKotlinValueParameter<*>>
  override val properties: LazySet<StatikKotlinProperty<*>>
  override val returnType: LazyDeferred<ReferenceName>
}

/** Represents a Kotlin function element. */
public interface StatikKotlinDeclaredFunction<out PARENT> :
  StatikKotlinFunction<PARENT>,
  StatikKotlinDeclaredElement<PARENT>
  where PARENT : StatikKotlinElementWithPackageName,
        PARENT : StatikKotlinElement,
        PARENT : HasPackageName

/** An extension element. */
public sealed interface StatikKotlinExtensionElement<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinCallable<PARENT>,
  StatikKotlinElement {
  /** The receiver type. */
  public val receiver: StatikType<*>
}

/** A Kotlin extension property. */
public interface StatikKotlinExtensionProperty<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinExtensionElement<PARENT>,
  StatikKotlinProperty<PARENT>

/** A Kotlin extension function. */
public interface StatikKotlinExtensionFunction<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinExtensionElement<PARENT>,
  StatikKotlinFunction<PARENT>

/** A Kotlin extension function. */
public interface StatikKotlinDeclaredExtensionFunction<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinExtensionElement<PARENT>,
  StatikKotlinDeclaredFunction<PARENT>
