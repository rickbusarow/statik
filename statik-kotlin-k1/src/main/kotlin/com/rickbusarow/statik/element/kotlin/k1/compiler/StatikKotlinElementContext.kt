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

package com.rickbusarow.statik.element.kotlin.k1.compiler

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.compiler.StatikElementContext
import com.rickbusarow.statik.compiler.inerceptor.NameParser
import com.rickbusarow.statik.element.HasStatikElementContext
import com.rickbusarow.statik.element.StatikFile
import com.rickbusarow.statik.element.kotlin.StatikKotlinFile
import com.rickbusarow.statik.element.kotlin.k1.KotlinEnvironment
import com.rickbusarow.statik.element.kotlin.psi.StatikKotlinFileImpl
import com.rickbusarow.statik.element.kotlin.k1.K1Environment
import com.rickbusarow.statik.name.QualifiedDeclaredName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.StatikLanguage
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.map
import kotlinx.coroutines.coroutineScope
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice
import java.io.File

@InternalStatikApi
public interface HasStatikKotlinElementContext : HasStatikElementContext<StatikKotlinElementContext> {

  public suspend fun bindingContext(): BindingContext {
    return context.bindingContextDeferred.await()
  }

  public suspend fun <K, V> bindingContext(readOnlySlice: ReadOnlySlice<K, V>?, key: K): V? {
    return bindingContext().get(readOnlySlice, key)
  }
}

@InternalStatikApi
public interface StatikKotlinElementContext : StatikElementContext<PsiElement> {

  /**
   * A deferred [KotlinEnvironment][com.rickbusarow.statik.element.kotlin.psi.KotlinEnvironment]
   * that provides a context for Kotlin language features.
   * */
  public val kotlinEnvironmentDeferred: LazyDeferred<K1Environment>

  /**
   * A deferred binding context obtained from the
   * [KotlinEnvironment][com.rickbusarow.statik.element.kotlin.psi.KotlinEnvironment].
   * This context is used to resolve bindings in the system.
   */
  public val bindingContextDeferred: LazyDeferred<BindingContext>

  public suspend fun file(ktFile: KtFile): StatikKotlinFile
}

/**
 * Provides a context for parsing and resolving elements in a module check system. This
 * class is designed to work with any type `T` that represents a symbol in the system.
 *
 * @property nameParser The parser used to parse names in the system.
 * @property language The language that is compatible with the system.
 * @property kotlinEnvironmentDeferred A deferred [K1Environment]
 *   that provides a context for Kotlin language features.
 * @property stdLibNameOrNull A function that takes a [ReferenceName] and returns a
 *   [QualifiedDeclaredName] from the standard library, or null if no such name exists.
 */
@InternalStatikApi
public class K1ElementContext(
  override val nameParser: NameParser,
  override val language: StatikLanguage,
  override val kotlinEnvironmentDeferred: LazyDeferred<K1Environment>,
  override val stdLibNameOrNull: ReferenceName.() -> QualifiedDeclaredName?
) : StatikKotlinElementContext {

  private val files: MutableMap<KtFile, StatikKotlinFileImpl> = mutableMapOf()

  /**
   * A deferred binding context obtained from the [K1Environment].
   * This context is used to resolve bindings in the system.
   */
  override val bindingContextDeferred: LazyDeferred<BindingContext> =
    kotlinEnvironmentDeferred
      .map { it.bindingContextDeferred.await() }

  override suspend fun file(ktFile: KtFile): StatikKotlinFileImpl {
    return files.getOrPut(ktFile) {
      StatikKotlinFileImpl(
        context = this@StatikKotlinElementContextImpl,
        file = File(ktFile.virtualFilePath),
        psi = ktFile
      )
    }
  }

  /**
   * Resolves the declared name of a symbol in the system. This method is not yet implemented.
   *
   * @param symbol The symbol whose declared name is to be resolved.
   * @return The declared name of the symbol, or null if the symbol does not have a declared name.
   */
  override suspend fun declaredNameOrNull(symbol: PsiElement): QualifiedDeclaredName? {
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
  override suspend fun resolveReferenceNameOrNull(
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
