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
@file:Suppress("TooManyFunctions")

package com.rickbusarow.statik.element.kotlin.k1.psi.resolve

import com.rickbusarow.statik.InternalStatikApi
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.isPublic

@InternalStatikApi
public fun KtModifierListOwner.isPublicNotOverridden(): Boolean = isPublic && !isOverride()

@InternalStatikApi
public fun KtModifierListOwner.isPrivateOrInternal(): Boolean = isPrivate() || isInternal()

@InternalStatikApi
public fun KtModifierListOwner.isAbstract(): Boolean = hasModifier(KtTokens.ABSTRACT_KEYWORD)

@InternalStatikApi
public fun KtModifierListOwner.isOverride(): Boolean = hasModifier(KtTokens.OVERRIDE_KEYWORD)

@InternalStatikApi
public fun KtModifierListOwner.isOpen(): Boolean = hasModifier(KtTokens.OPEN_KEYWORD)

@InternalStatikApi
public fun KtModifierListOwner.isExternal(): Boolean = hasModifier(KtTokens.EXTERNAL_KEYWORD)

@InternalStatikApi
public fun KtModifierListOwner.isOperator(): Boolean = hasModifier(KtTokens.OPERATOR_KEYWORD)

@InternalStatikApi
public fun KtModifierListOwner.isConstant(): Boolean = hasModifier(KtTokens.CONST_KEYWORD)

@InternalStatikApi
public fun KtModifierListOwner.isInternal(): Boolean = hasModifier(KtTokens.INTERNAL_KEYWORD)

@InternalStatikApi
public fun KtModifierListOwner.isLateinit(): Boolean = hasModifier(KtTokens.LATEINIT_KEYWORD)

@InternalStatikApi
public fun KtModifierListOwner.isInline(): Boolean = hasModifier(KtTokens.INLINE_KEYWORD)

@InternalStatikApi
public fun KtModifierListOwner.isExpect(): Boolean = hasModifier(KtTokens.EXPECT_KEYWORD)
