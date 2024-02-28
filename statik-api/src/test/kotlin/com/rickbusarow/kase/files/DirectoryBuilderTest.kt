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

package com.rickbusarow.kase.files

import com.rickbusarow.kase.DefaultTestEnvironment
import com.rickbusarow.kase.HasTestEnvironmentFactory
import com.rickbusarow.kase.Kase1
import com.rickbusarow.kase.asTests
import com.rickbusarow.kase.kase
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.File

class DirectoryBuilderTest : HasTestEnvironmentFactory<DefaultTestEnvironment.Factory> {

  override val testEnvironmentFactory = DefaultTestEnvironment.Factory()

  val entryPoints: List<Kase1<(File) -> DirectoryBuilder>>
    get() = listOf(
      kase(displayName = "extension - java.io.File ") { it.directoryBuilder() },
      kase(displayName = "extension - java.nio.files.Path ") { it.toPath().directoryBuilder() },
      kase(displayName = "invoke() - java.io.File") { DirectoryBuilder(it) },
      kase(displayName = "invoke() - java.nio.files.Path") { DirectoryBuilder(it.toPath()) }
    )

  @TestFactory
  fun `canary`() = entryPoints.asTests(testEnvironmentFactory) { (builderFactory) ->
    val builder = builderFactory(workingDir)

    builder.shouldBeInstanceOf<DirectoryBuilder>()
  }

  @Test
  fun `multiple files can be written in the same builder`() = test {

    workingDir.directoryBuilder {
      file("file1.txt", "file1")
      file("file2.txt", "file2")
    }

    workingDir.resolve("file1.txt") shouldHaveText "file1"
    workingDir.resolve("file2.txt") shouldHaveText "file2"
  }

  @Test
  fun `multiple files can be written in the same nested dir block`() = test {

    workingDir.directoryBuilder {
      dir("nested") {
        file("file1.txt", "file1")
        file("file2.txt", "file2")
      }
    }

    workingDir.resolve("nested/file1.txt") shouldHaveText "file1"
    workingDir.resolve("nested/file2.txt") shouldHaveText "file2"
  }

  @Test
  fun `multiple files can be written in independent nested dir blocks`() = test {

    workingDir.directoryBuilder {
      dir("nested") {
        file("file1.txt", "file1")
      }
      dir("nested") {
        file("file2.txt", "file2")
      }
    }

    workingDir.resolve("nested/file1.txt") shouldHaveText "file1"
    workingDir.resolve("nested/file2.txt") shouldHaveText "file2"
  }
}
