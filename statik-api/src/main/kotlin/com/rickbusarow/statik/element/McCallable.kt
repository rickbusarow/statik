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

import com.rickbusarow.statik.element.McCallable.McJavaCallable
import com.rickbusarow.statik.element.McCallable.McKtCallable
import com.rickbusarow.statik.element.McFunction.McKtFunction
import com.rickbusarow.statik.element.McParameter.McKtParameter
import com.rickbusarow.statik.element.McProperty.McKtProperty
import com.rickbusarow.statik.element.McVisibility.McJavaVisibility
import com.rickbusarow.statik.element.McVisibility.McKtVisibility
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.TypeName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

/** A sealed interface representing a callable element in the codebase. */
sealed interface McCallable<out PARENT : McElement> :
  McElement,
  McElementWithParent<PARENT>,
  HasVisibility,
  McAnnotated {

  /** A sealed interface representing a Java callable element. */
  sealed interface McJavaCallable<out PARENT : McJavaElement> : McCallable<PARENT>, McJavaElement {

    override val visibility: McJavaVisibility
  }

  /** A sealed interface representing a Kotlin callable element. */
  sealed interface McKtCallable<out PARENT : McKtElement> :
    McCallable<PARENT>,
    McKtElementWithParent<PARENT> {
    override val psi: KtCallableDeclaration

    override val visibility: McKtVisibility
  }
}

/** A sealed interface representing a property element in the codebase. */
sealed interface McProperty<out PARENT : McElement> : McCallable<PARENT>, Declared<PARENT> {

  /** A deferred property representing the type name of the property. */
  val typeReferenceName: LazyDeferred<TypeName>

  /** Flag indicating whether the property is mutable. */
  val isMutable: Boolean

  /** A sealed interface representing a Java property element. */
  sealed interface McJavaProperty<out PARENT : McJavaElement> :
    McProperty<PARENT>,
    McJavaCallable<PARENT> {
    /** An interface representing a Java member property. */
    interface JavaMemberProperty<out PARENT : McJavaElement> : McJavaProperty<PARENT>
  }

  /** A sealed interface representing a Kotlin property element. */
  sealed interface McKtProperty<out PARENT : McKtElement> :
    McProperty<PARENT>,
    McKtCallable<PARENT> {
    override val psi: KtCallableDeclaration

    /** A sealed interface representing a Kotlin member property element. */
    interface KtMemberProperty<out PARENT : McKtElement> : McKtProperty<PARENT> {
      override val psi: KtProperty
    }

    /** A sealed interface representing a Kotlin extension property element. */
    interface KtExtensionProperty<out PARENT : McKtElement> :
      KtMemberProperty<PARENT>,
      McHasTypeParameters<PARENT> {
      override val psi: KtProperty
    }

    /** A sealed interface representing a Kotlin constructor property element. */
    interface KtConstructorProperty<out PARENT : McKtElement> : McKtProperty<PARENT> {
      override val psi: KtParameter
    }
  }
}

/** A sealed interface representing a parameter element in the codebase. */
sealed interface McParameter<out PARENT : McElement> : McCallable<PARENT>, McElement {
  /** The index of the parameter. */
  val index: Int

  /** A sealed interface representing a Java parameter element. */
  interface McJavaParameter<out PARENT : McJavaElement> :
    McParameter<PARENT>,
    McJavaCallable<PARENT>

  /** A sealed interface representing a Kotlin parameter element. */
  interface McKtParameter<out PARENT : McKtElement> :
    McParameter<PARENT>,
    McKtCallable<PARENT>
}

/** A sealed interface representing a function element in the codebase. */
sealed interface McFunction<out PARENT : McElement> :
  McCallable<PARENT>,
  McElement,
  McHasTypeParameters<PARENT> {

  /** A lazy set of parameters for the function. */
  val parameters: LazySet<McParameter<*>>

  /** A lazy set of properties for the function. */
  val properties: LazySet<McProperty<*>>

  /** A deferred property representing the return type of the function. */
  val returnType: LazyDeferred<ReferenceName>

  /** A sealed interface representing a Java function element. */
  interface McJavaFunction<out PARENT : McJavaElement> :
    McFunction<PARENT>,
    McJavaCallable<PARENT>

  /** represents a Kotlin function element. */
  interface McKtFunction<out PARENT : McKtElement> : McFunction<PARENT>, McKtCallable<PARENT> {

    override val psi: KtFunction
    override val parameters: LazySet<McKtParameter<*>>
    override val properties: LazySet<McKtProperty<*>>
    override val returnType: LazyDeferred<ReferenceName>
  }
}

/** A sealed interface representing an extension element in the codebase. */
sealed interface McKtExtensionElement<out PARENT : McKtElement> :
  McKtCallable<PARENT>,
  McKtElement {
  /** The receiver type. */
  val receiver: McType<*>

  /** A sealed interface representing a Kotlin extension property. */
  interface McKtExtensionProperty<out PARENT : McKtElement> :
    McKtExtensionElement<PARENT>,
    McKtProperty<PARENT>

  /** A sealed interface representing a Kotlin extension function. */
  interface McKtExtensionFunction<out PARENT : McKtElement> :
    McKtExtensionElement<PARENT>,
    McKtFunction<PARENT>
}
