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

package com.rickbusarow.statik.testing

import com.rickbusarow.statik.name.AndroidDataBindingDeclaredName
import com.rickbusarow.statik.name.AndroidDataBindingReferenceName
import com.rickbusarow.statik.name.AndroidRDeclaredName
import com.rickbusarow.statik.name.AndroidRReferenceName
import com.rickbusarow.statik.name.DeclaredName
import com.rickbusarow.statik.name.PackageName
import com.rickbusarow.statik.name.QualifiedAndroidResourceDeclaredName
import com.rickbusarow.statik.name.QualifiedAndroidResourceReferenceName
import com.rickbusarow.statik.name.QualifiedDeclaredName
import com.rickbusarow.statik.name.ReferenceName
import com.rickbusarow.statik.name.ReferenceName.Companion.asReferenceName
import com.rickbusarow.statik.name.SimpleName.Companion.stripPackageNameFromFqName
import com.rickbusarow.statik.name.StatikLanguage
import com.rickbusarow.statik.name.StatikName
import com.rickbusarow.statik.name.UnqualifiedAndroidResourceName
import com.rickbusarow.statik.name.UnqualifiedAndroidResourceReferenceName
import com.rickbusarow.statik.name.asDeclaredName
import com.rickbusarow.statik.utils.lazy.LazyDeferred
import com.rickbusarow.statik.utils.lazy.LazySet
import com.rickbusarow.statik.utils.trace.Trace
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

interface StatikNameTest : TrimmedAsserts {

  val defaultLanguage: StatikLanguage

  class JvmFileBuilder {

    val referenceNames: MutableList<ReferenceName> = mutableListOf()
    val apiReferenceNames: MutableList<ReferenceName> = mutableListOf()
    val declarations: MutableList<QualifiedDeclaredName> = mutableListOf()

    fun references(builder: NormalReferenceBuilder.() -> Unit) {
      NormalReferenceBuilder().builder()
    }

    fun apiReferences(builder: ApiReferenceBuilder.() -> Unit) {
      ApiReferenceBuilder().builder()
    }

    fun declarations(builder: DeclarationsBuilder.() -> Unit) {
      DeclarationsBuilder().builder()
    }

    open class ReferenceBuilder(
      private val target: MutableList<ReferenceName>
    ) {

      fun androidR(packageName: PackageName = PackageName("com.test")): AndroidRReferenceName =
        AndroidRReferenceName(packageName, StatikLanguage.XML)
          .also { target.add(it) }

      fun androidDataBinding(name: String): AndroidDataBindingReferenceName =
        AndroidDataBindingReferenceName(name, StatikLanguage.XML)
          .also { target.add(it) }

      fun qualifiedAndroidResource(name: String): QualifiedAndroidResourceReferenceName =
        QualifiedAndroidResourceReferenceName(name, StatikLanguage.XML)
          .also { target.add(it) }

      fun unqualifiedAndroidResource(name: String): UnqualifiedAndroidResourceReferenceName =
        UnqualifiedAndroidResourceReferenceName(name, StatikLanguage.XML)
          .also { target.add(it) }

      fun kotlin(name: String): ReferenceName = ReferenceName(name, StatikLanguage.KOTLIN)
        .also { target.add(it) }

      fun java(name: String): ReferenceName = ReferenceName(name, StatikLanguage.JAVA)
        .also { target.add(it) }
    }

    inner class NormalReferenceBuilder : ReferenceBuilder(referenceNames)

    inner class ApiReferenceBuilder : ReferenceBuilder(apiReferenceNames)

    inner class DeclarationsBuilder {
      fun kotlin(
        name: String,
        packageName: PackageName = PackageName("com.subject")
      ): QualifiedDeclaredName = DeclaredName.kotlin(
        packageName,
        name.stripPackageNameFromFqName(packageName)
      )
        .also { declarations.add(it) }

      fun java(
        name: String,
        packageName: PackageName = PackageName("com.subject")
      ): QualifiedDeclaredName = DeclaredName.java(
        packageName,
        name.stripPackageNameFromFqName(packageName)
      )
        .also { declarations.add(it) }

      fun agnostic(
        name: String,
        packageName: PackageName = PackageName("com.subject")
      ): QualifiedDeclaredName = name.stripPackageNameFromFqName(packageName)
        .asDeclaredName(packageName)
        .also { declarations.add(it) }
    }
  }

  // infix fun JvmFile.shouldBeJvmFile(config: JvmFileBuilder.() -> Unit) {
  //
  //   val other = JvmFileBuilder().also { it.config() }
  //
  //   assertSoftly {
  //     "references".asClue {
  //       references shouldBe other.referenceNames
  //     }
  //     "api references".asClue {
  //       apiReferences shouldBe other.apiReferenceNames
  //     }
  //     "declarations".asClue {
  //       declarations shouldBe other.declarations
  //     }
  //   }
  // }

  infix fun Collection<QualifiedDeclaredName>.shouldBe(other: Collection<QualifiedDeclaredName>) {
    prettyPrint().trimmedShouldBe(other.prettyPrint(), StatikNameTest::class)
  }

  infix fun LazySet<ReferenceName>.shouldBe(other: Collection<ReferenceName>) {
    runBlocking(Trace.start(StatikNameTest::class)) {
      toList()
        .distinct()
        .prettyPrint().trimmedShouldBe(other.prettyPrint(), StatikNameTest::class)
    }
  }

  infix fun LazyDeferred<Set<ReferenceName>>.shouldBe(other: Collection<ReferenceName>) {
    runBlocking(Trace.start(StatikNameTest::class)) {
      await()
        .distinct()
        .prettyPrint().trimmedShouldBe(other.prettyPrint(), StatikNameTest::class)
    }
  }

  infix fun List<LazySet.DataSource<ReferenceName>>.shouldBe(other: Collection<ReferenceName>) {
    runBlocking(Trace.start(StatikNameTest::class)) {
      flatMap { it.get() }
        .distinct()
        .prettyPrint()
        .trimmedShouldBe(other.prettyPrint(), StatikNameTest::class)
    }
  }

  fun kotlin(
    name: String,
    packageName: PackageName = PackageName("com.subject")
  ): QualifiedDeclaredName =
    DeclaredName.kotlin(packageName, name.stripPackageNameFromFqName(packageName))

  fun java(
    name: String,
    packageName: PackageName = PackageName("com.subject")
  ): QualifiedDeclaredName =
    DeclaredName.java(packageName, name.stripPackageNameFromFqName(packageName))

  fun agnostic(
    name: String,
    packageName: PackageName = PackageName("com.subject")
  ): QualifiedDeclaredName =
    name.stripPackageNameFromFqName(packageName).asDeclaredName(packageName)

  fun String.asReferenceName(): ReferenceName = this@asReferenceName.asReferenceName(
    defaultLanguage
  )

  fun androidR(packageName: PackageName = PackageName("com.test")): AndroidRReferenceName =
    AndroidRReferenceName(packageName, defaultLanguage)

  fun androidDataBinding(name: String): AndroidDataBindingReferenceName =
    AndroidDataBindingReferenceName(name, defaultLanguage)

  fun qualifiedAndroidResource(name: String): QualifiedAndroidResourceReferenceName =
    QualifiedAndroidResourceReferenceName(name, defaultLanguage)

  fun unqualifiedAndroidResource(name: String): UnqualifiedAndroidResourceReferenceName =
    UnqualifiedAndroidResourceReferenceName(name, defaultLanguage)
}

fun Collection<StatikName>.prettyPrint(): String = asSequence()
  .map { statikName ->
    val typeName = when (statikName) {
      // references
      is UnqualifiedAndroidResourceReferenceName -> "unqualifiedAndroidResource"
      is AndroidRReferenceName -> "androidR"
      is QualifiedAndroidResourceReferenceName -> "qualifiedAndroidResource"
      is AndroidDataBindingReferenceName -> "androidDataBinding"
      is ReferenceName -> when {
        statikName.isJava() -> "java"
        statikName.isKotlin() -> "kotlin"
        statikName.isXml() -> "xml"
        else -> throw IllegalArgumentException("???")
      }

      is AndroidRDeclaredName -> "androidR"
      is UnqualifiedAndroidResourceName -> statikName.prefix.asString
      is QualifiedAndroidResourceDeclaredName -> "qualifiedAndroidResource"
      is AndroidDataBindingDeclaredName -> "androidDataBinding"

      // declarations
      is QualifiedDeclaredName -> {
        when {
          statikName.languages.containsAll(
            setOf(StatikLanguage.KOTLIN, StatikLanguage.JAVA)
          ) -> "agnostic"
          statikName.languages.contains(StatikLanguage.KOTLIN) -> "kotlin"
          statikName.languages.contains(StatikLanguage.JAVA) -> "java"
          statikName.languages.contains(StatikLanguage.XML) -> "xml"
          else -> throw IllegalArgumentException("???")
        }
      }
      // package
      is PackageName -> "packageName"
      else -> error("unrecognized type: $statikName")
    }
    typeName to statikName
  }
  .groupBy { it.first }
  .toList()
  .sortedBy { it.first }
  .joinToString("\n") { (typeName, pairs) ->

    pairs.map { it.second }
      .sortedBy { it.asString }
      .joinToString("\n", "$typeName {\n", "\n}") { "\t${it.asString}" }
  }
