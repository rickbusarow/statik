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
import com.rickbusarow.statik.element.kotlin.psi.testing.SubjectBuilder.SubjectParams
import com.rickbusarow.statik.utils.stdlib.justifyToFirstLine
import com.rickbusarow.statik.utils.stdlib.letIf
import com.rickbusarow.statik.utils.stdlib.replaceRegex
import org.intellij.lang.annotations.Language

object Properties {

  val explicitTypes = listOf(
    SubjectParams(
      afterProperty = ": String",
      typeAsString = "kotlin.String",
      imports = emptyList()
    ),
    SubjectParams(
      afterProperty = ": kotlin.String",
      typeAsString = "kotlin.String",
      imports = emptyList()
    ),
    SubjectParams(
      afterProperty = ": String",
      typeAsString = "kotlin.String",
      imports = listOf("kotlin.String")
    ),
    SubjectParams(
      afterProperty = ": KotlinString",
      typeAsString = "kotlin.String",
      imports = listOf("kotlin.String as KotlinString")
    ),
    SubjectParams(
      afterProperty = ": KotlinString",
      typeAsString = "com.subject.KotlinString",
      imports = emptyList(),
      additionalTypes = "typealias KotlinString = String"
    ),
    SubjectParams(
      afterProperty = ": Int",
      typeAsString = "kotlin.Int",
      imports = emptyList()
    ),
    SubjectParams(
      afterProperty = ": kotlin.Int",
      typeAsString = "kotlin.Int",
      imports = emptyList()
    ),
    SubjectParams(
      afterProperty = ": Int",
      typeAsString = "kotlin.Int",
      imports = listOf("kotlin.Int")
    ),
    SubjectParams(
      afterProperty = ": Lib1Class",
      typeAsString = "com.lib1.Lib1Class",
      imports = listOf("com.lib1.Lib1Class")
    )
  )

  val inferredTypes = listOf(
    SubjectParams(
      afterProperty = "= \"hello world\"",
      typeAsString = "kotlin.String",
      imports = emptyList()
    ),
    SubjectParams(
      afterProperty = "= \"hello world\"",
      typeAsString = "kotlin.String",
      imports = listOf("kotlin.String")
    ),
    SubjectParams(
      afterProperty = "= 3",
      typeAsString = "kotlin.Int",
      imports = emptyList()
    ),
    SubjectParams(
      afterProperty = "= 3_00",
      typeAsString = "kotlin.Int",
      imports = emptyList()
    ),
    SubjectParams(
      afterProperty = "= 3",
      typeAsString = "kotlin.Int",
      imports = listOf("kotlin.Int")
    ),
    SubjectParams(
      afterProperty = "= 3L",
      typeAsString = "kotlin.Long",
      imports = emptyList()
    ),
    SubjectParams(
      afterProperty = "= 3_00L",
      typeAsString = "kotlin.Long",
      imports = emptyList()
    ),
    SubjectParams(
      afterProperty = "= 3L",
      typeAsString = "kotlin.Long",
      imports = listOf("kotlin.Long")
    ),
    SubjectParams(
      afterProperty = "= Lib1Class()",
      typeAsString = "com.lib1.Lib1Class",
      imports = listOf("com.lib1.Lib1Class")
    )
  )
}

data class SubjectBuilder(
  val name: String,
  private val builder: (declarationKeyword: String, params: SubjectParams) -> String
) {

  fun build(declarationKeyword: String, params: SubjectParams): String = builder(
    declarationKeyword,
    params
  )
    .justifyToFirstLine()
    .replaceRegex("""\n{3,}""", "\n\n")
    .trim()
    .plus("\n\n")

  class SubjectParams(
    // override val displayName: String,
    val afterProperty: String,
    val typeAsString: String,
    val imports: List<String>,
    @Language("kotlin")
    val additionalTypes: String = ""
  ) : Kase4<String, String, List<String>, String> {

    override val displayName: String
      get() = "vax subjectParam $afterProperty"
        .letIf(imports.isNotEmpty()) { "$it   (with imports)" }

    override val a1 = afterProperty
    override val a2 = typeAsString
    override val a3 = imports
    override val a4 = additionalTypes
  }
}
