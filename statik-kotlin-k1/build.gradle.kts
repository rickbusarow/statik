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

plugins {
  alias(libs.plugins.mahout.kotlin.jvm.module)
}

mahout {
  publishing {
    publishMaven(
      artifactId = "statik-kotlin-k1",
      pomDescription = "Kotlin language models backed by PSI elements for Kotlin 1.x.x"
    )
  }

  poko()
}

kotlin {
  compilerOptions {
    optIn.add("com.rickbusarow.statik.InternalStatikApi")
    explicitApi()
  }
}

dependencies {
  api(libs.junit.jupiter.api)

  api(project(":statik-api"))
  api(project(":statik-kotlin"))
  api(project(":statik-logging"))

  implementation(libs.kotlin.compiler)
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.rickBusarow.dispatch.core)
  implementation(libs.semVer)

  testImplementation(libs.cashapp.turbine)
  testImplementation(libs.classgraph)
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.junit.jupiter.engine)
  testImplementation(libs.junit4)
  testImplementation(libs.kotest.assertions.api)
  testImplementation(libs.kotest.assertions.core.jvm)
  testImplementation(libs.kotest.assertions.shared)
  testImplementation(libs.kotest.common)
  testImplementation(libs.kotest.property.jvm)
  testImplementation(libs.kotlin.reflect)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.rickBusarow.kase)

  testImplementation(project(":statik-testing-internal"))
}
