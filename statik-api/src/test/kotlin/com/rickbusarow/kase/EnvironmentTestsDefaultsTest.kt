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

import com.rickbusarow.kase.files.HasWorkingDir
import com.rickbusarow.kase.files.HasWorkingDir.Companion.cleanStringForFileSystem
import com.rickbusarow.kase.files.TestLocation
import com.rickbusarow.kase.stdlib.dropView
import com.rickbusarow.kase.utils.Cat
import com.rickbusarow.kase.utils.CatTestEnvironment
import com.rickbusarow.kase.utils.Dog
import com.rickbusarow.kase.utils.DogTestEnvironment
import com.rickbusarow.kase.utils.DogTestEnvironment.Factory
import com.rickbusarow.kase.utils.allNodes
import com.rickbusarow.kase.utils.asList
import com.rickbusarow.kase.utils.currentMethodName
import com.rickbusarow.kase.utils.userDir
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.File
import java.util.stream.Stream

internal class EnvironmentTestsDefaultsTest : EnvironmentTests<Dog, DogTestEnvironment, Factory> {

  override val testEnvironmentFactory = Factory()

  val className = this::class.simpleName!!

  val dogs = listOf(
    Dog("Eddie", 1),
    Dog("Clifford", 2),
    Dog("Lassie", 3)
  )

  val cats = listOf(
    Cat("Felix", 1),
    Cat("Garfield", 2),
    Cat("Sylvester", 3)
  )

  val testFactoryKases = listOf(
    DogTestKase("HasParams extension", dogs.size) { environments, a, b ->
      val hasDogs = object : HasParams<Dog> {
        override val params: List<Dog>
          get() = dogs
      }
      hasDogs.testFactory { dog ->
        environments[kase(a, b, dog)] = this
      }
    },
    DogTestKase(
      displayName = "vararg with two params",
      dogCount = 2
    ) { environments, a, b ->
      testFactory(dogs.first(), dogs[1]) { dog ->
        environments[kase(a, b, dog)] = this
      }
    },
    DogTestKase(
      displayName = "vararg with multiple params",
      dogCount = dogs.size
    ) { environments, a, b ->
      testFactory(dogs.first(), dogs[1], *dogs.dropView(2).toTypedArray()) { dog ->
        environments[kase(a, b, dog)] = this
      }
    },
    DogTestKase(
      displayName = "list params",
      dogCount = dogs.size
    ) { environments, a, b ->
      testFactory(dogs) { dog ->
        environments[kase(a, b, dog)] = this
      }
    },
    DogTestKase(
      displayName = "sequence params",
      dogCount = dogs.size
    ) { environments, a, b ->
      testFactory(dogs.asSequence()) { dog ->
        environments[kase(a, b, dog)] = this
      }
    }
  )

  @Test
  fun `test without a factory argument creates a single test with that factory`() {

    var environment: DogTestEnvironment? = null

    test(dogs.first()) {
      environment = this
    }

    environment.shouldNotBeNull()

    environment?.dog shouldBe dogs.first()

    val base = HasWorkingDir.baseWorkingDir()

    val functionDir = File(className)
      .resolve(cleanStringForFileSystem(currentMethodName()))

    environment?.workingDir?.relativeTo(base) shouldBe functionDir
  }

  @Test
  fun `test with a factory argument for an ad-hoc type creates a single test with that factory`() {

    var environment: CatTestEnvironment? = null

    test(cats.first(), testEnvironmentFactory = CatTestEnvironment.Factory()) {
      environment = this
    }

    environment.shouldNotBeNull()

    environment?.cat shouldBe cats.first()

    val base = HasWorkingDir.baseWorkingDir()

    val functionDir = File(className)
      .resolve(cleanStringForFileSystem(currentMethodName()))

    environment?.workingDir?.relativeTo(base) shouldBe functionDir
  }

  @Test
  fun `top-level HasParams testFactory extension`() {

    val base = HasWorkingDir.baseWorkingDir()

    val functionDir = File(className)
      .resolve(cleanStringForFileSystem(currentMethodName()))

    val hasDogs = object : HasParams<Dog> {
      override val params: List<Dog>
        get() = dogs
    }

    val environments = mutableMapOf<Dog, DogTestEnvironment>()

    // This stacktrace must be immediately above the testFactory call,
    // so that its line number is only off by one.
    val thisStackTrace = TestLocation.testStackTraceElement()
    val testNodes = hasDogs.testFactory { dog ->
      environments[dog] = this
    }
      .asList()
      .mapIndexed { i, node ->
        // The tests haven't executed yet, so we have to do it manually.
        (node as DynamicTest).executable.execute()
        dogs[i] to node
      }
      .toMap()

    testNodes shouldHaveSize 3

    for (dog in dogs) {
      val environment = environments[dog]
      environment.shouldNotBeNull()

      environment.dog shouldBe dog

      val expectedDir = functionDir.resolve(cleanStringForFileSystem(dog.displayName))

      environment.workingDir.relativeTo(base) shouldBe expectedDir

      val node = testNodes[dog]
      node.shouldNotBeNull()

      node.displayName shouldBe dog.displayName

      node.testSourceUri.get().query shouldBe "line=${thisStackTrace.lineNumber + 1}"
    }
  }

  @TestFactory
  fun `multi-layer testFactory`() = testFactory(testFactoryKases) { kase ->
    val (dogCount, dogTestFactory) = kase
    val kaseDisplayName = kase.displayName

    val ints = listOf(1, 2)
    val chars = listOf('a', 'b')

    val testCount = dogCount * ints.size * chars.size

    val userDir = userDir()
    val kaseDir = HasWorkingDir.baseWorkingDir()

    val functionDir = File(className)
      .resolve(cleanStringForFileSystem(currentMethodName()))

    val environments = mutableMapOf<Kase3<Int, Char, Dog>, DogTestEnvironment>()

    val thisStackTrace = TestLocation.testStackTraceElement()
    val nodes = ints.asContainers({ "${kaseDisplayName}_$it" }) ac1@{ a ->
      chars.asContainers ac2@{ b ->
        dogTestFactory.run { this@ac2.invoke(environments = environments, a = a, b = b) }
      }
    }
      .allNodes()
      .filterIsInstance<DynamicTest>()
      // We have to execute the tests manually since they're not normal test functions.
      .onEach { it.executable.execute() }
      .toList()

    val allKeys = kases(ints, chars, dogs.take(dogCount))

    allKeys shouldHaveSize testCount
    nodes shouldHaveSize testCount

    for ((node, key) in nodes.zip(allKeys)) {
      val environment = environments[key]
      environment.shouldNotBeNull()

      environment.dog shouldBe key.a3

      val expectedDir = functionDir
        .resolve(cleanStringForFileSystem("${kaseDisplayName}_${key.a1}"))
        .resolve(cleanStringForFileSystem(key.a2.toString()))
        .resolve(cleanStringForFileSystem(key.a3.displayName))

      environment.workingDir.relativeTo(kaseDir) shouldBe expectedDir

      node.displayName shouldBe key.a3.displayName

      val line = thisStackTrace.lineNumber + 2

      node.testSourceUri.get().query shouldBe "line=$line"

      val uri = node.testSourceUri.get()

      val relativeUriPath = File(uri.path).relativeTo(userDir)

      relativeUriPath shouldBe File("src/test/kotlin/com/rickbusarow/kase")
        .resolve(thisStackTrace.fileName!!)
    }
  }

  class DogTestKase(
    override val displayName: String,
    val dogCount: Int,
    val testFactory: MultiLayerDogTests
  ) : Kase2<Int, MultiLayerDogTests> by kase(dogCount, testFactory) {
    override fun component1() = dogCount
    override fun component2() = testFactory
  }

  fun interface MultiLayerDogTests {
    operator fun EnvironmentTestNodeBuilder<Dog, DogTestEnvironment, Factory>.invoke(
      environments: MutableMap<Kase3<Int, Char, Dog>, DogTestEnvironment>,
      a: Int,
      b: Char
    ): Stream<out DynamicNode>
  }
}
