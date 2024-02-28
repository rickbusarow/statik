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

package com.rickbusarow.kase.utils

import com.rickbusarow.kase.stdlib.dropView
import java.io.File
import java.util.stream.Stream
import kotlin.streams.asSequence

/**
 * Attempts to skip the call to `getStackTrace`, the call to this function, and
 * any synthetic/default functions. Use a non-zero [skip] to add an extra offset.
 *
 * @since 0.7.0
 */
fun currentMethodName(skip: Int = 0): String {
  val syntheticsReg = """\d*?\.?(?:invoke(?:Suspend)?|default|\d+)""".toRegex()
  return Thread.currentThread()
    .stackTrace
    .dropWhile {
      when (it.methodName) {
        "getStackTrace" -> true
        "currentMethodName" -> true
        "currentMethodName\$default" -> true
        else -> false
      }
    }
    .dropView(skip)
    .first()
    .let { element ->
      element.methodName.takeIf { !it.matches(syntheticsReg) }
        ?: element.className.split(".").last()
          .split("$").last { !it.matches(syntheticsReg) }
    }
}

/**
 * A `Stream<E>.toList()` that's compatible with Java 8.
 *
 * @since 0.7.0
 */
fun <E> Stream<E>.asList() = asSequence().toList()

fun userDir() = File(System.getProperty("user.dir"))
