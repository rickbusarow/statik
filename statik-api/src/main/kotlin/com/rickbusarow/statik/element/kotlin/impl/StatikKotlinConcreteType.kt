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

package com.rickbusarow.statik.element.kotlin.impl

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.compiler.StatikElementContext
import com.rickbusarow.statik.element.kotlin.StatikKotlinConcreteType
import com.rickbusarow.statik.element.kotlin.StatikKotlinElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinFile
import com.rickbusarow.statik.name.HasPackageName
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

@Suppress("FunctionNaming")
@InternalStatikApi
public fun <PARENT> StatikKotlinConcreteType(
  context: StatikElementContext<PsiElement>,
  containingFile: StatikKotlinFile,
  clazz: KtClassOrObject,
  parent: PARENT
): StatikKotlinConcreteType<*>?
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName = when (clazz) {
  is KtClass ->
    if (clazz.isInterface()) {
      StatikKotlinInterfaceImpl(
        context = context,
        containingFile = containingFile,
        psi = clazz,
        parent = parent
      )
    } else {
      StatikKotlinClassImpl(
        context = context,
        containingFile = containingFile,
        psi = clazz,
        parent = parent
      )
    }

  is KtObjectDeclaration ->
    if (clazz.isCompanion()) {
      StatikKotlinCompanionObjectImpl(
        context = context,
        containingFile = containingFile,
        psi = clazz,
        parent = parent
      )
    } else {
      require(!clazz.isObjectLiteral())
      StatikKotlinObjectImpl(
        context = context,
        containingFile = containingFile,
        psi = clazz,
        parent = parent
      )
    }

  else -> null
}

@Suppress("FunctionName")
internal fun <PARENT> KtElement.StatikKotlinConcreteTypesDirect(
  context: StatikElementContext<PsiElement>,
  containingFile: StatikKotlinFile,
  parent: PARENT
): Set<StatikKotlinConcreteType<*>>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  return getChildrenOfType<KtClassOrObject>()
    .mapNotNull { clazz ->

      StatikKotlinConcreteType(
        context = context,
        containingFile = containingFile,
        clazz = clazz,
        parent = parent
      )
    }.toSet()
}
