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

package com.rickbusarow.statik.utils.coroutines

import com.rickbusarow.statik.InternalStatikApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

/** @return true if at least one element matches the given predicate com.rickbusarow.statik .name */
@InternalStatikApi
public suspend fun <T> Flow<T>.any(predicate: suspend (T) -> Boolean): Boolean {
  val matching = firstOrNull(predicate)

  return matching != null
}

/**
 * @return a [Flow] containing only distinct elements from the receiver
 *   flow. When there are equal elements in the receiver, the first value
 *   is the one emitted in the returned flow. com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T> Flow<T>.distinct(): Flow<T> = flow {
  val past = mutableSetOf<T>()
  collect {
    if (past.add(it)) emit(it)
  }
}

/**
 * @return true if the receiver [Flow] contains [element],
 *   otherwise false. com.rickbusarow.statik .name*/
@InternalStatikApi
public suspend fun <T> Flow<T>.contains(element: T): Boolean {
  return any { it == element }
}

/**
 * A slightly optimized version of `flatMapConcat {...}.toList()`
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
public suspend fun <T, R> Flow<T>.flatMapListConcat(
  destination: MutableList<R> = mutableListOf(),
  transform: suspend (T) -> Iterable<R>
): List<R> {
  return fold(destination) { acc, value ->
    acc.apply { addAll(transform(value)) }
  }
}

/**
 * A slightly optimized version of `flatMapConcat {...}.toList()`
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
public suspend fun <T> Flow<Iterable<T>>.flatMapListConcat(
  destination: MutableList<T> = mutableListOf()
): List<T> {
  return fold(destination) { acc, iterable ->
    acc.apply { addAll(iterable) }
  }
}

/**
 * A slightly optimized version of `flatMapConcat {...}.toSet()`
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
public suspend fun <T, R> Flow<T>.flatMapSetConcat(
  destination: MutableSet<R> = mutableSetOf(),
  transform: suspend (T) -> Iterable<R>
): Set<R> {
  return fold(destination) { acc, value ->
    acc.also {
      when (val iterable = transform(value)) {
        is Set<*> -> it.addAll(iterable)
        else -> it.addAll(iterable.toSet())
      }
    }
  }
}

/**
 * A slightly optimized version of `flatMapConcat {...}.toSet()`
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
public suspend fun <T> Flow<Iterable<T>>.flatMapSetConcat(
  destination: MutableSet<T> = mutableSetOf()
): Set<T> {
  return fold(destination) { acc, iterable ->
    acc.also {
      when (iterable) {
        is Set<*> -> it.addAll(iterable)
        else -> it.addAll(iterable.toSet())
      }
    }
  }
}

/**
 * Returns a [Flow] from the receiver [Flow], performing [transform]
 * upon each element *concurrently* before that element is emitted.
 *
 * **This is a "hot" flow**, since [transform] is performed eagerly.
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T, R> Flow<T>.mapAsync(transform: suspend (T) -> R): Flow<R> {
  return channelFlow {
    this@mapAsync.collect {
      launch { send(transform(it)) }
    }
  }
}

/**
 * Shorthand for `mapAsync(transform).flatMapSetConcat { it.toSet() }`
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
public suspend fun <T, R> Iterable<T>.flatMapSetMerge(
  transform: suspend (T) -> Iterable<R>
): Set<R> {
  return mapAsync(transform).flatMapSetConcat { it.toSet() }
}

/**
 * Shorthand for `mapAsync(transform).toList().flatten()`
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
public suspend fun <T, R> Iterable<T>.flatMapListMerge(
  transform: suspend (T) -> Iterable<R>
): List<R> {
  return mapAsync(transform).toList().flatten()
}

/**
 * Shorthand for `mapAsync(transform).toList().flatten()`
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
public suspend fun <T, R> Flow<T>.flatMapListMerge(
  transform: suspend (T) -> Iterable<R>
): List<R> {
  return mapAsync(transform).toList().flatten()
}

/**
 * Returns a [Flow] from the receiver [Iterable], performing [transform]
 * upon each element *concurrently* before that element is emitted.
 *
 * **This is a "hot" flow**, since [transform] is performed eagerly.
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T, R> Iterable<T>.mapAsync(transform: suspend (T) -> R): Flow<R> {

  return channelFlow {
    forEach {
      launch { send(transform(it)) }
    }
  }
}

/**
 * Returns a [Flow] from the receiver [Iterable], performing [action]
 * upon each element *concurrently* before that element is emitted.
 *
 * **This is a "hot" flow**, since [action] is performed eagerly.
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T> Iterable<T>.onEachAsync(action: suspend (T) -> Unit): Flow<T> {

  return channelFlow {
    forEach {
      launch {
        action(it)
        send(it)
      }
    }
  }
}

/**
 * **This is a "hot" flow**, since [transform] is performed eagerly.
 *
 * @return a [Flow] from the receiver [Sequence], performing [transform] upon each
 *   element *concurrently* before that element is emitted. com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T, R> Sequence<T>.mapAsync(transform: suspend (T) -> R): Flow<R> {
  return channelFlow {
    forEach {
      launch { send(transform(it)) }
    }
  }
}

/**
 * **This is a "hot" flow**, since [transform] is performed eagerly.
 *
 * @return a [Flow] from the receiver [Flow], performing [transform] and filtering
 *   out `null` values upon each element *concurrently*. com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T, R : Any> Flow<T>.mapAsyncNotNull(transform: suspend (T) -> R?): Flow<R> {
  return channelFlow {
    this@mapAsyncNotNull.onEach { element -> transform(element)?.let { send(it) } }
      .launchIn(this)
  }
}

/**
 * **This is a "hot" flow**, since [transform] is performed eagerly.
 *
 * @return a [Flow] from the receiver [Iterable], performing [transform] and filtering
 *   out `null` values upon each element *concurrently*. com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T, R : Any> Iterable<T>.mapAsyncNotNull(transform: suspend (T) -> R?): Flow<R> {
  return channelFlow {
    forEach { element ->
      launch { transform(element)?.let { send(it) } }
    }
  }
}

/**
 * **This is a "hot" flow**, since [transform] is performed eagerly.
 *
 * @return a [Flow] from the receiver [Sequence], performing [transform] and filtering
 *   out `null` values upon each element *concurrently*. com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T, R : Any> Sequence<T>.mapAsyncNotNull(transform: suspend (T) -> R?): Flow<R> {
  return channelFlow {
    forEach { element ->
      launch { transform(element)?.let { send(it) } }
    }
  }
}

/**
 * **This is a "hot" flow**, since [predicate] is performed eagerly.
 *
 * @return a [Flow] from the receiver [Flow], filtering values based
 *   upon [predicate] *concurrently*. com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T> Flow<T>.filterAsync(predicate: suspend (T) -> Boolean): Flow<T> {
  return channelFlow {
    this@filterAsync.onEach { if (predicate(it)) send(it) }
      .launchIn(this)
  }
}

/**
 * **This is a "hot" flow**, since [predicate] is performed eagerly.
 *
 * @return a [Flow] from the receiver [Iterable], filtering values
 *   based upon [predicate] *concurrently*. com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T> Iterable<T>.filterAsync(predicate: suspend (T) -> Boolean): Flow<T> {
  return channelFlow {
    forEach { launch { if (predicate(it)) send(it) } }
  }
}

/**
 * **This is a "hot" flow**, since [predicate] is performed eagerly.
 *
 * @return a [Flow] from the receiver [Sequence], filtering values
 *   based upon [predicate] *concurrently*. com.rickbusarow.statik .name*/
@InternalStatikApi
public fun <T> Sequence<T>.filterAsync(predicate: suspend (T) -> Boolean): Flow<T> {
  return channelFlow {
    forEach { launch { if (predicate(it)) send(it) } }
  }
}

/**
 * Shorthand for `onCompletion { if (it == null) emitAll(other) }`
 *
 * ```
 * val mySingleFlow = someFlow + someOtherFlow
 * ```
 */
@InternalStatikApi
public operator fun <T> Flow<T>.plus(other: Flow<T>): Flow<T> {
  return onCompletion { if (it == null) emitAll(other) }
}
