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
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet

/** A callable element. */
public interface StatikCallable<out PARENT : StatikElement> :
  StatikElement,
  StatikElementWithParent<PARENT>,
  StatikAnnotated {

  /**
   * The resolved type of this callable.
   *
   * This is the declared/public type for a variable,
   * or the declared return type in a function signature.
   *
   * If the type isn't specified, then it will be inferred.
   */
  public val returnType: LazyDeferred<ReferenceName>

  /**
   * The resolved type of this callable.
   *
   * This is the declared/public type for a variable,
   * or the declared return type in a function signature.
   *
   * This value is `null` if the type isn't specified.
   */
  public val returnTypeDeclaration: StatikTypeReference<StatikCallable<PARENT>>?
}

/** A property element. */
public interface StatikProperty<out PARENT : StatikElement> :
  StatikCallable<PARENT>,
  StatikDeclaredElement<PARENT> {

  override val returnTypeDeclaration: StatikTypeReference<StatikProperty<PARENT>>?

  /**  */
  public val isMutable: Boolean
}

/** A function element. */
public interface StatikFunction<out PARENT : StatikElement> :
  StatikCallable<PARENT>,
  StatikHasValueParameters<PARENT>,
  StatikHasTypeParameters<PARENT> {

  /** Local variables declared in the function body. */
  public val properties: LazySet<StatikProperty<*>>

  override val returnTypeDeclaration: StatikTypeReference<StatikFunction<PARENT>>?
}
