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

package com.rickbusarow.statik.element.kotlin

import com.rickbusarow.statik.element.StatikAnnotated
import com.rickbusarow.statik.element.StatikAnnotation
import com.rickbusarow.statik.element.StatikAnnotationArgument
import com.rickbusarow.statik.utils.lazy.LazySet

/** Represents an annotated element. */
public interface StatikKotlinAnnotated : StatikAnnotated {

  /** The annotations of this element. */
  override val annotations: LazySet<StatikKotlinAnnotation<*>>
}

public interface StatikKotlinAnnotation<out PARENT : StatikKotlinElement> :
  StatikKotlinElementWithParent<PARENT>,
  StatikAnnotation<PARENT>

/** Represents an argument of an annotation. */
public interface StatikKotlinAnnotationArgument<out PARENT : StatikKotlinElement> :
  StatikKotlinElementWithParent<PARENT>,
  StatikAnnotationArgument<PARENT>
