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

package com.rickbusarow.statik.element.kotlin.psi.testing

import com.rickbusarow.kase.Kase4
import com.rickbusarow.statik.element.kotlin.psi.testing.SubjectBuilder.SubjectPropertyParams.Companion.subjectProperty
import com.rickbusarow.statik.utils.stdlib.justifyToFirstLine
import com.rickbusarow.statik.utils.stdlib.letIf
import com.rickbusarow.statik.utils.stdlib.replaceRegex
import org.intellij.lang.annotations.Language

object Properties {

  val explicitTypes = listOf(
    subjectProperty(
      afterProperty = ": String",
      typeAsString = "kotlin.String",
      imports = emptyList()
    ),
    subjectProperty(
      afterProperty = ": kotlin.String",
      typeAsString = "kotlin.String",
      imports = emptyList()
    ),
    subjectProperty(
      afterProperty = ": String",
      typeAsString = "kotlin.String",
      imports = listOf("kotlin.String")
    ),
    subjectProperty(
      afterProperty = ": KotlinString",
      typeAsString = "kotlin.String",
      imports = listOf("kotlin.String as KotlinString")
    ),
    subjectProperty(
      afterProperty = ": KotlinString",
      typeAsString = "com.subject.KotlinString",
      imports = emptyList(),
      additionalTypes = "typealias KotlinString = String"
    ),
    subjectProperty(
      afterProperty = ": Int",
      typeAsString = "kotlin.Int",
      imports = emptyList()
    ),
    subjectProperty(
      afterProperty = ": kotlin.Int",
      typeAsString = "kotlin.Int",
      imports = emptyList()
    ),
    subjectProperty(
      afterProperty = ": Int",
      typeAsString = "kotlin.Int",
      imports = listOf("kotlin.Int")
    ),
    subjectProperty(
      afterProperty = ": Lib1Class",
      typeAsString = "com.lib1.Lib1Class",
      imports = listOf("com.lib1.Lib1Class")
    )
  )

  val inferredTypes = listOf(
    subjectProperty(
      afterProperty = "= \"hello world\"",
      typeAsString = "kotlin.String",
      imports = emptyList()
    ),
    subjectProperty(
      afterProperty = "= \"hello world\"",
      typeAsString = "kotlin.String",
      imports = listOf("kotlin.String")
    ),
    subjectProperty(
      afterProperty = "= 3",
      typeAsString = "kotlin.Int",
      imports = emptyList()
    ),
    subjectProperty(
      afterProperty = "= 3_00",
      typeAsString = "kotlin.Int",
      imports = emptyList()
    ),
    subjectProperty(
      afterProperty = "= 3",
      typeAsString = "kotlin.Int",
      imports = listOf("kotlin.Int")
    ),
    subjectProperty(
      afterProperty = "= 3L",
      typeAsString = "kotlin.Long",
      imports = emptyList()
    ),
    subjectProperty(
      afterProperty = "= 3_00L",
      typeAsString = "kotlin.Long",
      imports = emptyList()
    ),
    subjectProperty(
      afterProperty = "= 3L",
      typeAsString = "kotlin.Long",
      imports = listOf("kotlin.Long")
    ),
    subjectProperty(
      afterProperty = "= Lib1Class()",
      typeAsString = "com.lib1.Lib1Class",
      imports = listOf("com.lib1.Lib1Class")
    )
  )
}

data class SubjectBuilder(
  val name: String,
  private val builder: (declarationKeyword: String, params: SubjectPropertyParams) -> String
) {

  fun build(declarationKeyword: String, params: SubjectPropertyParams): String = builder(
    declarationKeyword,
    params
  )
    .justifyToFirstLine()
    .replaceRegex("""\n{3,}""", "\n\n")
    .trim()
    .plus("\n\n")

  class SubjectPropertyParams private constructor(
    // override val displayName: String,
    val afterProperty: String,
    val typeAsString: String,
    val imports: List<String>,
    val additionalTypes: String = ""
  ) : Kase4<String, String, List<String>, String> {

    override val displayName: String
      get() = "val/var subjectProperty $afterProperty"
        .letIf(imports.isNotEmpty()) { "$it   (with imports)" }

    override val a1 = afterProperty
    override val a2 = typeAsString
    override val a3 = imports
    override val a4 = additionalTypes

    companion object {
      fun subjectProperty(
        afterProperty: String,
        typeAsString: String,
        imports: List<String>,
        @Language("kotlin")
        additionalTypes: String = ""
      ): SubjectPropertyParams = SubjectPropertyParams(
        afterProperty = afterProperty,
        typeAsString = typeAsString,
        imports = imports,
        additionalTypes = additionalTypes
      )
    }
  }
}
