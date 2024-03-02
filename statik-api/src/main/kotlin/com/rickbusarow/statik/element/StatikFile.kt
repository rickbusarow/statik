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

import com.rickbusarow.statik.name.DeclaredName
import com.rickbusarow.statik.name.HasPackageName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.lazy.LazySet
import java.io.File

/** Represents a file. */
public interface StatikFile : StatikElement, HasPackageName {
  /** The actual file. */
  public val file: File

  /** The imports in this file. */
  public val imports: LazySet.DataSource<ReferenceName>

  /** The API references in this file. */
  public val apiReferences: List<LazySet.DataSource<ReferenceName>>

  /** The references in this file. */
  public val references: List<LazySet.DataSource<ReferenceName>>

  /** The declarations in this file. */
  public val declarations: List<LazySet.DataSource<DeclaredName>>

  /** The declared types in this file. */
  public val declaredTypes: LazySet<StatikConcreteType<*>>

  /** The declared types and inner types in this file. */
  public val declaredTypesAndInnerTypes: LazySet<StatikConcreteType<*>>

  /** The wildcard imports in this file. */
  public val wildcardImports: LazySet.DataSource<String>
}
