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
  poko()
}

kotlin {
  compilerOptions {
    optIn.add("com.rickbusarow.statik.InternalStatikApi")
  }
}

dependencies {
  api(libs.cashapp.turbine)
  api(libs.classgraph)
  api(libs.junit.jupiter)
  api(libs.junit.jupiter.api)
  api(libs.junit.jupiter.engine)
  api(libs.junit4)
  api(libs.kotest.assertions.api)
  api(libs.kotest.assertions.core.jvm)
  api(libs.kotest.assertions.shared)
  api(libs.kotest.common)
  api(libs.kotest.property.jvm)
  api(libs.kotlin.reflect)
  api(libs.kotlinx.coroutines.test)
  api(libs.rickBusarow.kase)

  api(project(":statik-api"))

  implementation(libs.kotlin.compiler)
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.rickBusarow.dispatch.core)
  implementation(libs.semVer)
}
