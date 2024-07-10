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

package com.rickbusarow.statik.element

import com.rickbusarow.statik.utils.lazy.LazySet

public sealed interface StatikType<out PARENT : StatikElement> :
  StatikElementWithParent<PARENT>,
  StatikHasTypeParameters<PARENT>,
  StatikAnnotated

public interface StatikTypeDeclaration<out PARENT : StatikElement> :
  StatikType<PARENT> {

  /**
   * In a concrete type, this represents super-classes and interfaces.
   *
   * In a generic type, supers are the upper bound(s).
   */
  public val superTypes: LazySet<StatikTypeReference<*>>
}

/** Represents a class, interface, object, or companion object */
public interface StatikConcreteType<out PARENT : StatikElement> :
  StatikTypeDeclaration<PARENT> {

  override val containingFile: StatikFile

  public val innerTypes: LazySet<StatikConcreteType<*>>
  public val innerTypesRecursive: LazySet<StatikType<*>>
  public val properties: LazySet<StatikProperty<*>>
  public val functions: LazySet<StatikFunction<*>>
}

public interface StatikTypeReference<out PARENT : StatikElement> :
  StatikType<PARENT>
