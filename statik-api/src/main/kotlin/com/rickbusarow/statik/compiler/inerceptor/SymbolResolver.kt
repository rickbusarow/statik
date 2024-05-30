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

package com.rickbusarow.statik.compiler.inerceptor

import com.rickbusarow.statik.name.QualifiedDeclaredName

/**
 * Represents a resolver that can resolve symbols in the system. The type
 * `T` represents the type of the symbols that this resolver can handle.
 */
@Deprecated("Probably not used?")
public fun interface SymbolResolver<T> {
  /**
   * Resolves the declared name of a symbol in the system.
   *
   * @param symbol The symbol whose declared name is to be resolved.
   * @return The declared name of the symbol, or null if the symbol does not have a declared name.
   */
  public suspend fun declaredNameOrNull(symbol: T): QualifiedDeclaredName?
}
