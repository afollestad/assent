/* Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent

/** @author Aidan Follestad (afollestad) */
internal class Queue<T> {

  private var data: MutableList<T> = mutableListOf()

  fun push(item: T) = data.add(item)

  fun poll() = if (data.isNotEmpty()) data[0] else null

  fun pop(): T {
    val result = poll() ?: throw IllegalStateException("Queue is empty, cannot pop.")
    data.removeAt(0)
    return result
  }

  fun size() = data.size

  fun isEmpty() = data.isEmpty()

  fun isNotEmpty() = data.isNotEmpty()

  fun clear() = data.clear()

  operator fun plusAssign(item: T) {
    push(item)
  }
}
