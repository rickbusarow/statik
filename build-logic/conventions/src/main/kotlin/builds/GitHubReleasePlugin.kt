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

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.github.breadmoirai.githubreleaseplugin.GithubReleasePlugin
import com.rickbusarow.kgx.applyOnce
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class GitHubReleasePlugin : Plugin<Project> {
  override fun apply(target: Project) {

    target.plugins.applyOnce<GithubReleasePlugin>()

    target.extensions.configure(GithubReleaseExtension::class.java) { release ->
      release.token {
        target.properties["GITHUB_PERSONAL_ACCESS_TOKEN"] as? String
          ?: throw GradleException(
            "In order to release, you must provide a GitHub Personal Access Token " +
              "as a property named 'GITHUB_PERSONAL_ACCESS_TOKEN'."
          )
      }
      release.owner.set("rickbusarow")

      release.generateReleaseNotes.set(false)
      release.overwrite.set(false)
      release.dryRun.set(false)
      release.draft.set(false)

      release.tagName.set(target.VERSION_NAME)
      release.releaseName.set(target.VERSION_NAME)

      release.body.set(
        target.provider {
          if (target.versionIsSnapshot) {
            throw GradleException(
              "do not create a GitHub release for a snapshot. (version is $${target.VERSION_NAME})."
            )
          }

          val versionHeaderRegex = """## \[?$SEMVER_REGEX]?(?: .*)?""".toRegex()

          val split = target.file("CHANGELOG.md").readLines()
            .splitInclusive { versionHeaderRegex.matches(it) }

          split.singleOrNull { it[0].startsWith("## [${target.VERSION_NAME}]") }
            ?.joinToString("\n") { it.trim() }
            ?.trim()
            ?.also { body ->

              if (body.isBlank()) {
                throw GradleException("The changelog for this version cannot be blank.")
              }
            }
            ?: throw GradleException(
              "There should be exactly one Changelog header matching ${target.VERSION_NAME}, " +
                "but there are ${split.size}:\n" +
                split.map { it.first() }.joinToString("\n") { "\t$it" }
            )
        }
      )
    }
  }
}
