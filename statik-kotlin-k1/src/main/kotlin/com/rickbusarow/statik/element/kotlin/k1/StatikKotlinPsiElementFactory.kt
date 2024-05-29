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

package com.rickbusarow.statik.element.kotlin.k1

import com.rickbusarow.statik.compiler.StatikElementContext
import com.rickbusarow.statik.compiler.StatikElementFactory
import com.rickbusarow.statik.element.StatikElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinFile
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikKotlinElementContext
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/** Creates a [StatikKotlinElement] */
public interface StatikKotlinPsiElementFactory : StatikElementFactory<PsiElement> {
  /**
   * @param context the context from which symbols should be resolved
   * @param fileSystemFile the java.io.File containing this element
   * @param backingElement the AST symbol used for actual parsing
   * @return a KtFile for this [backingElement]
   */
  public suspend fun createKotlinFile(
    context: StatikKotlinElementContext,
    fileSystemFile: File,
    backingElement: KtFile
  ): StatikKotlinFile
}

internal class K1PsiElementFactory : StatikKotlinPsiElementFactory {

  override suspend fun createKotlinFile(
    context: StatikKotlinElementContext,
    fileSystemFile: File,
    backingElement: KtFile
  ): K1KotlinFile = K1KotlinFile(
    context = context,
    file = fileSystemFile,
    node = context.kotlinEnvironmentDeferred.await().ktFile(fileSystemFile)
  )

  override fun create(
    context: StatikElementContext<PsiElement>,
    fileSystemFile: File,
    backingElement: PsiElement,
    parent: StatikElement
  ): StatikElement {
    TODO("Not yet implemented")
  }
}
