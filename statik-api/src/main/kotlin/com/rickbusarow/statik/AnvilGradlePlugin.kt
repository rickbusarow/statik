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

package com.rickbusarow.statik

import com.rickbusarow.statik.name.QualifiedDeclaredName
import com.rickbusarow.statik.name.ReferenceName
import dev.drewhamilton.poko.Poko
import net.swiftzer.semver.SemVer
import org.jetbrains.kotlin.name.FqName

@Poko
class AnvilGradlePlugin(
  val version: SemVer,
  val generateDaggerFactories: Boolean
)

@Poko
class AnvilAnnotatedType(
  val contributedTypeDeclaration: QualifiedDeclaredName,
  val contributedScope: AnvilScopeName
)

@Poko
class RawAnvilAnnotatedType(
  val declaredName: QualifiedDeclaredName,
  val anvilScopeNameEntry: AnvilScopeNameEntry
)

@Poko
class AnvilScopeName(val fqName: FqName) {
  override fun toString(): String = fqName.asString()
}

@Poko
class AnvilScopeNameEntry(val name: ReferenceName)
