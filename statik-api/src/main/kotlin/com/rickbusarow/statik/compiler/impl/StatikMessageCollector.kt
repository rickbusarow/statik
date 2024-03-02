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

package com.rickbusarow.statik.compiler.impl

import com.rickbusarow.statik.logging.StatikLogger
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer

/**
 * A message collector class that logs the compiler messages according to the given log level.
 *
 * @param messageRenderer The renderer that will be used to render messages.
 * @param logger The logger instance used for logging the messages.
 * @param logLevel The level of logging.
 */
internal class StatikMessageCollector(
  private val messageRenderer: MessageRenderer,
  private val logger: StatikLogger,
  private val logLevel: LogLevel
) : MessageCollector {

  private val totalMessages = mutableListOf<String>()
  private val ignoredMessages = mutableListOf<String>()

  /**
   * Logs a report message with its severity and source location.
   *
   * @param severity The severity of the message.
   * @param message The message to be logged.
   * @param location The source location of the message.
   */
  override fun report(
    severity: CompilerMessageSeverity,
    message: String,
    location: CompilerMessageSourceLocation?
  ) {

    val rendered = messageRenderer.render(severity, message, location)

    totalMessages.add(rendered)

    when (logLevel) {
      LogLevel.ERRORS -> if (severity.isError) {
        logger.failure(rendered)
      } else {
        ignoredMessages.add(rendered)
      }

      LogLevel.WARNINGS_AS_ERRORS -> if (severity.isWarning || severity.isError) {
        logger.failure(rendered)
      } else {
        ignoredMessages.add(rendered)
      }

      LogLevel.VERBOSE -> if (severity.isWarning || severity.isError) {
        logger.failure(rendered)
      } else {
        logger.info(rendered)
      }
    }
  }

  /** Clears the total and ignored messages count. */
  override fun clear() {
    ignoredMessages.clear()
    totalMessages.clear()
  }

  /**
   * Checks if there were any errors reported.
   *
   * @return Boolean value indicating if there were any errors.
   */
  override fun hasErrors(): Boolean = totalMessages.isNotEmpty()

  /** Prints a warning message about the number of ignored issues, if there are any. */
  fun printIssuesIfAny() {
    if (hasErrors()) {
      logger.warning(renderAll())
    }
  }

  fun renderAll(): String = buildString {
    if (hasErrors()) {
      appendLine("Analysis completed with ${totalMessages.size} ignored issues.")

      for (msg in totalMessages) {
        appendLine(msg)
      }
    }
  }

  /** Enum class to define log levels. */
  enum class LogLevel {
    ERRORS,
    WARNINGS_AS_ERRORS,
    VERBOSE
  }
}
