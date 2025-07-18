/*
 * Copyright (C) 2025 Rick Busarow
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
import com.rickbusarow.statik.logging.StatikLogger
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.ResetManager
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import com.rickbusarow.statik.utils.stdlib.isKotlinFile
import dispatch.core.withIO
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles.JVM_CONFIG_FILES
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.cli.jvm.compiler.report
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmSdkRoots
import org.jetbrains.kotlin.cli.jvm.modules.CoreJrtFileSystem
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaFile
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.incremental.isJavaFile
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File

/**
 * @property moduleName an arbitrary, identifiable name for the module being analyzed
 * @property classpathFiles `.jar` files from external dependencies
 * @param sourceDirs all jvm source code directories for
 *   this source set, like `[...]/myProject/src/main/java`.
 * @property kotlinLanguageVersion the version of Kotlin being used
 * @property jvmTarget the version of Java being compiled to
 * @property dependencyModuleDescriptors provides the module descriptors of
 *   all dependency source sets from the current module and dependency modules
 * @property logger logs Kotlin compiler messages during analysis
 * @param resetManager used to reset caching
 */
internal class K1EnvironmentImpl(
  val moduleName: String,
  val classpathFiles: LazyDeferred<List<File>>,
  private val sourceDirs: Collection<File>,
  val kotlinLanguageVersion: LanguageVersion,
  val jvmTarget: JvmTarget,
  val dependencyModuleDescriptors: LazyDeferred<List<ModuleDescriptorImpl>>,
  val logger: StatikLogger,
  private val resetManager: ResetManager
) : K1Environment {

  private val sourceFiles by lazy {
    sourceDirs.asSequence()
      .flatMap { dir -> dir.walkTopDown() }
      .filter { it.isFile }
      .toSet()
  }

  override val compilerConfiguration: LazyDeferred<CompilerConfiguration> = lazyDeferred {

    createCompilerConfiguration(
      classpathFiles = classpathFiles.await(),
      sourceFiles = sourceFiles.toList(),
      kotlinLanguageVersion = kotlinLanguageVersion,
      jvmTarget = jvmTarget
    )
  }

  override val coreEnvironment: LazyDeferred<KotlinCoreEnvironment> = lazyDeferred {
    createKotlinCoreEnvironment(compilerConfiguration.await())
  }

  override val lightPsiFactory: LazyDeferred<StatikPsiFileFactoryImpl> = lazyDeferred {
    StatikPsiFileFactoryImpl(this)
  }

  override val heavyPsiFactory: LazyDeferred<StatikPsiFileFactoryImpl> = lazyDeferred {
    analysisResultDeferred.await()
    StatikPsiFileFactoryImpl(this)
  }
  override val analysisResultDeferred: LazyDeferred<AnalysisResult> = lazyDeferred {

    val psiFactory = lightPsiFactory.await()

    val ktFiles = kotlinSourceFiles
      .map { file -> psiFactory.createKotlin(file) }

    createAnalysisResult(
      coreEnvironment = coreEnvironment.await(),
      ktFiles = ktFiles,
      dependencyModuleDescriptors = dependencyModuleDescriptors.await()
    )
  }

  override val bindingContextDeferred: LazyDeferred<BindingContext> = lazyDeferred {
    analysisResultDeferred.await().bindingContext
  }

  override val moduleDescriptorDeferred: LazyDeferred<ModuleDescriptorImpl> = lazyDeferred {
    analysisResultDeferred.await().moduleDescriptor as ModuleDescriptorImpl
  }

  val messageCollector: StatikMessageCollector by lazy {
    StatikMessageCollector(
      messageRenderer = MessageRenderer.GRADLE_STYLE,
      logger = logger,
      logLevel = StatikMessageCollector.LogLevel.WARNINGS_AS_ERRORS
    )
  }

  private val kotlinSourceFiles by lazy { sourceFiles.filter { it.isKotlinFile() } }

  override suspend fun bestAvailablePsiFactory(): StatikPsiFileFactoryImpl {
    return when {
      heavyPsiFactory.isCompleted -> heavyPsiFactory.getCompleted()
      analysisResultDeferred.isCompleted -> heavyPsiFactory.await()
      else -> lightPsiFactory.await()
    }
  }

  override suspend fun javaPsiFile(file: File): PsiJavaFile {
    // Type resolution for Java Psi files assumes that analysis has already been run.
    // Otherwise, we get:
    // `UninitializedPropertyAccessException: lateinit property module has not been initialized`
    analysisResultDeferred.await()
    return heavyPsiFactory.await().createJava(file)
  }

  override suspend fun ktFile(file: File): KtFile {
    return bestAvailablePsiFactory().createKotlin(file)
  }

  private suspend fun createAnalysisResult(
    coreEnvironment: KotlinCoreEnvironment,
    ktFiles: List<KtFile>,
    dependencyModuleDescriptors: List<ModuleDescriptorImpl>
  ): AnalysisResult = withIO {

    val analyzer = AnalyzerWithCompilerReport(
      messageCollector = messageCollector,
      languageVersionSettings = coreEnvironment.configuration.languageVersionSettings,
      renderDiagnosticName = false
    )

    analyzer.analyzeAndReport(ktFiles) {
      TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
        project = coreEnvironment.project,
        files = ktFiles,
        trace = NoScopeRecordCliBindingTrace(coreEnvironment.project),
        configuration = coreEnvironment.configuration,
        packagePartProvider = coreEnvironment::createPackagePartProvider,
        declarationProviderFactory = ::FileBasedDeclarationProviderFactory,
        explicitModuleDependencyList = dependencyModuleDescriptors
      )
    }

    messageCollector.printIssuesIfAny()

    analyzer.analysisResult
  }

  private fun createCompilerConfiguration(
    classpathFiles: List<File>,
    sourceFiles: List<File>,
    kotlinLanguageVersion: LanguageVersion,
    jvmTarget: JvmTarget
  ): CompilerConfiguration {
    val javaFiles = mutableListOf<File>()
    val kotlinFiles = mutableListOf<String>()

    sourceFiles.forEach { file ->
      when {
        file.isKotlinFile() -> kotlinFiles.add(file.absolutePath)
        file.isJavaFile() -> javaFiles.add(file)
      }
    }

    return CompilerConfiguration().apply {

      put(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, messageCollector)
      put(JVMConfigurationKeys.JVM_TARGET, jvmTarget)
      put(CommonConfigurationKeys.MODULE_NAME, moduleName)

      val languageVersionSettings = LanguageVersionSettingsImpl(
        languageVersion = kotlinLanguageVersion,
        apiVersion = ApiVersion.createByLanguageVersion(kotlinLanguageVersion)
      )
      put(CommonConfigurationKeys.LANGUAGE_VERSION_SETTINGS, languageVersionSettings)

      addJavaSourceRoots(javaFiles)
      addKotlinSourceRoots(kotlinFiles)
      addJvmClasspathRoots(classpathFiles)
      configureJdkClasspathRoots()
    }
  }

  private fun CompilerConfiguration.configureJdkClasspathRoots() {
    if (getBoolean(JVMConfigurationKeys.NO_JDK)) return

    val jdkHome = get(JVMConfigurationKeys.JDK_HOME)
    val (javaRoot, classesRoots) = if (jdkHome == null) {
      val javaHome = File(System.getProperty("java.home"))
      put(JVMConfigurationKeys.JDK_HOME, javaHome)

      javaHome to PathUtil.getJdkClassesRootsFromCurrentJre()
    } else {
      jdkHome to PathUtil.getJdkClassesRoots(jdkHome)
    }

    if (!CoreJrtFileSystem.isModularJdk(javaRoot)) {
      if (classesRoots.isEmpty()) {
        report(CompilerMessageSeverity.ERROR, "No class roots are found in the JDK path: $javaRoot")
      } else {
        addJvmSdkRoots(classesRoots)
      }
    }
  }

  private fun createKotlinCoreEnvironment(
    configuration: CompilerConfiguration
  ): KotlinCoreEnvironment {
    // https://github.com/JetBrains/kotlin/commit/2568804eaa2c8f6b10b735777218c81af62919c1
    setIdeaIoUseFallback()

    return KotlinCoreEnvironment.createForProduction(
      projectDisposable = resetManager,
      configuration = configuration,
      configFiles = JVM_CONFIG_FILES
    )
  }
}
