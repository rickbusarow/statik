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

package builds

import com.rickbusarow.kgx.checkProjectIsRoot
import com.rickbusarow.kgx.extras
import com.rickbusarow.kgx.getOrPut
import org.ec4j.core.Cache.Caches
import org.ec4j.core.PropertyTypeRegistry
import org.ec4j.core.Resource.Resources
import org.ec4j.core.ResourcePath.ResourcePaths
import org.ec4j.core.ResourcePropertiesService
import org.ec4j.core.model.EditorConfig
import org.ec4j.core.model.Version
import org.ec4j.core.parser.EditorConfigModelHandler
import org.ec4j.core.parser.EditorConfigParser
import org.ec4j.core.parser.ErrorHandler
import org.gradle.api.Project
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Parses an .editorconfig file at [editorConfigFile].
 *
 * @since 0.1.0
 */
fun editorConfig(editorConfigFile: File): EditorConfig {

  val parser = EditorConfigParser.builder().build()
  val resource = editorConfigFile.toResource()

  val handler = EditorConfigModelHandler(PropertyTypeRegistry.default_(), Version.CURRENT)

  parser.parse(resource, handler, ErrorHandler.THROW_SYNTAX_ERRORS_IGNORE_OTHERS)

  return handler.editorConfig
}

/**
 * Returns a map of all Kotlin settings found in the `.editorconfig`,
 * where keys are the property name and the value is the trimmed text to
 * the right of the '='. Note that every value is a String, including.
 *
 * @since 0.1.0
 */
fun Project.editorConfigKotlinProperties(): Map<String, String> {

  checkProjectIsRoot { "only call this function from a root project" }

  val ecFile = file(".editorconfig")

  return extras.getOrPut(ecFile.path) { editorConfigKotlinProperties(ecFile, rootDir) }
}

/**
 * Returns a map of all Kotlin settings found in the `.editorconfig`,
 * where keys are the property name and the value is the trimmed text to
 * the right of the '='. Note that every value is a String, including.
 *
 * @since 0.1.0
 */
fun editorConfigKotlinProperties(editorConfigFile: File, rootDir: File): Map<String, String> {

  val myCache = Caches.none()
  val propService = ResourcePropertiesService.builder()
    .cache(myCache)
    .defaultEditorConfig(editorConfig(editorConfigFile))
    .rootDirectory(rootDir.toResourcePaths())
    .build()

  return propService
    .queryProperties(rootDir.resolve("foo.kt").toResource())
    .properties
    .values
    .associate { it.name to it.sourceValue }
}

private fun File.toResource() = Resources.ofPath(toPath(), StandardCharsets.UTF_8)
private fun File.toResourcePaths() = ResourcePaths.ofPath(toPath(), StandardCharsets.UTF_8)
