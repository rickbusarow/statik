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

package com.rickbusarow.statik.element.kotlin.psi

import com.rickbusarow.statik.InternalStatikApi
import com.rickbusarow.statik.compiler.StatikElementContext
import com.rickbusarow.statik.compiler.inerceptor.NameParser.NameParserPacket
import com.rickbusarow.statik.element.internal.HasChildrenInternal
import com.rickbusarow.statik.element.internal.HasChildrenInternalDelegate
import com.rickbusarow.statik.element.kotlin.StatikKotlinAnnotation
import com.rickbusarow.statik.element.kotlin.StatikKotlinAnnotationArgument
import com.rickbusarow.statik.element.kotlin.StatikKotlinElement
import com.rickbusarow.statik.element.kotlin.psi.resolve.kotlinStdLibNameOrNull
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
import org.jetbrains.kotlin.psi.KtValueArgument

internal fun KtAnnotated.annotations(
  context: StatikElementContext<PsiElement>,
  parent: StatikKotlinElement
): Set<StatikKotlinAnnotationImpl<*>> = annotationEntries
  .mapToSet {
    StatikKotlinAnnotationImpl(
      context = context,
      psi = it,
      parent = parent
    )
  }

@Poko
@InternalStatikApi
public class StatikKotlinAnnotationImpl<out PARENT : StatikKotlinElement>(
  private val context: StatikElementContext<PsiElement>,
  override val psi: KtAnnotationEntry,
  override val parent: PARENT
) : StatikKotlinAnnotation<PARENT>,
  HasChildrenInternal by HasChildrenInternalDelegate() {

  override val referenceName: LazyDeferred<ReferenceName> = lazyDeferred {

    context.nameParser.parse(
      NameParserPacket(
        file = containingFile,
        toResolve = psi.shortName.requireNotNull().asString().asReferenceName(KOTLIN),
        referenceLanguage = KOTLIN,
        stdLibNameOrNull = { asString.kotlinStdLibNameOrNull() }
      )
    )
      .requireNotNull()
  }
}

@Poko
@InternalStatikApi
public class StatikKotlinAnnotationArgumentImpl<out PARENT : StatikKotlinElement>(
  private val context: StatikElementContext<PsiElement>,
  override val psi: KtValueArgument,
  override val parent: PARENT
) : StatikKotlinAnnotationArgument<PARENT>,
  HasChildrenInternal by HasChildrenInternalDelegate() {

  override val value: Any = TODO()

  override val type: LazyDeferred<ReferenceName?> = lazyDeferred {

    psi
    TODO()
  }
}
