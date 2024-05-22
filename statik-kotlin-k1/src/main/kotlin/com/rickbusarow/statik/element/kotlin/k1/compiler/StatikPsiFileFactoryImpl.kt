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

import com.rickbusarow.statik.element.kotlin.k1.K1Environment
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.vfs.StandardFileSystems
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFileManager
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.ConcurrentHashMap

/**
 * A real implementation of [StatikPsiFileFactory] using a curated [K1Environment].
 *
 * The files created from this factory are backed by a meaningful
 * [BindingContext][org.jetbrains.kotlin.resolve.BindingContext] which
 * is aware of the full classpath and may be used for type resolution.
 */
internal class StatikPsiFileFactoryImpl(
  kotlinEnvironment: K1Environment
) : AbstractStatikPsiFileFactory(),
  StatikPsiFileFactory {

  override val coreEnvironment: LazyDeferred<KotlinCoreEnvironment> =
    kotlinEnvironment.coreEnvironment

  private val fileCache = ConcurrentHashMap<File, PsiFile>()

  private val psiProjectDeferred = lazyDeferred { coreEnvironment.await().project }
  private val psiManager = lazyDeferred {
    PsiManager.getInstance(psiProjectDeferred.await())
  }
  private val virtualFileSystem = lazyDeferred {
    // make sure that the PsiManager has initialized, or we'll get NPE's when trying to initialize
    // the VirtualFileManager instance
    psiManager.await()

    VirtualFileManager.getInstance()
      .getFileSystem(StandardFileSystems.FILE_PROTOCOL)
  }

  override suspend fun create(file: File): PsiFile {
    return fileCache.getOrPut(file) {
      if (!file.exists()) throw FileNotFoundException("could not find file $file")

      val vFile = virtualFileSystem.await().findFileByPath(file.absolutePath)
        ?: throw FileNotFoundException("could not find file $file")

      val psi = psiManager.await().findFile(vFile)

      when (file.extension) {
        "java" -> psi as PsiFile
        "kt", "kts" -> psi as PsiFile
        else -> error(
          "file extension must be one of [java, kt, kts], but it was `${file.extension}`."
        )
      }
    }
  }
}
