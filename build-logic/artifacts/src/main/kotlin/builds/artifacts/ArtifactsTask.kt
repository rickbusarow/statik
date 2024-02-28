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

package builds.artifacts

import builds.existsOrNull
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile

abstract class ArtifactsTask(
  private val projectLayout: ProjectLayout
) : DefaultTask() {

  /**
   * This file contains all definitions for published artifacts.
   *
   * It's located at the root of the project, assuming that the task is run from the root project.
   *
   * @since 0.1.0
   */
  @get:OutputFile
  protected val reportFile: RegularFile by lazy {
    projectLayout.projectDirectory.file("artifacts.json")
  }

  /**
   * All artifacts as they are defined in the project right now.
   *
   * This is a lazy delegate because it's accessing [project], and Gradle's configuration caching
   * doesn't allow direct references to `project` in task properties or inside task actions.
   * Somehow, it doesn't complain about this even though it's definitely accessed at runtime.
   *
   * @since 0.1.0
   */
  @get:Internal
  protected val currentList by lazy { project.createArtifactList() }

  @get:Internal
  protected val moshiAdapter: JsonAdapter<List<ArtifactConfig>> by lazy {

    val type = Types.newParameterizedType(
      List::class.java,
      ArtifactConfig::class.java
    )

    Moshi.Builder()
      .build()
      .adapter(type)
  }

  @get:Internal
  protected val baselineArtifacts by lazy {
    moshiAdapter
      .fromJson(
        // If the file doesn't exist, there may not be any published artifacts.
        // Just pass in an empty array so that we're not forced to create an empty file.
        reportFile.asFile.existsOrNull()?.readText() ?: "[]"
      )
      .orEmpty()
  }

  private fun Project.createArtifactList(): List<ArtifactConfig> {

    val map = subprojects
      .flatMap { sub ->
        sub.extensions.findByType(PublishingExtension::class.java)
          ?.publications
          ?.filterIsInstance<MavenPublication>()
          .orEmpty()
          .map { publication -> sub to publication }
      }
      .mapNotNull { (sub, publication) ->

        val group: String? = publication.groupId
        val artifactId: String? = publication.artifactId
        val pomDescription: String? = publication.pom.description.orNull
        val packaging: String? = publication.pom.packaging

        listOfNotNull(group, artifactId, pomDescription, packaging)
          .also { allProperties ->

            require(allProperties.isEmpty() || allProperties.size == 4) {
              "expected all properties to be null or none to be null for project `${sub.path}, " +
                "but got:\n" +
                "group : $group\n" +
                "artifactId : $artifactId\n" +
                "pom description : $pomDescription\n" +
                "packaging : $packaging"
            }
          }
          .takeIf { it.size == 4 }
          ?.let { (group, artifactId, pomDescription, packaging) ->

            // TODO Try to find something closer to the bare metal.  Ultimately, what matters
            //  is what's written in the .module file. The JavaPluginExtension doesn't necessarily
            //  represent what's in the .module file. If setting `JavaCompile.options.release`, that
            //  will change what's in .module but that won't be reflected upstream in the extension.
            val javaVersion = sub.extensions
              .getByType(JavaPluginExtension::class.java)
              .sourceCompatibility
              .toString()

            ArtifactConfig(
              gradlePath = sub.path,
              group = group,
              artifactId = artifactId,
              description = pomDescription,
              packaging = packaging,
              javaVersion = javaVersion,
              publicationName = publication.name
            )
          }
      }

    return map
  }

  protected fun ArtifactConfig.isIgnored(): Boolean {

    val inMacOS = Os.isFamily(Os.FAMILY_MAC)
    // The macOS artifacts all have publication names like 'iosX64', 'watchosX86', 'iosSimulatorArm64', etc.
    val macOnly = """^(?:ios|tvos|watchos|macos).*""".toRegex()

    return !inMacOS && publicationName.matches(macOnly)
  }

  protected fun ignoredArtifactsMessage(ignored: List<ArtifactConfig>): String = buildString {
    append("The existing artifacts file references artifacts which can only be created ")
    append("from a computer running macOS, so they cannot be validated now ")
    appendLine("(running ${System.getProperty("os.name")}).")
    appendLine()
    appendLine("\tThese artifacts cannot be checked:")
    appendLine()
    ignored.forEach {
      appendLine(it.message())
      appendLine()
    }
  }

  protected fun ArtifactConfig.message(): String {
    return """
            |                     gradlePath  - $gradlePath
            |                          group  - $group
            |                     artifactId  - $artifactId
            |                pom description  - $description
            |                      packaging  - $packaging
            |                publicationName  - $publicationName
    """.trimMargin()
  }
}
