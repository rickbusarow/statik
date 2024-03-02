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
  HasVisibility,
  StatikAnnotated

/** A property element. */
public interface StatikProperty<out PARENT : StatikElement> :
  StatikCallable<PARENT>,
  StatikDeclaredElement<PARENT> {

  /** The type name of the property. */
  public val typeReferenceName: LazyDeferred<ReferenceName>

  /**  */
  public val isMutable: Boolean
}

/** A parameter element. */
public interface StatikParameter<out PARENT : StatikElement> :
  StatikCallable<PARENT>,
  StatikElement {
  /** The index of the parameter. */
  public val index: Int
}

/** A function element. */
public interface StatikFunction<out PARENT : StatikElement> :
  StatikCallable<PARENT>,
  StatikHasTypeParameters<PARENT> {

  /** The value parameters of the function. */
  public val parameters: LazySet<StatikParameter<*>>

  /** Local variables declared in the function body. */
  public val properties: LazySet<StatikProperty<*>>

  /** The return type of the function. */
  public val returnType: LazyDeferred<ReferenceName>
}
