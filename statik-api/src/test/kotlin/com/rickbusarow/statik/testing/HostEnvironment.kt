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

package com.rickbusarow.statik.testing

import com.rickbusarow.statik.utils.stdlib.requireNotNull
import com.rickbusarow.statik.utils.stdlib.segments
import io.github.classgraph.ClassGraph
import java.io.File

object HostEnvironment {
  /** Everything in the classpath of the runtime executing the test */
  val inheritedClasspath: List<File> by lazy { getHostClasspaths() }

  /** Everything in the classpath of the runtime executing the test */
  @Deprecated(
    "renamed to inheritedClasspath",
    ReplaceWith(
      "inheritedClasspath",
      "com.squareup.kable.testing.kotlin.HostEnvironment.inheritedClasspath"
    )
  )
  val classpath: List<File> get() = inheritedClasspath

  /**  */
  @Suppress("MagicNumber")
  val allInheritedKableProjects: List<File> by lazy {
    inheritedClasspath.filter { it.isFile && it.extension == "jar" }
      .filter {
        // [..., "statik", "statik-api", "build", "libs", "statik-api-0.1.0-SNAPSHOT.jar"]
        it.segments()
          // ["build", "libs", "statik-api-0.1.0-SNAPSHOT.jar"]
          .takeLast(3)
          // ["build", "libs"]
          .dropLast(1) == listOf("build", "libs")
      }
  }

  /* */
  val wireGrpcClient: File by lazy {
    findInClasspath(group = "com.squareup.wire", module = "wire-grpc-client-jvm")
  }

  /* */
  val wireRuntime: File by lazy {
    findInClasspath(group = "com.squareup.wire", module = "wire-runtime-jvm")
  }

  /* */
  val anvilAnnotations: File by lazy {
    findInClasspath(group = "com.squareup.anvil", module = "annotations")
  }

  /**     */
  val autoServiceAnnotations: File by lazy {
    findInClasspath(group = "com.google.auto.service", module = "auto-service-annotations")
  }

  /* */
  val javaxInject: File by lazy {
    findInClasspath(group = "javax.inject", module = "javax.inject")
  }

  /**     */
  val clikt: File by lazy {
    findInClasspath(group = "com.github.ajalt.clikt", module = "clikt-jvm")
  }

  /* */
  val okioJvm: File by lazy {
    findInClasspath(group = "com.squareup.okio", module = "okio-jvm")
  }

  /**     */
  val kotlinxSerializationCoreJvm: File by lazy {
    findInClasspath(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-core-jvm")
  }

  /* */
  val kotlinStdLibJar: File by lazy {
    findInClasspath(kotlinDependencyRegex("(kotlin-stdlib|kotlin-runtime)"))
  }

  /* */
  val kotlinStdLibCommonJar: File by lazy {
    findInClasspath(kotlinDependencyRegex("kotlin-stdlib-common"))
  }

  /* */
  val kotlinStdLibJdkJar: File by lazy {
    findInClasspath(kotlinDependencyRegex("kotlin-stdlib-jdk[0-9]+"))
  }

  private fun kotlinDependencyRegex(prefix: String): Regex {
    return Regex("$prefix(-[0-9]+\\.[0-9]+(\\.[0-9]+)?)([-0-9a-zA-Z]+)?\\.jar")
  }

  /** Tries to find a file matching the given [regex] in the host process' classpath. */
  fun findInClasspath(regex: Regex): File {
    return inheritedClasspath.firstOrNull { classpath ->
      classpath.name.matches(regex)
    }
      .requireNotNull { "could not find classpath file via regex: $regex" }
  }

  /** Tries to find a .jar file given pieces of its maven coordinates */
  fun findInClasspath(
    group: String? = null,
    module: String? = null,
    version: String? = null
  ): File {
    require(group != null || module != null || version != null)
    return inheritedClasspath.firstOrNull { classpath ->

      val classpathIsLocal = classpath.absolutePath.contains(".m2/repository/")

      val (fileGroup, fileModule, fileVersion) = if (classpathIsLocal) {
        parseMavenLocalClasspath(classpath)
      } else {
        parseGradleCacheClasspath(classpath)
      }

      if (group != null && group != fileGroup) return@firstOrNull false
      if (module != null && module != fileModule) return@firstOrNull false
      version == null || version == fileVersion
    }
      .requireNotNull {
        "could not find classpath file [group: $group, module: $module, version: $version]"
      }
  }

  private fun parseMavenLocalClasspath(classpath: File): List<String> {
    // ~/.m2/repository/com/square/anvil/compiler-utils/1.0.0/compiler-utils-1.0.0.jar
    return classpath.absolutePath
      .substringAfter(".m2/repository/")
      // Groups have their dots replaced with file separators, like "com/squareup/anvil".
      // Module names use dashes, so they're unchanged.
      .split(File.separatorChar)
      // ["com", "square", "anvil", "compiler-utils", "1.0.0", "compiler-1.0.0.jar"]
      // drop the simple name and extension
      .dropLast(1)
      .let { segments ->

        listOf(
          // everything but the last two segments is the group
          segments.dropLast(2).joinToString("."),
          // second-to-last segment is the module
          segments[segments.lastIndex - 1],
          // the last segment is the version
          segments.last()
        )
      }
  }

  @Suppress("MagicNumber")
  private fun parseGradleCacheClasspath(classpath: File): List<String> {
    // example of a starting path:
    // [...]/com.square.anvil/compiler/1.0.0/911d07691411f7cbccf00d177ac41c1af38/compiler-1.0.0.jar
    return classpath.absolutePath
      .split(File.separatorChar)
      // [..., "com.square.anvil", "compiler", "1.0.0", "91...38", "compiler-1.0.0.jar"]
      .dropLast(2)
      .takeLast(3)
  }

  /** Returns the files on the classloader's classpath and module path. */
  private fun getHostClasspaths(): List<File> {
    val classGraph = ClassGraph()
      .enableSystemJarsAndModules()
      .removeTemporaryFilesAfterScan()

    val classpaths = classGraph.classpathFiles
    val modules = classGraph.modules.mapNotNull { it.locationFile }

    return (classpaths + modules).distinctBy(File::getAbsolutePath)
  }
}
