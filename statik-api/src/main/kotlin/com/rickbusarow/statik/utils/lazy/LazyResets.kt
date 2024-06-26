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

@file:Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction")

package com.rickbusarow.statik.utils.lazy

import com.rickbusarow.statik.InternalStatikApi
import kotlinx.coroutines.DisposableHandle
import org.jetbrains.kotlin.com.intellij.openapi.Disposable

@InternalStatikApi
public interface LazyResets<out T : Any> : Lazy<T>, Resets

@InternalStatikApi
public interface Resets {
  public fun reset()
}

@InternalStatikApi
public inline fun <reified T : Any> ResetManager.lazyResets(
  noinline valueFactory: () -> T
): LazyResets<T> = LazyResets(this, valueFactory)

@InternalStatikApi
public fun <T : Any> LazyResets(
  resetManager: ResetManager,
  valueFactory: () -> T
): LazyResets<T> =
  LazyResetsImpl(resetManager, valueFactory)

internal class LazyResetsImpl<out T : Any>(
  private val resetManager: ResetManager,
  private val valueFactory: () -> T
) : LazyResets<T> {

  private var lazyHolder: Lazy<T> = createLazy()

  override val value: T
    get() = lazyHolder.value

  override fun isInitialized(): Boolean = lazyHolder.isInitialized()

  private fun createLazy() = lazy {
    resetManager.register(this)
    valueFactory()
  }

  override fun reset() {
    lazyHolder = createLazy()
  }
}

@InternalStatikApi
public interface ResetManager : Resets, Disposable {
  public fun register(delegate: Resets)

  override fun dispose()

  override fun reset()
  public fun child(childDelegates: MutableCollection<Resets> = mutableListOf()): ResetManager

  public companion object {
    public operator fun invoke(): ResetManager = ResetManagerImpl()
  }
}

public object EmptyResetManager : ResetManager {
  override fun register(delegate: Resets): Unit = Unit
  override fun dispose(): Unit = Unit
  override fun reset(): Unit = Unit
  override fun child(childDelegates: MutableCollection<Resets>): EmptyResetManager = this
}

public class ResetManagerImpl(
  private val delegates: MutableCollection<Resets> = mutableListOf()
) : DisposableHandle, ResetManager {

  override fun register(delegate: Resets) {
    synchronized(delegates) {
      delegates.add(delegate)
    }
  }

  override fun dispose() {
    reset()
  }

  override fun reset() {
    synchronized(delegates) {
      delegates.forEach { it.reset() }
      delegates.clear()
    }
  }

  override fun child(childDelegates: MutableCollection<Resets>): ResetManagerImpl {
    return ResetManagerImpl(childDelegates)
      .also { child -> register(child) }
  }
}
