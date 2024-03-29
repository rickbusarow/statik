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

import com.rickbusarow.statik.element.StatikElement
import com.rickbusarow.statik.element.StatikElementWithParent

/** Represents a Java element in the source code. */
public interface StatikJavaElement : StatikElement {
  /** The Java file that contains this element. */
  override val containingFile: StatikJavaFile
}

/** Represents an element with a parent element. */
public interface StatikJavaElementWithParent<out PARENT : StatikJavaElement> :
  StatikElementWithParent<PARENT>, StatikJavaElement
