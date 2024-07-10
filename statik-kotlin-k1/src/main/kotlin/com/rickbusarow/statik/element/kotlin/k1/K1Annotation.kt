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
import com.rickbusarow.statik.compiler.StatikElementContext
import com.rickbusarow.statik.compiler.inerceptor.NameParser.NameParserPacket
import com.rickbusarow.statik.element.internal.HasChildrenInternal
import com.rickbusarow.statik.element.internal.HasChildrenInternalDelegate
import com.rickbusarow.statik.element.kotlin.StatikKotlinAnnotation
import com.rickbusarow.statik.element.kotlin.k1.psi.resolve.kotlinStdLibNameOrNull
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.ReferenceName.Companion.asReferenceName
import com.rickbusarow.statik.name.StatikLanguage.KOTLIN
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.lazyDeferred
import com.rickbusarow.statik.utils.stdlib.mapToSet
import com.rickbusarow.statik.utils.stdlib.requireNotNull
import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry

internal fun KtAnnotated.annotations(
  context: StatikElementContext<PsiElement>,
  parent: K1Element
): Set<K1Annotation<*>> = annotationEntries
  .mapToSet {
    K1Annotation(
      context = context,
      node = it,
      parent = parent
    )
  }

@Poko
@InternalStatikApi
public class K1Annotation<out PARENT : K1Element>(
  private val context: StatikElementContext<PsiElement>,
  override val node: KtAnnotationEntry,
  override val parent: PARENT
) : StatikKotlinAnnotation<PARENT>,
  K1Element,
  HasChildrenInternal by HasChildrenInternalDelegate() {

  override val referenceName: LazyDeferred<ReferenceName> = lazyDeferred {

    context.nameParser.parse(
      NameParserPacket(
        file = containingFile,
        toResolve = node.shortName.requireNotNull().asString().asReferenceName(KOTLIN),
        referenceLanguage = KOTLIN,
        stdLibNameOrNull = { asString.kotlinStdLibNameOrNull() }
      )
    )
      .requireNotNull()
  }
}
