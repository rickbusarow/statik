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

import com.rickbusarow.kase.asTests
import com.rickbusarow.statik.name.StatikLanguage.JAVA
import com.rickbusarow.statik.name.StatikLanguage.KOTLIN
import com.rickbusarow.statik.name.StatikLanguage.XML
import com.rickbusarow.statik.testing.internal.BaseStatikNameTest
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class ReferenceNameTest : BaseStatikNameTest() {

  @Nested
  inner class `java reference` {

    val subject = ReferenceName("com.test.Subject", JAVA)

    @TestFactory
    fun `equals matches other reference types with the same name and language`() =
      allReferenceNames("com.test.Subject", languages = listOf(JAVA))
        .asTests { (other) ->

          other shouldBe subject
          subject shouldBe other
        }

    @TestFactory
    fun `equals does not match any reference types with a different language`() =
      allReferenceNames("com.test.Subject", languages = listOf(XML, KOTLIN))
        .asTests { (other) ->

          other shouldNotBe subject
          subject shouldNotBe other
        }

    @TestFactory
    fun `equals does not match any reference types with a different name`() =
      allReferenceNames("com.test.Other", languages = listOf(JAVA))
        .asTests { (other) ->

          other shouldNotBe subject
          subject shouldNotBe other
        }
  }

  @Test
  fun `duplicate names of different languages are allowed in a set`() {
    val list = listOf(
      ReferenceName("name", KOTLIN),
      ReferenceName("name", JAVA),
      ReferenceName("name", XML)
    )

    val set = list.toSet()

    set.toList() shouldBe list
  }

  @Test
  fun `Android R reference equals Android R declaration`() {

    val r = AndroidRReferenceName(PackageName("com.test"), KOTLIN)
    val d = AndroidResourceDeclaredName.r(PackageName("com.test"))

    r shouldBe d

    setOf<StatikName>(d).contains(r) shouldBe true
    setOf<StatikName>(r).contains(d) shouldBe true

    d shouldBe r
  }
}
