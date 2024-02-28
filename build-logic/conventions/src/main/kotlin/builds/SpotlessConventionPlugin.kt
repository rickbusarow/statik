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

import com.diffplug.gradle.spotless.FormatExtension
import com.diffplug.gradle.spotless.GroovyGradleExtension
import com.diffplug.gradle.spotless.JavascriptExtension
import com.diffplug.gradle.spotless.JsonExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.diffplug.gradle.spotless.SpotlessTask
import com.rickbusarow.kgx.EagerGradleApi
import com.rickbusarow.kgx.allProjectsTasksMatchingName
import com.rickbusarow.kgx.checkProjectIsRoot
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.util.PatternFilterable

abstract class SpotlessConventionPlugin : Plugin<Project> {
  @OptIn(EagerGradleApi::class)
  override fun apply(target: Project) {

    target.checkProjectIsRoot()

    target.plugins.apply(SpotlessPlugin::class.java)

    target.tasks.withType(SpotlessTask::class.java).configureEach { spotlessTask ->
      spotlessTask.mustRunAfter(":artifactsDump")
      spotlessTask.mustRunAfter(target.allProjectsTasksMatchingName("apiDump"))
      spotlessTask.mustRunAfter(target.allProjectsTasksMatchingName("dependencyGuard"))
      spotlessTask.mustRunAfter(target.allProjectsTasksMatchingName("dependencyGuardBaseline"))
    }

    target.extensions.configure(SpotlessExtension::class.java) { spotless ->

      spotless.addYaml(target)
      spotless.addJson(target)
      spotless.addJavascript(target)
      spotless.addMarkdown(target)
    }
  }

  private fun SpotlessExtension.addYaml(target: Project) {
    format("yaml") { yaml ->

      yaml.target(target) {
        include("**/*.yml")
      }

      yaml.prettier(target.libs.versions.prettier.get())
    }
  }

  private fun SpotlessExtension.addJson(target: Project) {
    format("json", JsonExtension::class.java) { json ->

      json.target(target) {
        include("website/**/*.json")
        include("**/*.json")
      }
      json.simple().indentWithSpaces(2)
    }
  }

  private fun SpotlessExtension.addMarkdown(target: Project) {
    format("markdown") { markdown ->
      markdown.target(target) {
        include("**/*.md")
        include("**/*.mdx")
      }

      markdown.prettier(target.libs.versions.prettier.get())

      markdown.withinBlocksRegex(
        "groovy block in markdown",
        //language=regexp
        """```groovy.*\n((?:(?!```)[\s\S])*)""",
        GroovyGradleExtension::class.java
      ) { groovyGradle ->
        groovyGradle.greclipse()
        groovyGradle.indentWithSpaces(2)
      }
    }
  }

  private fun SpotlessExtension.addJavascript(target: Project) {
    format("javascript", JavascriptExtension::class.java) { js ->

      js.target(target) {
        include("**/*.js")
      }

      js.prettier()
    }
  }

  private fun ConfigurableFileTree.commonExcludes(target: Project): PatternFilterable {
    return exclude(
      target.subprojects.flatMap { subproject ->
        listOf(
          "${subproject.file("api")}/**",
          "${subproject.file("dependencies")}/**"
        )
      }
    )
      .exclude(
        "**/.docusaurus/**",
        "**/build/**",
        "**/dokka-archive/**",
        "**/node_modules/**",
        "website/static/api/**",
        "artifacts.json",
        ".gradle/**",
        ".git/**"
      )
  }

  private inline fun FormatExtension.target(
    target: Project,
    crossinline fileTreeConfig: ConfigurableFileTree.() -> Unit
  ) {
    target(
      target.fileTree(target.projectDir) {
        fileTreeConfig(it)
        it.commonExcludes(target)
      }
    )
  }
}
