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

package com.rickbusarow.statik.element.kotlin.psi

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.element.kotlin.HasKotlinVisibility
import com.rickbusarow.statik.element.kotlin.StatikKotlinVisibility
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierListOwner

@InternalStatikApi
public class StatikKotlinVisibilityDelegate(psi: KtModifierListOwner) : HasKotlinVisibility {
  override val visibility: StatikKotlinVisibility by lazy {
    when {
      psi.hasModifier(KtTokens.PRIVATE_KEYWORD) -> StatikKotlinVisibility.Private
      psi.hasModifier(KtTokens.INTERNAL_KEYWORD) -> StatikKotlinVisibility.Internal
      psi.hasModifier(KtTokens.PROTECTED_KEYWORD) -> StatikKotlinVisibility.Protected
      psi.hasModifier(KtTokens.PUBLIC_KEYWORD) -> StatikKotlinVisibility.Public
      else -> StatikKotlinVisibility.Public
    }
  }
}
