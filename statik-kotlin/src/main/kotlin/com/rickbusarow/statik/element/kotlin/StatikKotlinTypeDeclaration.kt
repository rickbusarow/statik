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

import com.rickbusarow.statik.element.StatikConcreteType
import com.rickbusarow.statik.element.StatikDeclaredElement
import com.rickbusarow.statik.element.StatikTypeDeclaration
import com.rickbusarow.statik.element.StatikTypeReference
import com.rickbusarow.statik.name.HasPackageName
import com.rickbusarow.statik.utils.lazy.LazySet

public interface StatikKotlinTypeReference<out PARENT : StatikKotlinElement> :
  StatikTypeReference<PARENT>,
  StatikKotlinElementWithParent<PARENT>

public interface StatikKotlinTypeDeclaration<out PARENT : StatikKotlinElement> :
  StatikTypeDeclaration<PARENT>,
  StatikKotlinElementWithParent<PARENT>

public interface StatikKotlinConcreteType<out PARENT> :
  StatikKotlinTypeDeclaration<PARENT>,
  StatikConcreteType<PARENT>,
  StatikKotlinDeclaredElement<PARENT>,
  StatikKotlinElementWithParent<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  override val innerTypes: LazySet<StatikKotlinConcreteType<*>>
  override val innerTypesRecursive: LazySet<StatikKotlinConcreteType<*>>

  override val properties: LazySet<StatikKotlinProperty<*>>
  override val functions: LazySet<StatikKotlinDeclaredFunction<*>>
}

public interface StatikKotlinAnnotationClass<out PARENT> :
  StatikKotlinConcreteType<PARENT>,
  StatikKotlinElementWithParent<PARENT>,
  StatikDeclaredElement<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName

public interface StatikKotlinClass<out PARENT> :
  StatikKotlinConcreteType<PARENT>,
  StatikKotlinElementWithParent<PARENT>,
  StatikDeclaredElement<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  public val primaryConstructor: StatikKotlinFunction<*>?

  /** All constructors, including the primary if it exists */
  public val constructors: LazySet<StatikKotlinFunction<*>>
}

public interface StatikKotlinCompanionObject<out PARENT> :
  StatikKotlinConcreteType<PARENT>,
  StatikKotlinElementWithParent<PARENT>,
  StatikDeclaredElement<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName

public interface StatikKotlinTypeAlias<out PARENT> :
  StatikKotlinConcreteType<PARENT>,
  StatikKotlinElementWithParent<PARENT>,
  StatikDeclaredElement<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName

public interface StatikKotlinEnum<out PARENT> :
  StatikKotlinConcreteType<PARENT>,
  StatikKotlinElementWithParent<PARENT>,
  StatikDeclaredElement<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName

public interface StatikKotlinInterface<out PARENT> :
  StatikKotlinConcreteType<PARENT>,
  StatikKotlinElementWithParent<PARENT>,
  StatikDeclaredElement<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName

public interface StatikKotlinObject<out PARENT> :
  StatikKotlinConcreteType<PARENT>,
  StatikKotlinElementWithParent<PARENT>,
  StatikDeclaredElement<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName
