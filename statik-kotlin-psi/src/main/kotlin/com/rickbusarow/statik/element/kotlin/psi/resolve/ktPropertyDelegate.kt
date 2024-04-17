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

package com.rickbusarow.statik.element.kotlin.psi.resolve

import com.rickbusarow.statik.InternalStatikApi
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

@InternalStatikApi
public fun KtPropertyDelegate.returnType(bindingContext: BindingContext): KotlinType? {
  val property = this.parent as? KtProperty ?: return null
  val propertyDescriptor =
    bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, property] as? PropertyDescriptor
  return propertyDescriptor?.getter?.let {
    bindingContext[BindingContext.DELEGATED_PROPERTY_RESOLVED_CALL, it]
      ?.resultingDescriptor
      ?.returnType
  }
}
