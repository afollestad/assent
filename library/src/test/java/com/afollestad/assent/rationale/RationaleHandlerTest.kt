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
package com.afollestad.assent.rationale

import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import com.afollestad.assent.AssentResult
import com.afollestad.assent.Permission
import com.afollestad.assent.Permission.ACCESS_FINE_LOCATION
import com.afollestad.assent.Permission.CALL_PHONE
import com.afollestad.assent.Permission.READ_CONTACTS
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.afollestad.assent.testutil.AssertableCallback
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

private const val CALL_PHONE_RATIONALE = "We need to call your friends!"
private const val WRITE_STORAGE_RATIONALE = "We need to save your files!"

class RationaleHandlerTest {
  private val activity = mock<Activity>()
  private val responder = MockRequestResponder()
  private val shouldShow = MockShouldShowRationale()
  private val callback = AssertableCallback()

  private val rationaleHandler = object : RationaleHandler(
      activity,
      responder.requester,
      shouldShow
  ) {
    override fun showRationale(
      permission: Permission,
      message: CharSequence,
      onContinue: (confirmed: Boolean) -> Unit
    ) = shouldShow.handleShowRationale(permission, message, onContinue)
  }.apply {
    onPermission(READ_CONTACTS, CALL_PHONE_RATIONALE)
    onPermission(ACCESS_FINE_LOCATION, WRITE_STORAGE_RATIONALE)
  }

  @Before fun setup() {
    shouldShow.showFor(READ_CONTACTS)
    shouldShow.showFor(ACCESS_FINE_LOCATION)
  }

  @Test fun `request two simple permissions`() {
    val permissions = arrayOf(
        CALL_PHONE,
        WRITE_EXTERNAL_STORAGE
    )
    responder.allow(CALL_PHONE, WRITE_EXTERNAL_STORAGE)

    rationaleHandler.requestPermissions(
        permissions,
        69,
        callback.consumer
    )

    assertThat(responder.log().single()).isEqualTo(
        arrayOf(CALL_PHONE, WRITE_EXTERNAL_STORAGE)
    )
    callback.assertInvokes(
        AssentResult(
            listOf(CALL_PHONE, WRITE_EXTERNAL_STORAGE),
            intArrayOf(PERMISSION_GRANTED, PERMISSION_GRANTED)
        )
    )
  }

  @Test fun `request two simple permissions but deny one`() {
    val permissions = arrayOf(
        CALL_PHONE,
        WRITE_EXTERNAL_STORAGE
    )
    responder.allow(WRITE_EXTERNAL_STORAGE)
    responder.deny(CALL_PHONE)

    rationaleHandler.requestPermissions(
        permissions,
        69,
        callback.consumer
    )

    assertThat(responder.log().single()).isEqualTo(
        arrayOf(CALL_PHONE, WRITE_EXTERNAL_STORAGE)
    )
    callback.assertInvokes(
        AssentResult(
            listOf(CALL_PHONE, WRITE_EXTERNAL_STORAGE),
            intArrayOf(PERMISSION_DENIED, PERMISSION_GRANTED)
        )
    )
  }

  @Test fun `request two rationale permissions`() {
    val permissions = arrayOf(
        READ_CONTACTS,
        ACCESS_FINE_LOCATION
    )
    responder.allow(READ_CONTACTS, ACCESS_FINE_LOCATION)
    shouldShow.confirmFor(READ_CONTACTS, ACCESS_FINE_LOCATION)

    rationaleHandler.requestPermissions(
        permissions,
        69,
        callback.consumer
    )

    assertThat(responder.log().first()).isEqualTo(
        arrayOf(READ_CONTACTS)
    )
    assertThat(responder.log().second()).isEqualTo(
        arrayOf(ACCESS_FINE_LOCATION)
    )
    callback.assertInvokes(
        AssentResult(
            listOf(READ_CONTACTS, ACCESS_FINE_LOCATION),
            intArrayOf(PERMISSION_GRANTED, PERMISSION_GRANTED)
        )
    )
  }

  @Test fun `request two rationale permissions but deny one`() {
    val permissions = arrayOf(
        READ_CONTACTS,
        ACCESS_FINE_LOCATION
    )
    responder.allow(ACCESS_FINE_LOCATION)
    responder.deny(READ_CONTACTS)
    shouldShow.confirmFor(READ_CONTACTS, ACCESS_FINE_LOCATION)

    rationaleHandler.requestPermissions(
        permissions,
        69,
        callback.consumer
    )

    assertThat(responder.log().first()).isEqualTo(
        arrayOf(READ_CONTACTS)
    )
    assertThat(responder.log().second()).isEqualTo(
        arrayOf(ACCESS_FINE_LOCATION)
    )
    callback.assertInvokes(
        AssentResult(
            listOf(READ_CONTACTS, ACCESS_FINE_LOCATION),
            intArrayOf(PERMISSION_DENIED, PERMISSION_GRANTED)
        )
    )
  }

  @Test fun `request two rationale permissions but do not confirm one`() {
    val permissions = arrayOf(
        READ_CONTACTS,
        ACCESS_FINE_LOCATION
    )
    responder.allow(ACCESS_FINE_LOCATION, READ_CONTACTS)
    shouldShow.confirmFor(ACCESS_FINE_LOCATION)
    shouldShow.doNotConfirmFor(READ_CONTACTS)

    rationaleHandler.requestPermissions(
        permissions,
        69,
        callback.consumer
    )

    assertThat(responder.log().single()).isEqualTo(
        arrayOf(ACCESS_FINE_LOCATION)
    )
    callback.assertInvokes(
        AssentResult(
            listOf(READ_CONTACTS, ACCESS_FINE_LOCATION),
            intArrayOf(PERMISSION_DENIED, PERMISSION_GRANTED)
        )
    )
  }

  @Test fun `request two simple and two rationale permissions`() {
    val permissions = arrayOf(
        CALL_PHONE,
        WRITE_EXTERNAL_STORAGE,
        READ_CONTACTS,
        ACCESS_FINE_LOCATION
    )
    responder.allow(CALL_PHONE, WRITE_EXTERNAL_STORAGE, READ_CONTACTS, ACCESS_FINE_LOCATION)
    shouldShow.confirmFor(READ_CONTACTS, ACCESS_FINE_LOCATION)

    rationaleHandler.requestPermissions(
        permissions,
        69,
        callback.consumer
    )

    assertThat(responder.log().first()).isEqualTo(
        arrayOf(CALL_PHONE, WRITE_EXTERNAL_STORAGE)
    )
    assertThat(responder.log().second()).isEqualTo(
        arrayOf(READ_CONTACTS)
    )
    assertThat(responder.log().last()).isEqualTo(
        arrayOf(ACCESS_FINE_LOCATION)
    )
    callback.assertInvokes(
        AssentResult(
            listOf(CALL_PHONE, WRITE_EXTERNAL_STORAGE, READ_CONTACTS, ACCESS_FINE_LOCATION),
            intArrayOf(
                PERMISSION_GRANTED, PERMISSION_GRANTED, PERMISSION_GRANTED, PERMISSION_GRANTED
            )
        )
    )
  }

  @Test fun `request two simple and two rationale permissions but do not confirm one`() {
    val permissions = arrayOf(
        CALL_PHONE,
        WRITE_EXTERNAL_STORAGE,
        READ_CONTACTS,
        ACCESS_FINE_LOCATION
    )
    responder.allow(CALL_PHONE, WRITE_EXTERNAL_STORAGE, READ_CONTACTS, ACCESS_FINE_LOCATION)
    shouldShow.confirmFor(ACCESS_FINE_LOCATION)
    shouldShow.doNotConfirmFor(READ_CONTACTS)

    rationaleHandler.requestPermissions(
        permissions,
        69,
        callback.consumer
    )

    assertThat(responder.log().first()).isEqualTo(
        arrayOf(CALL_PHONE, WRITE_EXTERNAL_STORAGE)
    )
    assertThat(responder.log().last()).isEqualTo(
        arrayOf(ACCESS_FINE_LOCATION)
    )
    callback.assertInvokes(
        AssentResult(
            listOf(CALL_PHONE, WRITE_EXTERNAL_STORAGE, READ_CONTACTS, ACCESS_FINE_LOCATION),
            intArrayOf(
                PERMISSION_GRANTED, PERMISSION_GRANTED, PERMISSION_DENIED, PERMISSION_GRANTED
            )
        )
    )
  }

  @After fun cleanup() {
    responder.reset()
    shouldShow.reset()
  }
}

private fun <T> List<T>.second(): T {
  if (isEmpty())
    throw NoSuchElementException("List is empty.")
  else if (size < 2)
    throw NoSuchElementException("There is not a second element.")
  return this[1]
}
