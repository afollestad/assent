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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

internal class Data {

  internal val requestQueue = Queue<PendingRequest>()
  internal var currentPendingRequest: PendingRequest? = null
  internal var permissionFragment: PermissionFragment? = null

  companion object {

    val LOCK = Any()
    var instance: Data? = null

    private const val TAG_ACTIVITY = "[assent_permission_fragment/activity]"
    private const val TAG_FRAGMENT = "[assent_permission_fragment/fragment]"

    fun get(): Data {
      if (instance == null) {
        instance = Data()
      }
      return instance ?: throw IllegalStateException()
    }

    fun assureFragment(context: Context): PermissionFragment = with(get()) {
      if (permissionFragment == null) {
        val newFragment = PermissionFragment() // we store for nullability protection
        permissionFragment = newFragment
        when (context) {
          is FragmentActivity -> context.transact {
            add(newFragment, TAG_ACTIVITY)
          }
          else -> throw UnsupportedOperationException(
              "Unable to assure the permission Fragment on Context $context"
          )
        }
      }
      return permissionFragment ?: throw IllegalStateException()
    }

    fun assureFragment(context: Fragment): PermissionFragment = with(get()) {
      if (permissionFragment == null) {
        val newFragment = PermissionFragment() // we store for nullability protection
        permissionFragment = PermissionFragment()
        context.transact {
          add(newFragment, TAG_FRAGMENT)
        }
      }
      return permissionFragment ?: throw IllegalStateException()
    }

    fun forgetFragment() = with(get()) {
      permissionFragment?.detach()
      permissionFragment = null
    }
  }
}
