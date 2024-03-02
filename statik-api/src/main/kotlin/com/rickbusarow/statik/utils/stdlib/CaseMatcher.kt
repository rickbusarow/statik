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

package com.rickbusarow.statik.utils.stdlib

import com.rickbusarow.statik.InternalStatikApi

/**
 * https://gist.github.com/SuppieRK/a6fb471cf600271230c8c7e532bdae4b
 *
 * com.rickbusarow.statik .name*/
@InternalStatikApi
internal sealed class CaseMatcher(private val regex: Regex) {

  /**
   * Represents matching for flat case, e.g. 'flatcase'
   *
   * Can be also referred as: lower flat case
   *
   * com.rickbusarow.statik .name*/
  class LowerFlatCaseMatcher : CaseMatcher(Regex("[a-z\\d]+"))

  /**
   * Represents matching for upper flat case, e.g. 'UPPERFLATCASE'
   *
   * com.rickbusarow.statik .name*/
  class UpperFlatCaseMatcher : CaseMatcher(Regex("[A-Z\\d]+"))

  /**
   * Represents matching for camel case, e.g. 'camelCase'
   *
   * Can be also referred as: lower camel case, dromedary case
   *
   * com.rickbusarow.statik .name*/
  class LowerCamelCaseMatcher : CaseMatcher(Regex("""[a-z]+(?:[A-Z\d]+[a-z\d]+[A-Za-z\d]*)*"""))

  /**
   * Represents matching for upper camel case, e.g. 'UpperCamelCase'
   *
   * Can be also referred as: pascal case, studly case
   *
   * com.rickbusarow.statik .name*/
  class UpperCamelCaseMatcher : CaseMatcher(Regex("""[A-Z][a-z\d]+(?:[A-Z]+[a-z\d]*)*"""))

  /**
   * Represents matching for snake case, e.g. 'snake_case'
   *
   * Can be also referred as: lower snake case, pothole case
   *
   * com.rickbusarow.statik .name*/
  class SnakeCaseMatcher : CaseMatcher(Regex("""[a-z\d]+(?:_[a-z\d]+)*"""))

  /**
   * Represents matching for screaming snake case, e.g. 'SCREAMING_SNAKE_CASE'
   *
   * Can be also referred as: upper snake case, macro case, constant case
   *
   * com.rickbusarow.statik .name*/
  class ScreamingSnakeCaseMatcher : CaseMatcher(Regex("""[A-Z\d]+(?:_[A-Z\d]+)*"""))

  /**
   * Represents matching for camel snake case, e.g. 'Camel_Snake_Case'
   *
   * com.rickbusarow.statik .name*/
  class CamelSnakeCaseMatcher : CaseMatcher(Regex("""[A-Z][a-z\d]+(?:_[A-Z]+[a-z\d]*)*"""))

  /**
   * Represents matching for kebab case, e.g. 'kebab-case'
   *
   * Can be also referred as: lower kebab case, dash case, lisp case
   *
   * com.rickbusarow.statik .name*/
  class KebabCaseMatcher : CaseMatcher(Regex("""[a-z\d]+(?:-[a-z\d]+)*"""))

  /**
   * Represents matching for screaming kebab case, e.g. 'SCREAMING-KEBAB-CASE'
   *
   * Can be also referred as: upper kebab case, cobol case
   *
   * com.rickbusarow.statik .name*/
  class ScreamingKebabCaseMatcher : CaseMatcher(Regex("""[A-Z\d]+(?:-[A-Z\d]+)*"""))

  /**
   * Represents matching for train case, e.g. 'Train-Case'
   *
   * com.rickbusarow.statik .name*/
  class TrainCaseMatcher : CaseMatcher(Regex("""[A-Z][a-z\d]+(?:-[A-Z]+[a-z\d]*)*"""))

  /**
   * Represents matching for custom regular expressions
   *
   * com.rickbusarow.statik .name*/
  class CustomMatcher(regex: Regex) : CaseMatcher(regex)

  open fun matches(source: String?): Boolean {
    return source?.matches(regex) ?: false
  }

  override fun toString(): String {
    return "${this.javaClass.simpleName}('$regex')"
  }
}
