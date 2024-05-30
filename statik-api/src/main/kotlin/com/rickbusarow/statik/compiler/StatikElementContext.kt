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

import com.rickbusarow.statik.compiler.inerceptor.NameParser
import com.rickbusarow.statik.element.StatikFile
import com.rickbusarow.statik.name.QualifiedDeclaredName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.StatikLanguage

public interface StatikElementContext<T> {
  public val nameParser: NameParser
  public val language: StatikLanguage
  public val stdLibNameOrNull: ReferenceName.() -> QualifiedDeclaredName?

  /**
   * Resolves the declared name of a symbol in the system. This method is not yet implemented.
   *
   * @param symbol The symbol whose declared name is to be resolved.
   * @return The declared name of the symbol, or null if the symbol does not have a declared name.
   */
  public suspend fun declaredNameOrNull(symbol: T): QualifiedDeclaredName?

  /**
   * Resolves a reference name in a given file. This method
   * uses the [nameParser] to parse the reference name.
   *
   * @param file The file in which the reference name is to be resolved.
   * @param toResolve The reference name to resolve.
   * @return The resolved reference name, or null if the reference name could not be resolved.
   */
  public suspend fun resolveReferenceNameOrNull(
    file: StatikFile<*>,
    toResolve: ReferenceName
  ): ReferenceName?
}
