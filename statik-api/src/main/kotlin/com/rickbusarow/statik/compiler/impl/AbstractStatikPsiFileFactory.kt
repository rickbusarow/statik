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

import com.rickbusarow.statik.compiler.StatikPsiFileFactory
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.stdlib.isKotlinFile
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaFile
import org.jetbrains.kotlin.incremental.isJavaFile
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/** Base class for an [StatikPsiFileFactory] implementation */
internal abstract class AbstractStatikPsiFileFactory : StatikPsiFileFactory {

  /**
   * wrapper around "core" settings like Kotlin version,
   * source files, and classpath files (external dependencies)
   */
  abstract val coreEnvironment: LazyDeferred<KotlinCoreEnvironment>

  protected abstract suspend fun create(file: File): PsiFile

  override suspend fun createKotlin(file: File): KtFile {
    require(file.exists()) { "could not find file $file" }
    require(file.isKotlinFile()) {
      "the file's extension must be either `.kt` or `.kts`, but it was `${file.extension}`."
    }

    return create(file) as KtFile
  }

  override suspend fun createJava(file: File): PsiJavaFile {

    require(file.isJavaFile()) {
      "the file's extension must be `.java`, but it was `${file.extension}`."
    }

    return create(file) as PsiJavaFile
  }
}
