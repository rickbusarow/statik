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

import com.rickbusarow.statik.element.StatikAnnotated
import com.rickbusarow.statik.element.StatikFile
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.lazy.LazySet

/** Represents a single Kotlin file. */
public interface StatikKotlinFile :
  StatikFile,
  StatikKotlinElement,
  StatikAnnotated,
  StatikKotlinElementWithPackageName {

  override val declaredTypes: LazySet<StatikKotlinConcreteType<*>>

  override val declaredTypesAndInnerTypes: LazySet<StatikKotlinConcreteType<*>>

  /** The top level functions in this file. */
  public val topLevelFunctions: LazySet<StatikKotlinFunction<*>>

  /** The top level properties in this file. */
  public val topLevelProperties: LazySet<StatikKotlinProperty<*>>

  /** The import aliases in this file. */
  public val importAliases: Map<String, ReferenceName>
}
