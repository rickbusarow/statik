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

package com.rickbusarow.statik.element.impl.kotlin.psi.internal

import com.rickbusarow.statik.InternalStatikApi
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.isPublic

@InternalStatikApi
internal fun KtModifierListOwner.isPublicNotOverridden() = isPublic && !isOverride()

@InternalStatikApi
internal fun KtModifierListOwner.isPrivateOrInternal() = isPrivate() || isInternal()

@InternalStatikApi
internal fun KtModifierListOwner.isAbstract() = hasModifier(KtTokens.ABSTRACT_KEYWORD)

@InternalStatikApi
internal fun KtModifierListOwner.isOverride() = hasModifier(KtTokens.OVERRIDE_KEYWORD)

@InternalStatikApi
internal fun KtModifierListOwner.isOpen() = hasModifier(KtTokens.OPEN_KEYWORD)

@InternalStatikApi
internal fun KtModifierListOwner.isExternal() = hasModifier(KtTokens.EXTERNAL_KEYWORD)

@InternalStatikApi
internal fun KtModifierListOwner.isOperator() = hasModifier(KtTokens.OPERATOR_KEYWORD)

@InternalStatikApi
internal fun KtModifierListOwner.isConstant() = hasModifier(KtTokens.CONST_KEYWORD)

@InternalStatikApi
internal fun KtModifierListOwner.isInternal() = hasModifier(KtTokens.INTERNAL_KEYWORD)

@InternalStatikApi
internal fun KtModifierListOwner.isLateinit() = hasModifier(KtTokens.LATEINIT_KEYWORD)

@InternalStatikApi
internal fun KtModifierListOwner.isInline() = hasModifier(KtTokens.INLINE_KEYWORD)

@InternalStatikApi
internal fun KtModifierListOwner.isExpect() = hasModifier(KtTokens.EXPECT_KEYWORD)
