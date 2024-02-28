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

import com.rickbusarow.kase.KaseTestFactoryTest.CustomTestEnvironment
import com.rickbusarow.kase.files.HasWorkingDir.Companion.baseWorkingDir
import com.rickbusarow.kase.files.HasWorkingDir.Companion.cleanStringForFileSystem
import com.rickbusarow.kase.files.TestLocation
import com.rickbusarow.kase.files.enclosingClassesSimpleNames
import com.rickbusarow.kase.stdlib.dropView
import com.rickbusarow.kase.utils.ParamTypes.A
import com.rickbusarow.kase.utils.ParamTypes.B
import com.rickbusarow.kase.utils.ParamTypes.C
import com.rickbusarow.kase.utils.ParamTypes.D
import com.rickbusarow.kase.utils.currentMethodName
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import java.io.File
import java.util.stream.Stream

internal class KaseTestFactoryTest : KaseTestFactory<Kase2<A, B>, CustomTestEnvironment, CustomTestEnvironment.Factory> {
  override val params: List<Kase2<A, B>>
    get() = kases(
      listOf(A("a1"), A("a2")),
      listOf(B("b1"), B("b2"))
    )

  override val testEnvironmentFactory = CustomTestEnvironment.Factory()

  val Any.classNames
    get() = this::class.java.enclosingClassesSimpleNames()
      .toList()

  val cs: List<Kase1<C>> = kases(listOf(C("c1"), C("c2")))
  val ds: List<Kase1<D>> = kases(listOf(D("d1"), D("d2")))

  @TestFactory
  fun `scoping for multiplied streams`(): Stream<out DynamicNode> {
    val base = baseWorkingDir()

    val functionDir = file(classNames)
      .resolve(cleanStringForFileSystem(currentMethodName()))

    return params.asContainers { k1 ->

      cs.asTests(testEnvironmentFactory) { k2 ->

        val path = functionDir
          .resolve(cleanStringForFileSystem(k1.displayName))
          .resolve(cleanStringForFileSystem(k2.displayName))

        workingDir.relativeTo(base) shouldBe path
      }
    }
  }

  @TestFactory
  fun `scoping nested asTests`(): Stream<out DynamicNode> {
    val base = baseWorkingDir()

    val functionDir = file(classNames)
      .resolve(cleanStringForFileSystem(currentMethodName()))

    return cs.asContainers { k1 ->

      params.asTests { k2 ->

        val path = functionDir
          .resolve(cleanStringForFileSystem(k1.displayName))
          .resolve(cleanStringForFileSystem(k2.displayName))

        workingDir.relativeTo(base) shouldBe path
      }
    }
  }

  @TestFactory
  fun `scoping nested testFactory`(): Stream<out DynamicNode> {
    val base = baseWorkingDir()

    val functionDir = file(classNames)
      .resolve(cleanStringForFileSystem(currentMethodName()))

    return cs.asContainers { k1 ->

      testFactory { k2 ->

        val path = functionDir
          .resolve(cleanStringForFileSystem(k1.displayName))
          .resolve(cleanStringForFileSystem(k2.displayName))

        workingDir.relativeTo(base) shouldBe path
      }
    }
  }

  @TestFactory
  fun `multiple layers of containers results in multiple parent directories`(): Stream<out DynamicNode> {
    val base = baseWorkingDir()

    val functionDir = file(classNames)
      .resolve(cleanStringForFileSystem(currentMethodName()))

    return ds.asContainers { k1 ->

      cs.asContainers { k2 ->

        params.asTests { k3 ->

          val path = functionDir
            .resolve(cleanStringForFileSystem(k1.displayName))
            .resolve(cleanStringForFileSystem(k2.displayName))
            .resolve(cleanStringForFileSystem(k3.displayName))

          workingDir.relativeTo(base) shouldBe path
        }
      }
    }
  }

  @Nested
  inner class `nested tests` {

    @TestFactory
    fun `scoping nested asTests with custom environment`(): Stream<out DynamicNode> {

      val base = baseWorkingDir()

      val functionDir = file(this@`nested tests`.classNames)
        .resolve(cleanStringForFileSystem(currentMethodName()))

      return cs.asContainers { k1 ->

        params.asTests { k2 ->

          val path = functionDir
            .resolve(cleanStringForFileSystem(k1.displayName))
            .resolve(cleanStringForFileSystem(k2.displayName))

          workingDir.relativeTo(base) shouldBe path
        }
      }
    }
  }

  class CustomTestEnvironment(
    testParameterDisplayNames: List<String>,
    testLocation: TestLocation
  ) : DefaultTestEnvironment(testParameterDisplayNames, testLocation) {
    class Factory : NoParamTestEnvironmentFactory<CustomTestEnvironment> {
      override fun create(names: List<String>, location: TestLocation): CustomTestEnvironment =
        CustomTestEnvironment(
          testParameterDisplayNames = names,
          testLocation = location
        )
    }
  }

  fun file(parts: List<String>): File = parts
    .map(::cleanStringForFileSystem)
    .dropView(1)
    .fold(File(parts.first())) { acc, s -> acc.resolve(s) }
}
