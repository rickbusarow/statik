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
import com.rickbusarow.statik.element.kotlin.StatikKotlinConcreteType
import com.rickbusarow.statik.element.kotlin.StatikKotlinFile
import com.rickbusarow.statik.element.kotlin.StatikKotlinProperty
import com.rickbusarow.statik.element.kotlin.k1.compiler.HasStatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.compiler.StatikKotlinElementContext
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.ImportParser
import com.rickbusarow.statik.name.DeclaredName
import com.rickbusarow.statik.name.PackageName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.lazy.dataSource
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import com.rickbusarow.statik.utils.stdlib.mapToSet
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.toSet
import org.jetbrains.kotlin.fileClasses.javaFileFacadeFqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.io.File

@InternalStatikApi
public class K1KotlinFile(
  override val context: StatikKotlinElementContext,
  override val file: File,
  override val node: KtFile
) : StatikKotlinFile<KtFile>,
  K1ElementWithPackageName<KtFile>,
  HasStatikKotlinElementContext,
  HasChildrenInternal by HasChildrenInternalDelegate() {

  override val annotations: LazySet<K1Annotation<K1KotlinFile>> = lazySet {
    node.fileAnnotationList
    TODO("Not yet implemented")
  }
  override val declaredTypes: LazySet<StatikKotlinConcreteType<*, *>> = lazySet {
    node.StatikKotlinConcreteTypesDirect(
      context = context,
      containingFile = this,
      parent = this
    )
  }
  override val declaredTypesAndInnerTypes: LazySet<StatikKotlinConcreteType<*, *>> = lazySet {
    declaredTypes.fold(emptySet()) { acc, type ->
      acc + type + type.innerTypesRecursive.toSet()
    }
  }
  override val containingFile: K1KotlinFile get() = this

  @Suppress("UnusedPrivateProperty")
  private val fileJavaFacadeName by lazy { node.javaFileFacadeFqName.asString() }

  private val importParser by unsafeLazy { ImportParser(node.importDirectives) }

  // For `import com.foo as Bar`, the entry is `"Bar" to "com.foo"`
  override val importAliases: Map<String, ReferenceName> by lazy {
    importParser.aliasMap
  }

  override val imports: LazySet.DataSource<ReferenceName> =
    dataSource(priority = LazySet.DataSource.Priority.HIGH) {
      importParser.imports
    }
  override val wildcardImports: LazySet.DataSource<String> = dataSource {
    importParser.wildcards
  }

  override val packageName: PackageName by lazy { PackageName(node.packageFqName.asString()) }

  override val topLevelFunctions: LazySet<K1DeclaredFunction<K1KotlinFile>> = lazySet {
    node.getChildrenOfType<KtNamedFunction>()
      .mapToSet { K1DeclaredFunction(context = context, node = it, parent = this) }
  }
  override val topLevelProperties: LazySet<StatikKotlinProperty<*, *>>
    get() = TODO("Not yet implemented")
  override val apiReferences: List<LazySet.DataSource<ReferenceName>> = emptyList()
  override val declarations: List<LazySet.DataSource<DeclaredName>> = emptyList()
  override val references: List<LazySet.DataSource<ReferenceName>> = emptyList()
}
