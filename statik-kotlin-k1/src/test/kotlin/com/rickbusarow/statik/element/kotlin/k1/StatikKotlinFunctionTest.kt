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

import com.rickbusarow.statik.element.kotlin.StatikKotlinConcreteType
import com.rickbusarow.statik.element.kotlin.StatikKotlinDeclaredFunction
import com.rickbusarow.statik.element.kotlin.StatikKotlinFile
import com.rickbusarow.statik.element.kotlin.k1.psi.traversal.PsiTreePrinter.Companion.printEverything
import com.rickbusarow.statik.element.kotlin.k1.testing.ProjectTest
import com.rickbusarow.statik.element.kotlin.k1.testing.Properties
import com.rickbusarow.statik.element.kotlin.k1.testing.PsiTestEnvironment
import com.rickbusarow.statik.element.kotlin.psi.testing.ProjectTest
import com.rickbusarow.statik.element.kotlin.psi.testing.Properties
import com.rickbusarow.statik.element.kotlin.psi.testing.PsiTestEnvironment
import com.rickbusarow.statik.element.kotlin.psi.utils.traversal.StatikTreePrinter.Companion.printEverything
import com.rickbusarow.statik.element.kotlin.psi.utils.traversal.StatikTreePrinter.Companion.printEverythingFromPSINode
import com.rickbusarow.statik.name.StatikLanguage
import com.rickbusarow.statik.testing.internal.StatikNameTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.TestFactory

class StatikKotlinFunctionTest : ProjectTest(), StatikNameTest {

  override val testEnvironmentFactory = PsiTestEnvironment

  override val defaultLanguage = StatikLanguage.KOTLIN

  suspend fun StatikKotlinFile.subjectClass(): StatikKotlinConcreteType<*> {
    return declaredTypesAndInnerTypes.toList()
      .single { it.simpleNames.last().asString == "SubjectClass" }
  }

  suspend fun StatikKotlinConcreteType<*>.function(name: String): StatikKotlinDeclaredFunction<*> {
    return functions.first { it.simplestName.asString == name }
  }

  suspend fun StatikKotlinConcreteType<*>.subjectFun() = function("subjectFunction")

  @TestFactory
  fun `caaaaaanary thing`() = Properties.explicitTypes
    .take(1)
    .asTests { params ->

      val file = createKotlin(
        """
        package com.subject

        import java.io.Serializable

        class SubjectClass {
          fun <S: Number, T> subjectFunction(a: Int, b: String, c: T): String
            where T : java.io.Serializable,
                  T : CharSequence {
            println("hello")
            val d = "some value - ${'$'}a - ${'$'}b - ${'$'}c"
            return d
          }
        }
        """
      )

      val subjectFunction = file.subjectClass().subjectFun()

      subjectFunction.printEverything()
      subjectFunction.printEverythingFromPSINode()

      // subjectFunction.childrenOfTypeRecursive<StatikKotlinMemberProperty<*>>()
      //   .single()
      //   .typeReferenceName.await().asString shouldBe "kotlin.String"

      // subjectFunction.returnType.await().asString shouldBe "kotlin.String"
    }
}
