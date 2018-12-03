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

import android.Manifest
import android.annotation.SuppressLint

/**
 * See http://developer.android.com/guide/topics/security/permissions.html#normal-dangerous for a
 * list of 'dangerous' permissions that require a permission request on API 23.
 */
@SuppressLint("InlinedApi")
enum class Permission(val value: String) {
  UNKNOWN(""),

  READ_CALENDAR(Manifest.permission.READ_CALENDAR),

  WRITE_CALENDAR(Manifest.permission.WRITE_CALENDAR),

  CAMERA(Manifest.permission.CAMERA),

  READ_CONTACTS(Manifest.permission.READ_CONTACTS),
  WRITE_CONTACTS(Manifest.permission.WRITE_CONTACTS),
  GET_ACCOUNTS(Manifest.permission.GET_ACCOUNTS),

  ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
  ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),

  RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),

  READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE),
  CALL_PHONE(Manifest.permission.CALL_PHONE),
  READ_CALL_LOG(Manifest.permission.READ_CALL_LOG),
  WRITE_CALL_LOG(Manifest.permission.WRITE_CALL_LOG),
  ADD_VOICEMAIL(Manifest.permission.ADD_VOICEMAIL),
  USE_SIP(Manifest.permission.USE_SIP),
  PROCESS_OUTGOING_CALLS(Manifest.permission.PROCESS_OUTGOING_CALLS),

  BODY_SENSORS(Manifest.permission.BODY_SENSORS),

  SEND_SMS(Manifest.permission.SEND_SMS),
  RECEIVE_SMS(Manifest.permission.RECEIVE_SMS),
  READ_SMS(Manifest.permission.READ_SMS),
  RECEIVE_WAP_PUSH(Manifest.permission.RECEIVE_WAP_PUSH),
  RECEIVE_MMS(Manifest.permission.RECEIVE_MMS),

  READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
  WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),

  SYSTEM_ALERT_WINDOW(Manifest.permission.SYSTEM_ALERT_WINDOW);

  companion object {
    fun parse(raw: String): Permission {
      for (option in Permission.values()) {
        if (option.value == raw) {
          return option
        }
      }
      return UNKNOWN
    }
  }
}
