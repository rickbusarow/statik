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

import com.rickbusarow.statik.name.SimpleName.Companion.asSimpleName
import com.rickbusarow.statik.name.SimpleName.Companion.asString
import com.rickbusarow.statik.utils.lazy.unsafeLazy

/**
 * - fully qualified generated resources like `com.example.R.string.app_name`
 * - generated data-/view-binding declarations like `com.example.databinding.FragmentListBinding`
 * - unqualified resources which can be consumed in downstream projects, like `R.string.app_name`
 * - R names, like `com.example.R`
 */
public sealed interface AndroidName : StatikName, HasSimpleNames {

  public companion object {
    /** @return example: `com.example.app.R` */
    public fun r(packageName: PackageName): AndroidRName = AndroidRName(packageName)

    /** @return `com.example.R.string.app_name` */
    public fun qualifiedAndroidResource(
      sourceR: AndroidRName,
      sourceResource: UnqualifiedAndroidResourceName
    ): AndroidResourceNameWithRName = AndroidResourceNameWithRName(
      androidRName = sourceR,
      resourceName = sourceResource
    )

    /** @return `com.example.databinding.FragmentListBinding` */
    public fun dataBinding(
      sourceLayout: UnqualifiedAndroidResourceName,
      packageName: PackageName
    ): AndroidDataBindingName = AndroidDataBindingName(
      packageName = packageName,
      sourceLayout = sourceLayout
    )
  }
}

/** example: `com.example.app.R` */
public class AndroidRName(
  override val packageName: PackageName
) : NameWithPackageName, AndroidName {

  override val simpleNames: List<SimpleName> by lazy { listOf("R".asSimpleName()) }

  override val asString: String by unsafeLazy {
    packageName.append(simpleNames.asString())
  }

  /**
   * Models fully qualified names like `com.example.R.string.app_name`
   * or unqualified ones like `string.app_name`.
   */
  public sealed interface AndroidResourceName : AndroidName, StatikName, HasSimpleNames {
    /** example: 'string' in `R.string.app_name` */
    public val prefix: SimpleName

    /** example: 'app_name' in `R.string.app_name` */
    public val identifier: SimpleName
  }
}
