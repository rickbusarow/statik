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

package com.rickbusarow.statik.utils.stdlib

import com.rickbusarow.statik.InternalStatikApi
import kotlin.reflect.KClass

@InternalStatikApi
public val KClass<*>.packageName: String
  get() = java.`package`.name

/** ex: `[Outer, Mid, Inner]` for `com.example.Outer.Mid.Inner` */
@InternalStatikApi
public val KClass<*>.simpleNames: List<String>
  get() = generateSequence(java) { it.enclosingClass }
    .map { it.simpleName }
    .toList()
    .reversed()

/** ex: `[Outer, Mid, Inner]` for `com.example.Outer.Mid.Inner` */
@InternalStatikApi
public val Class<*>.simpleNames: List<String>
  get() = generateSequence(this) { it.enclosingClass }
    .map { it.simpleName }
    .toList()
    .reversed()

/** ex: `Outer.Mid.Inner` for `com.example.Outer.Mid.Inner` */
@InternalStatikApi
public val KClass<*>.simpleNamesConcat: String
  get() = simpleNames.joinToStringDot()

/** ex: `Outer.Mid.Inner` for `com.example.Outer.Mid.Inner` */
@InternalStatikApi
public val Class<*>.simpleNamesConcat: String
  get() = simpleNames.joinToStringDot()
