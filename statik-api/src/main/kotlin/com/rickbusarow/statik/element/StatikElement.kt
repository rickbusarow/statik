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

package com.rickbusarow.statik.element

import kotlinx.coroutines.flow.Flow

/**
 * Base interface for all code elements parsed from source
 * files. This includes classes, functions, variables, etc.
 */
public interface StatikElement<NODE : Any> : HasChildren {

  /** The underlying AST's node representing the physical code element in the source code. */
  public val node: NODE

  public val text: String

  /** The file that contains this element. */
  public val containingFile: StatikFile<*>
}

public interface HasChildren {

  /** The direct children elements of this element. */
  public val children: Flow<StatikElement<*>>
  // get() = flowOf()
}

/** Represents an element with a parent element. */
public interface StatikElementWithParent<out PARENT : StatikElement<*>, NODE : Any> : StatikElement<NODE> {
  /** The parent element */
  public val parent: PARENT
}

// MyStatikElement
// StatikElement<Foo>

/**
 * Generates a sequence of parent elements.
 *
 * @receiver An element with a parent.
 * @return A sequence of parent elements.
 */
public fun StatikElementWithParent<*, *>.parents(): Sequence<StatikElement<*>> {

  return generateSequence<StatikElement<*>>(this) { element ->

    (element as? StatikElementWithParent<*, *>)?.parent
  }
}
