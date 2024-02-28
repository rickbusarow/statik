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

import com.rickbusarow.statik.forAllBlocking
import com.rickbusarow.statik.name.SimpleName.Companion.SIMPLE_NAME_REGEX
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern
import org.junit.jupiter.api.Test

internal class SimpleNameTest {

  @Test
  fun `any valid name string just becomes wrapped by the class`() {

    Arb.stringPattern(SIMPLE_NAME_REGEX.pattern)
      .forAllBlocking { name ->

        shouldNotThrow<Throwable> {
          SimpleName(name)
        }
      }
  }

  @Test
  fun `a name with whitespaces is allowed if wrapped in backticks`() {

    val name = "`a name with whitespaces is allowed if wrapped in backticks`"

    shouldNotThrow<Throwable> {
      SimpleName(name).asString shouldBe name
    }
  }

  @Test
  fun `a name with a white space throws exception with message`() {

    Arb.stringPattern("""\s+""")
      .forAllBlocking { name ->

        shouldThrowWithMessage<IllegalArgumentException>(
          "SimpleName names must be valid Java identifier " +
            "without a dot qualifier.  This name was: `$name`"
        ) {
          SimpleName(name)
        }
      }
  }

  @Test
  fun `a name with a dot throws exception with message`() {

    Arb.stringPattern("""\.+""")
      .forAllBlocking { name ->

        shouldThrowWithMessage<IllegalArgumentException>(
          "SimpleName names must be valid Java identifier " +
            "without a dot qualifier.  This name was: `$name`"
        ) {
          SimpleName(name)
        }
      }
  }

  @Test
  fun `an empty name throws exception with message`() {

    val t = shouldThrow<IllegalArgumentException> {
      SimpleName("")
    }

    t.message shouldBe "SimpleName names must be valid Java identifier " +
      "without a dot qualifier.  This name was: ``"
  }

  @Test
  fun `a blank name throws exception with message`() {

    Arb.stringPattern("\\s*")
      .forAllBlocking { name ->

        shouldThrowWithMessage<IllegalArgumentException>(
          "SimpleName names must be valid Java identifier " +
            "without a dot qualifier.  This name was: `$name`"
        ) {
          SimpleName(name)
        }
      }
  }

  @Test
  fun `an empty package name becomes DEFAULT`() {

    PackageName("") shouldBe PackageName.DEFAULT
  }

  @Test
  fun `a blank package name becomes DEFAULT`() {

    Arb.stringPattern("\\s*")
      .forAllBlocking { name ->

        PackageName(name) shouldBe PackageName.DEFAULT
      }
  }
}
