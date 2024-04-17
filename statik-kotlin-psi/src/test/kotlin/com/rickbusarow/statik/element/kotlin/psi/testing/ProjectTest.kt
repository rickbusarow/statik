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

package com.rickbusarow.statik.element.kotlin.psi.testing

import com.rickbusarow.kase.EnvironmentTests
import com.rickbusarow.kase.TestEnvironment
import com.rickbusarow.kase.TestEnvironmentFactory
import com.rickbusarow.kase.files.TestLocation
import com.rickbusarow.statik.element.kotlin.psi.testing.PsiTestEnvironment.Factory
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Base test class for tests related to projects. Provides
 * useful utility functions for setting up project-related tests.
 */
abstract class ProjectTest : EnvironmentTests<Any, PsiTestEnvironment, Factory> {

  override fun <PARAM, ENV : TestEnvironment> test(
    param: PARAM,
    testEnvironmentFactory: TestEnvironmentFactory<PARAM, ENV>,
    names: List<String>,
    testLocation: TestLocation,
    testAction: suspend ENV.() -> Unit
  ) {
    super.test(
      param = param,
      testEnvironmentFactory = testEnvironmentFactory,
      names = names,
      testLocation = testLocation
    ) {

      (this as? PsiTestEnvironment)
        ?.messageCollector
        ?.await()
        ?.let { mc ->

          withClue({ mc.renderAll() }) {
            mc.hasErrors() shouldBe false
          }
        }
      testAction()
    }
  }
}
