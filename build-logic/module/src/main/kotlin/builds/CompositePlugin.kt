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

import builds.artifacts.ArtifactsCheckTask
import builds.artifacts.ArtifactsDumpTask
import com.autonomousapps.tasks.BuildHealthTask
import com.github.jengelman.gradle.plugins.shadow.internal.JavaJarExec
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.rickbusarow.kgx.checkProjectIsRoot
import com.rickbusarow.kgx.dependsOn
import com.rickbusarow.kgx.internal.InternalGradleApiAccess
import com.rickbusarow.kgx.internal.allProjects
import com.rickbusarow.kgx.undecoratedTypeName
import com.rickbusarow.ktlint.KtLintTask
import io.gitlab.arturbosch.detekt.Detekt
import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import modulecheck.gradle.task.AbstractModuleCheckTask
import modulecheck.gradle.task.MultiRuleModuleCheckTask
import modulecheck.gradle.task.SingleRuleModuleCheckTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.testing.Test
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaTask

abstract class CompositePlugin : Plugin<Project> {
  @OptIn(InternalGradleApiAccess::class)
  override fun apply(target: Project) {

    target.checkProjectIsRoot()
    require(target.gradle.includedBuilds.isNotEmpty()) {
      "Only apply the 'composite' plugin to a root project with included builds.  " +
        "This project has no included builds, " +
        "so the plugin would just waste time searching the task graph."
    }

    target.afterEvaluate {

      val propagatedTaskTypes = listOf(
        /* api validation */
        KotlinApiBuildTask::class, KotlinApiCompareTask::class,
        /* artifacts-check */
        ArtifactsCheckTask::class, ArtifactsDumpTask::class,
        /* DAGP */
        BuildHealthTask::class,
        /* ModuleCheck */
        AbstractModuleCheckTask::class, MultiRuleModuleCheckTask::class, SingleRuleModuleCheckTask::class,
        /* detekt */
        Detekt::class,
        /* dokka */
        AbstractDokkaTask::class, DokkaMultiModuleTask::class, DokkaTask::class,
        /* gradle */
        Copy::class, Delete::class, Exec::class, JavaExec::class, Sync::class, Test::class, Zip::class,
        /* ktlint-gradle-plugin */
        KtLintTask::class,
        /* publishing */
        AbstractPublishToMaven::class, PublishToMavenLocal::class,
        /* shadow */
        JavaJarExec::class, ShadowJar::class
      )
      val propagatedTaskTypeNames = propagatedTaskTypes
        .mapTo(mutableSetOf()) { it.qualifiedName!! }

      // tasks which can only be matched by name, probably because their type is just `DefaultTask`
      val otherNames = setOf(
        "lintKotlin",
        "formatKotlin",
        "ktlintFormat",
        "ktlintCheck",
        "moveProtos",
        "deleteSrcGen",
        "dependencyGuard",
        "dependencyGuardBaseline",
        // The Wire Gradle plugin creates a `generateProtos` which is `DefaultTask`,
        // even though they have a `WireTask` type
        "generateProtos",
        LifecycleBasePlugin.CHECK_TASK_NAME
      )

      // Loop through all included projects, looking for task types which are commonly invoked from the
      // root path.  For each of these, look for same-name tasks in the internal modules of included
      // builds, and make the root task depend upon those as well.
      target.gradle.includedBuilds
        .asSequence()
        .flatMap { it.allProjects() }
        .flatMap { includedProject ->
          includedProject.tasks
            .matching { includedTask ->
              includedTask.name in otherNames ||
                includedTask.undecoratedTypeName() in propagatedTaskTypeNames ||
                propagatedTaskTypes.any { it.isInstance(includedTask) }
            }
            // Various clean tasks are added by the idea plugin after this runs.
            .matching { !it.name.startsWith("cleanIdea") }
            .names
            .map { it to includedProject.tasks.named(it) }
        }
        // Group all tasks from all included projects with the same name, so that we only need to look
        // up the task once for this target project.
        .groupBy { it.first }
        .forEach { (name, pairs) ->

          val taskProviders = pairs.map { it.second }

          if (target.tasks.names.contains(name)) {
            target.tasks.named(name).dependsOn(taskProviders)
          } else {
            target.tasks.register(name, BuildLogicTask::class.java).dependsOn(taskProviders)
          }
        }
    }
  }
}
