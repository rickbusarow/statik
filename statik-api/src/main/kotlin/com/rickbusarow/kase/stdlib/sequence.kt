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

package com.rickbusarow.kase.stdlib

/**
 * Returns a sequence containing all elements of original sequence
 * and then all elements of the given [elements] collection.
 *
 * Note that the source sequence and the collection being added are iterated
 * only when an `iterator` is requested from the resulting sequence. Changing
 * any of them between successive calls to `iterator` may affect the result.
 *
 * The operation is _intermediate_ and _stateless_.
 *
 * @since 0.7.0
 */
internal fun <E> Sequence<E>.plus(vararg elements: E): Sequence<E> {
  return sequenceOf(this, elements.asSequence()).flatten()
}
