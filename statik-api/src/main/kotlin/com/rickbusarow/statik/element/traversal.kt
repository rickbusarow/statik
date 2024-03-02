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

package com.rickbusarow.statik.element

import com.rickbusarow.statik.utils.coroutines.distinct
import com.rickbusarow.statik.utils.coroutines.plus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
public fun StatikElement.childrenRecursive(): Flow<StatikElement> {
  return flowOf(this)
    .plus(children.flatMapConcat(StatikElement::childrenRecursive))
    .distinct()
}

public inline fun <reified E : StatikElement> StatikElement.childrenOfTypeRecursive(): Flow<E> {
  return childrenRecursive().filterIsInstance()
}
