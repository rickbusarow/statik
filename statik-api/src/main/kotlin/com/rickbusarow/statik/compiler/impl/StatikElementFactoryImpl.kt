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

package com.rickbusarow.statik.compiler.impl

import com.rickbusarow.statik.compiler.StatikElementContext
import com.rickbusarow.statik.compiler.StatikElementFactory
import com.rickbusarow.statik.element.StatikElement
import com.rickbusarow.statik.element.kotlin.impl.StatikKotlinFileImpl
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

internal class StatikElementFactoryImpl<T> : StatikElementFactory<T> {

  override suspend fun createKtFile(
    context: StatikElementContext<PsiElement>,
    fileSystemFile: File,
    backingElement: KtFile
  ): StatikKotlinFileImpl = StatikKotlinFileImpl(
    context = context,
    file = fileSystemFile,
    psi = context.kotlinEnvironmentDeferred.await().ktFile(fileSystemFile)
  )

  override fun create(
    context: StatikElementContext<T>,
    fileSystemFile: File,
    backingElement: T,
    parent: StatikElement
  ): StatikElement {
    TODO("Not yet implemented")
  }
}
