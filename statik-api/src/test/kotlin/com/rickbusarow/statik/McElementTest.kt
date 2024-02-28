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
import com.rickbusarow.statik.element.McElement
import com.rickbusarow.statik.element.McElementWithParent
import com.rickbusarow.statik.element.McFile
import com.rickbusarow.statik.element.McJavaElement
import com.rickbusarow.statik.element.McKtDeclaredElement
import com.rickbusarow.statik.element.McKtElement
import io.kotest.assertions.asClue
import io.kotest.matchers.reflection.shouldBeOfType
import io.kotest.matchers.reflection.shouldBeSubtypeOf
import io.kotest.matchers.reflection.shouldHaveMemberProperty
import io.kotest.matchers.reflection.shouldNotHaveMemberProperty
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream
import kotlin.reflect.full.isSubclassOf

class McElementTest {

  @TestFactory
  fun `every McElement type except files should have a 'parent' property`(): Stream<out DynamicNode> {

    // The language-specific element types are technically subclasses,
    // but also can't have parents.
    val excluded = setOf(
      McJavaElement::class,
      McKtElement::class,
      McKtDeclaredElement::class
    )

    return McElement::class.sealedSubclassesRecursive()
      .filterNot { it in excluded }
      .asTests({ sub ->
        val packageName = sub.java.`package`.name
        sub.qualifiedName.toString().removePrefix("$packageName.")
      }) { sub ->

        when {
          sub.isSubclassOf(McFile::class) -> {
            sub shouldNotHaveMemberProperty "parent"
          }

          sub.isSubclassOf(McJavaElement::class) ->
            "java element types should have java parents".asClue {
              sub.shouldBeSubtypeOf<McElementWithParent<McJavaElement>>()
              sub.shouldHaveMemberProperty("parent") { property ->
                property.returnType.shouldBeOfType<McJavaElement>()
              }
            }

          sub.isSubclassOf(McKtElement::class) ->
            "kotlin element types should have kotlin parents".asClue {
              sub.shouldBeSubtypeOf<McElementWithParent<McKtElement>>()
              sub.shouldHaveMemberProperty("parent") { property ->
                property.returnType.shouldBeOfType<McKtElement>()
              }
            }

          else ->
            "base element types should have agnostic parents".asClue {
              sub.shouldBeSubtypeOf<McElementWithParent<McElement>>()
              sub.shouldHaveMemberProperty("parent") { property ->
                property.returnType.shouldBeOfType<McElement>()
              }
            }
        }
      }
  }
}
