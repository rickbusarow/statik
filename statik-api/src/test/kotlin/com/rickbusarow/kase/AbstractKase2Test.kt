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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AbstractKase2Test {

  @Nested
  inner class `destructuring components` {

    @Test
    fun `a data class impl destructures to its args`() {

      data class Custom(
        val name: String,
        val age: Int
      ) : AbstractKase2<String, Int>(name, age)

      val (name, age) = Custom("Bluey", 6)
      name shouldBe "Bluey"
      age shouldBe 6
    }

    @Test
    fun `a non data class impl still destructures to its args`() {

      class Custom(
        name: String,
        age: Int
      ) : AbstractKase2<String, Int>(name, age)

      val (name, age) = Custom("Bluey", 6)
      name shouldBe "Bluey"
      age shouldBe 6
    }
  }

  @Nested
  inner class `display names` {

    @Test
    fun `a data class impl with default factory uses toString`() {

      data class Custom(
        val name: String,
        val age: Int
      ) : AbstractKase2<String, Int>(name, age)

      Custom("Bluey", 6).displayName shouldBe "name=Bluey, age=6"
    }

    @Test
    fun `a non data class impl with default factory uses toString`() {

      class Custom(
        name: String,
        age: Int
      ) : AbstractKase2<String, Int>(name, age)

      Custom("Bluey", 6).displayName shouldBe "AbstractKase2(a1=Bluey, a2=6)"
    }

    @Test
    fun `a data class impl with a custom factory uses the custom name`() {

      data class Custom(
        val name: String,
        val age: Int,
        private val displayNameFactory: KaseDisplayNameFactory<Kase2<String, Int>>
      ) : AbstractKase2<String, Int>(name, age, displayNameFactory)

      Custom("Bluey", 4) { "foo" }.displayName shouldBe "foo"
    }
  }
}
