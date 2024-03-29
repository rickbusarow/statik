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

import com.rickbusarow.statik.name.AndroidRName.AndroidResourceName
import com.rickbusarow.statik.name.SimpleName.Companion.asSimpleName
import com.rickbusarow.statik.name.SimpleName.Companion.asString
import com.rickbusarow.statik.utils.lazy.unsafeLazy
import java.io.File
import kotlin.io.path.name

/**
 * example: `string.app_name`
 *
 * @property prefix 'string' in `R.string.app_name`
 * @property identifier 'app_name' in `R.string.app_name`
 */
public class UnqualifiedAndroidResourceName private constructor(
  override val prefix: SimpleName,
  override val identifier: SimpleName
) : AndroidResourceName {

  override val simpleNames: List<SimpleName> by unsafeLazy {
    listOf("R".asSimpleName(), prefix, identifier)
  }
  override val segments: List<String> by unsafeLazy { simpleNames.map { it.asString } }
  override val asString: String by unsafeLazy { simpleNames.asString() }

  /**
   * @return the fully qualified name of a generated Android resource, like
   *   `com.example.R.string.app_name` from the combination of `com.example.R` and `string.app_name`
   */
  public fun toAndroidResourceNameWithRName(
    androidRDeclaration: AndroidRName
  ): AndroidResourceNameWithRName {
    return AndroidName.qualifiedAndroidResource(
      sourceR = AndroidRName(androidRDeclaration.packageName),
      sourceResource = this
    )
  }

  public companion object {

    private val XML_REGEX = """"?@\+?(.*)/(.*)"?""".toRegex()

    /** `anim.foo` */
    public fun anim(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("anim".asSimpleName(), identifier = identifier)

    /** `animator.foo` */
    public fun animator(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("animator".asSimpleName(), identifier = identifier)

    /** `array.foo` */
    public fun array(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("array".asSimpleName(), identifier = identifier)

    /** `bool.foo` */
    public fun bool(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("bool".asSimpleName(), identifier = identifier)

    /** `color.foo` */
    public fun color(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("color".asSimpleName(), identifier = identifier)

    /** `dimen.foo` */
    public fun dimen(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("dimen".asSimpleName(), identifier = identifier)

    /** `drawable.foo` */
    public fun drawable(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("drawable".asSimpleName(), identifier = identifier)

    /** `font.foo` */
    public fun font(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("font".asSimpleName(), identifier = identifier)

    /** `id.foo` */
    public fun id(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("id".asSimpleName(), identifier = identifier)

    /** `integer.foo` */
    public fun integer(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("integer".asSimpleName(), identifier = identifier)

    /** `layout.foo` */
    public fun layout(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("layout".asSimpleName(), identifier = identifier)

    /** `menu.foo` */
    public fun menu(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("menu".asSimpleName(), identifier = identifier)

    /** `mipmap.foo` */
    public fun mipmap(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("mipmap".asSimpleName(), identifier = identifier)

    /** `raw.foo` */
    public fun raw(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("raw".asSimpleName(), identifier = identifier)

    /** `string.foo` */
    public fun string(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("string".asSimpleName(), identifier = identifier)

    /** `style.foo` */
    public fun style(identifier: SimpleName): UnqualifiedAndroidResourceName =
      UnqualifiedAndroidResourceName("style".asSimpleName(), identifier = identifier)

    /** @return all resources declared within the given [file] */
    public fun fromFile(file: File): UnqualifiedAndroidResourceName? {
      val dir = file.toPath().parent?.name ?: return null
      val name = file.nameWithoutExtension

      return when {
        dir.startsWith("anim") -> anim(name.asSimpleName())
        dir.startsWith("animator") -> animator(name.asSimpleName())
        dir.startsWith("color") -> color(name.asSimpleName())
        dir.startsWith("dimen") -> dimen(name.asSimpleName())
        dir.startsWith("drawable") -> drawable(name.asSimpleName())
        dir.startsWith("font") -> font(name.asSimpleName())
        dir.startsWith("layout") -> layout(name.asSimpleName())
        dir.startsWith("menu") -> menu(name.asSimpleName())
        dir.startsWith("mipmap") -> mipmap(name.asSimpleName())
        dir.startsWith("raw") -> raw(name.asSimpleName())
        else -> null
      }
    }

    /** @return `id.foo` for [type] `id` and [name] `foo` */
    public fun fromValuePair(type: String, name: String): UnqualifiedAndroidResourceName? {
      val fixedName = name.replace('.', '_')
      return when (type.removePrefix("android:")) {
        "anim" -> anim(fixedName.asSimpleName())
        "animator" -> animator(fixedName.asSimpleName())
        "array" -> array(fixedName.asSimpleName())
        "bool" -> bool(fixedName.asSimpleName())
        "color" -> color(fixedName.asSimpleName())
        "dimen" -> dimen(fixedName.asSimpleName())
        "drawable" -> drawable(fixedName.asSimpleName())
        "font" -> font(fixedName.asSimpleName())
        "id" -> id(fixedName.asSimpleName())
        "integer" -> integer(fixedName.asSimpleName())
        "integer-array" -> array(fixedName.asSimpleName())
        "layout" -> layout(fixedName.asSimpleName())
        "menu" -> menu(fixedName.asSimpleName())
        "mipmap" -> mipmap(fixedName.asSimpleName())
        "raw" -> raw(fixedName.asSimpleName())
        "string" -> string(fixedName.asSimpleName())
        "style" -> style(fixedName.asSimpleName())
        else -> null
      }
    }

    /** @return a resource declaration from a string in XML, like `@+id/______` */
    public fun fromXmlString(str: String): UnqualifiedAndroidResourceName? {
      val (prefix, name) = XML_REGEX.find(str)?.destructured ?: return null

      return fromValuePair(prefix, name)
    }
  }
}
