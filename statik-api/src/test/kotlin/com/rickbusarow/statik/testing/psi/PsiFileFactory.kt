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

package com.rickbusarow.statik.testing.psi

import com.rickbusarow.statik.utils.stdlib.requireNotNull
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.StandardFileSystems
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFileManager
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFileSystem
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.incremental.isJavaFile
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/**
 * The files created from this factory are backed by a meaningful
 * [BindingContext][org.jetbrains.kotlin.resolve.BindingContext] which
 * is aware of the full classpath and may be used for type resolution.
 */
class PsiFileFactory(
  private val coreEnvironment: KotlinCoreEnvironment
) {

  private val psiProject: Project by lazy { coreEnvironment.project }
  private val psiManager: PsiManager by lazy { PsiManager.getInstance(psiProject) }
  private val virtualFileSystem: VirtualFileSystem by lazy {
    // make sure that the PsiManager has initialized, or we'll get NPE's when trying to initialize
    // the VirtualFileManager instance
    psiManager
    VirtualFileManager.getInstance()
      .getFileSystem(StandardFileSystems.FILE_PROTOCOL)
  }

  @Suppress("ThrowsCount")
  private fun create(file: File): PsiFile {
    val vFile = virtualFileSystem.findFileByPath(file.absolutePath)

    vFile.requireNotNull { "could not find file $file" }

    val psi = psiManager.findFile(vFile)

    return when (file.extension) {
      "java" -> psi as PsiFile
      "kt", "kts" -> psi as PsiFile
      else -> throw IllegalArgumentException(
        "file extension must be one of [java, kt, kts], but it was `${file.extension}`."
      )
    }
  }

  /**
   * Returns a cached KtFile associated with this regular file, or creates a new one.
   *
   * @throws IllegalArgumentException if the file is not a Kotlin file or if it doesn't exist
   */
  fun createKotlin(file: File): KtFile {
    require(file.isKotlinFile()) {
      "the file's extension must be either `.kt` or `.kts`, but it was `${file.extension}`."
    }

    return create(file) as KtFile
  }

  /**
   * Returns a cached PsiJavaFile associated with this regular file, or creates a new one.
   *
   * @throws IllegalArgumentException if the file is not a Java file or if it doesn't exist
   */
  fun createJava(file: File): PsiJavaFile {

    require(!file.isJavaFile()) {
      "the file's extension must be `.java`, but it was `${file.extension}`."
    }

    return create(file) as PsiJavaFile
  }
}
