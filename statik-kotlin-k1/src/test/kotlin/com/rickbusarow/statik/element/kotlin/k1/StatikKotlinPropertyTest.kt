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
import com.rickbusarow.statik.element.kotlin.StatikKotlinFile
import com.rickbusarow.statik.element.kotlin.StatikKotlinProperty
import com.rickbusarow.statik.element.kotlin.k1.testing.ProjectTest
import com.rickbusarow.statik.element.kotlin.k1.testing.Properties
import com.rickbusarow.statik.element.kotlin.k1.testing.PsiTestEnvironment
import com.rickbusarow.statik.element.kotlin.k1.testing.SubjectBuilder
import com.rickbusarow.statik.name.ParameterizedReferenceName.Companion.parameterizedBy
import com.rickbusarow.statik.name.StatikLanguage
import com.rickbusarow.statik.testing.internal.StatikNameTest
import com.rickbusarow.statik.utils.stdlib.justifyToFirstLine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class StatikKotlinPropertyTest : ProjectTest(), StatikNameTest {

  override val testEnvironmentFactory = PsiTestEnvironment

  override val defaultLanguage = StatikLanguage.KOTLIN

  suspend fun StatikKotlinFile.subjectClass() = declaredTypesAndInnerTypes.toList()
    .single { it.simpleNames.last().asString == "SubjectClass" }

  suspend fun StatikKotlinConcreteType<*>.property(name: String): StatikKotlinProperty<*> =
    properties.first { it.simplestName.asString == name }

  suspend fun StatikKotlinConcreteType<*>.subjectProp() = property("subjectProp")

  @TestFactory
  fun `constructor property explicit types`() = Properties.explicitTypes.asTests { params ->

    val file = createKotlin(constructorProperty.build("val", params))

    val subjectProp = file.subjectClass().subjectProp()

    subjectProp.typeReferenceName.await().asString shouldBe params.typeAsString
  }

  @TestFactory
  fun `interface body explicit types`() = Properties.explicitTypes.asTests { params ->

    val file = createKotlin(interfaceBodyProperty.build("val", params))

    val subjectProp = file.subjectClass().subjectProp()

    subjectProp.typeReferenceName.await().asString shouldBe params.typeAsString
  }

  @TestFactory
  fun `class body inferred types`() = Properties.inferredTypes.asTests { params ->

    val file = createKotlin(classBodyProperty.build("val", params))

    val subjectProp = file.subjectClass().subjectProp()

    subjectProp.typeReferenceName.await().asString shouldBe params.typeAsString
  }

  @Nested
  inner class `constructor property type resolution` {

    @Test
    fun `constructor property with no default value should have resolved type`() = test {

      val file = createKotlin(
        """
        package com.subject

        import com.lib1.Lib1Class

        class SubjectClass(var subjectProp: Lib1Class)

        """
      )

      val subjectProp = file.subjectClass().subjectProp()

      subjectProp.typeReferenceName.await() shouldBe "com.lib1.Lib1Class".asReferenceName()
    }

    @Test
    fun `constructor property with explicit single-param stdlib generic type should resolve`() =
      test {

        val file = createKotlin(
          """
          package com.subject

          class SubjectClass(
            val subjectProp: List<String>
          )
          """
        )

        val subjectProp = file.subjectClass().subjectProp()

        subjectProp.typeReferenceName.await() shouldBe "kotlin.collections.List".asReferenceName()
          .parameterizedBy("kotlin.String".asReferenceName())
      }
  }

  @Nested
  inner class `member property type resolution` {

    @Test
    fun `member property with explicit type should have resolved type`() = test {

      val file = createKotlin(
        """
        package com.subject

        import com.lib1.Lib1Class
        import kotlin.properties.Delegates

        class SubjectClass {
          lateinit var subjectProp : Lib1Class
        }
        """
      )

      val subjectProp = file.subjectClass().subjectProp()

      subjectProp.typeReferenceName.await() shouldBe "com.lib1.Lib1Class".asReferenceName()
    }

    @Test
    fun `member property with explicit stdlib type should resolve`() = test {

      val file = createKotlin(
        """
        package com.subject

        import com.lib1.Lib1Class

        class SubjectClass {
          val subjectProp: String = "foo"
        }
        """
      )

      val subjectProp = file.subjectClass().subjectProp()

      subjectProp.typeReferenceName.await() shouldBe "kotlin.String".asReferenceName()
    }

    @Test
    fun `member property with explicit single-param stdlib generic type should resolve`() = test {
      val file = createKotlin(
        """
        package com.subject

        class SubjectClass {
          lateinit var subjectProp: List<String>
        }
        """
      )

      val subjectProp = file.subjectClass().subjectProp()

      subjectProp.typeReferenceName.await() shouldBe "kotlin.collections.List".asReferenceName()
        .parameterizedBy("kotlin.String".asReferenceName())
    }

    @Test
    fun `member property with inferred type from fully imported generic delegate should have resolved type`() =
      test {

        val file = createKotlin(
          """
          package com.subject

          import com.lib1.Lib1Class
          import kotlin.properties.Delegates.notNull

          class SubjectClass {
            var subjectProp by notNull<Lib1Class>()
          }
          """
        )

        val subjectProp = file.subjectClass().subjectProp()

        subjectProp.typeReferenceName.await() shouldBe "com.lib1.Lib1Class".asReferenceName()
      }

    @Test
    fun `member property with inferred type from qualified generic delegate should have resolved type`() =
      test {

        val file = createKotlin(
          """
          package com.subject

          import com.lib1.Lib1Class
          import kotlin.properties.Delegates

          class SubjectClass {
            var subjectProp by Delegates.notNull<Lib1Class>()
          }
          """
        )

        val subjectProp = file.subjectClass().subjectProp()

        subjectProp.typeReferenceName.await() shouldBe "com.lib1.Lib1Class".asReferenceName()
      }

    @Test
    fun `member property with nested type should have resolved type`() = test {

      val file = createKotlin(
        """
        package com.subject

        import com.lib1.Lib1Class

        class SubjectClass {
          class NestedClass
          val nested: NestedClass = NestedClass()
        }
        """
      )

      val nested = file.subjectClass().property("nested")

      nested.typeReferenceName.await() shouldBe "com.subject.SubjectClass.NestedClass".asReferenceName()
    }

    @Test
    fun `member property with generic type with multiple type parameters should resolve`() = test {
      val file = createKotlin(
        """
        package com.subject

        class SubjectClass {
          val map: Map<String, Int> = mapOf()
        }
        """
      )

      val map = file.subjectClass().property("map")

      map.typeReferenceName.await() shouldBe "kotlin.collections.Map".asReferenceName()
        .parameterizedBy("kotlin.String".asReferenceName(), "kotlin.Int".asReferenceName())
    }

    @Test
    fun `member property with typealias type should have resolved alias type`() = test {
      val file = createKotlin(
        """
        package com.subject

        typealias StringList = List<String>

        class SubjectClass {
          val subjectProp: StringList = emptyList()
        }
        """
      )

      val subjectProp = file.subjectClass().subjectProp()

      subjectProp.typeReferenceName.await() shouldBe "com.subject.StringList".asReferenceName()
    }

    @Test
    fun `member property with a generic typealias type should have resolved alias type`() = test {
      val file = createKotlin(
        """
        package com.subject

        typealias StringList<T> = List<T>

        class SubjectClass {
          val subjectProp: StringList<String> = emptyList()
        }
        """
      )

      val subjectProp = file.subjectClass().subjectProp()

      subjectProp.typeReferenceName.await() shouldBe "com.subject.StringList".asReferenceName()
        .parameterizedBy("kotlin.String".asReferenceName())
    }

    @Test
    fun `member property with import alias type should have the original imported type`() = test {
      val file = createKotlin(
        """
        package com.subject

        import kotlin.Unit as KUnit

        class SubjectClass {
          lateinit var kunit: KUnit
        }
        """
      )

      val subjectProp = file.subjectClass().property("kunit")

      subjectProp.typeReferenceName.await() shouldBe "kotlin.Unit".asReferenceName()
    }

    @Test
    fun `member property with nullable type should have resolved type`() = test {
      val file = createKotlin(
        """
        package com.subject

        class SubjectClass {
          val subjectProp: String? = null
        }
        """
      )

      val subjectProp = file.subjectClass().subjectProp()

      subjectProp.typeReferenceName.await() shouldBe "kotlin.String".asReferenceName()
    }

    @Test
    fun `member property with custom generic type should have resolved type`() = test {
      val file = createKotlin(
        """
        package com.subject

        class MyGeneric<T>

        class SubjectClass {
          val myGeneric: MyGeneric<String> = MyGeneric()
        }
        """
      )

      val myGeneric = file.subjectClass().property("myGeneric")

      myGeneric.typeReferenceName.await() shouldBe "com.subject.MyGeneric".asReferenceName()
        .parameterizedBy("kotlin.String".asReferenceName())
    }

    @Test
    fun `member property with type from different module should have resolved type`() = test {

      val file = createKotlin(
        """
        package com.subject

        import com.lib1.Lib1Class

        class SubjectClass {
          val subjectProp: Lib1Class = Lib1Class()
        }
        """
      )

      val subjectProp = file.subjectClass().subjectProp()

      subjectProp.typeReferenceName.await() shouldBe "com.lib1.Lib1Class".asReferenceName()
    }

    @Test
    fun `member property with complex nested generic type should have resolved type`() = test {
      val file = createKotlin(
        """
        package com.subject

        class MyGeneric<T>

        class SubjectClass {
          val myGeneric: MyGeneric<List<String>> = MyGeneric()
        }
        """
      )

      val myGeneric = file.subjectClass().property("myGeneric")

      myGeneric.typeReferenceName.await() shouldBe "com.subject.MyGeneric".asReferenceName()
        .parameterizedBy(
          "kotlin.collections.List".asReferenceName()
            .parameterizedBy("kotlin.String".asReferenceName())
        )
    }
  }

  private val constructorProperty = SubjectBuilder("constructor property") { keyword, params ->
    //language=kotlin
    """
    package com.subject

    ${params.imports.joinToString("\n") { "import $it" }}

    class SubjectClass(
      $keyword subjectProp ${params.afterProperty}
    )

    ${params.additionalTypes.justifyToFirstLine()}
    """
  }

  private val interfaceBodyProperty = SubjectBuilder("interface body property") { keyword, params ->
    //language=kotlin
    """
    package com.subject

    ${params.imports.joinToString("\n") { "import $it" }}

    interface SubjectClass {
      $keyword subjectProp ${params.afterProperty}
    }

    ${params.additionalTypes.justifyToFirstLine()}
    """
  }

  private val classBodyProperty = SubjectBuilder("class body property") { keyword, params ->
    //language=kotlin
    """
    package com.subject

    ${params.imports.joinToString("\n") { "import $it" }}

    class SubjectClass {
      $keyword subjectProp ${params.afterProperty}
    }

    ${params.additionalTypes.justifyToFirstLine()}
    """
  }
}
