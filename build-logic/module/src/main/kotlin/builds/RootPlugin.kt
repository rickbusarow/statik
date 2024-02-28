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

import builds.artifacts.ArtifactsPlugin
import com.rickbusarow.kgx.checkProjectIsRoot
import com.rickbusarow.kgx.inCI
import com.rickbusarow.kgx.isRealRootProject
import modulecheck.gradle.ModuleCheckExtension
import modulecheck.gradle.ModuleCheckPlugin
import org.gradle.api.Project

/**
 * Applied to the real project root and the root project of any included build except this one.
 *
 * @since 0.1.0
 */
abstract class RootPlugin : BaseModulePlugin() {
  override fun apply(target: Project) {

    target.checkProjectIsRoot()

    target.plugins.apply("com.autonomousapps.dependency-analysis")

    target.extensions.create("root", RootExtension::class.java)

    super.apply(target)

    target.plugins.apply(ArtifactsPlugin::class.java)
    target.plugins.apply(BenManesVersionsPlugin::class.java)
    target.plugins.apply(DokkaVersionArchivePlugin::class.java)
    target.plugins.apply(GitHubReleasePlugin::class.java)
    target.plugins.apply(SpotlessConventionPlugin::class.java)

    target.plugins.apply(ModuleCheckPlugin::class.java)

    target.extensions.configure(ModuleCheckExtension::class.java) { extension ->
      extension.deleteUnused = true
      extension.checks { checks ->
        checks.sortDependencies = true
      }
    }

    // Hack for ensuring that when 'publishToMavenLocal' is invoked from the root project,
    // all subprojects are published.  This is used in plugin tests.
    target.tasks.register("publishToMavenLocal", BuildLogicTask::class.java) {
      target.subprojects.forEach { sub ->
        it.dependsOn(sub.tasks.matching { it.name == "publishToMavenLocal" })
      }
    }
    target.tasks.register("publishToMavenLocalNoDokka", BuildLogicTask::class.java) {
      target.subprojects.forEach { sub ->
        it.dependsOn(sub.tasks.matching { it.name == "publishToMavenLocalNoDokka" })
      }
    }

    if (target.gradle.includedBuilds.isNotEmpty()) {
      target.plugins.apply("composite")
    }

    if (inCI() && target.isRealRootProject()) {
      target.logger.lifecycle("CI environment detected.")
    }
  }
}
