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

import com.rickbusarow.statik.compiler.inerceptor.NameParser.NameParserPacket
import com.rickbusarow.statik.element.StatikFile
import com.rickbusarow.statik.name.QualifiedDeclaredName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.StatikLanguage
import dev.drewhamilton.poko.Poko

/**
 * Intercepts parsing operations. Implementations of this interface should
 * provide a way to parse a given `NameParserPacket` into a `ReferenceName`.
 */
public fun interface NameParser {
  /**
   * Parses the given packet into a `ReferenceName`.
   *
   * @param packet The packet to parse.
   * @return The parsed `ReferenceName`, or `null` if parsing was unsuccessful.
   */
  public suspend fun parse(packet: NameParserPacket): ReferenceName?

  /**
   * @property file The file being parsed.
   * @property toResolve The reference name to be resolved.
   * @property referenceLanguage The language of the file (Java or Kotlin).
   * @property stdLibNameOrNull A function that returns a `QualifiedDeclaredName` if the
   *   receiver name is part of the stdlib of this `referenceLanguage`, otherwise null.
   */
  @Poko
  public class NameParserPacket(
    public val file: StatikFile,
    public val toResolve: ReferenceName,
    public val referenceLanguage: StatikLanguage,
    public val stdLibNameOrNull: ReferenceName.() -> QualifiedDeclaredName?
  ) {

    public fun copy(
      file: StatikFile = this.file,
      toResolve: ReferenceName = this.toResolve,
      referenceLanguage: StatikLanguage = this.referenceLanguage,
      stdLibNameOrNull: ReferenceName.() -> QualifiedDeclaredName? = this.stdLibNameOrNull
    ): NameParserPacket = NameParserPacket(
      file = file,
      toResolve = toResolve,
      referenceLanguage = referenceLanguage,
      stdLibNameOrNull = stdLibNameOrNull
    )
  }
}

/**
 * Represents a chain of parsing operations.
 *
 * @property packet The packet to be parsed.
 * @param interceptors The list of interceptors in the chain.
 */
public class ParsingChain private constructor(
  override val packet: NameParserPacket,
  private val interceptors: List<ParsingInterceptor>
) : ParsingInterceptor.Chain {

  /**
   * Passes the `packet` argument on to the next interceptor in this chain.
   *
   * @param packet The packet to pass on.
   * @return The result of the next interceptor in the
   *   chain, or `null` if there are no more interceptors.
   */
  override suspend fun proceed(packet: NameParserPacket): ReferenceName? {
    val next = ParsingChain(packet, interceptors.drop(1))

    val interceptor = interceptors.first()

    return interceptor.intercept(next)
  }

  /**
   * Factory for creating instances of `ParsingChain`.
   *
   * @param interceptors The list of interceptors to include in the chain.
   */
  public class Factory(
    private val interceptors: List<ParsingInterceptor>
  ) : NameParser {

    /**
     * Parses the given packet into a `ReferenceName` using the chain of interceptors.
     *
     * @param packet The packet to parse.
     * @return The parsed `ReferenceName`, or `null` if parsing was unsuccessful.
     */
    override suspend fun parse(packet: NameParserPacket): ReferenceName? {

      return ParsingChain(packet, interceptors).proceed(packet)
    }
  }
}

/**
 * Intercepts parsing operations. Implementations of this interface should provide
 * a way to intercept the parsing process and potentially modify the result.
 */
public fun interface ParsingInterceptor {

  /**
   * Intercepts the parsing process.
   *
   * @param chain The chain of parsing operations.
   * @return The intercepted `ReferenceName`, or `null` if the interception was unsuccessful.
   */
  public suspend fun intercept(chain: Chain): ReferenceName?

  /** Represents a chain of parsing operations. */
  public interface Chain {
    /** */
    public val packet: NameParserPacket

    /**
     * Passes the `packet` argument on to the next interceptor in this chain.
     *
     * @param packet The packet to pass on.
     * @return The result of the next interceptor in the
     *   chain, or `null` if there are no more interceptors.
     */
    public suspend fun proceed(packet: NameParserPacket): ReferenceName?
  }
}
