/* Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class QueueTest {

  @Test
  fun test_push() {
    val queue = Queue<String>()
    queue.push("One")
    queue += "Two"

    assertThat(queue.size()).isEqualTo(2)
    assertThat(queue.isEmpty()).isFalse()
    assertThat(queue.isNotEmpty()).isTrue()
  }

  @Test
  fun test_poll() {
    val queue = Queue<String>()
    queue.push("One")
    queue += "Two"

    val result = queue.poll()
    assertThat(result).isEqualTo("One")
    assertThat(queue.size()).isEqualTo(2)
  }

  @Test
  fun test_pop() {
    val queue = Queue<String>()
    queue.push("One")
    queue += "Two"

    val result = queue.pop()
    assertThat(result).isEqualTo("One")
    assertThat(queue.size()).isEqualTo(1)
    assertThat(queue.poll()).isEqualTo("Two")
  }
}
