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

package com.rickbusarow.statik.utils.lazy

import com.rickbusarow.statik.InternalStatikApi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Lazily invokes some the first time [await][Deferred.await]
 * or [join][kotlinx.coroutines.Job.join] is called.
 *
 * This action is only invoked once. The caller's coroutine is used to execute this action.
 *
 * ```
 * val expensive = lazyDeferred { repository.getAll() }
 *
 * suspend fun getExpensive() = expensive.await()
 * ```
 */
@InternalStatikApi
public interface LazyDeferred<out T> : LazySetComponent<T> {
  public val isCompleted: Boolean
  public suspend fun await(): T
  public suspend operator fun invoke(): T = await()

  /**
   * Immediately returns the deferred value **if already completed**.
   *
   * This should only be called after manually checking the value of [isCompleted].
   *
   * @throws [IllegalStateException] if this deferred value has not [completed][isCompleted] yet.
   */
  public fun getCompleted(): T
}

/**
 * Lazily invokes [action] the first time [await][Deferred.await]
 * or [join][kotlinx.coroutines.Job.join] is called.
 *
 * This [action] is only invoked once. The caller's coroutine is used to execute this action.
 *
 * ```
 * val expensive = lazyDeferred { repository.getAll() }
 *
 * suspend fun getExpensive() = expensive.await()
 * ```
 */
@InternalStatikApi
public fun <T> lazyDeferred(action: suspend () -> T): LazyDeferred<T> {

  return LazyDeferredImpl(
    action = action,
    lock = Mutex(false)
  )
}

@InternalStatikApi
public fun <T, R> LazyDeferred<T>.map(transform: suspend (T) -> R): LazyDeferred<R> {
  return lazyDeferred { transform(await()) }
}

@InternalStatikApi
public fun <T> (suspend () -> T).asLazyDeferred(): LazyDeferred<T> = lazyDeferred(this)

@InternalStatikApi
public suspend fun <T> Collection<LazyDeferred<T>>.awaitAll(): List<T> {
  return if (isEmpty()) {
    emptyList()
  } else {
    coroutineScope {
      map { it.await() }
    }
  }
}

internal class LazyDeferredImpl<T>(
  private val action: suspend () -> T,
  private val lock: Mutex
) : LazyDeferred<T> {

  @Volatile
  private var _completed = false

  @Suppress("ktlint:standard:backing-property-naming")
  private var _value: T? = null

  override val isCompleted: Boolean get() = _completed

  override suspend fun await(): T {
    if (!_completed) {
      lock.withLock {
        // re-check for completion inside the lock to avoid races
        if (!_completed) {
          _value = action.invoke()
          _completed = true
        }
      }
    }
    @Suppress("UNCHECKED_CAST")
    return _value as T
  }

  override fun getCompleted(): T {
    check(_completed) { "LazyDeferred has not been completed yet." }
    @Suppress("UNCHECKED_CAST")
    return _value as T
  }
}
