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

package com.rickbusarow.kase.utils

import com.rickbusarow.kase.Kase2
import com.rickbusarow.kase.ParamTestEnvironmentFactory
import com.rickbusarow.kase.TestEnvironment
import com.rickbusarow.kase.files.HasWorkingDir
import com.rickbusarow.kase.files.TestLocation

data class Dog(
  val name: String,
  val age: Int,
  override val displayName: String = "Dog(name: '$name' | age: $age)"
) : Kase2<String, Int> {
  override val a1: String get() = name
  override val a2: Int get() = age
}

class DogTestEnvironment(
  val dog: Dog,
  testParameterDisplayNames: List<String>,
  testLocation: TestLocation = TestLocation.get()
) : TestEnvironment,
  HasWorkingDir by HasWorkingDir.invoke(testParameterDisplayNames, testLocation) {

  class Factory : ParamTestEnvironmentFactory<Dog, DogTestEnvironment> {
    override fun create(
      params: Dog,
      names: List<String>,
      location: TestLocation
    ): DogTestEnvironment = DogTestEnvironment(
      dog = params,
      testParameterDisplayNames = names,
      testLocation = location
    )
  }
}
