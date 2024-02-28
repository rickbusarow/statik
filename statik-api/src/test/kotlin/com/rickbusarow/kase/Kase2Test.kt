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

package com.rickbusarow.kase

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Kase2Test {

  @Test
  fun `kase function should correctly create a Kase2 instance with given parameters`() {
    val kase = kase(a1 = "Test1", a2 = "Test2") { "a1 == $a1, a2 == $a2" }

    kase.a1 shouldBe "Test1"
    kase.a2 shouldBe "Test2"
    kase.displayName shouldBe "a1 == Test1, a2 == Test2"
  }

  @Test
  fun `kase function should correctly create a Kase2 instance with default display name factory`() {
    val kase = kase(a1 = "Test1", a2 = "Test2")

    kase.a1 shouldBe "Test1"
    kase.a2 shouldBe "Test2"
    kase.displayName shouldBe "a1: Test1 | a2: Test2"
  }

  @Test
  fun `kases function should correctly create a list of Kase2 instances`() {
    val kases = kases(listOf("Test1", "Test2"), listOf("Test3", "Test4"))

    kases.size shouldBe 4
    kases[0].a1 shouldBe "Test1"
    kases[0].a2 shouldBe "Test3"
    kases[0].displayName shouldBe "a1: Test1 | a2: Test3"
    kases[1].a1 shouldBe "Test1"
    kases[1].a2 shouldBe "Test4"
    kases[1].displayName shouldBe "a1: Test1 | a2: Test4"
    kases[2].a1 shouldBe "Test2"
    kases[2].a2 shouldBe "Test3"
    kases[2].displayName shouldBe "a1: Test2 | a2: Test3"
    kases[3].a1 shouldBe "Test2"
    kases[3].a2 shouldBe "Test4"
    kases[3].displayName shouldBe "a1: Test2 | a2: Test4"
  }

  @Test
  fun `times operator should correctly create a list of Kase3 instances`() {
    val kase1 = kase(a1 = "Test1", a2 = "Test2")
    val kase2 = kase(a1 = "Test3")
    val kase3 = kase(a1 = "Test4")

    val kases = listOf(kase1) * listOf(kase2, kase3)

    kases.size shouldBe 2
    kases[0].a1 shouldBe "Test1"
    kases[0].a2 shouldBe "Test2"
    kases[0].a3 shouldBe "Test3"
    kases[1].a1 shouldBe "Test1"
    kases[1].a2 shouldBe "Test2"
    kases[1].a3 shouldBe "Test4"
  }
}
