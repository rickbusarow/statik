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

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikPsiFileFactory
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File

/** Models everything needed to create Psi files for [BindingContext]-backed type resolution. */
@InternalStatikApi
public interface K1Environment : HasAnalysisResult {
  /**
   * Used to create Psi files without necessarily performing compiler analysis first.
   * This is only useful for Kotlin files, as Java Psi files require analysis first.
   */
  public val lightPsiFactory: LazyDeferred<StatikPsiFileFactory>

  /**
   * Used to create Psi files with a guarantee that compiler analysis is
   * done first. This is required to use [java Psi files][PsiJavaFile],
   */
  public val heavyPsiFactory: LazyDeferred<StatikPsiFileFactory>

  /**
   * wrapper around "core" settings like Kotlin version,
   * source files, and classpath files (external dependencies)
   */
  public val coreEnvironment: LazyDeferred<KotlinCoreEnvironment>

  /**
   * "core" settings like Kotlin version, source files, and classpath files (external dependencies)
   */
  public val compilerConfiguration: LazyDeferred<CompilerConfiguration>

  /**
   * Returns the best [StatikPsiFileFactory] available without having to run compiler analysis.
   *
   * If analysis is completed, this will return [heavyPsiFactory].
   * Otherwise, it will return [lightPsiFactory].
   */
  public suspend fun bestAvailablePsiFactory(): StatikPsiFileFactory

  /**
   * Returns a cached [KtFile] if one has already been created, otherwise creates a new
   * one. Note that these files are usable before compiler analysis has been executed.
   */
  public suspend fun ktFile(file: File): KtFile

  /**
   * Returns a cached [PsiJavaFile] if one has already been created, otherwise creates
   * a new one. Note that Java Psi files require compiler analysis to execute first.
   */
  public suspend fun javaPsiFile(file: File): PsiJavaFile
}

/**
 * Holds the [AnalysisResult], [BindingContext], and [ModuleDescriptorImpl] for a [K1Environment].
 * These are retrieved from an [AnalysisResult][org.jetbrains.kotlin.analyzer.AnalysisResult].
 */
@InternalStatikApi
public interface HasAnalysisResult {
  /**
   * The result of file analysis. This object is very expensive to create, but it's created lazily.
   *
   * Holds the [bindingContextDeferred] and [moduleDescriptorDeferred]
   * used for last-resort type and reference resolution.
   */
  public val analysisResultDeferred: LazyDeferred<AnalysisResult>

  /**
   * Used as the entry point for type resolution in Psi files.
   * Under the hood, it frequently delegates to this environment's
   * ModuleDescriptor or the descriptors from its dependency environments.
   */
  public val bindingContextDeferred: LazyDeferred<BindingContext>

  /**
   * The real force behind type resolution. Prefer using [bindingContextDeferred]
   * as the entry point, as it will give references to Psi elements when they're
   * known. But when we have to resolve things from dependencies (including other
   * source sets in the same module), this is always done using the descriptor.
   *
   * N.B. This is not thread-safe. This holds lazily cached data. That cache is
   * partially filled after the initial analysis, but the cache is still added
   * to when this descriptor is used in the analysis of downstream compilations.
   *
   * N.B. This has to be an -Impl instead of just the `ModuleDescriptor` interface
   * because `TopDownAnalyzerFacadeForJVM.createContainer(...)` requires the -Impl type.
   */
  public val moduleDescriptorDeferred: LazyDeferred<ModuleDescriptorImpl>
}
