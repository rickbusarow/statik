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

package com.rickbusarow.statik.element.kotlin.k1.testing

import com.rickbusarow.kase.DefaultTestEnvironment
import com.rickbusarow.kase.NoParamTestEnvironmentFactory
import com.rickbusarow.kase.TestEnvironment
import com.rickbusarow.kase.files.DirectoryBuilder
import com.rickbusarow.kase.files.HasWorkingDir
import com.rickbusarow.kase.files.TestLocation
import com.rickbusarow.statik.compiler.inerceptor.ParsingChain
import com.rickbusarow.statik.element.kotlin.StatikKotlinFile
import com.rickbusarow.statik.element.kotlin.k1.K1KotlinFile
import com.rickbusarow.statik.element.kotlin.k1.compiler.K1ElementContext
import com.rickbusarow.statik.element.kotlin.k1.compiler.K1EnvironmentImpl
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikMessageCollector
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.ImportAliasUnwrappingParsingInterceptor
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.kotlinStdLibNameOrNull
import com.rickbusarow.statik.logging.PrintLogger
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.StatikLanguage
import com.rickbusarow.statik.testing.internal.HostEnvironment
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.ResetManager
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import com.rickbusarow.statik.utils.lazy.map
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import com.rickbusarow.statik.utils.stdlib.createSafely
import com.rickbusarow.statik.utils.stdlib.div
import com.rickbusarow.statik.utils.stdlib.filterLines
import com.rickbusarow.statik.utils.stdlib.singletonList
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.config.JvmTarget.JVM_11
import org.jetbrains.kotlin.config.LanguageVersion.KOTLIN_1_8

class PsiTestEnvironment(
  private val hasWorkingDir: HasWorkingDir
) : TestEnvironment by DefaultTestEnvironment(hasWorkingDir) {

  val sources: DirectoryBuilder by unsafeLazy {
    DirectoryBuilder(workingDir / "sources")
      .also { it.createLib1Class() }
  }

  internal val kotlinEnvironmentDeferred: LazyDeferred<K1EnvironmentImpl> = lazyDeferred {
    K1EnvironmentImpl(
      moduleName = "statik-test",
      classpathFiles = lazyDeferred {
        listOf(
          HostEnvironment.kotlinStdLibJar,
          HostEnvironment.kotlinStdLibJdkJar
        )
      },
      sourceDirs = sources.path.singletonList(),
      kotlinLanguageVersion = KOTLIN_1_8,
      jvmTarget = JVM_11,
      dependencyModuleDescriptors = lazyDeferred { emptyList() },
      logger = PrintLogger(),
      resetManager = ResetManager()
    )
  }

  internal val messageCollector: LazyDeferred<StatikMessageCollector> = kotlinEnvironmentDeferred
    .map { it.messageCollector }

  suspend fun createKotlin(@Language("kotlin") content: String): StatikKotlinFile {

    val def = FileDef(content)

    val javaFile = sources.path
      .resolve("src/main/kotlin")
      .resolve(def.fileRelativePath)
      .createSafely(content.trimIndent())

    val ktFile = kotlinEnvironmentDeferred.await().ktFile(javaFile)

    // val androidDataBinding = AndroidDataBindingNameProvider { emptyLazySet() }
    // val androidRNameProvider = RealAndroidRNameProvider(updatedProject, sourceSetName)
    // val declarationsInPackage = RealDeclarationsProvider(updatedProject)

    val nameParser = ParsingChain.Factory(
      listOf(
        ImportAliasUnwrappingParsingInterceptor()
        // ConcatenatingParsingInterceptor(
        // androidRNameProvider = androidRNameProvider,
        // dataBindingNameProvider = androidDataBinding,
        // declarationsProvider = declarationsInPackage,
        // sourceSetName = SourceSetName("main")
        // )
      )
    )

    val context = K1ElementContext(
      nameParser = nameParser,
      language = StatikLanguage.KOTLIN,
      kotlinEnvironmentDeferred = kotlinEnvironmentDeferred,
      stdLibNameOrNull = ReferenceName::kotlinStdLibNameOrNull
    )

    return K1KotlinFile(
      context = context,
      file = javaFile,
      psi = ktFile
    )
  }

  override fun toString(): String = hasWorkingDir.toString()
    .filterLines { !it.endsWith(".class") }

  private fun DirectoryBuilder.createLib1Class() {
    dir("src/main/kotlin") {
      kotlinFile(
        "com/lib1/Lib1Class.kt",
        """
        package com.lib1

        class Lib1Class
        """
      )
    }
  }

  data class FileDef(val content: String) {
    val packageName by unsafeLazy {
      "package (.*)".toRegex()
        .find(content)
        ?.destructured
        ?.component1()
        .orEmpty()
    }

    val packageDir = packageName.replace(".", "/")

    val fileSimpleName = """(?:interface|class|object) (\w+)""".toRegex()
      .find(content)
      ?.groupValues
      ?.get(1)
      ?.let { "$it.kt" }
      ?: "SourceClass.kt"

    val fileRelativePath by unsafeLazy { "$packageDir/$fileSimpleName" }
  }

  companion object Factory : NoParamTestEnvironmentFactory<PsiTestEnvironment> {
    override fun create(
      names: List<String>,
      location: TestLocation
    ): PsiTestEnvironment = PsiTestEnvironment(
      hasWorkingDir = HasWorkingDir(testVariantNames = names, testLocation = location)
    )
  }
}
