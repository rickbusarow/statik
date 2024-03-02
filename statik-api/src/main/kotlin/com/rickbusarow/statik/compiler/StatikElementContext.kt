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
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import kotlinx.coroutines.coroutineScope
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Provides a context for parsing and resolving elements in a module check system. This
 * class is designed to work with any type `T` that represents a symbol in the system.
 *
 * @property nameParser The parser used to parse names in the system.
 * @property language The language that is compatible with the system.
 * @property kotlinEnvironmentDeferred A deferred [KotlinEnvironment]
 *   that provides a context for Kotlin language features.
 * @property stdLibNameOrNull A function that takes a [ReferenceName] and returns a
 *   [QualifiedDeclaredName] from the standard library, or null if no such name exists.
 */
public class StatikElementContext<T>(
  public val nameParser: NameParser,
  public val language: StatikLanguage,
  public val kotlinEnvironmentDeferred: LazyDeferred<KotlinEnvironment>,
  public val stdLibNameOrNull: ReferenceName.() -> QualifiedDeclaredName?
) {

  /**
   * A deferred binding context obtained from the [KotlinEnvironment].
   * This context is used to resolve bindings in the system.
   */
  public val bindingContextDeferred: LazyDeferred<BindingContext> = lazyDeferred {
    kotlinEnvironmentDeferred.await()
      .bindingContextDeferred.await()
  }

  /**
   * Resolves the declared name of a symbol in the system. This method is not yet implemented.
   *
   * @param symbol The symbol whose declared name is to be resolved.
   * @return The declared name of the symbol, or null if the symbol does not have a declared name.
   */
  public suspend fun declaredNameOrNull(symbol: T): QualifiedDeclaredName? {
    coroutineScope {
      TODO("not yet implemented $symbol")
    }
  }

  /**
   * Resolves a reference name in a given file. This method
   * uses the [nameParser] to parse the reference name.
   *
   * @param file The file in which the reference name is to be resolved.
   * @param toResolve The reference name to resolve.
   * @return The resolved reference name, or null if the reference name could not be resolved.
   */
  public suspend fun resolveReferenceNameOrNull(
    file: StatikFile,
    toResolve: ReferenceName
  ): ReferenceName? {
    return nameParser.parse(
      NameParser.NameParserPacket(
        file = file,
        toResolve = toResolve,
        referenceLanguage = language,
        stdLibNameOrNull = stdLibNameOrNull
      )
    )
  }
}
