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

import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.StatikName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet

/**
 * Parameters and arguments, for values or types.
 *
 * Does **not** include member properties.
 *
 * ```
 *           |       type          |       value
 * parameter | StatikTypeParameter | StatikValueParameter
 * argument  | StatikTypeArgument  | StatikValueArgument
 *
 * ```
 */
public sealed interface StatikVariable<out PARENT : StatikElement> : StatikElementWithParent<PARENT> {
  /** The index of the parameter. */
  public val index: Int
}

public sealed interface StatikTypeVariable<out PARENT : StatikElement> :
  StatikVariable<PARENT>,
  StatikType<PARENT>

public sealed interface StatikValueVariable<out PARENT : StatikElement> :
  StatikVariable<PARENT>,
  StatikCallable<PARENT> {
  public val name: StatikName

  @Deprecated("renamed to returnType", ReplaceWith("returnType"))
  public val type: LazyDeferred<ReferenceName>
    get() = returnType
}

public sealed interface StatikParameter<out PARENT : StatikElement> : StatikVariable<PARENT>
public sealed interface StatikArgument<out PARENT : StatikElement> : StatikVariable<PARENT>

/** Represents a generic type used as a parameter, like `<T>` or `<R: Any>`. */
public interface StatikTypeParameter<out PARENT : StatikElement> :
  StatikParameter<PARENT>,
  StatikTypeVariable<PARENT>

/**
 * Represents a value parameter, like `x: Int` in `fun foo(x: Int)` or `class MyClass(val x: Int)`.
 */
public interface StatikValueParameter<out PARENT : StatikElement> :
  StatikParameter<PARENT>,
  StatikValueVariable<PARENT>

/** Represents a generic type used as a parameter, like `<T>` or `<R: Any>`. */
public interface StatikTypeArgument<out PARENT : StatikElement> :
  StatikArgument<PARENT>,
  StatikTypeVariable<PARENT>

/**
 * Represents a value parameter, like `x: Int` in `fun foo(x: Int)` or `class MyClass(val x: Int)`.
 */
public interface StatikValueArgument<out PARENT : StatikElement> :
  StatikParameter<PARENT>,
  StatikValueVariable<PARENT>

/** Represents an element with type parameters. */
public interface StatikHasTypeParameters<out PARENT : StatikElement> : StatikElementWithParent<PARENT> {

  /** The type parameters of this element. */
  public val typeParameters: LazySet<StatikTypeParameter<*>>
}

/** Represents an element with type parameters. */
public interface StatikHasValueParameters<out PARENT : StatikElement> : StatikElementWithParent<PARENT> {

  /** The type parameters of this element. */
  public val valueParameters: LazySet<StatikValueParameter<*>>
}
