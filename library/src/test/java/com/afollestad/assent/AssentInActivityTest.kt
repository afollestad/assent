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
package com.afollestad.assent

import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.afollestad.assent.Permission.READ_CONTACTS
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.afollestad.assent.internal.Assent
import com.afollestad.assent.internal.PendingRequest
import com.afollestad.assent.internal.PermissionFragment
import com.afollestad.assent.testutil.AssertableCallback
import com.afollestad.assent.testutil.MockResponseQueue
import com.afollestad.assent.testutil.NoManifestTestRunner
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(NoManifestTestRunner::class)
class AssentInActivityTest {
  private val allowedPermissions = mutableSetOf<Permission>()
  private val callback = AssertableCallback()

  private val fragmentTransaction = mock<FragmentTransaction>()
  private val fragmentManager = mock<FragmentManager> {
    on { beginTransaction() } doReturn fragmentTransaction
  }

  private val permissionFragment = mock<PermissionFragment>()
  private val responseQueue = MockResponseQueue(allowedPermissions, permissionFragment)

  private val activity = mock<FragmentActivity> {
    // FRAGMENT TRANSACTIONS
    on { supportFragmentManager } doReturn fragmentManager
    // CHECK PERMISSION
    on { checkPermission(any(), eq(0), any()) } doAnswer { inv ->
      val checkPermission = inv.getArgument<String>(0)
      val parsedCheckPermission = Permission.parse(checkPermission)
      return@doAnswer if (allowedPermissions.contains(parsedCheckPermission)) {
        PackageManager.PERMISSION_GRANTED
      } else {
        PackageManager.PERMISSION_DENIED
      }
    }
  }

  @Before fun setup() {
    allowedPermissions.clear()
    Assent.fragmentCreator = { permissionFragment }

    whenever(permissionFragment.perform(any())).thenCallRealMethod()
    whenever(
        permissionFragment.onRequestPermissionsResult(any(), any(), any())
    ).thenCallRealMethod()
    whenever(permissionFragment.detach()).thenCallRealMethod()

    whenever(permissionFragment.requestPermissions(any(), any())).doAnswer { inv ->
      val permissions: Array<out String> = inv.getArgument(0)
      val requestCode: Int = inv.getArgument(1)
      responseQueue.handle(permissions, requestCode)
    }
  }

  @Test fun `check all granted - all true`() {
    allowedPermissions.addAll(arrayOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS))
    val condition = activity.isAllGranted(WRITE_EXTERNAL_STORAGE, READ_CONTACTS)
    assertThat(condition).isTrue()
  }

  @Test fun `check all granted - one true`() {
    allowedPermissions.add(WRITE_EXTERNAL_STORAGE)
    val condition = activity.isAllGranted(WRITE_EXTERNAL_STORAGE, READ_CONTACTS)
    assertThat(condition).isFalse()
  }

  @Test fun `check all granted - none true`() {
    val condition = activity.isAllGranted(WRITE_EXTERNAL_STORAGE, READ_CONTACTS)
    assertThat(condition).isFalse()
  }

  @Test fun `ask for permission - no rationale handler - all granted`() {
    allowedPermissions.addAll(arrayOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS))
    activity.askForPermissions(
        WRITE_EXTERNAL_STORAGE, READ_CONTACTS,
        requestCode = 69, callback = callback.consumer
    )

    responseQueue.respondToAll()

    permissionFragment.assertPerformRequest(69)
    verify(permissionFragment).requestPermissions(
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        69
    )
    verify(permissionFragment).onRequestPermissionsResult(
        69,
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        intArrayOf(PERMISSION_GRANTED, PERMISSION_GRANTED)
    )

    callback.assertInvokes(
        AssentResult(
            permissions = listOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS),
            grantResults = intArrayOf(PERMISSION_GRANTED, PERMISSION_GRANTED)
        )
    )
  }

  @Test fun `ask for permission - no rationale handler - one denied`() {
    allowedPermissions.add(WRITE_EXTERNAL_STORAGE)
    activity.askForPermissions(
        WRITE_EXTERNAL_STORAGE, READ_CONTACTS,
        requestCode = 70, callback = callback.consumer
    )

    responseQueue.respondToAll()

    permissionFragment.assertPerformRequest(70)
    verify(permissionFragment).requestPermissions(
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        70
    )
    verify(permissionFragment).onRequestPermissionsResult(
        70,
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        intArrayOf(PERMISSION_GRANTED, PERMISSION_DENIED)
    )

    callback.assertInvokes(
        AssentResult(
            permissions = listOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS),
            grantResults = intArrayOf(PERMISSION_GRANTED, PERMISSION_DENIED)
        )
    )
  }

  @Test fun `ask for permission - no rationale handler - all denied`() {
    activity.askForPermissions(
        WRITE_EXTERNAL_STORAGE, READ_CONTACTS,
        requestCode = 71, callback = callback.consumer
    )

    responseQueue.respondToAll()

    permissionFragment.assertPerformRequest(71)
    verify(permissionFragment).requestPermissions(
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        71
    )
    verify(permissionFragment).onRequestPermissionsResult(
        71,
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        intArrayOf(PERMISSION_DENIED, PERMISSION_DENIED)
    )

    callback.assertInvokes(
        AssentResult(
            permissions = listOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS),
            grantResults = intArrayOf(PERMISSION_DENIED, PERMISSION_DENIED)
        )
    )
  }

  @Test fun `ask for permission - no rationale handler - handles duplicate requests`() {
    allowedPermissions.addAll(arrayOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS))
    activity.askForPermissions(
        WRITE_EXTERNAL_STORAGE, READ_CONTACTS,
        requestCode = 69, callback = callback.consumer
    )
    activity.askForPermissions(
        WRITE_EXTERNAL_STORAGE, READ_CONTACTS,
        requestCode = 71, callback = callback.consumer
    )

    responseQueue.respondToAll()

    permissionFragment.assertPerformRequest(69)
    verify(permissionFragment, times(1)).requestPermissions(
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        69
    )
    verify(permissionFragment, times(1)).onRequestPermissionsResult(
        69,
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        intArrayOf(PERMISSION_GRANTED, PERMISSION_GRANTED)
    )

    callback.assertInvokes(
        AssentResult(
            permissions = listOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS),
            grantResults = intArrayOf(PERMISSION_GRANTED, PERMISSION_GRANTED)
        ),
        AssentResult(
            permissions = listOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS),
            grantResults = intArrayOf(PERMISSION_GRANTED, PERMISSION_GRANTED)
        )
    )
  }

  @Test fun `ask for permission - no rationale handler - queue different requests`() {
    allowedPermissions.addAll(arrayOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS))
    activity.askForPermissions(
        WRITE_EXTERNAL_STORAGE,
        requestCode = 69, callback = callback.consumer
    )
    activity.askForPermissions(
        READ_CONTACTS,
        requestCode = 69, callback = callback.consumer
    )

    responseQueue.respondToOne()

    permissionFragment.assertPerformRequest(69)
    verify(permissionFragment, times(1)).requestPermissions(
        arrayOf(WRITE_EXTERNAL_STORAGE.value),
        69
    )
    verify(permissionFragment, times(1)).onRequestPermissionsResult(
        69,
        arrayOf(WRITE_EXTERNAL_STORAGE.value),
        intArrayOf(PERMISSION_GRANTED)
    )

    callback.assertInvokes(
        AssentResult(
            permissions = listOf(WRITE_EXTERNAL_STORAGE),
            grantResults = intArrayOf(PERMISSION_GRANTED)
        )
    )

    responseQueue.respondToOne()

    permissionFragment.assertPerformRequest(70, 1)
    verify(permissionFragment, times(1)).requestPermissions(
        arrayOf(READ_CONTACTS.value),
        70
    )
    verify(permissionFragment, times(1)).onRequestPermissionsResult(
        70,
        arrayOf(READ_CONTACTS.value),
        intArrayOf(PERMISSION_GRANTED)
    )

    callback.assertInvokes(
        AssentResult(
            permissions = listOf(READ_CONTACTS),
            grantResults = intArrayOf(PERMISSION_GRANTED)
        )
    )
  }

  @Test fun `run with permissions - granted - invokes`() {
    allowedPermissions.addAll(arrayOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS))
    activity.runWithPermissions(
        WRITE_EXTERNAL_STORAGE, READ_CONTACTS,
        requestCode = 10, execute = callback.consumer
    )

    responseQueue.respondToAll()

    permissionFragment.assertPerformRequest(10)
    verify(permissionFragment, times(1)).requestPermissions(
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        10
    )
    verify(permissionFragment, times(1)).onRequestPermissionsResult(
        10,
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        intArrayOf(PERMISSION_GRANTED, PERMISSION_GRANTED)
    )

    callback.assertInvokes(
        AssentResult(
            permissions = listOf(WRITE_EXTERNAL_STORAGE, READ_CONTACTS),
            grantResults = intArrayOf(PERMISSION_GRANTED, PERMISSION_GRANTED)
        )
    )
  }

  @Test fun `run with permissions - denied - does not invoke`() {
    allowedPermissions.add(READ_CONTACTS)
    activity.runWithPermissions(
        WRITE_EXTERNAL_STORAGE, READ_CONTACTS,
        requestCode = 10, execute = callback.consumer
    )
    responseQueue.respondToAll()

    verify(permissionFragment, times(1)).requestPermissions(
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        10
    )
    verify(permissionFragment, times(1)).onRequestPermissionsResult(
        10,
        arrayOf(WRITE_EXTERNAL_STORAGE.value, READ_CONTACTS.value),
        intArrayOf(PERMISSION_DENIED, PERMISSION_GRANTED)
    )

    callback.assertDoesNotInvoke()
  }
}

internal fun PermissionFragment.assertPerformRequest(
  requestCode: Int,
  index: Int = 0
) {
  val requestCaptor = argumentCaptor<PendingRequest>()
  verify(this, atLeastOnce()).perform(requestCaptor.capture())
  val values = requestCaptor.allValues
  assertThat(values[index].requestCode).isEqualTo(requestCode)
}
