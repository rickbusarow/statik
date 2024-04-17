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
import com.rickbusarow.statik.utils.lazy.LazySet.DataSource
import com.rickbusarow.statik.utils.lazy.LazySet.DataSource.Priority
import com.rickbusarow.statik.utils.lazy.LazySet.DataSource.Priority.MEDIUM
import com.rickbusarow.statik.utils.lazy.internal.DataSourceImpl
import com.rickbusarow.statik.utils.lazy.internal.LazySetImpl
import com.rickbusarow.statik.utils.stdlib.flatMapToSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toSet

@InternalStatikApi
public interface LazySet<out E> : Flow<E>, LazySetComponent<E> {

  public val isFullyCached: Boolean

  public suspend fun contains(element: Any?): Boolean

  /** @return true if the two LazySets have any element in common, otherwise false */
  public suspend fun containsAny(other: LazySet<Any?>): Boolean

  public suspend fun isEmpty(): Boolean
  public suspend fun isNotEmpty(): Boolean

  public fun snapshot(): State<E>

  /**
   * A **Lazy** data source for a [LazySet], which performs some
   * suspending operation [get] to incrementally add data to the `LazySet`.
   */
  @InternalStatikApi
  public interface DataSource<out E> :
    Comparable<DataSource<*>>,
    LazySetComponent<E>,
    LazyDeferred<Set<E>> {

    /**
     * The priority which should be applied to this source while
     * in a LazySet. Higher priority sources are invoked first.
     */
    public val priority: Priority

    /** Called to retrieve this source's data. Implementations are thread-safe and lazy. */
    public suspend fun get(): Set<E>

    public enum class Priority : Comparable<Priority> {
      HIGH,
      MEDIUM,
      LOW
    }

    override fun compareTo(other: DataSource<*>): Int {
      return priority.compareTo(other.priority)
    }
  }

  public class State<out E>(
    public val cache: Set<E>,
    public val remaining: List<DataSource<E>>
  ) {
    private val remainingMap by lazy {
      remaining.groupBy { it.priority }
    }

    public fun nextSources(): Sequence<List<DataSource<E>>> {
      return sequence {
        Priority.values()
          .forEach { priority ->

            val sources = remainingMap[priority]

            if (sources != null) {
              yield(sources)
            }
          }
      }
    }
  }
}

@InternalStatikApi
public suspend inline fun <reified T : E, E> LazySet<E>.getOrNull(element: E): T? {
  return takeIf { it.contains(element) }
    ?.filterIsInstance<T>()
    ?.firstOrNull { it == element }
}

@InternalStatikApi
public sealed interface LazySetComponent<out E>

@InternalStatikApi
public suspend fun <T : B, E : B, B> LazySet<T>.containsAny(elements: Collection<E>): Boolean {
  return elements.any { contains(it) }
}

@InternalStatikApi
public fun <E> Flow<E>.asDataSource(priority: Priority = MEDIUM): DataSource<E> =
  dataSource(priority) { toSet() }

@InternalStatikApi
public fun <E> LazyDeferred<Set<E>>.asDataSource(priority: Priority = MEDIUM): DataSource<E> =
  dataSource(priority) { await() }

@InternalStatikApi
public fun <E> Lazy<Set<E>>.asDataSource(priority: Priority = MEDIUM): DataSource<E> =
  dataSource(priority) { value }

@InternalStatikApi
public fun <E> dataSourceOf(vararg elements: E, priority: Priority = MEDIUM): DataSource<E> =
  DataSourceImpl(priority, lazyDeferred { elements.toSet() })

/** @return A DataSource<E> from this [priority] and [factory] */
@InternalStatikApi
public fun <E> dataSource(
  priority: Priority = MEDIUM,
  factory: LazyDeferred<Set<E>>
): DataSource<E> =
  DataSourceImpl(priority, factory)

/** @return A DataSource<E> from this [priority] and [factory] */
@InternalStatikApi
public fun <E> dataSource(
  priority: Priority = MEDIUM,
  factory: suspend () -> Set<E>
): DataSource<E> {
  return DataSourceImpl(priority, lazyDeferred { factory() })
}

@InternalStatikApi
public fun <E> Collection<LazySetComponent<E>>.toLazySet(): LazySet<E> = lazySet(this)

@InternalStatikApi
public fun <E> lazySet(vararg children: LazySetComponent<E>): LazySet<E> {
  return lazySet(children.asList())
}

@InternalStatikApi
public fun <E> lazySet(
  priority: Priority = MEDIUM,
  dataSource: suspend () -> Set<E>
): LazySet<E> {
  return lazySet(dataSource(priority, dataSource))
}

@JvmName("lazySetSingle")
@InternalStatikApi
public fun <E> lazySet(priority: Priority = MEDIUM, dataSource: suspend () -> E): LazySet<E> {
  return lazySet(dataSource(priority) { setOf(dataSource()) })
}

@InternalStatikApi
public fun <E> Flow<E>.toLazySet(priority: Priority = MEDIUM): LazySet<E> {
  return lazySet(priority) { toSet() }
}

@InternalStatikApi
public fun <E> lazySet(children: Collection<LazySetComponent<E>>): LazySet<E> {
  val (sets, dataSources) = children.partition { it is LazySet<*> }
  @Suppress("UNCHECKED_CAST")
  return createLazySet(
    sets as List<LazySet<E>>,
    dataSources as List<DataSource<E>>
  )
}

@InternalStatikApi
public fun <E> emptyLazySet(): LazySet<E> {
  return createLazySet(emptyList(), emptyList())
}

internal fun <E> createLazySet(
  children: Collection<LazySet<E>>,
  sources: Collection<DataSource<E>>
): LazySet<E> {
  val childCaches = children
    .map { child -> child.snapshot() }

  val cache = childCaches.flatMapToSet { it.cache }
  val remaining = childCaches.flatMapToSet { it.remaining }
    .plus(sources)
    .sortedByDescending { it.priority }

  return LazySetImpl(cache, remaining)
}
