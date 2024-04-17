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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Casts this [Any] instance to the desired type [T].
 *
 * @return This instance cast to type [T].
 * @throws ClassCastException if this instance is not of type [T].
 */
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated("no", ReplaceWith("(this as T)"))
@InternalStatikApi
public inline fun <T> Any.cast(): T = this as T

/**
 * Attempts to cast this instance to the specified type
 * [T], returning `null` if the cast is not possible.
 *
 * **Usage:**
 * ```
 * val number: Any = "Not a number"
 * val integer: Int? = number.safeAs<Int>() // integer will be null
 * ```
 *
 * @return This instance cast to type [T], or `null` if the cast is not possible.
 */
@Deprecated("no", ReplaceWith("(this as? T)"))
@InternalStatikApi
public inline fun <reified T : Any> Any?.safeAs(): T? = this as? T

/**
 * shorthand for `requireNotNull(this)`
 *
 * @throws IllegalArgumentException if receiver is null
 */
@OptIn(ExperimentalContracts::class)
@Suppress("NOTHING_TO_INLINE")
@InternalStatikApi
public inline fun <T : Any> T?.requireNotNull(): T {
  contract {
    returns() implies (this@requireNotNull != null)
  }
  return requireNotNull(this)
}

/**
 * shorthand for `requireNotNull(this, lazyMessage)`
 *
 * @throws IllegalArgumentException if receiver is null
 */
@OptIn(ExperimentalContracts::class)
@InternalStatikApi
public inline fun <T : Any> T?.requireNotNull(lazyMessage: () -> Any): T {
  contract {
    returns() implies (this@requireNotNull != null)
  }
  return requireNotNull(this, lazyMessage)
}

/**
 * A functional version of [kotlin.require]
 *
 * @param condition The lambda function that defines the condition.
 * @param lazyMessage The lambda function that generates the error message.
 * @return This instance, after passing the check.
 * @throws IllegalStateException if the condition is not met.
 */
@InternalStatikApi
public inline fun <T> T.require(condition: (T) -> Boolean, lazyMessage: (T) -> Any): T = apply {
  check(condition(this)) { lazyMessage(this) }
}

/**
 * A functional version of [kotlin.checkNotNull]
 *
 * @return This instance, guaranteed not to be `null`.
 * @throws IllegalStateException if this instance is `null`.
 */
@Suppress("NOTHING_TO_INLINE")
@InternalStatikApi
public inline fun <T : Any> T?.checkNotNull(): T = checkNotNull(this)

/**
 * A functional version of [kotlin.checkNotNull]
 *
 * @param lazyMessage The lambda function that generates the error message.
 * @return This instance, guaranteed not to be `null`.
 * @throws IllegalStateException if this instance is `null`.
 */
@InternalStatikApi
public inline fun <T : Any> T?.checkNotNull(
  lazyMessage: () -> Any
): T = checkNotNull(this, lazyMessage)

/**
 * A functional version of [kotlin.check]
 *
 * @param condition The lambda function that defines the condition.
 * @param lazyMessage The lambda function that generates the error message.
 * @return This instance, after passing the check.
 * @throws IllegalStateException if the condition is not met.
 */
@InternalStatikApi
public inline fun <T> T.check(condition: (T) -> Boolean, lazyMessage: (T) -> Any): T = apply {
  check(condition(this)) { lazyMessage(this) }
}
