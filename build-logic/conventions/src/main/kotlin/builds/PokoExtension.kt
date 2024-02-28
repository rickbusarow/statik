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

package builds

import com.rickbusarow.kgx.libsCatalog
import com.rickbusarow.kgx.pluginId
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleVersionSelector

interface PokoExtension {

  fun Project.poko() {

    val implementation = configurations.getByName("implementation")
    val testCompileOnly = configurations.getByName("testCompileOnly")

    val pokoAnnotationsProvider = project.libs.poko.annotations
    val pokoAnnotations = pokoAnnotationsProvider.get()
    val pokoAnnotationsModule = pokoAnnotations.module

    implementation.withDependencies { deps ->
      deps.removeIf {
        pokoAnnotationsModule == (it as? ModuleVersionSelector)?.module
      }
    }

    val compileOnly = configurations.getByName("compileOnly")

    // Poko adds its annotation artifact as 'implementation', which is unnecessary.
    // Replace it with a 'compileOnly' dependency.
    compileOnly.dependencies.addLater(pokoAnnotationsProvider)
    testCompileOnly.dependencies.addLater(pokoAnnotationsProvider)

    pluginManager.apply(libsCatalog.pluginId("poko"))
  }
}
