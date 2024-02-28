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

import com.rickbusarow.statik.element.McFile.McJavaFile
import com.rickbusarow.statik.element.McFile.McKtFile
import com.rickbusarow.statik.element.McFunction.McKtFunction
import com.rickbusarow.statik.element.McProperty.McKtProperty
import com.rickbusarow.statik.element.McType.McConcreteType.McJavaType
import com.rickbusarow.statik.element.McType.McConcreteType.McKtType
import com.rickbusarow.statik.utils.lazy.LazySet

sealed interface McType<out PARENT : McElement> :
  McElementWithParent<PARENT>,
  McHasTypeParameters<PARENT>,
  McAnnotated {

  /**
   * In a concrete type, this represents super-classes and interfaces.
   *
   * In a generic type, supers are the upper bound(s).
   */
  val superTypes: LazySet<McType<*>>

  /** Represents a class, interface, object, or companion object */
  sealed interface McConcreteType<out PARENT : McElement> :
    McType<PARENT>,
    McElementWithParent<PARENT>,
    Declared<PARENT> {

    override val containingFile: McFile

    val innerTypes: LazySet<McConcreteType<*>>
    val innerTypesRecursive: LazySet<McType<*>>
    val properties: LazySet<McProperty<*>>
    val functions: LazySet<McFunction<*>>

    interface McJavaType<out PARENT : McJavaElement> :
      McType<PARENT>,
      McJavaElementWithParent<PARENT> {
      override val parent: PARENT
    }

    interface McKtType<out PARENT : McKtElement> :
      McType<PARENT>,
      McKtElementWithParent<PARENT> {
      override val parent: PARENT
    }

    sealed interface McJavaConcreteType<out PARENT : McJavaElement> :
      McConcreteType<PARENT>,
      McJavaType<PARENT>,
      McJavaElementWithParent<PARENT> {

      override val innerTypes: LazySet<McJavaConcreteType<*>>
      override val innerTypesRecursive: LazySet<McJavaConcreteType<*>>

      override val containingFile: McJavaFile

      interface McJavaInterface<out PARENT : McJavaElement> :
        McJavaConcreteType<PARENT>,
        Declared<PARENT>

      interface McJavaClass<out PARENT : McJavaElement> :
        McJavaConcreteType<PARENT>,
        Declared<PARENT> {

        val constructors: LazySet<McFunction.McJavaFunction<*>>
      }
    }

    interface McKtConcreteType<out PARENT : McKtElement> :
      McKtType<PARENT>,
      McConcreteType<PARENT>,
      McKtDeclaredElement<PARENT>,
      McKtElementWithParent<PARENT> {

      override val innerTypes: LazySet<McKtConcreteType<*>>
      override val innerTypesRecursive: LazySet<McKtConcreteType<*>>

      override val containingFile: McKtFile
      override val properties: LazySet<McKtProperty<*>>
      override val functions: LazySet<McKtFunction<*>>

      interface McKtAnnotationClass<out PARENT : McKtElement> :
        McKtConcreteType<PARENT>,
        McKtElementWithParent<PARENT>,
        Declared<PARENT>

      interface McKtClass<out PARENT : McKtElement> :
        McKtConcreteType<PARENT>,
        McKtElementWithParent<PARENT>,
        Declared<PARENT> {

        val primaryConstructor: McKtFunction<*>?

        /** All constructors, including the primary if it exists */
        val constructors: LazySet<McKtFunction<*>>
      }

      interface McKtCompanionObject<out PARENT : McKtElement> :
        McKtConcreteType<PARENT>,
        McKtElementWithParent<PARENT>,
        Declared<PARENT>

      interface McKtTypeAlias<out PARENT : McKtElement> :
        McKtConcreteType<PARENT>,
        McKtElementWithParent<PARENT>,
        Declared<PARENT>

      interface McKtEnum<out PARENT : McKtElement> :
        McKtConcreteType<PARENT>,
        McKtElementWithParent<PARENT>,
        Declared<PARENT>

      interface McKtInterface<out PARENT : McKtElement> :
        McKtConcreteType<PARENT>,
        McKtElementWithParent<PARENT>,
        Declared<PARENT>

      interface McKtObject<out PARENT : McKtElement> :
        McKtConcreteType<PARENT>,
        McKtElementWithParent<PARENT>,
        Declared<PARENT>
    }
  }

  /** Represents a generic type used as a parameter, like `<T>` or `<R: Any>`. */
  interface McTypeParameter<out PARENT : McElement> : McType<PARENT> {
    interface McJavaTypeParameter<out PARENT : McJavaElement> :
      McTypeParameter<PARENT>,
      McJavaType<PARENT>

    interface McKtTypeParameter<out PARENT : McKtElement> :
      McTypeParameter<PARENT>,
      McKtType<PARENT>
  }
}
