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

package com.rickbusarow.statik.element.kotlin.k1

import com.rickbusarow.statik.element.StatikElementWithParent
import com.rickbusarow.statik.element.kotlin.StatikKotlinElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinElementWithPackageName
import org.jetbrains.kotlin.psi.KtElement

public interface K1Element : StatikKotlinElement {

  override val node: KtElement

  /** An alias for [node]. */
  public val psi: KtElement get() = node

  override val text: String
    get() = node.text

  override val containingFile: K1KotlinFile
    get() = when (this) {
      is K1ExtensionElement<*> -> parent.containingFile
      is K1ElementWithParent<*> -> parent.containingFile
      is K1KotlinFile -> this
      else -> error("Unknown Kotlin element type: $this")
    }
}

/** */
public interface K1ElementWithPackageName :
  K1Element,
  StatikKotlinElementWithPackageName

/** Represents an element with a parent element. */
public interface K1ElementWithParent<out PARENT : K1Element> :
  StatikElementWithParent<PARENT>,
  K1Element
