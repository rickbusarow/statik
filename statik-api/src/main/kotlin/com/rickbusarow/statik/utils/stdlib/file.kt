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

package com.rickbusarow.statik.utils.stdlib

import com.rickbusarow.statik.InternalStatikApi
import java.io.File

/**
 * Walks upward in the file tree, looking for a directory which will resolve [relativePath].
 *
 * For example, given a receiver File path of './a/b/c/' and a `relativePath` of
 * 'foo/bar.txt', this function will attempt to resolve the following paths in order:
 *
 * ```text
 * ./a/b/c/foo/bar.txt
 * ./a/b/foo/bar.txt
 * ./a/foo/bar.txt
 * ./foo/bar.txt
 * ```
 *
 * @returns the first path to contain an [existent][File.exists]
 *   File for [relativePath], or `null` if it could not be resolved
 * @see resolveInParent for a version which throws if nothing is resolved
 */
@InternalStatikApi
public fun File.resolveInParentOrNull(relativePath: String): File? {
  return resolve(relativePath).existsOrNull()
    ?: parentFile?.resolveInParentOrNull(relativePath)
}

/**
 * Non-nullable version of [resolveInParentOrNull]
 *
 * @see resolveInParentOrNull for a nullable, non-throwing variant
 * @throws IllegalArgumentException if a file cannot be resolved
 */
@InternalStatikApi
public fun File.resolveInParent(relativePath: String): File {
  return requireNotNull(resolveInParentOrNull(relativePath)) {
    "Could not resolve a file with relative path in any parent paths.\n" +
      "\t       relative path: $relativePath\n" +
      "\tstarting parent path: $absolutePath"
  }.normalize()
}

/** @return the receiver [File] if it exists in the file system, otherwise null */
@InternalStatikApi
public fun File.existsOrNull(): File? = takeIf { it.exists() }

/** `File("a/b/c/d.txt").segments() == ["a", "b", "c", "d.txt"]` */
@InternalStatikApi
public fun File.segments(): List<String> = path.split(File.separatorChar)

/* */
@InternalStatikApi
public operator fun File.div(relative: String): File = resolve(relative)

/* */
@InternalStatikApi
public operator fun File.div(relative: File): File = resolve(relative)

/**
 * Creates a new file if it doesn't already exist, creating parent
 * directories if necessary. If the file already exists, its content will
 * be overwritten. If content is provided, it will be written to the file.
 *
 * @param content The content to be written to the file. Defaults to null.
 * @param overwrite If true, any existing content will be overwritten. Otherwise, nothing is done.
 * @return The created file.
 */
@InternalStatikApi
public fun File.createSafely(content: String? = null, overwrite: Boolean = true): File = apply {
  when {
    content != null && (!exists() || overwrite) -> makeParentDir().writeText(content)
    else -> {
      makeParentDir().createNewFile()
    }
  }
}

/**
 * Creates the directories represented by the receiver [File] if they don't already exist.
 *
 * @receiver [File] The directories to create.
 * @return The directory file.
 */
@PublishedApi
internal fun File.mkdirsInline(): File = apply(File::mkdirs)

/**
 * Creates the parent directory of the receiver [File] if it doesn't already exist.
 *
 * @receiver [File] The file whose parent directory is to be created.
 * @return The file with its parent directory created.
 */

@PublishedApi
internal fun File.makeParentDir(): File = apply {
  val fileParent = requireNotNull(parentFile) { "File's `parentFile` must not be null." }
  fileParent.mkdirs()
}

/**
 * @return true if the file exists in the Java file system
 *   and has an extension of `.kt` or `.kts`, otherwise false
 */
@InternalStatikApi
public fun File.isKotlinFile(): Boolean = exists() && extension in setOf("kts", "kt")

/**
 * @return true if the file exists in the Java file system
 *   and has an extension of `.kts`, otherwise false
 */
@InternalStatikApi
public fun File.isKotlinScriptFile(): Boolean = exists() && extension == "kts"

/**
 * @return true if the file exists in the Java file
 *   system and has an extension of `.kt`, otherwise false
 */
@InternalStatikApi
public fun File.isKtFile(): Boolean = exists() && extension == "kt"
