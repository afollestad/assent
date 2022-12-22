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

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.fragment.app.Fragment
import com.afollestad.assent.AssentResult
import com.afollestad.assent.DefaultShouldShowRationale
import com.afollestad.assent.internal.Assent.Companion.ensureFragment
import com.afollestad.assent.internal.Assent.Companion.forgetFragment
import com.afollestad.assent.internal.Assent.Companion.get

/** @author Aidan Follestad (afollestad) */
class PermissionFragment : Fragment() {

  var launcher: ActivityResultLauncher<*>? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    log("onAttach(%s)", context)
  }

  override fun onDetach() {
    log("onDetach()")
    launcher?.unregister()
    super.onDetach()
  }

  internal fun perform(request: PendingRequest) {
    log("perform(%s)", request)
    launcher = registerForActivityResult(RequestMultiplePermissions()) {
      onPermissionsResponse(it)
    }.apply {
      launch(request.permissions.allValues())
    }
  }

  internal fun detach() {
    if (parentFragment != null) {
      log("Detaching PermissionFragment from parent Fragment %s", parentFragment)
      parentFragment?.transact {
        detach(this@PermissionFragment)
        remove(this@PermissionFragment)
      }
    } else if (activity != null) {
      log("Detaching PermissionFragment from Activity %s", activity)
      activity?.transact {
        detach(this@PermissionFragment)
        remove(this@PermissionFragment)
      }
    }
  }

  private fun onPermissionsResponse(results: Map<String, Boolean>) {
    val activity = activity ?: error("Fragment is not attached: $this")
    val shouldShowRationale = DefaultShouldShowRationale(activity)
    val result = AssentResult(
      results = results,
      shouldShowRationale = shouldShowRationale
    )
    log("onPermissionsResponse(): %s", result)
    handleResult(result)
  }
}

internal fun Fragment.handleResult(result: AssentResult) {
  log("handleResult(): %s", result)
  val currentRequest: PendingRequest? = get().currentPendingRequest
  if (currentRequest == null) {
    warn("onPermissionsResponse() called but there's no current pending request.")
    return
  }

  currentRequest.callbacks.invokeAll(result)
  get().currentPendingRequest = null

  if (get().requestQueue.isNotEmpty()) {
    // Execute the next request in the queue
    val nextRequest: PendingRequest = get().requestQueue.pop()
      .also { get().currentPendingRequest = it }
    log("Executing next request in the queue: %s", nextRequest)
    ensureFragment(this@handleResult).perform(nextRequest)
  } else {
    // No more requests to execute, we can destroy the Fragment
    log("Nothing more in the queue to execute, forgetting the PermissionFragment.")
    forgetFragment()
  }
}
