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

package com.rickbusarow.statik.name

import com.rickbusarow.statik.name.PackageName.Companion.asPackageName
import com.rickbusarow.statik.name.SimpleName.Companion.asSimpleName
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import com.rickbusarow.statik.utils.stdlib.letIf
import com.rickbusarow.statik.utils.stdlib.pluralString
import dev.drewhamilton.poko.Poko

/** either a [ClassName] or a [TypeParameter] */
public sealed interface TypeName : StatikName, HasSimpleNames {
  /** */
  public val nullable: Boolean

  /** @return a new instance of [TypeName] with nullability set to true. */
  public fun makeNullable(): TypeName

  /** @return a new instance of [TypeName] with nullability set to false. */
  public fun makeNotNullable(): TypeName
}

/**
 * Represents a class name in the Kotlin language. It includes the
 * package name, simple names, type arguments, and nullability.
 *
 * @property packageName The package name of the class.
 * @property simpleNames The list of simple names of the class.
 * @property typeArguments The list of type arguments of the class.
 * @property nullable Indicates if the class name is nullable.
 */
@Poko
public class ClassName(
  override val packageName: PackageName,
  override val simpleNames: List<SimpleName>,
  public val typeArguments: List<TypeName>,
  override val nullable: Boolean
) : TypeName, NameWithPackageName {

  override val asString: String by unsafeLazy {
    packageName.appendAsString(simpleNames)
      .letIf(nullable) { it.plus("?") }
  }

  /** ex: `com.example.MyGenericType<out T: SomeType>` */
  public val asStringWithTypeParameters: String by unsafeLazy {
    asString.letIf(typeArguments.isNotEmpty()) {
      it.plus(
        typeArguments.joinToString(
          separator = ", ",
          prefix = "<",
          postfix = ">",
          transform = TypeName::asString
        )
      )
    }
  }

  public constructor(
    packageName: String,
    vararg simpleNames: String,
    typeArguments: List<TypeName> = emptyList(),
    nullable: Boolean = false
  ) : this(
    packageName = packageName.asPackageName(),
    simpleNames = simpleNames.map { it.asSimpleName() },
    typeArguments = typeArguments,
    nullable = nullable
  )

  public fun copy(
    packageName: PackageName = this.packageName,
    simpleNames: List<SimpleName> = this.simpleNames,
    typeArguments: List<TypeName> = this.typeArguments,
    nullable: Boolean = this.nullable
  ): ClassName = ClassName(
    packageName = packageName,
    simpleNames = simpleNames,
    typeArguments = typeArguments,
    nullable = nullable
  )

  /** @return a new instance of [ClassName] with nullability set to true. */
  override fun makeNullable(): TypeName = copy(nullable = true)

  /** @return a new instance of [ClassName] with nullability set to false. */
  override fun makeNotNullable(): TypeName = copy(nullable = false)

  /**
   * @param typeArguments The type arguments to parameterize the class name with.
   * @return a new instance of [ClassName] with the provided type arguments.
   */
  public fun parameterizedBy(vararg typeArguments: TypeName): ClassName =
    copy(typeArguments = typeArguments.toList())
}

/**
 * examples:
 * ```
 * T : CharSequence
 * T
 * /*...*/ T /*...*/ where T: Bar, T: Baz
 * ```
 *
 * @property simpleName the simple name given to the generic, like `T` or `OutputT`
 * @property bounds empty if it's a simple generic
 * @property nullable `<T?>` vs `<T>`
 * @property variance The variance of the type parameter, can be either `IN`, `OUT`, or `null`.
 */
@Poko
public class TypeParameter(
  override val simpleName: SimpleName,
  public val bounds: List<TypeName>,
  override val nullable: Boolean,
  public val variance: Variance?
) : TypeName {

  override val segments: List<String> get() = listOf(simpleName.asString)
  override val simpleNames: List<SimpleName> get() = listOf(simpleName)
  override val asString: String by unsafeLazy {
    bounds.pluralString(
      empty = { simpleName.asString },
      single = { "${simpleName.asString} : ${it.asString}" },
      moreThanOne = { simpleName.asString }
    )
  }

  public constructor(
    name: SimpleName,
    vararg bounds: TypeName
  ) : this(simpleName = name, bounds = bounds.toList(), nullable = false, variance = null)

  public fun copy(
    simpleName: SimpleName = this.simpleName,
    bounds: List<TypeName> = this.bounds,
    nullable: Boolean = this.nullable,
    variance: Variance? = this.variance
  ): TypeParameter = TypeParameter(
    simpleName = simpleName,
    bounds = bounds,
    nullable = nullable,
    variance = variance
  )

  /** @return a new instance of [TypeParameter] with nullability set to true. */
  override fun makeNullable(): TypeName = copy(nullable = true)

  /** @return a new instance of [TypeParameter] with nullability set to false. */
  override fun makeNotNullable(): TypeName = copy(nullable = false)

  /** Represents the variance of a type parameter in the Kotlin language. */
  public enum class Variance {
    OUT,
    IN
  }
}
