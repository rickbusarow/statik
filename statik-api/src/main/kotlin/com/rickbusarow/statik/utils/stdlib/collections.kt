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

package com.rickbusarow.statik.utils.stdlib

import com.rickbusarow.statik.InternalStatikApi

/**
 * Computes the Cartesian product of a list of lists.
 *
 * Given a list of `n` lists `[L1, L2, ..., Ln]`, each containing `mi` elements,
 * this function returns a list containing the Cartesian product. The resulting
 * list will have `product(m1 * m2 * ... * mn)` elements. Each kaseParam is a
 * list formed by taking one kaseParam from each `Li` in the order they appear.
 *
 * ### Mathematical Representation
 *
 * Given the lists:
 * ```
 * L1:  a
 *      b
 *
 * L2:  1
 *      2
 *      3
 * ```
 *
 * The Cartesian product `L1 x L2` is:
 * ```
 *      a, 1
 *      a, 2
 *      a, 3
 *      b, 1
 *      b, 2
 *      b, 3
 * ```
 *
 * ### Example
 *
 * ```kotlin
 * val list1 = listOf("a", "b")
 * val list2 = listOf(1, 2, 3)
 * val input = listOf(list1, list2)
 *
 * val result = input.cartesianProduct()
 *
 * // result will be: [["a", 1], ["a", 2], ["a", 3], ["b", 1], ["b", 2], ["b", 3]]
 * ```
 *
 * @return List of lists containing the Cartesian product.
 */
@InternalStatikApi
internal fun <T> Iterable<Iterable<T>>.cartesianProduct(): List<List<T>> {
  return fold(listOf(emptyList())) { acc, list ->
    acc.flatMap { existingList ->
      list.map { element ->
        existingList + element
      }
    }
  }
}

/** shorthand for `values.flatten().distinct()` */
@InternalStatikApi
internal fun <K : Any, T : Any> Map<K, Collection<T>>.allValues(): List<T> {
  return values.flatten().distinct()
}

/**
 * Returns a string representation of the collection based on its size.
 *
 * @param empty A function that generates a string for when the collection is empty.
 * @param single A function that generates a string for when the collection contains
 *   a single element. The single element is passed as an argument to this function.
 * @param moreThanOne A function that generates a string for when the collection contains
 *   more than one element. The entire collection is passed as an argument to this function.
 * @return A string representation of the collection.
 */
@InternalStatikApi
internal fun <E> Collection<E>.pluralString(
  empty: () -> String,
  single: (E) -> String,
  moreThanOne: (Iterable<E>) -> String
): String = when (size) {
  0 -> empty()
  1 -> single(single())
  else -> moreThanOne(this)
}

/**
 * Finds the indices of all elements in the receiver collection that match the given predicate.
 *
 * @param predicate A function that determines if an
 *   element's index should be included in the output list.
 * @receiver The collection to be searched.
 * @return A list containing the indices of all elements that match the predicate.
 */
@InternalStatikApi
internal inline fun <E> Collection<E>.indicesOf(predicate: (E) -> Boolean): List<Int> {
  return buildList {
    for ((index, e) in this@indicesOf.withIndex()) {
      if (predicate(e)) {
        add(index)
      }
    }
  }
}
