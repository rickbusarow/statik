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

import com.rickbusarow.kase.files.TestLocation
import com.rickbusarow.kase.utils.allNodes
import com.rickbusarow.kase.utils.allTests
import com.rickbusarow.kase.utils.asList
import com.rickbusarow.kase.utils.names
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import java.util.stream.Stream

class TestNodeBuilderTest {

  @Test
  fun `asTests creates single test node`() {
    val dynamicNodesStream = listOf("Test1").asTests { }

    val dynamicNodes = dynamicNodesStream.asList()
    dynamicNodes.size shouldBe 1

    val testNode = dynamicNodes.single()
    testNode.shouldBeInstanceOf<DynamicTest>()
    testNode.displayName shouldBe "Test1"
  }

  @Test
  fun `testFactory creates multiple test nodes`() {
    val dynamicNodesStream = listOf("Test1", "Test2").asTests {}

    val dynamicNodes = dynamicNodesStream.asList()
    dynamicNodes shouldHaveSize 2

    dynamicNodes.shouldForAll { it.shouldBeInstanceOf<DynamicTest>() }

    val testNames = dynamicNodes.names()
    testNames shouldContainExactly listOf("Test1", "Test2")
  }

  @Test
  fun `asContainers creates a dynamic container`() {
    val dynamicNodesStream = listOf("Container1")
      .asContainers {
        listOf("Test3").asTests {}
      }

    val dynamicNodes = dynamicNodesStream.asList()
    dynamicNodes shouldHaveSize 1

    val container = dynamicNodes.first()
    container.shouldBeInstanceOf<DynamicContainer>()
    container.displayName shouldBe "Container1"

    val containerChildren = container.children.asList()
    containerChildren shouldHaveSize 1

    val testNode = containerChildren.first()
    testNode.shouldBeInstanceOf<DynamicTest>()
    testNode.displayName shouldBe "Test3"
  }

  @Test
  fun `TestNodeBuilder asTests creates dynamic tests`() {
    val elements = listOf("Element1", "Element2")

    val dynamicNodesStream = elements.asTests({ "Test $it" }) {}

    val dynamicNodes = dynamicNodesStream.asList()
    dynamicNodes shouldHaveSize 2

    val testNames = dynamicNodes.map { it.displayName }
    testNames shouldContainExactly listOf("Test Element1", "Test Element2")
  }

  @Test
  fun `TestNodeBuilder asContainers creates dynamic containers`() {
    val elements = listOf("Element1", "Element2")

    val dynamicNodesStream = elements.asContainers({ "Container $it" }) {
      listOf("Test $it").asTests { }
    }

    val dynamicNodes = dynamicNodesStream.asList()
    dynamicNodes shouldHaveSize 2

    val containerNames = dynamicNodes.map { it.displayName }
    containerNames shouldContainExactly listOf("Container Element1", "Container Element2")
  }

  @Test
  fun `Iterable asTests extension creates dynamic tests`() {
    val elements = listOf("Element1", "Element2")

    val dynamicNodesStream = kases(elements) { "[e: $a1]" }.asTests { }

    val dynamicNodes = dynamicNodesStream.asList()
    dynamicNodes shouldHaveSize 2

    val testNames = dynamicNodes.map { it.displayName }
    testNames shouldContainExactly listOf("[e: Element1]", "[e: Element2]")
  }

  @Test
  fun `iterable asContainers adds items as containers`() {
    val items = kases(listOf("Item1", "Item2"))
    val dynamicNodes = items.asContainers(testAction = { Stream.empty() }).asList()

    dynamicNodes.names() shouldBe listOf("a1: Item1", "a1: Item2")
  }

  @Test
  fun `iterable asTests adds items as tests`() {
    val items = kases(listOf("Item1", "Item2"))
    val dynamicNodes = items.asTests(testAction = { }).asList()

    dynamicNodes.names() shouldBe listOf("a1: Item1", "a1: Item2")
  }

  @Test
  fun `leaf node has stackFrame from root node`() {

    var invoked = false

    val thisStackTrace = TestLocation.testStackTraceElement()
    val dynamicNodes = listOf("Container").asContainers {
      listOf("Test").asTests {

        // The expected stackTraceElement is from one line before the call to `testFactory { }`,
        // so the line number in the string will be different.
        // Parse out the line number from the end of the line, add one,
        // and update the line with the new value.
        // That should match the stackFrame created in the DSL.
        val expected = thisStackTrace.lineNumber + 1

        testLocation.lineNumber shouldBe expected
        invoked = true
      }
    }
      .allNodes()

    dynamicNodes.allTests().single().executable.execute()

    invoked shouldBe true

    dynamicNodes.names() shouldBe listOf("Container", "Test")
  }
}
