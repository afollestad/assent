/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.assent

import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import com.afollestad.assent.Permission.ACCESS_COARSE_LOCATION
import com.afollestad.assent.Permission.CALL_PHONE
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class AssentResultTest {

  private val result = AssentResult(
      arrayOf(WRITE_EXTERNAL_STORAGE, ACCESS_COARSE_LOCATION),
      intArrayOf(PERMISSION_GRANTED, PERMISSION_DENIED)
  )

  @Test
  fun containsPermission_true() {
    assertThat(result.containsPermissions(WRITE_EXTERNAL_STORAGE)).isTrue()
  }

  @Test
  fun containsPermission_false() {
    assertThat(result.containsPermissions(CALL_PHONE)).isFalse()
  }

  @Test
  fun isAllGranted_true() {
    assertThat(result.isAllGranted(WRITE_EXTERNAL_STORAGE)).isTrue()
  }

  @Test
  fun isAllGranted_false_denied() {
    assertThat(result.isAllGranted(ACCESS_COARSE_LOCATION)).isFalse()
  }

  @Test(expected = IllegalArgumentException::class)
  fun isAllGranted_throws_notInSet() {
    result.isAllGranted(CALL_PHONE)
  }

  @Test
  fun isAllDenied_true() {
    assertThat(result.isAllDenied(ACCESS_COARSE_LOCATION)).isTrue()
  }

  @Test
  fun isAllDenied_false_granted() {
    assertThat(result.isAllDenied(WRITE_EXTERNAL_STORAGE)).isFalse()
  }

  @Test(expected = IllegalArgumentException::class)
  fun isAllDenied_throws_notInSet() {
    assertThat(result.isAllDenied(CALL_PHONE)).isFalse()
  }
}
