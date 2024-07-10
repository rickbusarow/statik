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

package com.rickbusarow.statik.compiler

import com.rickbusarow.statik.element.StatikElement
import java.io.File

/** Creates a [StatikElement] */
public interface StatikElementFactory<T> {
  /**
   * @param context the context from which symbols should be resolved
   * @param fileSystemFile the java.io.File containing this element
   * @param backingElement the AST symbol used for actual parsing
   * @param parent the parent element for this new element
   * @return some subtype of [StatikElement] which wraps [backingElement]
   */
  public fun create(
    context: StatikElementContext<T>,
    fileSystemFile: File,
    backingElement: T,
    parent: StatikElement
  ): StatikElement
}
