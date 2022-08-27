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

import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import com.afollestad.assent.GrantResult.DENIED
import com.afollestad.assent.GrantResult.GRANTED
import com.afollestad.assent.GrantResult.PERMANENTLY_DENIED
import com.afollestad.assent.Permission.ACCESS_BACKGROUND_LOCATION
import com.afollestad.assent.Permission.CALL_PHONE
import com.afollestad.assent.Permission.READ_CALENDAR
import com.afollestad.assent.Permission.READ_CONTACTS
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.afollestad.assent.rationale.ShouldShowRationale
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test

class AssentResultTest {
  private val result = AssentResult(
    mapOf(
      WRITE_EXTERNAL_STORAGE to GRANTED,
      ACCESS_BACKGROUND_LOCATION to DENIED,
      READ_CALENDAR to PERMANENTLY_DENIED
    )
  )

  @Test fun `grant results intArray constructor`() {
    val shouldShowRationale = mock<ShouldShowRationale>()
    whenever(shouldShowRationale.isPermanentlyDenied(READ_CALENDAR)).doReturn(true)

    assertThat(
      AssentResult(
        setOf(WRITE_EXTERNAL_STORAGE, ACCESS_BACKGROUND_LOCATION, READ_CALENDAR),
        intArrayOf(PERMISSION_GRANTED, PERMISSION_DENIED, PERMISSION_DENIED),
        shouldShowRationale
      )
    ).isEqualTo(result)
  }

  @Test fun `grant results list constructor`() {
    assertThat(
      AssentResult(
        setOf(WRITE_EXTERNAL_STORAGE, ACCESS_BACKGROUND_LOCATION, READ_CALENDAR),
        listOf(GRANTED, DENIED, PERMANENTLY_DENIED)
      )
    ).isEqualTo(result)
  }

  @Test fun `get result for one permission`() {
    assertThat(result[WRITE_EXTERNAL_STORAGE]).isEqualTo(GRANTED)
    assertThat(result[ACCESS_BACKGROUND_LOCATION]).isEqualTo(DENIED)
    assertThat(result[READ_CALENDAR]).isEqualTo(PERMANENTLY_DENIED)
  }

  @Test fun containsPermission() {
    assertThat(result.containsPermissions(WRITE_EXTERNAL_STORAGE)).isTrue()
    assertThat(result.containsPermissions(READ_CONTACTS)).isFalse()
  }

  @Test fun granted() {
    assertThat(result.granted()).isEqualTo(setOf(WRITE_EXTERNAL_STORAGE))
  }

  @Test fun denied() {
    assertThat(result.denied()).isEqualTo(setOf(ACCESS_BACKGROUND_LOCATION, READ_CALENDAR))
  }

  @Test fun permanentlyDenied() {
    assertThat(result.permanentlyDenied()).isEqualTo(setOf(READ_CALENDAR))
  }

  @Test fun isAllGranted() {
    assertThat(result.isAllGranted(WRITE_EXTERNAL_STORAGE)).isTrue()
    assertThat(result.isAllGranted(WRITE_EXTERNAL_STORAGE, ACCESS_BACKGROUND_LOCATION)).isFalse()
    assertThat(result.isAllGranted(ACCESS_BACKGROUND_LOCATION)).isFalse()
  }

  @Test fun isAllDenied() {
    assertThat(result.isAllDenied(ACCESS_BACKGROUND_LOCATION)).isTrue()
    assertThat(result.isAllDenied(READ_CALENDAR)).isTrue()
    assertThat(result.isAllDenied(READ_CALENDAR, WRITE_EXTERNAL_STORAGE)).isFalse()
    assertThat(result.isAllDenied(WRITE_EXTERNAL_STORAGE)).isFalse()
  }

  @Test fun `plus operator`() {
    val other = AssentResult(
      mapOf(CALL_PHONE to GRANTED)
    )
    assertThat(result + other).isEqualTo(
      AssentResult(
        mapOf(
          WRITE_EXTERNAL_STORAGE to GRANTED,
          ACCESS_BACKGROUND_LOCATION to DENIED,
          READ_CALENDAR to PERMANENTLY_DENIED,
          CALL_PHONE to GRANTED
        )
      )
    )
  }

  @Test fun `toString output is expected`() {
    assertThat(result.toString()).isEqualTo(
      "WRITE_EXTERNAL_STORAGE -> GRANTED, " +
        "ACCESS_BACKGROUND_LOCATION -> DENIED, " +
        "READ_CALENDAR -> PERMANENTLY_DENIED"
    )
  }
}
