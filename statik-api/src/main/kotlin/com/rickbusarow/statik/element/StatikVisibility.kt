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

public interface HasVisibility {
  public val visibility: StatikVisibility
}

public interface HasJavaVisibility : HasVisibility {
  override val visibility: StatikJavaVisibility
}

public interface StatikModifier
public interface StatikVisibility : StatikModifier

public sealed interface StatikJavaVisibility : StatikVisibility {
  public object Public : StatikJavaVisibility
  public object Protected : StatikJavaVisibility
  public object Private : StatikJavaVisibility
  public object PackagePrivate : StatikJavaVisibility
}
