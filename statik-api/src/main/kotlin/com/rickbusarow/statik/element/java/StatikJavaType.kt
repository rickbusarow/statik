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

package com.rickbusarow.statik.element.java

import com.rickbusarow.statik.element.StatikConcreteType
import com.rickbusarow.statik.element.StatikDeclaredElement
import com.rickbusarow.statik.element.StatikTypeDeclaration
import com.rickbusarow.statik.utils.lazy.LazySet

public interface StatikJavaType<out PARENT : StatikJavaElement<*>, NODE : Any> :
  StatikTypeDeclaration<PARENT, NODE>,
  StatikJavaElementWithParent<PARENT, NODE> {
  override val parent: PARENT
}

public sealed interface StatikJavaConcreteType<out PARENT : StatikJavaElement<*>, NODE : Any> :
  StatikConcreteType<PARENT, NODE>,
  StatikJavaType<PARENT, NODE>,
  StatikJavaElementWithParent<PARENT, NODE> {

  override val innerTypes: LazySet<StatikJavaConcreteType<*, *>>
  override val innerTypesRecursive: LazySet<StatikJavaConcreteType<*, *>>

  override val containingFile: StatikJavaFile<*>

  public interface StatikJavaInterface<out PARENT : StatikJavaElement<*>, NODE : Any> :
    StatikJavaConcreteType<PARENT, NODE>,
    StatikDeclaredElement<PARENT, NODE>

  public interface StatikJavaClass<out PARENT : StatikJavaElement<*>, NODE : Any> :
    StatikJavaConcreteType<PARENT, NODE>,
    StatikDeclaredElement<PARENT, NODE> {

    public val constructors: LazySet<StatikJavaFunction<*, *>>
  }
}
