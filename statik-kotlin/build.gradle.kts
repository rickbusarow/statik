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

plugins {
  id("com.rickbusarow.mahout.kotlin-jvm-module")
}

mahout {
  publishing {
    publishMaven(
      artifactId = "statik-kotlin",
      pomDescription = "Common Kotlin language types for the Statik AST"
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

val compilerVersionAttribute: Attribute<String> = Attribute.of(
  "com.rickbusarow.statik.compilerVersion",
  String::class.java
)
val main by sourceSets.getting
val test by sourceSets.getting

java {
  val kotlin1910 by sourceSets.creating {
    compileClasspath += main.output
    runtimeClasspath += main.output
  }
  val kotlin1920 by sourceSets.creating {
    compileClasspath += main.output
    runtimeClasspath += main.output
  }
  val kotlin2000 by sourceSets.creating {
    compileClasspath += main.output
    runtimeClasspath += main.output
  }
  registerFeature("kotlin1910") {
    usingSourceSet(kotlin1910)
    capability("org.jetbrains.kotlin", "kotlin-compiler-embeddable", "1.9.10")
  }
  registerFeature("kotlin1920") {
    usingSourceSet(kotlin1920)
    capability("org.jetbrains.kotlin", "kotlin-compiler-embeddable", "1.9.24")
  }
  registerFeature("kotlin2000") {
    usingSourceSet(kotlin1920)
    capability("org.jetbrains.kotlin", "kotlin-compiler-embeddable", "2.0.0")
  }
}

testing {
  @Suppress("UnstableApiUsage")
  suites {
    register("kotlin1910Test", JvmTestSuite::class) {
      dependencies {
        implementation(project())
      }
    }
    register("kotlin1920Test", JvmTestSuite::class) {
      dependencies {
        implementation(project())
      }
    }
    register("kotlin2000Test", JvmTestSuite::class) {
      dependencies {
        implementation(project())
      }
    }

    withType<JvmTestSuite>().configureEach {

      if (name.contains("kotlin")) {
        val prod = java.sourceSets.getByName(name.removeSuffix("Test"))
        sources.runtimeClasspath += prod.output + test.output + main.output
        sources.compileClasspath += prod.output + test.output + main.output
      }

      useJUnitJupiter(libs.versions.jUnit5)
      dependencies {
        implementation(libs.kotest.common)
        implementation(libs.kotest.assertions.api)
        implementation(libs.kotest.assertions.shared)
        implementation(libs.kotest.assertions.core.jvm)
      }
    }
  }
}

kotlin {

  val kotlin1910 by sourceSets.getting {
    dependsOn(sourceSets["main"])
    dependencies {
      implementation(kotlinCompilerEmbeddable("1.9.10"))
      implementation(kotlinStdlibJdk8("1.9.10"))
    }
  }
  val kotlin1920 by sourceSets.getting {
    dependsOn(sourceSets["main"])
    dependencies {
      implementation(kotlinCompilerEmbeddable("1.9.24"))
      implementation(kotlinStdlibJdk8("1.9.24"))
    }
  }

  val kotlin2000 by sourceSets.getting {
    dependsOn(sourceSets["main"])
    dependencies {
      implementation(kotlinCompilerEmbeddable("2.0.0"))
      implementation(kotlinStdlibJdk8("2.0.0"))
    }
  }

  val kotlin1910Test by sourceSets.getting { dependsOn(kotlin1910) }
  val kotlin1920Test by sourceSets.getting { dependsOn(kotlin1920) }
  val kotlin2000Test by sourceSets.getting { dependsOn(kotlin2000) }
}

fun kotlinStdlibCommon(version: String): String = kotlin("kotlin-stdlib-common", version)
fun kotlinStdlibJdk8(version: String): String = kotlin("kotlin-stdlib-jdk8", version)
fun kotlinCompilerEmbeddable(version: String): String =
  kotlin("kotlin-compiler-embeddable", version)

fun kotlin(artifactId: String, version: String): String =
  "org.jetbrains.kotlin:$artifactId:$version"

dependencyGuard {
  configurations.names
    .filter { it.endsWith("CompileClasspath") || it.endsWith("RuntimeClasspath") }
    .forEach { cfg -> configuration(cfg) }
}

dependencies {
  api(libs.junit.jupiter.api)

  api(project(":statik-api"))

  // implementation(libs.kotlin.compiler)
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
}

// TODO (rbusarow) delete me when done adding source sets
run {
  check(System.getenv("CI") == null) { "delete me when done adding source sets" }
  kotlin.sourceSets.names.forEach { n ->
    file("src/$n/kotlin/com/rickbusarow/statik").mkdirs()
  }
}
