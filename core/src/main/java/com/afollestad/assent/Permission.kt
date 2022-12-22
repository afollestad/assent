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

package com.afollestad.assent

import android.Manifest
import android.annotation.SuppressLint

@SuppressLint("InlinedApi")
enum class Permission(val value: String) {
  UNKNOWN(""),

  READ_CALENDAR(Manifest.permission.READ_CALENDAR),
  WRITE_CALENDAR(Manifest.permission.WRITE_CALENDAR),

  CAMERA(Manifest.permission.CAMERA),
  RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),
  BIND_TV_INTERACTIVE_APP(Manifest.permission.BIND_TV_INTERACTIVE_APP),

  READ_CONTACTS(Manifest.permission.READ_CONTACTS),
  WRITE_CONTACTS(Manifest.permission.WRITE_CONTACTS),
  GET_ACCOUNTS(Manifest.permission.GET_ACCOUNTS),

  ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
  ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
  ACCESS_BACKGROUND_LOCATION(Manifest.permission.ACCESS_BACKGROUND_LOCATION),

  READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE),
  CALL_PHONE(Manifest.permission.CALL_PHONE),
  READ_CALL_LOG(Manifest.permission.READ_CALL_LOG),
  WRITE_CALL_LOG(Manifest.permission.WRITE_CALL_LOG),
  ADD_VOICEMAIL(Manifest.permission.ADD_VOICEMAIL),
  USE_SIP(Manifest.permission.USE_SIP),
  READ_ASSISTANT_APP_SEARCH_DATA(Manifest.permission.READ_ASSISTANT_APP_SEARCH_DATA),
  READ_BASIC_PHONE_STATE(Manifest.permission.READ_BASIC_PHONE_STATE),
  READ_HOME_APP_SEARCH_DATA(Manifest.permission.READ_HOME_APP_SEARCH_DATA),

  BODY_SENSORS(Manifest.permission.BODY_SENSORS),
  BODY_SENSORS_BACKGROUND(Manifest.permission.BODY_SENSORS_BACKGROUND),

  SEND_SMS(Manifest.permission.SEND_SMS),
  RECEIVE_SMS(Manifest.permission.RECEIVE_SMS),
  READ_SMS(Manifest.permission.READ_SMS),
  RECEIVE_WAP_PUSH(Manifest.permission.RECEIVE_WAP_PUSH),
  RECEIVE_MMS(Manifest.permission.RECEIVE_MMS),
  DELIVER_COMPANION_MESSAGES(Manifest.permission.DELIVER_COMPANION_MESSAGES),
  MANAGE_WIFI_INTERFACES(Manifest.permission.MANAGE_WIFI_INTERFACES),
  MANAGE_WIFI_NETWORK_SELECTION(Manifest.permission.MANAGE_WIFI_NETWORK_SELECTION),
  NEARBY_WIFI_DEVICES(Manifest.permission.NEARBY_WIFI_DEVICES),
  OVERRIDE_WIFI_CONFIG(Manifest.permission.OVERRIDE_WIFI_CONFIG),

  READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
  READ_MEDIA_AUDIO(Manifest.permission.READ_MEDIA_AUDIO),
  READ_MEDIA_IMAGES(Manifest.permission.READ_MEDIA_IMAGES),
  READ_MEDIA_VIDEO(Manifest.permission.READ_MEDIA_VIDEO),
  READ_NEARBY_STREAMING_POLICY(Manifest.permission.READ_NEARBY_STREAMING_POLICY),
  REQUEST_COMPANION_PROFILE_APP_STREAMING(Manifest.permission.REQUEST_COMPANION_PROFILE_APP_STREAMING),
  REQUEST_COMPANION_PROFILE_AUTOMOTIVE_PROJECTION(Manifest.permission.REQUEST_COMPANION_PROFILE_AUTOMOTIVE_PROJECTION),
  REQUEST_COMPANION_PROFILE_COMPUTER(Manifest.permission.REQUEST_COMPANION_PROFILE_COMPUTER),
  REQUEST_COMPANION_SELF_MANAGED(Manifest.permission.REQUEST_COMPANION_SELF_MANAGED),
  WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),

  SYSTEM_ALERT_WINDOW(Manifest.permission.SYSTEM_ALERT_WINDOW),
  POST_NOTIFICATIONS(Manifest.permission.POST_NOTIFICATIONS),
  START_VIEW_APP_FEATURES(Manifest.permission.START_VIEW_APP_FEATURES),
  SUBSCRIBE_TO_KEYGUARD_LOCKED_STATE(Manifest.permission.SUBSCRIBE_TO_KEYGUARD_LOCKED_STATE),
  USE_EXACT_ALARM(Manifest.permission.USE_EXACT_ALARM),

  /** @deprecated */
  @Suppress("DEPRECATION")
  @Deprecated("Manifest.permission.PROCESS_OUTGOING_CALLS is deprecated.")
  PROCESS_OUTGOING_CALLS(Manifest.permission.PROCESS_OUTGOING_CALLS);

  companion object {
    @JvmStatic fun parse(raw: String): Permission =
      values().singleOrNull { it.value == raw } ?: UNKNOWN
  }
}
