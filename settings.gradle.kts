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

rootProject.name = "statik"

pluginManagement {
  repositories {
    maven {
      url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
      content {
        includeGroup("com.rickbusarow.mahout")
      }
    }
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}

plugins {
  id("com.gradle.develocity") version "3.17.5"
  id("com.gradle.common-custom-user-data-gradle-plugin") version "2.0.2"
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}

develocity {
  buildScan {

    val isCI = !System.getenv("CI").isNullOrEmpty()

    uploadInBackground = !isCI
    termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
    termsOfUseAgree = "yes"

    capture {
      testLogging = true
      buildLogging = true
      fileFingerprints = true
    }

    obfuscation {
      hostname { "<hostName>" }
      ipAddresses { listOf("<ip address>") }
      username { "<username>" }
    }

    val gitHubActions = System.getenv("GITHUB_ACTIONS")?.toBoolean() ?: false
    if (gitHubActions) {
      // ex: `octocat/Hello-World` as in github.com/octocat/Hello-World
      val repository = System.getenv("GITHUB_REPOSITORY")!!
      val runId = System.getenv("GITHUB_RUN_ID")!!

      link(
        "GitHub Action Run",
        "https://github.com/$repository/actions/runs/$runId"
      )
    }
  }
}

include(
  ":statik-api",
  ":statik-kotlin",
  ":statik-kotlin-k1",
  ":statik-logging",
  ":statik-testing-internal"
)
