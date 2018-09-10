package com.afollestad.assentsample

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.SdkSuppress
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import com.afollestad.assentsample.utils.TEXT_ALLOW
import com.afollestad.assentsample.utils.assertViewWithTextIsVisible
import com.afollestad.assentsample.utils.denyCurrentPermission
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class AssentTest {

  @Rule
  @JvmField
  var rule = ActivityTestRule(MainActivity::class.java)

  private lateinit var device: UiDevice

  @Before fun setup() {
    device = UiDevice.getInstance(getInstrumentation())
  }

  @Test fun test_basic() {
    onView(withText(R.string.none_granted))
        .check(matches(isDisplayed()))
    onView(withId(R.id.requestPermissionButton))
        .check(matches(isDisplayed()))
        .perform(click())

    device.assertViewWithTextIsVisible(TEXT_ALLOW, rule.activity)
    device.denyCurrentPermission()
    device.denyCurrentPermission()
  }
}