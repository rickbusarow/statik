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

import com.rickbusarow.kgx.EagerGradleApi
import com.rickbusarow.kgx.applyOnce
import com.rickbusarow.kgx.matchingName
import com.rickbusarow.ktlint.KtLintTask
import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.nio.file.Files
import kotlin.text.RegexOption.MULTILINE

abstract class KtLintConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {

    target.plugins.applyOnce("com.rickbusarow.ktlint")

    target.dependencies
      .add("ktlint", target.libs.rickBusarow.ktrules)

    target.tasks.withType(KtLintTask::class.java).configureEach { task ->
      task.dependsOn(":updateEditorConfigVersion")

      @OptIn(EagerGradleApi::class)
      task.mustRunAfter(
        target.tasks.matchingName("apiDump"),
        target.tasks.matchingName("dependencyGuard"),
        target.tasks.matchingName("dependencyGuardBaseline"),
        target.tasks.withType(KotlinApiBuildTask::class.java),
        target.tasks.withType(KotlinApiCompareTask::class.java)
      )
    }

    val editorconfig = target.file(".editorconfig")

    if (editorconfig.exists() && !Files.isSymbolicLink(editorconfig.toPath())) {

      target.tasks.register("updateEditorConfigVersion") { task ->
        val versionName = target.VERSION_NAME
        task.doLast {
          val oldText = editorconfig.readText()

          val reg = """^(kt-rules_project_version *?= *?)\S*$""".toRegex(MULTILINE)

          val newText = oldText.replace(reg, "$1$versionName")

          if (newText != oldText) {
            editorconfig.writeText(newText)
          }
        }
      }
    }
  }
}
