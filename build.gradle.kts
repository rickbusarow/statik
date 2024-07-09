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

import com.rickbusarow.doks.DoksTask
import com.rickbusarow.kgx.mustRunAfter
import modulecheck.gradle.ModuleCheckExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  dependencies {
    classpath(libs.kotlin.gradle.plugin)
    classpath(libs.rickBusarow.mahout.gradle.plugin)
  }
}

plugins {
  alias(libs.plugins.doks)
}

apply(plugin = "com.rickbusarow.mahout.root")

doks {
  dokSet {
    docs("README.md", "CHANGELOG.md")

    rule("maven-with-version") {
      regex = maven(mahoutProperties.group.get())
      replacement = "$1:$2:${libs.versions.rickBusarow.statik.get().escapeReplacement()}"
    }
    rule("statik-group") {
      regex = "com\\.rickbusarow\\.statik"
      replacement = mahoutProperties.group.get()
    }
  }
}

extensions.configure<ModuleCheckExtension> {
  checks.sortDependencies = false
}

subprojects.map {
  it.tasks.withType(KotlinCompile::class.java)
    .mustRunAfter(tasks.withType(DoksTask::class.java))
}

val foo by tasks.registering {
  doLast {
    rootDir.walkTopDown()
      .filter { it.path.contains("src/test/kotlin") }
      .filter { it.isFile }
      .mapNotNull { file ->

        val txt = file.readText()

        val name = """(?<=class )\w+""".toRegex()
          .find(txt)?.value
          ?: return@mapNotNull null

        file to name
      }
      .groupBy { it.second }
      .map { (key, value) ->
        key to value.map { it.first }.sorted()
      }
      .filter { it.second.size > 1 }
      .sortedBy { it.first }
      .forEach { (name, files) ->
        println(
          """
          | -- $name
          |${files.joinToString("\n")}
          """.trimMargin()
        )
      }
  }
}
