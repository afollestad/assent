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
import com.afollestad.assentsample.utils.allowCurrentPermission
import com.afollestad.assentsample.utils.assertViewWithTextIsVisible
import com.afollestad.assentsample.utils.denyCurrentPermission
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters.JVM
import org.junit.runners.MethodSorters.NAME_ASCENDING

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
@FixMethodOrder(NAME_ASCENDING)
class AssentTest {

  @Rule
  @JvmField
  var activityRule = ActivityTestRule(MainActivity::class.java)

  private lateinit var device: UiDevice

  @Before fun setup() {
    device = UiDevice.getInstance(getInstrumentation())
  }

  @Test fun a_test_grant_none() {
    onView(withText(R.string.none_granted))
        .check(matches(isDisplayed()))
    onView(withId(R.id.requestPermissionButton))
        .check(matches(isDisplayed()))
        .perform(click())

    device.assertViewWithTextIsVisible(TEXT_ALLOW, activityRule.activity)
    device.denyCurrentPermission()
    device.denyCurrentPermission()

    onView(withText(R.string.none_granted))
        .check(matches(isDisplayed()))
  }

  @Test fun b_test_grant_some() {
    onView(withText(R.string.none_granted))
        .check(matches(isDisplayed()))
    onView(withId(R.id.requestPermissionButton))
        .check(matches(isDisplayed()))
        .perform(click())

    device.assertViewWithTextIsVisible(TEXT_ALLOW, activityRule.activity)
    device.allowCurrentPermission()
    device.denyCurrentPermission()

    onView(withText(R.string.some_granted))
        .check(matches(isDisplayed()))
  }

  @Test fun c_test_grant_all() {
    onView(withText(R.string.none_granted))
        .check(matches(isDisplayed()))
    onView(withId(R.id.requestPermissionButton))
        .check(matches(isDisplayed()))
        .perform(click())

    device.assertViewWithTextIsVisible(TEXT_ALLOW, activityRule.activity)
    device.allowCurrentPermission()

    onView(withText(R.string.all_granted))
        .check(matches(isDisplayed()))
  }
}