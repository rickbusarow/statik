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

/** Represents an annotated element. */
public interface StatikAnnotated {

  /** The annotations of this element. */
  public val annotations: LazySet<StatikAnnotation<*>>
}

/** Represents an annotation. */
public interface StatikAnnotation<out PARENT : StatikElement> : StatikElementWithParent<PARENT> {

  /** The reference name of this annotation. */
  public val referenceName: LazyDeferred<ReferenceName?>
}

/** Represents an argument of an annotation. */
public interface StatikAnnotationArgument<out PARENT : StatikElement> : StatikElementWithParent<PARENT> {

  /** The type of this argument. */
  public val type: LazyDeferred<ReferenceName?>

  /** The value of this argument. */
  public val value: Any
}
