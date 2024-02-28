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

import com.rickbusarow.kgx.applyOnce
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.testing.Test
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

abstract class IntegrationTestsConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.plugins.applyOnce("idea")

    target.extensions.configure(JavaPluginExtension::class.java) { java ->

      val integrationTest = java.sourceSets
        .register(INTEGRATION_TEST) { ss ->

          ss.compileClasspath += target.javaSourceSet("main")
            .output
            .plus(target.javaSourceSet("test").output)
            .plus(target.configurations.getByName("testRuntimeClasspath"))

          ss.runtimeClasspath += ss.output + ss.compileClasspath
        }

      target.extensions.configure(IdeaModel::class.java) { idea ->
        idea.module { module ->
          integrationTest.configure { ss ->
            ss.allSource.srcDirs.forEach { srcDir ->
              module.testSources.from(srcDir)
            }
          }
        }
      }
    }

    target.configurations.register("${INTEGRATION_TEST}Compile") {
      it.extendsFrom(target.configurations.getByName("testCompileOnly"))
    }
    target.configurations.register("${INTEGRATION_TEST}Runtime") {
      it.extendsFrom(target.configurations.getByName("testRuntimeOnly"))
    }

    val integrationTestTask = target.tasks
      .register(INTEGRATION_TEST, Test::class.java) { task ->

        task.group = "verification"
        task.description = "tests the '$INTEGRATION_TEST' source set"

        val mainSourceSet = target.javaSourceSet("main")
        val integrationTestSourceSet = target.javaSourceSet(INTEGRATION_TEST)

        task.testClassesDirs = integrationTestSourceSet.output.classesDirs
        task.classpath = integrationTestSourceSet.runtimeClasspath
        task.inputs.files(integrationTestSourceSet.output.classesDirs)
        task.inputs.files(mainSourceSet.allSource)
        task.dependsOn(target.rootProject.tasks.named("publishToMavenLocalNoDokka"))
      }

    target.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) {
      it.dependsOn(integrationTestTask)
    }

    target.extensions.configure(KotlinJvmProjectExtension::class.java) { kotlin ->
      val compilations = kotlin.target.compilations

      compilations.getByName(INTEGRATION_TEST) {
        it.associateWith(compilations.getByName("main"))
      }
    }
  }

  private fun Project.javaSourceSet(name: String): SourceSet =
    extensions.getByType(JavaPluginExtension::class.java)
      .sourceSets
      .getByName(name)

  companion object {
    private const val INTEGRATION_TEST = "integrationTest"
  }
}
