/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("MemberVisibilityCanBePrivate")

package com.afollestad.assent.internal

/** @author Aidan Follestad (afollestad) */
internal class Queue<T> {
  private val data: MutableList<T> = mutableListOf()
  private val lock = Any()

  fun push(item: T) = synchronized(lock) {
    data.add(item)
  }

  fun pop(): T = synchronized(lock) {
    val result = data.firstOrNull()
        ?: throw IllegalStateException("Queue is empty, cannot pop.")
    data.removeAt(0)
    return result
  }

  fun isNotEmpty(): Boolean = data.isNotEmpty()

  operator fun plusAssign(item: T) {
    push(item)
  }
}
