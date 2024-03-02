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

import dev.drewhamilton.poko.Poko

@Poko
public class Report(
  public val entries: List<ReportEntry>
) {

  public fun joinToString(): String = buildString {
    entries.forEach { reportEntry ->
      when (reportEntry) {
        is ReportEntry.Failure -> appendLine(reportEntry.message)
        is ReportEntry.Info -> appendLine(reportEntry.message)
        is ReportEntry.Success -> appendLine(reportEntry.message)
        is ReportEntry.Warning -> appendLine(reportEntry.message)
      }
    }
  }

  public sealed interface ReportEntry {

    public val message: String

    /**     */
    @JvmInline
    public value class Warning(override val message: String) : ReportEntry

    /**     */
    @JvmInline
    public value class Info(override val message: String) : ReportEntry

    /**     */
    @JvmInline
    public value class Failure(override val message: String) : ReportEntry

    /**     */
    @JvmInline
    public value class Success(override val message: String) : ReportEntry

    public fun printToStdOut() {
      when (this) {
        is Failure -> println(message)
        is Info -> println(message)
        is Success -> println(message)
        is Warning -> println(message)
      }
    }
  }

  public class ReportBuilder(
    private val entries: MutableList<ReportEntry> = mutableListOf()
  ) {

    /**     */
    public fun warning(message: String) {
      entries.add(ReportEntry.Warning(message))
    }

    /**     */
    public fun info(message: String) {
      entries.add(ReportEntry.Info(message))
    }

    /**     */
    public fun failure(message: String) {
      entries.add(ReportEntry.Failure(message))
    }

    /**     */
    public fun success(message: String) {
      entries.add(ReportEntry.Success(message))
    }
  }

  public companion object {

    public fun build(buildAction: ReportBuilder.() -> Unit): Report {
      val entries = mutableListOf<ReportEntry>()

      ReportBuilder(entries).buildAction()

      return Report(entries)
    }
  }
}
