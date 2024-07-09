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

package com.rickbusarow.statik.element.java

import com.rickbusarow.statik.element.StatikHasTypeParameters
import com.rickbusarow.statik.element.StatikTypeParameter
import com.rickbusarow.statik.utils.lazy.LazySet

/** Represents an element with type parameters. */
public interface StatikJavaHasTypeParameters<out PARENT : StatikJavaElement<*>, NODE : Any> :
  StatikHasTypeParameters<PARENT, NODE>,
  StatikJavaElementWithParent<PARENT, NODE> {
  override val typeParameters: LazySet<StatikJavaTypeParameter<*, *>>
}

public interface StatikJavaTypeParameter<out PARENT : StatikJavaElement<*>, NODE : Any> :
  StatikTypeParameter<PARENT, NODE>,
  StatikJavaType<PARENT, NODE>
