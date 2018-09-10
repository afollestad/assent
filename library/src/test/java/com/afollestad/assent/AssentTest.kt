/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent

import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.afollestad.assent.Assent.Companion.onPermissionsResponse
import com.afollestad.assent.Assent.Companion.setAssentFragment
import com.afollestad.assent.Permission.ACCESS_COARSE_LOCATION
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Before
import org.junit.Test

internal interface TestRequestExecutor {
  fun execute(request: PendingRequest)
}

class AssentTest {

  private val activityOne = mock<FragmentActivity>()
  private val fragmentOne = mock<Fragment>()
  private val activityTwo = mock<FragmentActivity>()
  private val fragmentTwo = mock<Fragment>()
  private val requestExecutor = mock<TestRequestExecutor>()

  @Before
  fun setup() {
    Assent.destroy()
    Assent.requestExecutor = requestExecutor::execute
  }

  @Test
  fun setActivity_setsNew() {
    Assent.setActivity(activityOne, activityOne)
    assertThat(Assent.safeInstance.activity).isEqualTo(activityOne)
  }

  @Test
  fun setActivity_clearsSelf() {
    Assent.setActivity(activityOne, activityOne)
    assertThat(Assent.safeInstance.activity).isEqualTo(activityOne)
    Assent.setActivity(activityOne, null)
    assertThat(Assent.safeInstance.activity).isNull()
  }

  @Test
  fun setActivity_doesNotClearOther() {
    Assent.setActivity(activityOne, activityTwo)
    assertThat(Assent.safeInstance.activity).isEqualTo(activityTwo)
    Assent.setActivity(activityOne, null)
    assertThat(Assent.safeInstance.activity).isEqualTo(activityTwo)
  }

  @Test
  fun setFragment_setsNew() {
    setAssentFragment(fragmentOne, fragmentOne)
    assertThat(Assent.safeInstance.fragment).isEqualTo(fragmentOne)
  }

  @Test
  fun setFragment_clearsSelf() {
    setAssentFragment(fragmentOne, fragmentOne)
    assertThat(Assent.safeInstance.fragment).isEqualTo(fragmentOne)
    setAssentFragment(fragmentOne, null)
    assertThat(Assent.safeInstance.fragment).isNull()
  }

  @Test
  fun setFragment_doesNotClearOther() {
    setAssentFragment(fragmentOne, fragmentTwo)
    assertThat(Assent.safeInstance.fragment).isEqualTo(fragmentTwo)
    setAssentFragment(fragmentOne, null)
    assertThat(Assent.safeInstance.fragment).isEqualTo(fragmentTwo)
  }

  @Test
  fun request_and_response_one_granted() {
    var callbackCalled = false
    Assent.request(WRITE_EXTERNAL_STORAGE) {
      assertThat(it.permissions.size).isEqualTo(1)
      assertThat(it.permissions[0]).isEqualTo(WRITE_EXTERNAL_STORAGE)
      assertThat(it.grantResults.size).isEqualTo(1)
      assertThat(it.grantResults[0]).isEqualTo(PERMISSION_GRANTED)
      callbackCalled = true
    }
    val currentRequest = Assent.safeInstance.currentPendingRequest!!

    assertThat(currentRequest.requestCode).isEqualTo(69)
    assertThat(currentRequest.permissions.size).isEqualTo(1)
    assertThat(currentRequest.permissions[0]).isEqualTo(WRITE_EXTERNAL_STORAGE)
    assertThat(currentRequest.callbacks.size).isEqualTo(1)

    assertThat(Assent.safeInstance.requestQueue.isEmpty()).isTrue()

    onPermissionsResponse(
        arrayOf(WRITE_EXTERNAL_STORAGE.value),
        intArrayOf(PERMISSION_GRANTED)
    )

    assertThat(Assent.safeInstance.requestQueue.isEmpty()).isTrue()
    assertThat(Assent.safeInstance.currentPendingRequest).isNull()

    verify(requestExecutor, times(1)).execute(currentRequest)
    assertThat(callbackCalled)
        .withFailMessage("Callback should be called.")
        .isTrue()
  }

  @Test
  fun request_and_response_one_denied() {
    var callbackCalled = false
    Assent.request(WRITE_EXTERNAL_STORAGE) {
      assertThat(it.permissions.size).isEqualTo(1)
      assertThat(it.permissions[0]).isEqualTo(WRITE_EXTERNAL_STORAGE)
      assertThat(it.grantResults.size).isEqualTo(1)
      assertThat(it.grantResults[0]).isEqualTo(PERMISSION_DENIED)
      callbackCalled = true
    }
    val currentRequest = Assent.safeInstance.currentPendingRequest!!

    assertThat(currentRequest.requestCode).isEqualTo(69)
    assertThat(currentRequest.permissions.size).isEqualTo(1)
    assertThat(currentRequest.permissions[0]).isEqualTo(WRITE_EXTERNAL_STORAGE)
    assertThat(currentRequest.callbacks.size).isEqualTo(1)

    assertThat(Assent.safeInstance.requestQueue.isEmpty()).isTrue()

    onPermissionsResponse(
        arrayOf(WRITE_EXTERNAL_STORAGE.value),
        intArrayOf(PERMISSION_DENIED)
    )

    assertThat(Assent.safeInstance.requestQueue.isEmpty()).isTrue()
    assertThat(Assent.safeInstance.currentPendingRequest).isNull()

    verify(requestExecutor, times(1)).execute(currentRequest)
    assertThat(callbackCalled)
        .withFailMessage("Callback should be called.")
        .isTrue()
  }

  @Test
  fun request_and_response_two_duplicates() {
    var callbacksCalled = 0
    Assent.request(WRITE_EXTERNAL_STORAGE) {
      assertThat(it.permissions.size).isEqualTo(1)
      assertThat(it.permissions[0]).isEqualTo(WRITE_EXTERNAL_STORAGE)
      assertThat(it.grantResults.size).isEqualTo(1)
      assertThat(it.grantResults[0]).isEqualTo(PERMISSION_GRANTED)
      callbacksCalled++
    }
    Assent.request(WRITE_EXTERNAL_STORAGE) {
      assertThat(it.permissions.size).isEqualTo(1)
      assertThat(it.permissions[0]).isEqualTo(WRITE_EXTERNAL_STORAGE)
      assertThat(it.grantResults.size).isEqualTo(1)
      assertThat(it.grantResults[0]).isEqualTo(PERMISSION_GRANTED)
      callbacksCalled++
    }

    val currentRequest = Assent.safeInstance.currentPendingRequest!!

    assertThat(currentRequest.requestCode).isEqualTo(69)
    assertThat(currentRequest.permissions.size).isEqualTo(1)
    assertThat(currentRequest.permissions[0]).isEqualTo(WRITE_EXTERNAL_STORAGE)
    assertThat(currentRequest.callbacks.size).isEqualTo(2)

    assertThat(Assent.safeInstance.requestQueue.isEmpty()).isTrue()

    onPermissionsResponse(
        arrayOf(WRITE_EXTERNAL_STORAGE.value),
        intArrayOf(PERMISSION_GRANTED)
    )

    assertThat(Assent.safeInstance.requestQueue.isEmpty()).isTrue()
    assertThat(Assent.safeInstance.currentPendingRequest).isNull()

    verify(requestExecutor, times(1)).execute(currentRequest)
    assertThat(callbacksCalled)
        .withFailMessage("Both callbacks should be called.")
        .isEqualTo(2)
  }

  @Test
  fun request_and_response_two_simultaneous() {
    var callbacksCalled = 0
    Assent.request(WRITE_EXTERNAL_STORAGE) {
      assertThat(it.permissions.size).isEqualTo(1)
      assertThat(it.permissions[0]).isEqualTo(WRITE_EXTERNAL_STORAGE)
      assertThat(it.grantResults.size).isEqualTo(1)
      assertThat(it.grantResults[0]).isEqualTo(PERMISSION_GRANTED)
      callbacksCalled++
    }
    Assent.request(ACCESS_COARSE_LOCATION) {
      assertThat(it.permissions.size).isEqualTo(1)
      assertThat(it.permissions[0]).isEqualTo(ACCESS_COARSE_LOCATION)
      assertThat(it.grantResults.size).isEqualTo(1)
      assertThat(it.grantResults[0]).isEqualTo(PERMISSION_DENIED)
      callbacksCalled++
    }

    val currentRequest1 = Assent.safeInstance.currentPendingRequest!!
    assertThat(currentRequest1.requestCode).isEqualTo(69)
    assertThat(currentRequest1.permissions.size).isEqualTo(1)
    assertThat(currentRequest1.permissions[0]).isEqualTo(WRITE_EXTERNAL_STORAGE)
    assertThat(currentRequest1.callbacks.size).isEqualTo(1)

    assertThat(Assent.safeInstance.requestQueue.size()).isEqualTo(1)

    onPermissionsResponse(
        arrayOf(WRITE_EXTERNAL_STORAGE.value),
        intArrayOf(PERMISSION_GRANTED)
    )

    assertThat(Assent.safeInstance.requestQueue.isEmpty()).isTrue()

    val currentRequest2 = Assent.safeInstance.currentPendingRequest!!
    assertThat(currentRequest2).isNotEqualTo(currentRequest1)
    assertThat(currentRequest2.requestCode).isEqualTo(70)
    assertThat(currentRequest2.permissions.size).isEqualTo(1)
    assertThat(currentRequest2.permissions[0]).isEqualTo(ACCESS_COARSE_LOCATION)
    assertThat(currentRequest2.callbacks.size).isEqualTo(1)

    onPermissionsResponse(
        arrayOf(ACCESS_COARSE_LOCATION.value),
        intArrayOf(PERMISSION_DENIED)
    )

    assertThat(Assent.safeInstance.currentPendingRequest).isNull()

    verify(requestExecutor, times(1)).execute(currentRequest1)
    verify(requestExecutor, times(1)).execute(currentRequest2)
    assertThat(callbacksCalled)
        .withFailMessage("Both callbacks should be called.")
        .isEqualTo(2)
  }
}
