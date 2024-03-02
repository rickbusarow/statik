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

package com.rickbusarow.statik

import com.rickbusarow.kase.asTests
import com.rickbusarow.statik.element.StatikElement
import com.rickbusarow.statik.element.StatikElementWithParent
import com.rickbusarow.statik.element.StatikFile
import com.rickbusarow.statik.element.java.StatikJavaElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinDeclaredElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinElement
import com.rickbusarow.statik.utils.stdlib.simpleNamesConcat
import io.kotest.matchers.reflection.shouldBeOfType
import io.kotest.matchers.reflection.shouldBeSubtypeOf
import io.kotest.matchers.reflection.shouldHaveMemberProperty
import io.kotest.matchers.reflection.shouldNotHaveMemberProperty
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import kotlin.reflect.full.isSubclassOf

class StatikElementTest {

  @Nested
  inner class `parent property declarations` {

    // The language-specific element types are technically subclasses,
    // but also can't have parents.
    val excluded = setOf(
      StatikJavaElement::class,
      StatikKotlinElement::class,
      StatikKotlinDeclaredElement::class
    )

    val subs = StatikElement::class.subclassesRecursive()
      .filterNot { it in excluded }

    val files = subs.filter { it.isSubclassOf(StatikFile::class) }.toSet()
    val javaElements = subs.filter { it.isSubclassOf(StatikJavaElement::class) && it !in files }
    val ktElements = subs.filter { it.isSubclassOf(StatikKotlinElement::class) && it !in files }

    val covered = files + javaElements + ktElements

    val other = subs.filterNot { it in covered }

    @TestFactory
    fun `file types do not have parents`() = files.asTests({ it.simpleNamesConcat }) {
      it shouldNotHaveMemberProperty "parent"
    }

    @TestFactory
    fun `java element types should have java parents`() =
      javaElements.asTests({ it.simpleNamesConcat }) {
        it.shouldBeSubtypeOf<StatikElementWithParent<StatikJavaElement>>()
        it.shouldHaveMemberProperty("parent") { property ->
          property.returnType.shouldBeOfType<StatikJavaElement>()
        }
      }

    @TestFactory
    fun `kotlin element types should have kotlin parents`() =
      ktElements.asTests({ it.simpleNamesConcat }) {
        it.shouldBeSubtypeOf<StatikElementWithParent<StatikKotlinElement>>()
        it.shouldHaveMemberProperty("parent") { property ->
          property.returnType.shouldBeOfType<StatikKotlinElement>()
        }
      }

    @TestFactory
    fun `base element types should have agnostic parents`() =
      other.asTests({ it.simpleNamesConcat }) {
        it.shouldBeSubtypeOf<StatikElementWithParent<StatikElement>>()
        it.shouldHaveMemberProperty("parent") { property ->
          property.returnType.shouldBeOfType<StatikElement>()
        }
      }
  }
}
