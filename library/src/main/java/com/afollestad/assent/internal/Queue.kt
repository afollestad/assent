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
package com.afollestad.assent.internal

/** @author Aidan Follestad (afollestad) */
internal class Queue<T> {

  private var data: MutableList<T> = mutableListOf()

  fun push(item: T) = data.add(item)

  fun pop(): T {
    val result = poll() ?: throw IllegalStateException("Queue is empty, cannot pop.")
    data.removeAt(0)
    return result
  }

  fun size() = data.size

  fun isNotEmpty() = data.isNotEmpty()

  fun clear() = data.clear()

  operator fun plusAssign(item: T) {
    push(item)
  }

  private fun poll() = if (data.isNotEmpty()) data[0] else null
}
