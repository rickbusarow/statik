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
 * Wraps a given item in a set.
 *
 * This is a functional style shorthand for `setOf(this)`,
 * allowing a simpler syntax for creating a set from a single item.
 *
 * @receiver The item to be wrapped in a set.
 * @return A set containing only the receiver item.
 */
@InternalStatikApi
public fun <T> T.singletonSet(): Set<T> = setOf(this)

/**
 * Filters the receiver iterable and adds the matching elements to a set.
 *
 * This is a shorthand for `filterTo(destination, predicate)` where destination is a set.
 *
 * @param destination The destination set where the elements that match
 *   the predicate are placed. By default, it is an empty mutable set.
 * @param predicate A function that determines if an item should be included in the output set.
 * @receiver The iterable to be filtered.
 * @return A set containing elements from the receiver iterable that match the predicate.
 */
@InternalStatikApi
public inline fun <T> Iterable<T>.filterToSet(
  destination: MutableSet<T> = mutableSetOf(),
  predicate: (T) -> Boolean
): Set<T> {
  return filterTo(destination, predicate)
}

/**
 * Transforms the elements of the receiver collection and adds the results to a set.
 *
 * @param destination The destination set where the transformed
 *   elements are placed. By default, it is an empty mutable set.
 * @param transform A function that maps elements of the receiver collection to the output set.
 * @receiver The collection to be transformed.
 * @return A set containing the transformed elements from the receiver collection.
 */
@InternalStatikApi
public inline fun <C : Collection<T>, T, R> C.mapToSet(
  destination: MutableSet<R> = mutableSetOf(),
  transform: (T) -> R
): Set<R> {
  return mapTo(destination, transform)
}

/**
 * Transforms the elements of the receiver collection and adds the results to a set.
 *
 * @param destination The destination set where the transformed
 *   elements are placed. By default, it is an empty mutable set.
 * @param transform A function that maps elements of the receiver collection to the output set.
 * @receiver The collection to be transformed.
 * @return A set containing the transformed elements from the receiver collection.
 */
@InternalStatikApi
public inline fun <T, R> Array<T>.mapToSet(
  destination: MutableSet<R> = mutableSetOf(),
  transform: (T) -> R
): Set<R> {
  return mapTo(destination, transform)
}

/**
 * Transforms each element of the receiver iterable to an
 * iterable and flattens these iterables into a single set.
 *
 * @param destination The destination set where the transformed
 *   elements are placed. By default, it is an empty mutable set.
 * @param transform A function that maps elements of the
 *   receiver iterable to an iterable of output elements.
 * @receiver The iterable to be transformed.
 * @return A set containing the flattened transformed elements from the receiver iterable.
 */
@InternalStatikApi
public inline fun <T, R> Iterable<T>.flatMapToSet(
  destination: MutableSet<R> = mutableSetOf(),
  transform: (T) -> Iterable<R>
): Set<R> {
  return flatMapTo(destination, transform)
}

/**
 * Transforms each element of the receiver iterable to an
 * iterable and flattens these iterables into a single set.
 *
 * @param destination The destination set where the transformed
 *   elements are placed. By default, it is an empty mutable set.
 * @param transform A function that maps elements of the
 *   receiver iterable to an iterable of output elements.
 * @receiver The sequence to be transformed.
 * @return A set containing the flattened transformed elements from the receiver iterable.
 */
@InternalStatikApi
public inline fun <T, R> Sequence<T>.flatMapToSet(
  destination: MutableSet<R> = mutableSetOf(),
  transform: (T) -> Iterable<R>
): Set<R> {
  return flatMapTo(destination, transform)
}
