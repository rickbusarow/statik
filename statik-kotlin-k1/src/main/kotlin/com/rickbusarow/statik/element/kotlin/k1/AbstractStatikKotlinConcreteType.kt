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

package com.rickbusarow.statik.element.kotlin.k1

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.element.internal.HasChildrenInternal
import com.rickbusarow.statik.element.internal.HasChildrenInternalDelegate
import com.rickbusarow.statik.element.kotlin.HasKotlinVisibility
import com.rickbusarow.statik.element.kotlin.StatikKotlinAnnotation
import com.rickbusarow.statik.element.kotlin.StatikKotlinClass
import com.rickbusarow.statik.element.kotlin.StatikKotlinCompanionObject
import com.rickbusarow.statik.element.kotlin.StatikKotlinConcreteType
import com.rickbusarow.statik.element.kotlin.StatikKotlinDeclaredFunction
import com.rickbusarow.statik.element.kotlin.StatikKotlinElement
import com.rickbusarow.statik.element.kotlin.StatikKotlinFile
import com.rickbusarow.statik.element.kotlin.StatikKotlinFunction
import com.rickbusarow.statik.element.kotlin.StatikKotlinHasTypeParameters
import com.rickbusarow.statik.element.kotlin.StatikKotlinInterface
import com.rickbusarow.statik.element.kotlin.StatikKotlinObject
import com.rickbusarow.statik.element.kotlin.StatikKotlinProperty
import com.rickbusarow.statik.element.kotlin.StatikKotlinTypeDeclaration
import com.rickbusarow.statik.element.kotlin.StatikKotlinTypeParameter
import com.rickbusarow.statik.element.kotlin.StatikKotlinTypeReference
import com.rickbusarow.statik.element.kotlin.k1.compiler.HasStatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikKotlinElementContext
import com.rickbusarow.statik.name.DeclaredName
import com.rickbusarow.statik.name.HasPackageName
import com.rickbusarow.statik.name.PackageName
import com.rickbusarow.statik.name.SimpleName
import com.rickbusarow.statik.name.SimpleName.Companion.stripPackageNameFromFqName
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import com.rickbusarow.statik.utils.stdlib.mapToSet
import com.rickbusarow.statik.utils.stdlib.requireNotNull
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.toSet
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration

@InternalStatikApi
public abstract class AbstractStatikKotlinConcreteType<out PARENT> internal constructor(
  override val context: StatikKotlinElementContext,
  override val containingFile: StatikKotlinFile,
  override val psi: KtClassOrObject
) : StatikKotlinConcreteType<PARENT>,
  StatikKotlinTypeDeclaration<PARENT>,
  StatikKotlinHasTypeParameters<PARENT>,
  HasStatikKotlinElementContext,
  HasChildrenInternal by HasChildrenInternalDelegate()
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName {

  override val simpleNames: List<SimpleName> by unsafeLazy {
    psi.fqName.requireNotNull()
      .asString()
      .stripPackageNameFromFqName(containingFile.packageName)
  }

  override val declaredName: DeclaredName by lazy {
    DeclaredName.agnostic(
      containingFile.packageName,
      simpleNames
    )
  }

  final override val innerTypes: LazySet<StatikKotlinConcreteType<*>> = lazySet {
    psi.body
      ?.StatikKotlinConcreteTypesDirect(context, containingFile, parent)
      .orEmpty()
  }
  override val annotations: LazySet<StatikKotlinAnnotation<*>> = lazySet {
    psi.annotations(context, this)
  }
  override val innerTypesRecursive: LazySet<StatikKotlinConcreteType<*>> = lazySet {
    innerTypes.fold(emptySet()) { acc, type ->
      acc + type + type.innerTypesRecursive.toSet()
    }
  }
  override val properties: LazySet<StatikKotlinProperty<*>> = lazySet {

    buildSet {

      for (property in psi.body?.properties.orEmpty()) {

        add(
          K1MemberProperty(
            context = context,
            psi = property,
            parent = this@AbstractStatikKotlinConcreteType
          )
        )
      }

      val valueParams = psi.primaryConstructor?.valueParameters
        ?.filter { it.hasValOrVar() }
        .orEmpty()

      for (property in valueParams) {
        add(
          K1ConstructorProperty(
            context = context,
            psi = property,
            parent = this@AbstractStatikKotlinConcreteType
          )
        )
      }
    }
  }
  override val functions: LazySet<StatikKotlinDeclaredFunction<*>> = lazySet {
    psi.body?.functions
      .orEmpty()
      .mapToSet { K1DeclaredFunction(context = context, psi = it, parent = this) }
  }
  override val superTypes: LazySet<StatikKotlinTypeReference<*>> =
    lazySet { TODO("Not yet implemented") }
  override val typeParameters: LazySet<StatikKotlinTypeParameter<*>> = lazySet {
    TODO("Not yet implemented")
  }
  override val packageName: PackageName
    get() = containingFile.packageName

  override fun toString(): String {
    return buildString {
      append(this@AbstractStatikKotlinConcreteType::class.java.simpleName)
      append("(name = `${declaredName.asString}`, ")
      append("containingFile=${containingFile.file.path}, ")
      append("psi=${psi::class.simpleName}")
      append(")")
    }
  }
}

@InternalStatikApi
public class K1Class<out PARENT>(
  override val context: StatikKotlinElementContext,
  override val containingFile: StatikKotlinFile,
  override val psi: KtClass,
  override val parent: PARENT
) : AbstractStatikKotlinConcreteType<PARENT>(context, containingFile, psi),
  HasKotlinVisibility by StatikKotlinVisibilityDelegate(psi),
  StatikKotlinClass<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName {
  override val primaryConstructor: StatikKotlinFunction<*>
    get() = TODO("Not yet implemented")
  override val constructors: LazySet<StatikKotlinFunction<*>>
    get() = TODO("Not yet implemented")
}

@InternalStatikApi
public class K1Interface<out PARENT>(
  override val context: StatikKotlinElementContext,
  override val containingFile: StatikKotlinFile,
  override val psi: KtClass,
  override val parent: PARENT
) : AbstractStatikKotlinConcreteType<PARENT>(
  context = context,
  containingFile = containingFile,
  psi = psi
),
  HasKotlinVisibility by StatikKotlinVisibilityDelegate(psi),
  StatikKotlinInterface<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName

@InternalStatikApi
public class K1CompanionObject<out PARENT>(
  override val context: StatikKotlinElementContext,
  override val containingFile: StatikKotlinFile,
  override val psi: KtObjectDeclaration,
  override val parent: PARENT
) : AbstractStatikKotlinConcreteType<PARENT>(
  context = context,
  containingFile = containingFile,
  psi = psi
),
  HasKotlinVisibility by StatikKotlinVisibilityDelegate(psi),
  StatikKotlinCompanionObject<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName

@InternalStatikApi
public class K1Object<out PARENT>(
  override val context: StatikKotlinElementContext,
  override val containingFile: StatikKotlinFile,
  override val psi: KtObjectDeclaration,
  override val parent: PARENT
) : AbstractStatikKotlinConcreteType<PARENT>(
  context = context,
  containingFile = containingFile,
  psi = psi
),
  HasKotlinVisibility by StatikKotlinVisibilityDelegate(psi),
  StatikKotlinObject<PARENT>
  where PARENT : StatikKotlinElement,
        PARENT : HasPackageName
