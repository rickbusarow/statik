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

package com.rickbusarow.statik.stdlib

/**
 * Returns a list of all elements sorted according to the specified [selectors].
 *
 * The sort is _stable_. It means that equal elements
 * preserve their order relative to each other after sorting.
 */
fun <T> Iterable<T>.sortedWith(vararg selectors: (T) -> Comparable<*>): List<T> {
  if (this is Collection) {
    if (size <= 1) return this.toList()
    @Suppress("UNCHECKED_CAST")
    return (toTypedArray<Any?>() as Array<T>).apply { sortWith(compareBy(*selectors)) }.asList()
  }
  return toMutableList().apply { sortWith(compareBy(*selectors)) }
}

/**
 * Returns a list of all elements sorted according to the specified [selectors].
 *
 * The sort is _stable_. It means that equal elements
 * preserve their order relative to each other after sorting.
 */
fun <T> Iterable<T>.sortedWithDescending(vararg selectors: (T) -> Comparable<*>): List<T> {
  return sortedWith(*selectors).reversed()
}

/**
 * @return true if any element in [other] is contained
 *   within the receiver collection, otherwise returns false
 */
fun <E> Iterable<E>.containsAny(other: Iterable<Any?>): Boolean {

  return when {
    this === other -> true
    this is Set<E> && other is Set<*> -> {
      intersect(other.asSet()).isNotEmpty()
    }

    else -> {
      val thisAsSet = asSet()
      other.any { thisAsSet.contains(it) }
    }
  }
}

/**
 * shorthand for `this as? Set<E> ?: toSet()`
 *
 * @return itself if the receiver [Iterable] is already a
 *   `Set<E>`, otherwise calls `toSet()` to create a new one
 */
fun <E> Iterable<E>.asSet(): Set<E> = this as? Set<E> ?: toSet()

/**
 * shorthand for `this as? List<E> ?: toList()`
 *
 * @return itself if the receiver [Iterable] is already a
 *   `List<E>`, otherwise calls `toList()` to create a new one
 */
fun <E> Iterable<E>.asList(): List<E> = this as? List<E> ?: toList()

/**
 * shorthand for `this as? Collection<E> ?: toList()`
 *
 * @return itself if the receiver [Iterable] is already a
 *   `Collection<E>`, otherwise calls `toList()` to create a new one
 */
fun <E> Iterable<E>.asCollection(): Collection<E> = this as? Collection<E> ?: toList()

/**
 * Returns a list containing the elements from the receiver iterable up to and
 * including the first element for which the given predicate returns false.
 *
 * @param predicate A function that determines if an element should be included in the output list.
 * @receiver The iterable to be processed.
 * @return A list containing the elements from the receiver iterable up to
 *   and including the first element for which the predicate returns false.
 */
inline fun <E> Iterable<E>.takeWhileInclusive(predicate: (E) -> Boolean): List<E> {
  return buildList {
    for (e in this@takeWhileInclusive) {
      add(e)
      if (!predicate(e)) break
    }
  }
}

/**
 * This function iterates over the receiver iterable and adds each element
 * to the current chunk. Whenever the selector function returns true for an
 * element, that element concludes the current chunk and a new chunk is started.
 *
 * @param selector A function that determines when to
 *   conclude the current chunk and start a new one.
 * @receiver The iterable to be split into chunks.
 * @return A list of chunks, where each chunk is a list of elements from the receiver iterable.
 */
fun <E> Iterable<E>.chunkedBy(selector: (E) -> Boolean): List<List<E>> {
  return fold(mutableListOf<MutableList<E>>(mutableListOf())) { acc, e ->
    acc.last().add(e)
    acc.alsoIf(selector(e)) {
      it.add(mutableListOf())
    }
  }
}
