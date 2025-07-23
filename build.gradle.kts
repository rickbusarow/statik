/*
 * Copyright (C) 2025 Rick Busarow
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
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  dependencies {
    classpath(libs.kotlin.gradle.plugin)
  }
}

plugins {
  alias(libs.plugins.doks)
  alias(libs.plugins.poko) apply false
  alias(libs.plugins.mahout.root)

  // Avoid "the plugin is already in the classpath with an unknown version" issues
  // when consuming Mahout from a snapshot build.
  alias(libs.plugins.mahout.kotlin.jvm.module) apply false
}

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

subprojects.map {
  it.tasks.withType(KotlinCompile::class.java)
    .mustRunAfter(tasks.withType(DoksTask::class.java))
}
