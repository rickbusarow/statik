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

package com.rickbusarow.statik.compiler.inerceptor.impl

import com.rickbusarow.statik.compiler.DeclarationsProvider
import com.rickbusarow.statik.compiler.inerceptor.NameParser.NameParserPacket
import com.rickbusarow.statik.compiler.inerceptor.ParsingInterceptor
import com.rickbusarow.statik.name.QualifiedDeclaredName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.ReferenceName.Companion.asReferenceName
import com.rickbusarow.statik.name.SourceSetName
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull

internal class ConcatenatingParsingInterceptor(
  // private val androidRNameProvider: AndroidRNameProvider,
  // private val dataBindingNameProvider: AndroidDataBindingNameProvider,
  private val declarationsProvider: DeclarationsProvider,
  private val sourceSetName: SourceSetName
) : ParsingInterceptor {

  override suspend fun intercept(chain: ParsingInterceptor.Chain): ReferenceName? {

    val packet = chain.packet

    val file = packet.file

    val packageName = file.packageName

    val toResolve = packet.toResolve

    // val dataBindingDeclarations = dataBindingNameProvider.get()
    // val androidRNames = androidRNameProvider.getAll()
    // val localAndroidROrNull = androidRNameProvider.getLocalOrNull()

    return packet.importedReferenceOrNull(toResolve)
      ?.let { imported ->

        when {
          // toResolve.equals(localAndroidROrNull) -> {
          //   AndroidRReferenceName(file.packageName, packet.referenceLanguage)
          // }

          // androidRNames.contains(imported) -> {
          //   AndroidRReferenceName(
          //     imported.segments.dropLast(1).joinToString(".").asPackageName(),
          //     packet.referenceLanguage
          //   )
          // }

          // dataBindingDeclarations.contains(imported) -> {
          //   AndroidDataBindingReferenceName(imported.asString, packet.referenceLanguage)
          // }

          else -> imported
        }
      }
      ?: packet.stdLibNameOrNull(toResolve)?.asReferenceName(packet.referenceLanguage)
      ?: declarationsProvider
        .getWithUpstream(
          sourceSetName = sourceSetName,
          packageNameOrNull = packageName
        )
        .filterIsInstance<QualifiedDeclaredName>()
        .firstOrNull { it.isTopLevel && it.endsWithSimpleName(toResolve.referenceFirstName()) }
        ?.asString
        ?.asReferenceName(packet.referenceLanguage)
      ?: chain.proceed(packet)
  }

  // private suspend fun NameParserPacket.resolveInferredOrNull(
  //   toResolve: ReferenceName
  // ): ReferenceName? {
  //
  //   val fullyQualifiedAndWildcard = flow {
  //     // no import
  //     emit(toResolve)
  //
  //     // concat with any wildcard imports
  //     val concatenated = file.wildcardImports.get()
  //       .asFlow()
  //       .map { it.removeSuffix(".*") }
  //       .map {
  //         "$it.$toResolve".asReferenceName(referenceLanguage)
  //       }
  //
  //     emitAll(concatenated)
  //   }
  //
  //   val allDeclarations = declarationsProvider
  //     .getWithUpstream(
  //       sourceSetName = sourceSetName,
  //       packageNameOrNull = null
  //     )
  //
  //   return fullyQualifiedAndWildcard
  //     .firstOrNull { allDeclarations.contains(it) }
  // }

  private suspend fun NameParserPacket.importedReferenceOrNull(
    toResolve: ReferenceName
  ): ReferenceName? {
    return file.imports
      .get()
      .firstNotNullOfOrNull { importReference ->

        val matched = importReference.endsWith(toResolve.referenceFirstName())

        val referenceStart = toResolve.referenceFirstName()

        when {
          // Given a simple name without a qualifier and a matching import, like:
          // toResolve : "Foo"
          // import: "com.example.Foo"
          // ... just return the import.
          matched && referenceStart == toResolve.asString -> importReference
          // If it's matched but the name to resolve is qualified, then remove the part that matched
          // and concatenate.
          // toResolve: Foo.Bar
          // import: com.example.Foo
          // withoutStart = .Bar
          // concatenated = com.example.Foo.Bar
          matched -> {
            val withoutStart = toResolve.segments.drop(1).joinToString(".")
            "$importReference.$withoutStart".asReferenceName(referenceLanguage)
          }

          else -> null
        }
      }
  }

  @Suppress("UnusedPrivateMember")
  private fun String.referenceFirstName(): String = split('.').first()

  @Suppress("UnusedPrivateMember")
  private fun String.referenceLastName(): String = split('.').last()

  private fun ReferenceName.referenceFirstName(): String = segments.first()

  @Suppress("UnusedPrivateMember")
  private fun ReferenceName.referenceLastName(): String = segments.last()
}
