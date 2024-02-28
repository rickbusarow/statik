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

import com.rickbusarow.kase.Kase1
import com.rickbusarow.kase.asTests
import com.rickbusarow.kase.kase
import com.rickbusarow.kase.stdlib.remove
import com.rickbusarow.kase.stdlib.toStringPretty
import io.kotest.assertions.asClue
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.nio.file.Paths
import java.util.stream.Stream

private fun coords() = CoordinatesTestClass()
private fun coords2() = CoordinatesTestClass(Unit)

class TestLocationTest {

  private inline val factoryKases: List<Kase1<() -> TestLocation?>>
    get() = listOf(
      kase(displayName = "primary constructor initialization") {
        coords().primaryConstructorCoords
      },
      kase(displayName = "secondary constructor") {
        coords2().secondaryConstructorCoords
      },
      kase(displayName = "set inside init block") {
        coords().initBlockCoords
      },
      kase(displayName = "eager property initialization") {
        coords().eagerPropertyCoords
      },
      kase(displayName = "lazy property initialization") {
        coords().lazyPropertyCoords
      },
      kase(displayName = "getter delegate property initialization") {
        coords().getterDelegatePropertyCoords
      },
      kase(displayName = "function setter block") {
        coords().apply { setterFunction() }.functionSetterBlockCoords
      },
      kase(displayName = "coordinates from a non-inline function") {
        coords().coordsFromFunction()
      },
      kase(displayName = "coordinates from an inline function") {
        coords().coordsFromInlineFunction()
      }
    )

  @Test
  fun `test annotation in block body`() {

    val expected = TestLocation.get()

    val testClass = coords()

    testClass.primaryConstructorCoords shouldBe expected
    testClass.lazyPropertyCoords shouldBe expected
    testClass.getterDelegatePropertyCoords shouldBe expected
    testClass.initBlockCoords shouldBe expected
    testClass.eagerPropertyCoords shouldBe expected

    testClass.setterFunction()
    testClass.functionSetterBlockCoords shouldBe expected

    testClass.coordsFromFunction() shouldBe expected
    testClass.coordsFromInlineFunction() shouldBe expected

    coords2().secondaryConstructorCoords shouldBe expected
  }

  @Test
  fun `test annotation in a run expression body`(): Unit = run {

    val expected = TestLocation.get()

    val testClass = coords()

    testClass.primaryConstructorCoords shouldBe expected
    testClass.lazyPropertyCoords shouldBe expected
    testClass.getterDelegatePropertyCoords shouldBe expected
    testClass.initBlockCoords shouldBe expected
    testClass.eagerPropertyCoords shouldBe expected

    testClass.setterFunction()
    testClass.functionSetterBlockCoords shouldBe expected

    testClass.coordsFromFunction() shouldBe expected
    testClass.coordsFromInlineFunction() shouldBe expected

    coords2().secondaryConstructorCoords shouldBe expected
  }

  @Suppress("JUnitMalformedDeclaration", "JUnitMixedFramework")
  @org.junit.Test
  private fun `has a junit4 annotation`() = TestLocation.get()

  @Test
  fun `junit 4 test annotation`() {

    `has a junit4 annotation`() shouldBe expectedLocation("has a junit4 annotation")
  }

  @Nested
  inner class `test annotation in a nested class` {

    @Test
    fun `test annotation in a block body`() {

      val expected = TestLocation.get()

      val testClass = coords()

      testClass.primaryConstructorCoords shouldBe expected
      testClass.lazyPropertyCoords shouldBe expected
      testClass.getterDelegatePropertyCoords shouldBe expected
      testClass.initBlockCoords shouldBe expected
      testClass.eagerPropertyCoords shouldBe expected

      testClass.setterFunction()
      testClass.functionSetterBlockCoords shouldBe expected

      testClass.coordsFromFunction() shouldBe expected
      testClass.coordsFromInlineFunction() shouldBe expected

      coords2().secondaryConstructorCoords shouldBe expected
    }

    @Test
    fun `test annotation in a run expression body`() = run {

      val expected = TestLocation.get()

      val testClass = coords()

      testClass.primaryConstructorCoords shouldBe expected
      testClass.lazyPropertyCoords shouldBe expected
      testClass.getterDelegatePropertyCoords shouldBe expected
      testClass.initBlockCoords shouldBe expected
      testClass.eagerPropertyCoords shouldBe expected

      testClass.setterFunction()
      testClass.functionSetterBlockCoords shouldBe expected

      testClass.coordsFromFunction() shouldBe expected
      testClass.coordsFromInlineFunction() shouldBe expected

      coords2().secondaryConstructorCoords shouldBe expected
    }
  }

  @TestFactory
  fun `testFactory annotation in an expression syntax`(): Stream<out DynamicNode> =
    factoryKases.asTests { (coordsFactory) ->

      coordsFactory() shouldBe expectedLocation(
        "testFactory annotation in an expression syntax"
      )
    }

  @TestFactory
  fun `testFactory annotation inside a block body`(): Stream<out DynamicNode> {
    val expected = TestLocation.get()
    return factoryKases.asTests { (coordsFactory) ->
      coordsFactory() shouldBe expected
    }
  }

  fun expectedLocation(functionName: String): TestLocation {
    val clazz = TestLocationTest::class.java
    return TestLocation(
      fileName = "${clazz.simpleName}.kt",
      // line numbers don't matter since they're excluded from comparison
      lineNumber = 1,
      packageName = clazz.packageName(),
      declaringClass = clazz,
      declaringClassWithoutSynthetics = clazz,
      declaringClassSimpleNames = listOf(clazz.simpleName),
      callingFunctionSimpleName = functionName
    )
  }

  @Nested
  inner class `testFactory annotations in a nested class` {

    @TestFactory
    fun `testFactory annotation in an expression syntax`(): Stream<out DynamicNode> =
      factoryKases.asTests { (coordsFactory) ->

        coordsFactory() shouldBe expectedCoordsNested(
          "testFactory annotation in an expression syntax"
        )
      }

    @TestFactory
    fun `testFactory annotation inside a block body`(): Stream<out DynamicNode> {
      val expected = TestLocation.get()
      return factoryKases.asTests { (coordsFactory) ->
        coordsFactory() shouldBe expected
      }
    }

    fun expectedCoordsNested(functionName: String): TestLocation {
      val clazz =
        `testFactory annotations in a nested class`::class.java
      return TestLocation(
        fileName = "${clazz.enclosingClass!!.simpleName}.kt",
        // line numbers don't matter since they're excluded from comparison
        lineNumber = 0,
        packageName = clazz.packageName(),
        declaringClass = clazz,
        declaringClassWithoutSynthetics = clazz,
        declaringClassSimpleNames = clazz.simpleNames(),
        callingFunctionSimpleName = functionName
      )
    }
  }

  @Nested
  inner class `sanity checks` {

    @Test
    fun `testStackTraceElementOrNull for this function`() {

      val stackTrace = Thread.currentThread().stackTrace
      val thisElement = stackTrace[1]

      // check that things haven't moved before doing the real assertions
      thisElement.methodName shouldBe "testStackTraceElementOrNull for this function"
      thisElement.clazz().simpleBinaryName() shouldBe "sanity checks"

      thisElement.className shouldBe
        "com.rickbusarow.kase.files.TestLocationTest\$sanity checks"
      thisElement.testStackTraceElementOrNull() shouldBe thisElement
    }

    @Test
    fun `simple names`() {

      val className =
        Class.forName("com.rickbusarow.kase.files.TestLocationTest\$factoryKases\$1")

      className.simpleBinaryName() shouldBe "factoryKases"
      className.simpleName shouldBe "factoryKases\$1"

      className.enclosingClass!!.simpleBinaryName() shouldBe "TestLocationTest"
      className.enclosingClass!!.simpleName shouldBe "TestLocationTest"
    }

    @Test
    fun `segments`() {

      val className =
        Class.forName("com.rickbusarow.kase.files.TestLocationTest\$factoryKases\$1")

      className.segments() shouldBe listOf(
        "com",
        "rickbusarow",
        "kase",
        "files",
        "TestLocationTest",
        "factoryKases",
        "1"
      )

      className.enclosingClass!!.segments() shouldBe listOf(
        "com",
        "rickbusarow",
        "kase",
        "files",
        "TestLocationTest"
      )
    }

    @Test
    fun `test uri for Test annotation`() {

      val userDir = Paths.get("").toAbsolutePath().toFile()
        .invariantSeparatorsPath

      val rawUri = TestLocation.get().testUriOrNull?.toString()

      rawUri.shouldNotBeNull()

      // line numbers change too easily for a literal assertion
      val uriWithoutLineNumber = rawUri.remove("\\?line=\\d+$".toRegex())

      """
        |--
        |  userDir: $userDir
        |   rawUri: $rawUri
        |--
      """.trimMargin().asClue {

        uriWithoutLineNumber shouldEndWith
          "$userDir/src/test/kotlin/com/rickbusarow/kase/files/TestLocationTest.kt"

        val lineNumber = requireNotNull(rawUri.substringAfterLast("line=").toInt())

        lineNumber shouldBeGreaterThan 0
      }
    }
  }

  infix fun StackTraceElement?.shouldBe(other: StackTraceElement?) {

    assertSoftly {
      if (this == null) {
        if (other == null) {
          return
        }
        fail("expected $other but was null")
      }

      "lineNumber".asClue { lineNumber shouldBe other?.lineNumber }
      "className".asClue { className shouldBe other?.className }
      "methodName".asClue { methodName shouldBe other?.methodName }
      "fileName".asClue { fileName shouldBe other?.fileName }
    }
  }

  infix fun TestLocation?.shouldBe(other: TestLocation) {
    val reg = " {2}lineNumber=\\d+,\\n".toRegex()

    toStringPretty().remove(reg) shouldBe other.toStringPretty().remove(reg)
  }

  private fun Class<*>.packageName(): String = `package`.name
}
