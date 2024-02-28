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

package com.rickbusarow.kase.stdlib

/**
 * Shorthand for `subList(count, size)`.
 *
 * Returns a [view][List.subList] of the portion of this list starting
 * from the specified [count] index. The original list is not modified.
 *
 * @param count The number of elements to drop from the beginning of the list.
 * @return A view of the portion of this list starting from the specified [count] index.
 * @see List.subList for more information about list views.
 * @since 0.7.0
 */
internal fun <E> List<E>.dropView(count: Int): List<E> = subList(count, size)

/**
 * Shorthand for `subList(0, count)`.
 *
 * Returns a [view][List.subList] of the portion of this list starting from the first
 * element and ending at the specified [count] index. The original list is not modified.
 *
 * @param count The number of elements to take from the beginning of the list.
 * @return A view of the portion of this list starting from the specified [count] index.
 * @see List.subList for more information about list views.
 * @since 0.7.0
 */
internal fun <E> List<E>.takeView(count: Int): List<E> = subList(0, count)

/**
 * Shorthand for `subList(0, size - count)`.
 *
 * Returns a [view][List.subList] of the portion of this list starting from the
 * beginning and excluding the last [count] elements. The original list is not modified.
 *
 * @param count The number of elements to drop from the end of the list.
 * @return A view of the portion of this list starting from the specified [count] index.
 * @see List.subList for more information about list views.
 * @since 0.7.0
 */
internal fun <E> List<E>.dropLastView(count: Int): List<E> = subList(0, size - count)

/**
 * Shorthand for `subList(size - count, size)`.
 *
 * Returns a [view][List.subList] of the portion of this list starting [count]
 * elements from the end of the list. The original list is not modified.
 *
 * @param count The number of elements to take from the end of the list.
 * @return A view of the portion of this list starting from the specified [count] index.
 * @see List.subList for more information about list views.
 * @since 0.7.0
 */
internal fun <E> List<E>.takeLastView(count: Int): List<E> = subList(size - count, size)
