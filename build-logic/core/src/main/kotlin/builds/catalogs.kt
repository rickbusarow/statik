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

import com.rickbusarow.kgx.extras
import com.rickbusarow.kgx.getOrPut
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.internal.catalog.ExternalModuleDependencyFactory

/** @since 0.1.0 */
val Project.libs: LibrariesForLibs
  get() = extensions.getByType(LibrariesForLibs::class.java)

/** @since 0.1.0 */
fun ExternalModuleDependencyFactory.VersionNotationSupplier.get(): String = asProvider().get()

/** @since 0.1.0 */
val Project.VERSION_NAME: String
  get() = property("VERSION_NAME") as String

/** @since 0.1.0 */
val Project.versionIsSnapshot: Boolean
  get() = extras.getOrPut("versionIsSnapshot") { VERSION_NAME.endsWith("-SNAPSHOT") }

/** @since 0.1.0 */
val Project.GROUP: String
  get() = property("GROUP") as String

/**
 * "1.6", "1.7", "1.8", etc.
 *
 * @since 0.1.0
 */
val Project.KOTLIN_API: String
  get() = property("KOTLIN_API") as String

/**
 * ex: `rbusarow`
 *
 * @since 0.1.0
 */
val Project.GITHUB_OWNER: String
  get() = property("GITHUB_OWNER") as String

/**
 * ex: `rbusarow/kase`
 *
 * @since 0.1.0
 */
val Project.GITHUB_OWNER_REPO: String
  get() = property("GITHUB_OWNER_REPO") as String

/**
 * ex: `https://github.com/rbusarow/kase`
 *
 * @since 0.1.0
 */
val Project.GITHUB_REPOSITORY: String
  get() = property("GITHUB_REPOSITORY") as String

/**
 * the jdk used in packaging
 *
 * "1.6", "1.8", "11", etc.
 *
 * @since 0.1.0
 */
val Project.JVM_TARGET: String
  get() = property("JVM_TARGET") as String

/**
 * `6`, `8`, `11`, etc.
 *
 * @since 0.1.0
 */
val Project.JVM_TARGET_INT: Int
  get() = JVM_TARGET.substringAfterLast('.').toInt()

/**
 * the jdk used to build the project
 *
 * "1.6", "1.8", "11", etc.
 *
 * @since 0.1.0
 */
val Project.JDK: String
  get() = property("JDK") as String

/**
 * the jdk used to build the project
 *
 * "1.6", "1.8", "11", etc.
 *
 * @since 0.1.0
 */
val Project.JDK_INT: Int
  get() = JDK.substringAfterLast('.').toInt()
