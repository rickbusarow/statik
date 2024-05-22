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

package com.rickbusarow.statik.element.kotlin.k1.testing

import com.rickbusarow.statik.utils.stdlib.requireNotNull
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.incremental.isKotlinFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf
import java.io.File

/**
 * @return a depth-first traversal of all nested elements of type [T],
 *   including the nested elements of children, their children, etc.
 */
inline fun <reified T : PsiElement> PsiElement.getChildrenOfTypeRecursive(): Sequence<T> {
  return generateSequence(children.asSequence()) { children ->
    children.flatMap { it.children.asSequence() }
      .takeIf { it.iterator().hasNext() }
  }
    .flatten()
    .filterIsInstance<T>()
}

/** true if the property's type is nullable, such as in `val name: String?` */
val KtProperty.isNullable: Boolean
  get() = typeReference?.typeElement is KtNullableType

/**
 * @return true if the file exists in the Java file system
 *   and has an extension of `.kt` or `.kts`, otherwise false
 */
fun File.isKotlinFile(): Boolean = exists() && isKotlinFile(listOf("kts", "kt"))

/**
 * @return true if the file exists in the Java file system
 *   and has an extension of `.kts`, otherwise false
 */
fun File.isKotlinScriptFile(): Boolean = exists() && isKotlinFile(listOf("kts"))

/**
 * @return true if the file exists in the Java file
 *   system and has an extension of `.kt`, otherwise false
 */
fun File.isKtFile(): Boolean = exists() && isKotlinFile(listOf("kt"))

/** @return */
fun KtNamedDeclaration.simpleNames(): List<String> {
  val parents = parentsWithSelf.filterIsInstance<KtNamedDeclaration>()

  return parents.toList()
    .reversed()
    .map { it.nameAsSafeName.asString() }
}

/**
 * Returns a single [KtNamedFunction] from this class with the requested [functionName].
 *
 * This is an alternative to
 * [findFunctionByName][org.jetbrains.kotlin.psi.psiUtil.findFunctionByName] since that
 * function returns a [KtNamedDeclaration][org.jetbrains.kotlin.psi.KtNamedDeclaration]
 *
 * @return a [KtNamedFunction] from this class with this name, or throws if one cannot be found
 * @throws IllegalArgumentException if there is no function with
 *   this name or if there are multiple functions with that name
 */
fun KtClassOrObject.functionNamed(functionName: String): KtNamedFunction {
  val functions = declarations.filterIsInstance<KtNamedFunction>()

  return functions.singleOrNull { it.name == functionName }
    .requireNotNull { "could not find a function '$functionName' in ${functions.map { it.name }}" }
}

/**
 * Returns a single [KtProperty] from this class with the requested [name].
 *
 * This is an alternative to
 * [findPropertyByName][org.jetbrains.kotlin.psi.psiUtil.findPropertyByName] since that
 * function returns a [KtNamedDeclaration][org.jetbrains.kotlin.psi.KtNamedDeclaration]
 *
 * @return a [KtProperty] from this class with this name, or throws if one cannot be found
 * @see parameterNamed
 * @see propertyNamedOrNull for a nullable version which does not throw if there is no match
 * @throws IllegalArgumentException if there is no property with
 *   this name or if there are multiple properties with that name
 */
fun KtClassOrObject.propertyNamed(name: String): KtProperty {

  val declaration = findPropertyByName(name)
    .requireNotNull { "Could not find a named property or parameter for $name" }

  check(declaration is KtProperty) {
    "This 'property' with the name '$name' isn't a property, but a ${declaration::class.simpleName}.  " +
      "Use `parameterNamed($name)` or the JetBrains `findPropertyByName($name)` extension instead " +
      "and do your own cast if needed."
  }

  return declaration
}

/**
 * Returns a single [KtProperty] from this class with the requested [name],
 * or null if there is no property with that name. This is a safe version
 *
 * This is an alternative to
 * [findPropertyByName][org.jetbrains.kotlin.psi.psiUtil.findPropertyByName] since that
 * function returns a [KtNamedDeclaration][org.jetbrains.kotlin.psi.KtNamedDeclaration]
 *
 * @return a [KtProperty] from this class with this name, or null if one cannot be found
 * @see parameterNamed
 * @see propertyNamed for a non-nullable version which throws if no match is found
 */
fun KtClassOrObject.propertyNamedOrNull(name: String): KtProperty? {

  val declaration = findPropertyByName(name) ?: return null

  check(declaration is KtProperty) {
    "This 'property' with the name '$name' isn't a property, but a ${declaration::class.simpleName}.  " +
      "Use `parameterNamed($name)` or the JetBrains `findPropertyByName($name)` extension instead " +
      "and do your own cast if needed."
  }

  return declaration
}

/**
 * Returns a single [KtProperty] from this class with the requested [name].
 *
 * This is an alternative to
 * [findPropertyByName][org.jetbrains.kotlin.psi.psiUtil.findPropertyByName] since that
 * function returns a [KtNamedDeclaration][org.jetbrains.kotlin.psi.KtNamedDeclaration]
 *
 * @return a [KtProperty] from this class with this name, or throws if one cannot be found
 * @see parameterNamed
 * @throws IllegalArgumentException if there is no function with
 *   this name or if there are multiple functions with that name
 */
fun KtClassOrObject.parameterNamed(name: String): KtParameter {

  val declaration = findPropertyByName(name)
    .requireNotNull {

      val parameters = primaryConstructorParameters.joinToString(
        separator = "\n  ",
        prefix = "  "
      ) {
        it.name.toString()
      }

      val properties = (this as KtClass).getProperties()
        .joinToString(separator = "\n  ", prefix = "  ") { it.name.toString() }

      "Could not find a named property or parameter named: $name\n" +
        "existing primary constructor parameters:\n$parameters\n\n" +
        "existing properties:\n$properties"
    }

  check(declaration is KtParameter) {
    "This 'parameter' with the name '$name' isn't a parameter, but a ${declaration::class.simpleName}.  " +
      "Use `propertyNamed($name)` or the JetBrains `findPropertyByName($name)` extension instead " +
      "and do your own cast if needed."
  }

  return declaration
}

/**
 * @return the list of names for each function parameter. Note
 *   that these names will not include any wrapping backticks.
 * @see valueParameterIdentifiers for a version which includes backticks
 */
fun KtFunction.valueParameterNames(): List<String> = valueParameters
  .map { it.name.requireNotNull() }

/**
 * @return the list of names for each function parameter. Note
 *   that these names will include any wrapping backticks.
 * @see valueParameterNames for a version which does not include backticks
 */
fun KtFunction.valueParameterIdentifiers(): List<String> = valueParameters
  .map { it.identifyingElement.requireNotNull().text }

/**
 * @param name the simple name of the desired parameter
 * @return a parameter in this receiver function with a name of
 *   [name]. An exception is thrown if there is no matching parameter.
 * @throws IllegalArgumentException if there is no parameter with this name
 */
fun KtFunction.parameterNamed(name: String): KtParameter = valueParameters
  .singleOrNull { it.name == name }
  .requireNotNull { "could not find a parameter '$name' in ${valueParameters.map { it.name }}" }
