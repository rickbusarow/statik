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

package com.rickbusarow.statik.testing.internal

import com.rickbusarow.statik.testing.internal.ExtraSourceFile.Companion
import org.intellij.lang.annotations.Language
import java.io.File

/** A string with Kotlin code in it. */
typealias KotlinString = String

/** parses a Kotlin package from a String of Kotlin code */
fun KotlinString.kotlinPackageName(): String {

  val packageNameRegex = "package (\\S*)".toRegex()

  val lines = lines().filter { it.isNotBlank() }

  return lines.first { it.trim().matches(packageNameRegex) }
    .replace(packageNameRegex) { match ->
      match.destructured.component1().trim()
    }
}

/**
 * Models some source code with a relative path. The two properties may be combined
 * with a parent working directory to create source code for a test's compilation.
 *
 * @property relativePath a relative file path, including the file's name and extension
 * @property content the code to be written in the file
 */
data class ExtraSourceFile(
  val relativePath: String,
  val content: String
) {
  companion object
}

/**
 * ex:
 *
 * ```
 * val factory = ExtraSourceFile.from(
 *   """const val NAME = "Hello World"""",
 *   """
 *   package com.example
 *
 *   object Utils {
 *     fun doThings() { /* ... */ }
 *   }
 *   """.trimIndent()
 * )
 * ```
 *
 * @param kotlinStrings Kotlin source code
 * @return a factory from the [kotlinStrings], where each file's name is
 *   'Source_x.kt' and relative paths are derived from the content's package name
 */
fun Companion.from(
  @Language("kotlin") vararg kotlinStrings: KotlinString
): List<ExtraSourceFile> = ExtraSourceFile.from(
  *kotlinStrings
    .mapIndexed { index, content -> "Source_$index.kt" to content }
    .toTypedArray()
)

/**
 * ex:
 *
 * ```
 * val factory = ExtraSourceFile.from(
 *   "Utils.kt" to """const val NAME = "Hello World""""
 * )
 * ```
 *
 * @param fileSimpleNameToKotlinString pairs of the simple file name to the file's contents
 * @return a factory from the [fileSimpleNameToKotlinString] pairs, where
 *   file relative paths are derived from the content's package name
 */
fun Companion.from(
  vararg fileSimpleNameToKotlinString: Pair<String, KotlinString>
): List<ExtraSourceFile> = fileSimpleNameToKotlinString
  .map { (simpleName, content) ->
    val relative = content.kotlinPackageName()
      .replace('.', File.separatorChar)
      .let { if (it.isNotBlank()) "$it${File.separatorChar}" else it }
      .plus(simpleName)

    ExtraSourceFile(
      relativePath = relative,
      content = content
    )
  }
