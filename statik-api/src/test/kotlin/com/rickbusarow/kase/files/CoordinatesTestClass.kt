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

@Suppress("UNUSED_PARAMETER", "NOTHING_TO_INLINE")
internal class CoordinatesTestClass(
  val primaryConstructorCoords: TestLocation = TestLocation.get()
) {

  val lazyPropertyCoords by lazy { TestLocation.get() }
  val getterDelegatePropertyCoords
    get() = TestLocation.get()

  var secondaryConstructorCoords: TestLocation? = null
  var initBlockCoords: TestLocation? = null
  var functionSetterBlockCoords: TestLocation? = null

  init {
    initBlockCoords = TestLocation.get()
  }

  constructor(uniqueParameter: Unit) : this() {
    secondaryConstructorCoords = TestLocation.get()
  }

  val eagerPropertyCoords = TestLocation.get()

  fun setterFunction() {
    functionSetterBlockCoords = TestLocation.get()
  }

  fun coordsFromFunction() = TestLocation.get()

  inline fun coordsFromInlineFunction() = TestLocation.get()
}
