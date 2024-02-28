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

class Kase22Tests {

  @Test
  fun `kase function should correctly create a Kase22 instance with given parameters`() {
    val kase = kase(
      a1 = "Test1",
      a2 = "Test2",
      a3 = "Test3",
      a4 = "Test4",
      a5 = "Test5",
      a6 = "Test6",
      a7 = "Test7",
      a8 = "Test8",
      a9 = "Test9",
      a10 = "Test10",
      a11 = "Test11",
      a12 = "Test12",
      a13 = "Test13",
      a14 = "Test14",
      a15 = "Test15",
      a16 = "Test16",
      a17 = "Test17",
      a18 = "Test18",
      a19 = "Test19",
      a20 = "Test20",
      a21 = "Test21",
      a22 = "Test22"
    ) {
      "a1: $a1, a2: $a2, a3: $a3, a4: $a4, a5: $a5, a6: $a6, a7: $a7, a8: $a8, a9: $a9, a10: $a10, a11: $a11, a12: $a12, a13: $a13, a14: $a14, a15: $a15, a16: $a16, a17: $a17, a18: $a18, a19: $a19, a20: $a20, a21: $a21, a22: $a22"
    }

    kase.a1 shouldBe "Test1"
    kase.a2 shouldBe "Test2"
    kase.a3 shouldBe "Test3"
    kase.a4 shouldBe "Test4"
    kase.a5 shouldBe "Test5"
    kase.a6 shouldBe "Test6"
    kase.a7 shouldBe "Test7"
    kase.a8 shouldBe "Test8"
    kase.a9 shouldBe "Test9"
    kase.a10 shouldBe "Test10"
    kase.a11 shouldBe "Test11"
    kase.a12 shouldBe "Test12"
    kase.a13 shouldBe "Test13"
    kase.a14 shouldBe "Test14"
    kase.a15 shouldBe "Test15"
    kase.a16 shouldBe "Test16"
    kase.a17 shouldBe "Test17"
    kase.a18 shouldBe "Test18"
    kase.a19 shouldBe "Test19"
    kase.a20 shouldBe "Test20"
    kase.a21 shouldBe "Test21"
    kase.a22 shouldBe "Test22"
    kase.displayName shouldBe "a1: Test1, a2: Test2, a3: Test3, a4: Test4, a5: Test5, a6: Test6, a7: Test7, a8: Test8, a9: Test9, a10: Test10, a11: Test11, a12: Test12, a13: Test13, a14: Test14, a15: Test15, a16: Test16, a17: Test17, a18: Test18, a19: Test19, a20: Test20, a21: Test21, a22: Test22"
  }

  @Test
  fun `kase function should correctly create a Kase22 instance with default display name factory`() {
    val kase = kase(
      a1 = "Test1",
      a2 = "Test2",
      a3 = "Test3",
      a4 = "Test4",
      a5 = "Test5",
      a6 = "Test6",
      a7 = "Test7",
      a8 = "Test8",
      a9 = "Test9",
      a10 = "Test10",
      a11 = "Test11",
      a12 = "Test12",
      a13 = "Test13",
      a14 = "Test14",
      a15 = "Test15",
      a16 = "Test16",
      a17 = "Test17",
      a18 = "Test18",
      a19 = "Test19",
      a20 = "Test20",
      a21 = "Test21",
      a22 = "Test22"
    )

    kase.a1 shouldBe "Test1"
    kase.a2 shouldBe "Test2"
    kase.a3 shouldBe "Test3"
    kase.a4 shouldBe "Test4"
    kase.a5 shouldBe "Test5"
    kase.a6 shouldBe "Test6"
    kase.a7 shouldBe "Test7"
    kase.a8 shouldBe "Test8"
    kase.a9 shouldBe "Test9"
    kase.a10 shouldBe "Test10"
    kase.a11 shouldBe "Test11"
    kase.a12 shouldBe "Test12"
    kase.a13 shouldBe "Test13"
    kase.a14 shouldBe "Test14"
    kase.a15 shouldBe "Test15"
    kase.a16 shouldBe "Test16"
    kase.a17 shouldBe "Test17"
    kase.a18 shouldBe "Test18"
    kase.a19 shouldBe "Test19"
    kase.a20 shouldBe "Test20"
    kase.a21 shouldBe "Test21"
    kase.a22 shouldBe "Test22"
    kase.displayName shouldBe "a1: Test1 | a2: Test2 | a3: Test3 | a4: Test4 | a5: Test5 | a6: Test6 | a7: Test7 | a8: Test8 | a9: Test9 | a10: Test10 | a11: Test11 | a12: Test12 | a13: Test13 | a14: Test14 | a15: Test15 | a16: Test16 | a17: Test17 | a18: Test18 | a19: Test19 | a20: Test20 | a21: Test21 | a22: Test22"
  }
}
