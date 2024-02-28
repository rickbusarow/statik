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

import builds.Color.Companion.colorized
import builds.Color.RED
import builds.Color.YELLOW
import org.gradle.api.GradleException
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import javax.inject.Inject

/**
 * Evaluates all published artifacts in the project and compares the results to `/artifacts.json`.
 *
 * If there are any differences, the task will fail with a descriptive message.
 *
 * @since 0.1.0
 */
open class ArtifactsCheckTask @Inject constructor(
  objectFactory: ObjectFactory,
  projectLayout: ProjectLayout
) : ArtifactsTask(projectLayout) {

  init {
    description = "Parses the Maven artifact parameters for all modules " +
      "and compares them to those recorded in artifacts.json"
    group = "verification"
  }

  private val lenientOsProp: Property<Boolean> = objectFactory.property(Boolean::class.java)

  @set:Option(
    option = "lenient-os",
    description = "Do not fail the check if there are macOS-only artifacts which can't be checked."
  )
  var lenientOs: Boolean
    @Input
    get() = lenientOsProp.getOrElse(false)
    set(value) = lenientOsProp.set(value)

  @TaskAction
  fun run() {

    val expected = getExpectedArtifacts()

    val currentPaths = currentList.mapTo(mutableSetOf()) { it.key }

    val extraFromJson = expected.values.filterNot { it.key in currentPaths }
    val extraFromCurrent = currentList.filterNot { it.key in expected.keys }

    val changed = currentList.minus(expected.values.toSet())
      .minus(extraFromCurrent.toSet())
      .map { artifact ->
        expected.getValue(artifact.key) to artifact
      }

    // Each artifact needs to have a unique ID.  Repository managers will quietly allow overwrites
    // with duplicate IDs, so this is the last chance to catch it before publishing.
    val duplicateArtifactIds = currentList.findDuplicates { artifactId }

    // This is mostly superficial, but it ensures that a copy/pasted module also doesn't retain the
    // original's pom description.
    val duplicateDescriptions = currentList.findDuplicates { description }

    val foundSomething = sequenceOf(
      duplicateArtifactIds.keys,
      duplicateDescriptions.keys,
      extraFromJson,
      extraFromCurrent,
      changed
    ).any { it.isNotEmpty() }

    if (foundSomething) {
      reportChanges(
        duplicateArtifactIds = duplicateArtifactIds,
        duplicatePomDescriptions = duplicateDescriptions,
        missing = extraFromJson,
        extraFromCurrent = extraFromCurrent,
        changed = changed
      )
    }
  }

  private fun getExpectedArtifacts(): Map<String, ArtifactConfig> {

    // If this task isn't running on macOS, ignore the macOS-only artifacts.
    val (ignoredArtifacts, testableArtifacts) = baselineArtifacts
      .partition { it.isIgnored() }

    if (ignoredArtifacts.isNotEmpty()) {

      val message = buildString {

        appendLine(ignoredArtifactsMessage(ignoredArtifacts))

        if (!lenientOs) {
          appendLine()
          append("Add the `--lenient-os` option replace this failure with a warning: ")
          appendLine("./gradlew artifactsCheck --lenient-os")
        }
      }

      if (lenientOs) {
        logger.warn(message.colorized(YELLOW))
      } else {
        logger.error(message.colorized(RED))
        throw GradleException(
          "Artifacts check failed.  There are unchecked macOS-only artifacts " +
            "and lenient-os mode is not enabled."
        )
      }
    }

    return testableArtifacts.associateBy { it.key }
  }

  private fun <R : Comparable<R>> List<ArtifactConfig>.findDuplicates(
    selector: ArtifactConfig.() -> R
  ): Map<R, List<ArtifactConfig>> {
    // Group by publicationName + the value returned by `selector`, because it's fine to have
    // duplicates across different publications.
    return groupBy { it.selector() to it.publicationName }
      .filter { it.value.size > 1 }
      .mapKeys { it.key.first }
  }

  private fun reportChanges(
    duplicateArtifactIds: Map<String, List<ArtifactConfig>>,
    duplicatePomDescriptions: Map<String, List<ArtifactConfig>>,
    missing: List<ArtifactConfig>,
    extraFromCurrent: List<ArtifactConfig>,
    changed: List<Pair<ArtifactConfig, ArtifactConfig>>
  ) {

    val message = buildString {

      appendLine(
        "\tArtifact definitions don't match.  If this is intended, " +
          "run `./gradlew artifactsDump` and commit changes."
      )
      appendLine()

      maybeAddDuplicateValueMessages(duplicateArtifactIds, "artifact id")
      maybeAddDuplicateValueMessages(duplicatePomDescriptions, "pom description")

      maybeAddMissingArtifactMessages(missing)

      maybeAddExtraArtifactMessages(extraFromCurrent)

      maybeAddChangedValueMessages(changed)
    }

    logger.error(message.colorized(RED))

    throw GradleException("Artifacts check failed")
  }

  private fun StringBuilder.maybeAddDuplicateValueMessages(
    duplicates: Map<String, List<ArtifactConfig>>,
    propertyName: String
  ) = apply {

    if (duplicates.isNotEmpty()) {
      appendLine("\tDuplicate properties were found where they should be unique:")
      appendLine()
      duplicates.forEach { (value, artifacts) ->
        appendLine(
          "\t\t       projects - ${artifacts.map { "${it.gradlePath} (${it.publicationName})" }}"
        )
        appendLine("\t\t       property - $propertyName")
        appendLine("\t\tduplicate value - $value")
        appendLine()
      }
    }
  }

  private fun StringBuilder.maybeAddMissingArtifactMessages(missing: List<ArtifactConfig>) = apply {

    if (missing.isNotEmpty()) {
      val isAre = if (missing.size == 1) "is" else "are"
      appendLine(
        "\t${pluralsString(missing.size)} defined in `artifacts.json` but " +
          "$isAre missing from the project:"
      )
      appendLine()
      missing.forEach {
        appendLine(it.message())
        appendLine()
      }
    }
  }

  private fun StringBuilder.maybeAddExtraArtifactMessages(extraFromCurrent: List<ArtifactConfig>) =
    apply {

      if (extraFromCurrent.isNotEmpty()) {
        appendLine("\t${pluralsString(extraFromCurrent.size)} new:\n")
        extraFromCurrent.forEach {
          appendLine(it.message())
          appendLine()
        }
      }
    }

  private fun StringBuilder.maybeAddChangedValueMessages(
    changed: List<Pair<ArtifactConfig, ArtifactConfig>>
  ): StringBuilder = apply {

    fun appendDiff(propertyName: String, old: String, new: String) {
      appendLine("\t\t\told $propertyName - $old")
      appendLine("\t\t\tnew $propertyName - $new")
    }

    if (changed.isNotEmpty()) {
      appendLine("\t${pluralsString(changed.size)} changed:")
      changed.forEach { (old, new) ->

        appendLine()
        appendLine("\t    ${old.gradlePath} (${old.publicationName}) -")

        if (old.group != new.group) {
          appendDiff("group", old.group, new.group)
        }

        if (old.artifactId != new.artifactId) {
          appendDiff("artifact id", old.artifactId, new.artifactId)
        }

        if (old.description != new.description) {
          appendDiff("description", old.description, new.description)
        }

        if (old.packaging != new.packaging) {
          appendDiff("packaging", old.packaging, new.packaging)
        }
      }
      appendLine()
    }
  }

  private fun pluralsString(size: Int): String {
    return if (size == 1) {
      "This artifact is"
    } else {
      "These artifacts are"
    }
  }
}
