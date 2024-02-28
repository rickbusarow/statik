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

import com.rickbusarow.statik.compiler.KotlinEnvironment
import com.rickbusarow.statik.element.McFile.McKtFile
import com.rickbusarow.statik.element.NameParser2.NameParser2Packet
import com.rickbusarow.statik.name.McName.CompatibleLanguage
import com.rickbusarow.statik.name.QualifiedDeclaredName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/** Creates an [McElement] */
interface McElementFactory<T> {
  /**
   * @param context the context from which symbols should be resolved
   * @param fileSystemFile the java.io.File containing this element
   * @param backingElement the AST symbol used for actual parsing
   * @return a KtFile for this [backingElement]
   */
  suspend fun createKtFile(
    context: McElementContext<PsiElement>,
    fileSystemFile: File,
    backingElement: KtFile
  ): McKtFile

  /**
   * @param context the context from which symbols should be resolved
   * @param fileSystemFile the java.io.File containing this element
   * @param backingElement the AST symbol used for actual parsing
   * @param parent the parent element for this new element
   * @return some subtype of [McElement] which wraps [backingElement]
   */
  fun create(
    context: McElementContext<T>,
    fileSystemFile: File,
    backingElement: T,
    parent: McElement
  ): McElement
}

/**
 * Provides a context for parsing and resolving elements in a module check system.
 * This class is designed to work with any type `T` that represents a symbol in the
 * system. It uses a [NameParser2] to parse names, a [SymbolResolver] to resolve
 * symbols, and a [KotlinEnvironment] to provide a context for Kotlin language features.
 *
 * @property nameParser The parser used to parse names in the system.
 * @property symbolResolver The resolver used to resolve symbols in the system.
 * @property language The language that is compatible with the system.
 * @property kotlinEnvironmentDeferred A deferred [KotlinEnvironment]
 *   that provides a context for Kotlin language features.
 * @property stdLibNameOrNull A function that takes a [ReferenceName] and returns a
 *   [QualifiedDeclaredName] from the standard library, or null if no such name exists.
 */
class McElementContext<T>(
  val nameParser: NameParser2,
  val symbolResolver: SymbolResolver<T>,
  val language: CompatibleLanguage,
  val kotlinEnvironmentDeferred: LazyDeferred<KotlinEnvironment>,
  val stdLibNameOrNull: ReferenceName.() -> QualifiedDeclaredName?
) {

  /**
   * A deferred binding context obtained from the [KotlinEnvironment].
   * This context is used to resolve bindings in the system.
   */
  val bindingContextDeferred = lazyDeferred {
    kotlinEnvironmentDeferred.await()
      .bindingContextDeferred.await()
  }

  /**
   * Resolves the declared name of a symbol in the system. This method is not yet implemented.
   *
   * @param symbol The symbol whose declared name is to be resolved.
   * @return The declared name of the symbol, or null if the symbol does not have a declared name.
   */
  suspend fun declaredNameOrNull(symbol: T): QualifiedDeclaredName? {
    TODO()
  }

  /**
   * Resolves a reference name in a given file. This method
   * uses the [nameParser] to parse the reference name.
   *
   * @param file The file in which the reference name is to be resolved.
   * @param toResolve The reference name to resolve.
   * @return The resolved reference name, or null if the reference name could not be resolved.
   */
  suspend fun resolveReferenceNameOrNull(file: McFile, toResolve: ReferenceName): ReferenceName? {

    return nameParser.parse(
      NameParser2Packet(
        file = file,
        toResolve = toResolve,
        referenceLanguage = language,
        stdLibNameOrNull = stdLibNameOrNull
      )
    )
  }
}

/**
 * Represents a resolver that can resolve symbols in the system. The type
 * `T` represents the type of the symbols that this resolver can handle.
 */
fun interface SymbolResolver<T> {
  /**
   * Resolves the declared name of a symbol in the system.
   *
   * @param symbol The symbol whose declared name is to be resolved.
   * @return The declared name of the symbol, or null if the symbol does not have a declared name.
   */
  suspend fun declaredNameOrNull(symbol: T): QualifiedDeclaredName?
}

/**
 * Intercepts parsing operations. Implementations of this interface should
 * provide a way to parse a given `NameParser2Packet` into a `ReferenceName`.
 */
fun interface NameParser2 {
  /**
   * Parses the given packet into a `ReferenceName`.
   *
   * @param packet The packet to parse.
   * @return The parsed `ReferenceName`, or `null` if parsing was unsuccessful.
   */
  suspend fun parse(packet: NameParser2Packet): ReferenceName?

  /**
   * @property file The file being parsed.
   * @property toResolve The reference name to be resolved.
   * @property referenceLanguage The language of the file (Java or Kotlin).
   * @property stdLibNameOrNull A function that returns a `QualifiedDeclaredName` if the
   *   receiver name is part of the stdlib of this `referenceLanguage`, otherwise null.
   */
  data class NameParser2Packet(
    val file: McFile,
    val toResolve: ReferenceName,
    val referenceLanguage: CompatibleLanguage,
    val stdLibNameOrNull: ReferenceName.() -> QualifiedDeclaredName?
  )
}

/**
 * Intercepts parsing operations. Implementations of this interface should provide
 * a way to intercept the parsing process and potentially modify the result.
 */
fun interface ParsingInterceptor2 {

  /**
   * Intercepts the parsing process.
   *
   * @param chain The chain of parsing operations.
   * @return The intercepted `ReferenceName`, or `null` if the interception was unsuccessful.
   */
  suspend fun intercept(chain: Chain): ReferenceName?

  /** Represents a chain of parsing operations. */
  interface Chain {
    /** */
    val packet: NameParser2Packet

    /**
     * Passes the `packet` argument on to the next interceptor in this chain.
     *
     * @param packet The packet to pass on.
     * @return The result of the next interceptor in the
     *   chain, or `null` if there are no more interceptors.
     */
    suspend fun proceed(packet: NameParser2Packet): ReferenceName?
  }
}

/**
 * Represents a chain of parsing operations.
 *
 * @property packet The packet to be parsed.
 * @param interceptors The list of interceptors in the chain.
 */
class ParsingChain2 private constructor(
  override val packet: NameParser2Packet,
  private val interceptors: List<ParsingInterceptor2>
) : ParsingInterceptor2.Chain {

  /**
   * Passes the `packet` argument on to the next interceptor in this chain.
   *
   * @param packet The packet to pass on.
   * @return The result of the next interceptor in the
   *   chain, or `null` if there are no more interceptors.
   */
  override suspend fun proceed(packet: NameParser2Packet): ReferenceName? {
    val next = ParsingChain2(packet, interceptors.drop(1))

    val interceptor = interceptors.first()

    return interceptor.intercept(next)
  }

  /**
   * Factory for creating instances of `ParsingChain2`.
   *
   * @param interceptors The list of interceptors to include in the chain.
   */
  class Factory(
    private val interceptors: List<ParsingInterceptor2>
  ) : NameParser2 {

    /**
     * Parses the given packet into a `ReferenceName` using the chain of interceptors.
     *
     * @param packet The packet to parse.
     * @return The parsed `ReferenceName`, or `null` if parsing was unsuccessful.
     */
    override suspend fun parse(packet: NameParser2Packet): ReferenceName? {

      return ParsingChain2(packet, interceptors).proceed(packet)
    }
  }
}
