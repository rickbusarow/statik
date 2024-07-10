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

package com.rickbusarow.statik.name

import com.rickbusarow.statik.name.PackageName.Companion.asPackageName
import com.rickbusarow.statik.name.SimpleName.Companion.asSimpleName
import com.rickbusarow.statik.name.StatikLanguage.JAVA
import com.rickbusarow.statik.name.StatikLanguage.KOTLIN
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class AsDeclaredNameTest {
  @Nested
  inner class `iterable receiver` {

    @Test
    fun `asDeclaredName with no language creates agnostic declared name`() {

      val packageName = "com.test".asPackageName()
      val simpleNames = listOf("Subject".asSimpleName())

      simpleNames.asDeclaredName(packageName) shouldBe DeclaredName.agnostic(
        packageName = packageName,
        simpleNames = simpleNames
      )
    }

    @Test
    fun `asDeclaredName with Kotlin language creates Kotlin declared name`() {

      val packageName = "com.test".asPackageName()
      val simpleNames = listOf("Subject".asSimpleName())

      simpleNames.asDeclaredName(packageName, KOTLIN) shouldBe DeclaredName.kotlin(
        packageName = packageName,
        simpleNames = simpleNames
      )
    }

    @Test
    fun `asDeclaredName with Java language creates Java declared name`() {

      val packageName = "com.test".asPackageName()
      val simpleNames = listOf("Subject".asSimpleName())

      simpleNames.asDeclaredName(packageName, JAVA) shouldBe DeclaredName.java(
        packageName = packageName,
        simpleNames = simpleNames
      )
    }

    @Test
    fun `asDeclaredName with Java and Kotlin languages creates agnostic declared name`() {

      val packageName = "com.test".asPackageName()
      val simpleNames = listOf("Subject".asSimpleName())

      simpleNames.asDeclaredName(packageName, JAVA, KOTLIN) shouldBe DeclaredName.agnostic(
        packageName = packageName,
        simpleNames = simpleNames
      )
    }
  }
}
