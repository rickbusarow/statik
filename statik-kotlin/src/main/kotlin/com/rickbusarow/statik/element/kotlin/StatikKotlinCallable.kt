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
import com.rickbusarow.statik.element.StatikProperty
import com.rickbusarow.statik.element.StatikType

/** A Kotlin callable element. */
public interface StatikKotlinCallable<out PARENT : StatikKotlinElement> :
  StatikCallable<PARENT>,
  StatikKotlinElementWithParent<PARENT>

/** A Kotlin property element. */
public interface StatikKotlinProperty<out PARENT : StatikKotlinElementWithPackageName> :
  StatikProperty<PARENT>,
  StatikKotlinCallable<PARENT>

/** A Kotlin member property element. */
public interface StatikKotlinMemberProperty<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinProperty<PARENT>

/** A Kotlin extension property element. */
public interface StatikKotlinMemberExtensionProperty<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinMemberProperty<PARENT>,
  StatikKotlinHasTypeParameters<PARENT>

/** A Kotlin constructor property element. */
public interface StatikKotlinConstructorProperty<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinProperty<PARENT>

/** An extension element. */
public sealed interface StatikKotlinExtensionElement<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinCallable<PARENT> {
  /** The receiver type. */
  public val receiver: StatikType<*>
}

/** A Kotlin extension property. */
public interface StatikKotlinExtensionProperty<out PARENT : StatikKotlinElementWithPackageName> :
  StatikKotlinExtensionElement<PARENT>,
  StatikKotlinProperty<PARENT>
