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

import com.rickbusarow.statik.element.HasJavaVisibility
import com.rickbusarow.statik.element.StatikCallable
import com.rickbusarow.statik.element.StatikFunction
import com.rickbusarow.statik.element.StatikJavaVisibility
import com.rickbusarow.statik.element.StatikProperty
import com.rickbusarow.statik.element.StatikValueParameter
import com.rickbusarow.statik.name.HasPackageName

/** A Java callable element. */
public interface StatikJavaCallable<out PARENT : StatikJavaElement> :
  StatikCallable<PARENT>,
  HasJavaVisibility,
  StatikJavaElement {

  override val visibility: StatikJavaVisibility
}

/** A Java property element. */
public sealed interface StatikJavaProperty<out PARENT : StatikJavaElement> :
  StatikProperty<PARENT>,
  StatikJavaCallable<PARENT>

/** A Java member property. */
public interface JavaMemberProperty<out PARENT : StatikJavaElement> : StatikJavaProperty<PARENT>

public interface StatikJavaValueParameter<out PARENT : StatikJavaElement> :
  StatikValueParameter<PARENT>,
  StatikJavaCallable<PARENT>

/** A Java function element. */
public interface StatikJavaFunction<out PARENT> :
  StatikFunction<PARENT>,
  StatikJavaCallable<PARENT>
  where PARENT : StatikJavaElement,
        PARENT : HasPackageName
