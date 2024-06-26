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

import com.rickbusarow.statik.element.StatikDeclaredElement
import com.rickbusarow.statik.element.internal.HasChildrenInternal
import com.rickbusarow.statik.name.HasPackageName

/** Represents a declared Kotlin element in the source code. */
public interface StatikKotlinDeclaredElement<out PARENT> :
  StatikKotlinElementWithParent<PARENT>,
  StatikDeclaredElement<PARENT>,
  StatikKotlinElementWithPackageName,
  HasChildrenInternal
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  override val containingFile: StatikKotlinFile
  override val visibility: StatikKotlinVisibility
}
