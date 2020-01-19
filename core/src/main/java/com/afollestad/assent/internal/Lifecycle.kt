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
@file:Suppress("unused")

package com.afollestad.assent.internal

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/** @author Aidan Follestad (@afollestad) */
internal fun Any?.maybeObserveLifecycle(
  vararg watchFor: Event,
  onEvent: (Event) -> Unit
): Lifecycle? {
  if (this is LifecycleOwner) {
    return Lifecycle(this, watchFor, onEvent)
  }
  return null
}

/** @author Aidan Follestad (@afollestad) */
internal class Lifecycle(
  private var lifecycleOwner: LifecycleOwner?,
  private var watchFor: Array<out Event>,
  private var onEvent: ((Event) -> Unit)?
) : LifecycleObserver {
  init {
    lifecycleOwner?.lifecycle?.addObserver(this)
  }

  @OnLifecycleEvent(ON_CREATE)
  fun onCreate() {
    if (watchFor.isEmpty() || ON_CREATE in watchFor) {
      onEvent?.invoke(ON_CREATE)
    }
  }

  @OnLifecycleEvent(ON_START)
  fun onStart() {
    if (watchFor.isEmpty() || ON_START in watchFor) {
      onEvent?.invoke(ON_START)
    }
  }

  @OnLifecycleEvent(ON_RESUME)
  fun onResume() {
    if (watchFor.isEmpty() || ON_RESUME in watchFor) {
      onEvent?.invoke(ON_RESUME)
    }
  }

  @OnLifecycleEvent(ON_PAUSE)
  fun onPause() {
    if (watchFor.isEmpty() || ON_PAUSE in watchFor) {
      onEvent?.invoke(ON_PAUSE)
    }
  }

  @OnLifecycleEvent(ON_STOP)
  fun onStop() {
    if (watchFor.isEmpty() || ON_STOP in watchFor) {
      onEvent?.invoke(ON_STOP)
    }
  }

  @OnLifecycleEvent(ON_DESTROY)
  fun onDestroy() {
    lifecycleOwner?.lifecycle?.removeObserver(this)
    lifecycleOwner = null

    if (watchFor.isEmpty() || ON_DESTROY in watchFor) {
      onEvent?.invoke(ON_DESTROY)
    }
    onEvent = null
  }
}
