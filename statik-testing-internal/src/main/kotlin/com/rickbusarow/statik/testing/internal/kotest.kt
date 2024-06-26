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

package com.rickbusarow.statik.testing.internal

import io.kotest.common.ExperimentalKotest
import io.kotest.property.Gen
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.forAll
import kotlinx.coroutines.runBlocking

/** shorthand for the Kotest `forAll`, but blocking and always returning Unit */
fun <A> forAllBlocking(genA: Gen<A>, property: suspend PropertyContext.(A) -> Unit) {
  runBlocking<Unit> {
    @OptIn(ExperimentalKotest::class)
    forAll(PropTestConfig(), genA) { arg ->
      property.invoke(this, arg)
      true
    }
  }
}

/** shorthand for the Kotest `forAll`, but blocking and always returning Unit */
@JvmName("forAllBlockingExtension")
fun <A> Gen<A>.forAllBlocking(property: suspend PropertyContext.(A) -> Unit) {
  forAllBlocking(genA = this) { arg ->
    property.invoke(this, arg)
  }
}
