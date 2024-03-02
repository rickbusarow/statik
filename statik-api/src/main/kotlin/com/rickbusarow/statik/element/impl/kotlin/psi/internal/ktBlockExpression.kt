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
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtScriptInitializer
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

@InternalStatikApi
internal fun KtBlockExpression.nameSafe(): String? {

  val call: KtCallExpression? = getChildOfType<KtScriptInitializer>()
    ?.getChildOfType()
    ?: getChildOfType()

  call?.getChildOfType<KtNameReferenceExpression>()
    ?.text
    ?.let { simpleName -> return simpleName }

  val dotQualified: KtDotQualifiedExpression? = getChildOfType<KtScriptInitializer>()
    ?.getChildOfType()
    ?: getChildOfType()

  dotQualified?.let { dot ->

    val sel = dot.selectorExpression
      ?.getChildOfType<KtNameReferenceExpression>()
      ?.text

    return "${dot.receiverExpression.text}.$sel"
  }

  return getChildOfType<KtBlockExpression>()
    ?.getChildOfType<KtDotQualifiedExpression>()
    ?.text
}
