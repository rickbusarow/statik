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

sealed class ParamTypes {
  abstract val name: String

  override fun toString() = name
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ParamTypes) return false

    if (name != other.name) return false

    return true
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }

  class A(override val name: String) : ParamTypes()
  class B(override val name: String) : ParamTypes()
  class C(override val name: String) : ParamTypes()
  class D(override val name: String) : ParamTypes()
  class E(override val name: String) : ParamTypes()
  class F(override val name: String) : ParamTypes()
  class G(override val name: String) : ParamTypes()
  class H(override val name: String) : ParamTypes()
  class I(override val name: String) : ParamTypes()
  class J(override val name: String) : ParamTypes()
  class K(override val name: String) : ParamTypes()
  class L(override val name: String) : ParamTypes()
  class M(override val name: String) : ParamTypes()
  class N(override val name: String) : ParamTypes()
  class O(override val name: String) : ParamTypes()
  class P(override val name: String) : ParamTypes()
  class Q(override val name: String) : ParamTypes()
  class R(override val name: String) : ParamTypes()
  class S(override val name: String) : ParamTypes()
  class T(override val name: String) : ParamTypes()
  class U(override val name: String) : ParamTypes()
  class V(override val name: String) : ParamTypes()
  class W(override val name: String) : ParamTypes()
  class X(override val name: String) : ParamTypes()
  class Y(override val name: String) : ParamTypes()
  class Z(override val name: String) : ParamTypes()
}
