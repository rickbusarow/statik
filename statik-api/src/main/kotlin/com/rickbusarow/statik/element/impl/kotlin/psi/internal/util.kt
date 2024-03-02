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

package com.rickbusarow.statik.element.impl.kotlin.psi.internal

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.name.SimpleName
import com.rickbusarow.statik.name.SimpleName.Companion.asSimpleName
import com.rickbusarow.statik.utils.stdlib.requireNotNull
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

@InternalStatikApi
internal fun KtValueArgumentList.getByNameOrIndex(index: Int, name: String): KtValueArgument? {
  return arguments
    .firstOrNull { it.getArgumentName()?.text == name }
    ?: arguments
      .getOrNull(index)
}

@InternalStatikApi
internal fun KtElement.requireSimpleName(): SimpleName = name.requireNotNull().asSimpleName()
