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
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import timber.log.Timber

internal class Assent {

  internal val requestQueue = Queue<PendingRequest>()
  internal var currentPendingRequest: PendingRequest? = null
  internal var permissionFragment: PermissionFragment? = null

  companion object {

    val LOCK = Any()
    var instance: Assent? = null

    @VisibleForTesting(otherwise = PRIVATE)
    internal var fragmentCreator: () -> PermissionFragment = {
      PermissionFragment()
    }

    @VisibleForTesting(otherwise = PRIVATE)
    internal const val TAG_ACTIVITY = "[assent_permission_fragment/activity]"
    @VisibleForTesting(otherwise = PRIVATE)
    internal const val TAG_FRAGMENT = "[assent_permission_fragment/fragment]"

    fun get(): Assent {
      return instance ?: Assent().also {
        instance = it
      }
    }

    fun ensureFragment(context: Context): PermissionFragment = with(get()) {
      require(context is FragmentActivity) {
        "Unable to ensure the permission Fragment on Context $context"
      }

      permissionFragment = if (permissionFragment == null) {
        fragmentCreator().apply {
          log("Created new PermissionFragment for Context")
          context.transact { add(this@apply, TAG_ACTIVITY) }
        }
      } else {
        log("Re-using PermissionFragment for Context")
        permissionFragment
      }
      return permissionFragment ?: throw IllegalStateException()
    }

    fun ensureFragment(context: Fragment): PermissionFragment = with(get()) {
      permissionFragment = if (permissionFragment == null) {
        fragmentCreator().apply {
          log("Created new PermissionFragment for parent Fragment")
          context.transact { add(this@apply, TAG_FRAGMENT) }
        }
      } else {
        log("Re-using PermissionFragment for parent Fragment")
        permissionFragment
      }
      return permissionFragment ?: throw IllegalStateException()
    }

    fun forgetFragment() = with(get()) {
      log("forgetFragment()")
      permissionFragment?.detach()
      permissionFragment = null
    }
  }
}

private fun log(message: String) {
  Timber.tag("AssentData")
  Timber.d(message)
}
