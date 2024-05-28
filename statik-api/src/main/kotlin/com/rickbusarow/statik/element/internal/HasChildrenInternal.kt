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

package com.rickbusarow.statik.element.internal

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.element.HasChildren
import com.rickbusarow.statik.element.StatikElement
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazyDeferredImpl
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.LazySetComponent
import com.rickbusarow.statik.utils.lazy.asDataSource
import com.rickbusarow.statik.utils.lazy.dataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.sync.Mutex

@InternalStatikApi
public interface HasChildrenInternal : HasChildren {

  public fun <E : StatikElement?> child(dataSource: () -> E): Lazy<E>
  public fun <T : StatikElement> childDeferred(action: suspend () -> T): LazyDeferred<T>

  public fun <E : StatikElement> children(
    priority: LazySet.DataSource.Priority = LazySet.DataSource.Priority.MEDIUM,
    dataSource: suspend () -> Set<E>
  ): LazySet<E>

  // public class ChildBuilderScope(public val parent: HasChildrenInternal)
}

@InternalStatikApi
public open class HasChildrenInternalDelegate : HasChildrenInternal {

  private val _children: MutableList<LazySetComponent<StatikElement?>> = mutableListOf()

  override val children: Flow<StatikElement> by lazy {
    com.rickbusarow.statik.utils.lazy.lazySet(_children)
      .filterNotNull()
  }

  public final override fun <E : StatikElement?> child(
    dataSource: () -> E
  ): Lazy<E> = lazy(dataSource)
    .also { _children.add(it.asDataSource()) }

  public final override fun <E : StatikElement> children(
    priority: LazySet.DataSource.Priority,
    dataSource: suspend () -> Set<E>
  ): LazySet<E> = com.rickbusarow.statik.utils.lazy.lazySet(
    dataSource(
      priority = priority,
      factory = dataSource
    )
  )
    .also { _children.add(it) }

  public final override fun <T : StatikElement> childDeferred(
    action: suspend () -> T
  ): LazyDeferred<T> {

    return LazyDeferredImpl(
      action = action,
      lock = Mutex(false)
    )
      .also { _children.add(it) }
  }
}
