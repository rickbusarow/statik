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

import com.squareup.moshi.JsonClass
import java.io.Serializable

/**
 * Models the module-specific properties of published maven artifacts.
 *
 * see (Niklas Baudy's
 * [gradle-maven-publish-plugin](https://github.com/vanniktech/gradle-maven-publish-plugin))
 *
 * @property gradlePath the path of the Gradle project, such as `:workflow-core`
 * @property group The maven "group", which should always be
 *   `com.rickbusarow.kase`. This is the `GROUP` property in the Gradle plugin.
 * @property artifactId The maven "module", such as `workflow-core-jvm`.
 *   This is the `POM_ARTIFACT_ID` property in the Gradle plugin.
 * @property description The description of this specific artifact, such as
 *   "Workflow Core". This is the `POM_NAME` property in the Gradle plugin.
 * @property packaging `aar` or `jar`. This is the `POM_PACKAGING` property in the Gradle plugin.
 * @property javaVersion the java version of the artifact (typically 8 or 11). If
 *   not set explicitly, this defaults to the JDK version used to build the artifact.
 * @property publicationName typically 'maven', but other things for KMP artifacts
 * @since 0.1.0
 */
@JsonClass(generateAdapter = true)
data class ArtifactConfig(
  val gradlePath: String,
  val group: String,
  val artifactId: String,
  val description: String,
  val packaging: String,
  val javaVersion: String,
  val publicationName: String
) : Serializable, Comparable<ArtifactConfig> {
  /**
   * globally unique identifier for this artifact
   *
   * @since 0.1.0
   */
  val key = "$gradlePath+$publicationName"

  override fun compareTo(other: ArtifactConfig): Int {
    return gradlePath.compareTo(other.gradlePath)
  }

  companion object {
    /**
     * deserialization
     *
     * @since 0.0.1
     */
    const val serialVersionUID = 1L
  }
}
