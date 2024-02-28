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

package com.rickbusarow.statik.stdlib

import java.io.File
import java.util.Locale
import kotlin.LazyThreadSafetyMode.NONE

/** Converts all line separators in the receiver string to use `\n`. */
fun String.normaliseLineSeparators(): String = replace("\r\n|\r".toRegex(), "\n")

/**
 * Removes all occurrences of specified strings from the receiver string.
 *
 * @param strings Strings to be removed from the receiver string.
 * @return A new string with all occurrences of specified strings removed.
 */
fun String.remove(vararg strings: String): String = strings.fold(this) { acc, string ->
  acc.replace(string, "")
}

/**
 * Removes all matches of specified regular expressions from the receiver string.
 *
 * @param patterns Regular expressions to be removed from the receiver string.
 * @return A new string with all matches of specified regular expressions removed.
 */
fun String.remove(vararg patterns: Regex): String = patterns.fold(this) { acc, regex ->
  acc.replace(regex, "")
}

/** Removes ANSI controls like `\u001B[]33m` */
fun String.noAnsi(): String = remove("""\u001B\[[;\d]*m""".toRegex())

/** replace ` ` (a normal whitespace) with `·` */
val String.interpuncts: String get() = replace(" ", "·")

/** replace `·` with ` ` (a normal whitespace) */
val String.noInterpuncts: String get() = replace("·", " ")

/**
 * Decapitalizes the first character of this [String] using the specified [locale].
 *
 * @param locale The [Locale] to be used for decapitalization. Defaults to [Locale.US].
 * @receiver The original String.
 * @return The string with the first character decapitalized.
 */
fun String.decapitalize(locale: Locale = Locale.US): String =
  replaceFirstChar { it.lowercase(locale) }

/**
 * Capitalizes the first character of this [String] using the specified [locale].
 *
 * @param locale The [Locale] to be used for capitalization. Defaults to [Locale.US].
 * @receiver The original String.
 * @return The string with the first character capitalized.
 */
fun String.capitalize(locale: Locale = Locale.US): String =
  replaceFirstChar { it.uppercase(locale) }

/**
 * performs [transform] on each line
 *
 * Doesn't preserve the original line endings.
 */
fun CharSequence.mapLines(transform: (String) -> CharSequence): String = lineSequence()
  .joinToString("\n", transform = transform)

/**
 * performs [transform] on each line
 *
 * Doesn't preserve the original line endings.
 */
fun CharSequence.mapLinesIndexed(transform: (Int, String) -> CharSequence): String =
  lineSequence()
    .mapIndexed(transform)
    .joinToString("\n")

/**
 * Removes various bits of noise and machine-specific variables from a console or report output.
 * Cleans the provided string by applying various modifications such as normalising line separators,
 * using relative paths, enforcing Unix file separators, and removing specific strings or patterns.
 *
 * @param workingDir The working directory that will be used when making paths relative.
 * @receiver The raw string that needs to be cleaned.
 * @return The cleaned string after all the modifications have been applied.
 */
internal fun String.cleanOutput(workingDir: File): String {
  return normaliseLineSeparators()
    .useRelativePaths(workingDir)
    .alwaysUnixFileSeparators()
    .remove("in [\\d.]+ seconds\\.".toRegex())
    .noAnsi()
    .mapLines { it.trimEnd() }
    .trimEnd()
    .trimStart('\n')
}

/** replace absolute paths with relative ones */
internal fun String.useRelativePaths(workingDir: File): String {
  return alwaysUnixFileSeparators()
    .remove(
      // order matters here!!  absolute must go first
      workingDir.absolutePath.alwaysUnixFileSeparators(),
      workingDir.path.alwaysUnixFileSeparators()
    )
}

/** Replace Windows file separators with Unix ones, just for string comparison in tests */
fun String.alwaysUnixFileSeparators(): String = replace(File.separator, "/")

internal fun String.osFileSeparators(): String {
  return if ("win" in System.getProperty("os.name").lowercase()) {
    replace('/', File.separatorChar)
  } else {
    replace('\\', File.separatorChar)
  }
}

/**
 * Creates a string from all the elements separated using [separator]
 * and using the given [prefix] and [postfix] if supplied.
 *
 * If the collection could be huge, you can specify a non-negative value
 * of [limit], in which case only the first [limit] elements will be
 * appended, followed by the [truncated] string (which defaults to "...").
 */
fun <T> List<T>.joinToStringIndexed(
  separator: CharSequence = ", ",
  prefix: CharSequence = "",
  postfix: CharSequence = "",
  limit: Int = -1,
  truncated: CharSequence = "...",
  transform: (Int, T) -> CharSequence
): String {
  return buildString {
    append(prefix)
    var count = 0
    for (element in this@joinToStringIndexed) {
      if (++count > 1) append(separator)
      if (limit < 0 || count <= limit) {
        append(transform(count - 1, element))
      } else {
        break
      }
    }
    if (limit in 0 until count) append(truncated)
    append(postfix)
  }
}

/**
 * shorthand for `replaceIndent(" ".repeat(numSpaces))`
 *
 * @see kotlin.text.replaceIndent
 */
fun String.replaceIndent(numSpaces: Int): String {
  return replaceIndent(" ".repeat(numSpaces))
}

/**
 * example:
 *
 * ```
 * override fun toString() = buildString {
 *   appendLine("SomeClass(")
 *   indent {
 *     appendLine("prop1=$prop1")
 *     appendLine("prop2=$prop2")
 *   }
 *   appendLine(")")
 * }
 * ```
 */
inline fun StringBuilder.indent(
  leadingIndent: String = "  ",
  continuationIndent: String = leadingIndent,
  builder: StringBuilder.() -> Unit
) {

  val inner = buildString {
    append(leadingIndent)

    builder()
  }

  if (inner.isBlank()) return

  append(inner.prependContinuationIndent(continuationIndent))
}

/**
 * Prepends [continuationIndent] to every line of the original string.
 *
 * Doesn't preserve the original line endings.
 */
fun CharSequence.prependContinuationIndent(
  continuationIndent: String,
  skipBlankLines: Boolean = true
): String = mapLinesIndexed { i, line ->
  when {
    i == 0 -> line
    skipBlankLines && line.isBlank() -> line
    else -> "$continuationIndent$line"
  }
}

/**
 * Adds line breaks and indents to the output of data class `toString()`s.
 *
 * @see toStringPretty
 */
fun String.prettyToString(): String {
  return replace(",", ",\n")
    .replace("(", "(\n")
    .replace(")", "\n)")
    .replace("[", "[\n")
    .replace("]", "\n]")
    .replace("{", "{\n")
    .replace("}", "\n}")
    .replace("\\(\\s*\\)".toRegex(), "()")
    .replace("\\[\\s*]".toRegex(), "[]")
    .indentByBrackets()
    .replace("""\n *\n""".toRegex(), "\n")
}

/**
 * shorthand for `toString().prettyToString()`, which adds line breaks and indents to a string
 *
 * @see prettyToString
 */
fun Any?.toStringPretty(): String = when (this) {
  is Map<*, *> -> toList().joinToString("\n")
  else -> toString().prettyToString()
}

/** A naive auto-indent which just counts brackets. */
fun String.indentByBrackets(tab: String = "  "): String {

  var tabCount = 0

  val open = setOf('{', '(', '[', '<')
  val close = setOf('}', ')', ']', '>')

  return lines()
    .map { it.trim() }
    .joinToString("\n") { line ->

      if (line.firstOrNull() in close) {
        tabCount--
      }

      "${tab.repeat(tabCount)}$line"
        .also {

          // Arrows aren't brackets
          val noSpecials = line.remove("<=", "->")

          tabCount += noSpecials.count { char -> char in open }
          // Skip the first char because if it's a closing bracket, it was already counted above.
          tabCount -= noSpecials.drop(1).count { char -> char in close }
        }
    }
}

/**
 * Appends the specified [prefix] to this [CharSequence] and returns the resulting string.
 *
 * @param prefix The prefix to be added.
 * @receiver The original CharSequence.
 * @return The string with the prefix added.
 */
fun CharSequence.prefix(prefix: String): String = "$prefix$this"

/**
 * Adds the specified [prefix] to this [String] if it does not already start with it.
 *
 * @param prefix The prefix to be added if not present.
 * @receiver The original String.
 * @return The string with the prefix added if not present.
 */
fun String.prefixIfNot(prefix: String): String =
  if (this.startsWith(prefix)) this else "$prefix$this"

/**
 * Adds the specified [suffix] to this [String] if it does not already end with it.
 *
 * @param suffix The suffix to be added if not present.
 * @receiver The original String.
 * @return The string with the suffix added if not present.
 */
fun String.suffixIfNot(suffix: String): String = if (this.endsWith(suffix)) this else "$this$suffix"

/**
 * Finds and returns the minimum indentation for this [String], with an optional
 * [absoluteMinimum] to be returned if the minimum indent is less than the absolute minimum.
 *
 * @param absoluteMinimum The absolute minimum indent to
 *   be returned if the minimum is less. Defaults to " ".
 * @receiver The original String.
 * @return The minimum indentation of the string, or the absolute minimum if the minimum is less.
 */
fun String.findMinimumIndent(absoluteMinimum: String = "  "): String {

  if (contains("\t")) return "\t"

  val parsedMinimumOrNull = lines()
    .filter { it.isNotBlank() }
    .map { it.indentWidth() }
    .filter { it > 0 }
    .minOrNull()
    ?.let { " ".repeat(it) }

  return parsedMinimumOrNull
    ?.takeIf { it.length >= absoluteMinimum.length }
    ?: absoluteMinimum
}

/**
 * Calculates and returns the width of the indentation for this [String].
 *
 * @receiver The original String.
 * @return The width of the indentation for this string.
 */
private fun String.indentWidth(): Int =
  indexOfFirst { !it.isWhitespace() }.let { if (it == -1) length else it }

/**
 * @return a string with no leading or trailing whitespace, and
 *   no whitespace before or after any instance of [delimiter]
 * @since 0.12.0
 */
fun String.trimSegments(delimiter: String = "."): String {

  val regex = regex {
    val escaped = Regex.escapeReplacement(delimiter)
    append("""^\s+""")
    or()
    append("""\s+($escaped)\s+""")
    or()
    append("""\s+($escaped)""")
    or()
    append("""($escaped)\s+""")
    or()
    append("""\s+$""")
  }
  return replace(regex) { it.groupValues.getOrElse(1) { "" } }
}

/** shorthand for `splitAndMap(*delimiters) { it.trim() }` */
fun String.splitAndTrim(vararg delimiters: String): List<String> {
  return splitAndMap(*delimiters) { it.trim() }
}

/** shorthand for `splitAndMap(*delimiters) { it.trim() }` */
fun String.splitAndTrim(vararg delimiters: Char): List<String> {
  return splitAndMap(*delimiters) { it.trim() }
}

/** shorthand for `split(*delimiters).map { /* ... */ }` */
inline fun <T> String.splitAndMap(vararg delimiters: String, transform: (String) -> T): List<T> {
  return split(*delimiters).map(transform)
}

/** shorthand for `split(*delimiters).map { /* ... */ }` */
inline fun <T> String.splitAndMap(vararg delimiters: Char, transform: (String) -> T): List<T> {
  return split(*delimiters).map(transform)
}

/**
 * Creates a string from all the elements separated using [separator]
 * and using the given [prefix] and [postfix] if supplied.
 *
 * If the collection could be huge, you can specify a non-negative value
 * of [limit], in which case only the first [limit] elements will be
 * appended, followed by the [truncated] string (which defaults to "...").
 *
 * The operation is _terminal_.
 */
fun <T> Sequence<T>.joinToStringIndexed(
  separator: CharSequence = ", ",
  prefix: CharSequence = "",
  postfix: CharSequence = "",
  limit: Int = -1,
  truncated: CharSequence = "...",
  transform: (Int, T) -> CharSequence
): String {
  return buildString {
    append(prefix)
    var count = 0
    for (element in this@joinToStringIndexed) {
      if (++count > 1) append(separator)
      if (limit < 0 || count <= limit) {
        append(transform(count - 1, element))
      } else {
        break
      }
    }
    if (limit in 0 until count) append(truncated)
    append(postfix)
  }
}

/** `"$prefix$this$suffix"` */
fun CharSequence.wrapIn(prefix: String, suffix: String = prefix): String = "$prefix$this$suffix"

/** Replaces the deprecated Kotlin version, but hard-codes `Locale.US` */
fun String.capitalize(): String = replaceFirstChar {
  if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString()
}

/** shorthand for `joinToString("") { ... }` */
fun <E> Sequence<E>.joinToStringConcat(transform: ((E) -> CharSequence)? = null): String =
  joinToString("", transform = transform)

/** shorthand for `joinToString("") { ... }` */
fun <E> Iterable<E>.joinToStringConcat(transform: ((E) -> CharSequence)? = null): String =
  joinToString("", transform = transform)

/** shorthand for `joinToString("") { ... }` */
fun <E> Array<E>.joinToStringConcat(transform: ((E) -> CharSequence)? = null): String =
  joinToString("", transform = transform)

/** shorthand for `joinToString(".") { ... }` */
fun <E> Sequence<E>.joinToStringDot(transform: ((E) -> CharSequence)? = null): String =
  joinToString(".", transform = transform)

/** shorthand for `joinToString(".") { ... }` */
fun <E> Iterable<E>.joinToStringDot(transform: ((E) -> CharSequence)? = null): String =
  joinToString(".", transform = transform)

/** shorthand for `joinToString(".") { ... }` */
fun <E> Array<E>.joinToStringDot(transform: ((E) -> CharSequence)? = null): String =
  joinToString(".", transform = transform)

/**
 * Removes the indentation from every line in this string after the first line,
 * and also removes any blank lines appearing before the first non-blank line.
 *
 * This method assumes the original string has multiple lines. The first line is
 * left intact, but all subsequent lines have their leading white space removed.
 *
 * Example usage:
 *
 * ```
 * val text = """
 *     Keep leading
 *         Remove indent
 *     Remove indent
 * """
 * val result = text.trimIndentAfterFirstLine()
 * // Result is:
 * // """
 * // Keep leading
 * // Remove indent
 * // Remove indent
 * // """
 * ```
 *
 * @receiver The string from which to remove indentation after the first line.
 * @return A new string with the same first line, but
 *   with indentation removed from subsequent lines.
 */
fun String.trimIndentAfterFirstLine(): String {
  val split = lines()
  val first = split.first()
  val remaining = split.drop(1).joinToString("\n").trimIndent()
  return "$first\n$remaining"
}

/**
 * Adjusts the indentation of each line in the string to match the indentation
 * of the first non-blank line. The first non-blank line determines the
 * target indentation level, and subsequent lines are adjusted accordingly.
 *
 * @return The string with adjusted indentation.
 */
fun String.justifyToFirstLine(): String {
  val targetIndent = lineSequence()
    .firstOrNull { it.isNotBlank() }
    ?.indentWidth()
    ?: 0

  val indent by lazy(NONE) { " ".repeat(targetIndent) }

  return mapLines { line ->

    if (line.indentWidth() < targetIndent) {
      "$indent$line"
    } else {
      line
    }
  }
    .trimIndent()
}
