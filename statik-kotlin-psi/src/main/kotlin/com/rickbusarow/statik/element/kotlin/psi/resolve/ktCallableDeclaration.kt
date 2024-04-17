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
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import com.rickbusarow.statik.utils.stdlib.capitalize
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtValueArgumentList

@InternalStatikApi
public fun KtCallableDeclaration.isJvmStatic(): Boolean {
  return hasAnnotation(FqNames.jvmStatic)
}

@InternalStatikApi
public fun KtProperty.isJvmField(): Boolean {
  return hasAnnotation(FqNames.jvmField)
}

@InternalStatikApi
public fun KtFunction.jvmNameOrNull(): String? = annotatedJvmNameOrNull()

@InternalStatikApi
public fun KtPropertyAccessor.jvmNameOrNull(): String? = annotatedJvmNameOrNull()

private fun KtAnnotated.annotatedJvmNameOrNull(): String? {
  return annotationEntries
    .firstOrNull { it.shortName?.asString() == "JvmName" }
    ?.childrenOfTypeBreadthFirst<KtValueArgumentList>()
    ?.single()
    ?.childrenOfTypeBreadthFirst<KtLiteralStringTemplateEntry>()
    ?.single()
    ?.text
}

/**
 * Returns any custom names defined by `@JvmName(...)`, the default setter/getter
 * names if it's a property, or the same names as used by Kotlin for anything else.
 */
@Suppress("ComplexMethod")
@InternalStatikApi
public fun KtNamedDeclaration.jvmSimpleNames(): Set<String> {

  val identifier = nameAsSafeName.identifier

  val isPrefixMatchOrNull by unsafeLazy {
    // matches a name which starts with `is`, followed by something other than a lowercase letter.
    """^is([^a-z].*)""".toRegex().find(identifier)
  }

  return when (this) {
    is KtFunction -> {
      setOf(jvmNameOrNull() ?: isPrefixMatchOrNull?.value ?: nameAsSafeName.asString())
    }

    is KtProperty,
    is KtParameter -> {

      // const properties can't have JvmName annotations
      if (isConst()) return emptySet()

      // a Kotlin property `isAProperty` has a java setter of `setAProperty(...)`
      fun isPrefixSetterOrNull() = isPrefixMatchOrNull?.let {
        "set${it.destructured.component1()}"
      }

      val suffix by unsafeLazy { nameAsSafeName.identifier.capitalize() }

      buildSet {

        val get = (this@jvmSimpleNames as? KtProperty)?.getter?.jvmNameOrNull()
          ?: isPrefixMatchOrNull?.value
          ?: "get$suffix"

        add(get)

        val mutable = (this@jvmSimpleNames as? KtProperty)?.isVar
          ?: (this@jvmSimpleNames as KtParameter).isMutable

        if (mutable) {
          val set = (this@jvmSimpleNames as? KtProperty)?.setter?.jvmNameOrNull()
            ?: isPrefixSetterOrNull()
            ?: "set$suffix"

          add(set)
        }
      }
    }

    else -> {
      setOf(nameAsSafeName.asString())
    }
  }
}
