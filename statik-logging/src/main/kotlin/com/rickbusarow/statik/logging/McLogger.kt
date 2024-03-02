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

package com.rickbusarow.statik.logging

import com.rickbusarow.statik.logging.Report.ReportEntry

public interface StatikLogger {
  /**     */
  public fun printReport(report: Report)

  /**     */
  public fun warning(message: String)

  /**     */
  public fun info(message: String)

  /**     */
  public fun failure(message: String)

  /**     */
  public fun success(message: String)
}

public class PrintLogger : StatikLogger {

  override fun failure(message: String) {
    println(message)
  }

  override fun info(message: String) {
    println(message)
  }

  override fun printReport(report: Report) {
    println(report.joinToString())
  }

  override fun success(message: String) {
    println(message)
  }

  override fun warning(message: String) {
    println(message)
  }
}

public class ReportingLogger(
  private val mirrorToStandardOut: Boolean = true
) : StatikLogger {

  private val entries = mutableListOf<ReportEntry>()

  public fun collectReport(): Report = Report(entries)

  public fun clear() {
    entries.clear()
  }

  private fun addEntry(reportEntry: ReportEntry) {
    entries.add(reportEntry)
    if (mirrorToStandardOut) {
      reportEntry.printToStdOut()
      println()
    }
  }

  override fun printReport(report: Report) {
    entries.addAll(report.entries)
    if (mirrorToStandardOut) {
      report.entries
        .forEach { it.printToStdOut() }
      println()
    }
  }

  override fun failure(message: String) {
    addEntry(ReportEntry.Failure(message))
  }

  override fun info(message: String) {
    addEntry(ReportEntry.Info(message))
  }

  override fun success(message: String) {
    addEntry(ReportEntry.Success(message))
  }

  override fun warning(message: String) {
    addEntry(ReportEntry.Warning(message))
  }
}
