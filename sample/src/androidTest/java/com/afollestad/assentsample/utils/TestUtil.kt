package com.afollestad.assentsample.utils

import android.app.Activity
import android.os.Handler
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector

internal const val TEXT_ALLOW = "ALLOW"
internal const val TEXT_DENY = "DENY"
internal const val TEXT_NEVER_ASK_AGAIN = "Never ask again"

internal fun UiDevice.assertViewWithTextIsVisible(
  text: String,
  context: Activity,
  timeout: Long = 4000,
  elapsed: Long = 0
) {
  val allowButton = findObject(UiSelector().text(text))
  if (!allowButton.exists()) {
    if (elapsed >= timeout) {
      throw AssertionError("View with text <$text> not found after ${elapsed}ms")
    }
    val handler = Handler(context.mainLooper)
    handler.postDelayed({
      assertViewWithTextIsVisible(text, context, timeout, elapsed + 500)
    }, 500)
  }
}

fun UiDevice.allowCurrentPermission() {
  val allowButton = findObject(UiSelector().text(TEXT_ALLOW))
  allowButton.click()
}

fun UiDevice.denyCurrentPermission() {
  val denyButton = findObject(UiSelector().text(TEXT_DENY))
  denyButton.click()
}

fun UiDevice.denyCurrentPermissionPermanently() {
  val neverAskAgainCheckbox = findObject(UiSelector().text(TEXT_NEVER_ASK_AGAIN))
  neverAskAgainCheckbox.click()
  denyCurrentPermission()
}

fun UiDevice.grantPermission(permissionTitle: String) {
  val permissionEntry = findObject(UiSelector().text(permissionTitle))
  permissionEntry.click()
}