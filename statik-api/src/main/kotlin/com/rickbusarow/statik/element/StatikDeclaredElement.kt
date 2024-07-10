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

import com.rickbusarow.statik.name.DeclaredName
import com.rickbusarow.statik.name.HasPackageName
import com.rickbusarow.statik.name.HasSimpleNames

/** An element that has been declared, it can be a class, function, variable, etc. */
public interface StatikDeclaredElement<out PARENT : StatikElement> :
  StatikElementWithParent<PARENT>,
  HasPackageName,
  HasVisibility,
  HasSimpleNames {
  /** The name of this declared element. */
  public val declaredName: DeclaredName

  /** A boolean indicating if this is an API element or not. */
  public val isApi: Boolean get() = false
}
