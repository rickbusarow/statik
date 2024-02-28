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
package builds

/**
 * https://github.com/ziggy42/kolor
 *
 * @property code
 * @since 0.1.0
 */
@Suppress("MagicNumber")
enum class Color(val code: Int) {
  BLACK(30),
  RED(31),
  GREEN(32),
  YELLOW(33),
  BLUE(34),
  MAGENTA(35),
  CYAN(36),
  LIGHT_GRAY(37),
  DARK_GRAY(90),
  LIGHT_RED(91),
  LIGHT_GREEN(92),
  LIGHT_YELLOW(93),
  LIGHT_BLUE(94),
  LIGHT_MAGENTA(95),
  LIGHT_CYAN(96),
  WHITE(97);

  companion object {

    private val supported = "win" !in System.getProperty("os.name").lowercase()

    /**
     * returns a string in the given color
     *
     * @since 0.1.0
     */
    fun String.colorized(color: Color) = if (supported) {
      "\u001B[${color.code}m$this\u001B[0m"
    } else {
      this
    }
  }
}
