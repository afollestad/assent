# Assent

Assent is designed to make Android's runtime permissions easier and take less code in your app to use.

[ ![jCenter](https://api.bintray.com/packages/drummer-aidan/maven/assent/images/download.svg) ](https://bintray.com/drummer-aidan/maven/assent/_latestVersion)
[![Build Status](https://travis-ci.org/afollestad/assent.svg)](https://travis-ci.org/afollestad/assent)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f1a2334c4c0349699760391bb71f763e)](https://www.codacy.com/app/drummeraidan_50/assent?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=afollestad/assent&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

# Table of Contents

1. [Gradle Dependency](#gradle-dependency)
2. [The Basics](#the-basics)
    1. [With AssentActivity](#with-assentactivity)
    2. [Without AssentActivity](#without-assentactivity)
3. [From Fragments](#from-fragments)
    1. [With AssentFragment](#with-assentfragment)
    2. [Without AssentFragment](#without-assentfragment)
4. [Using Results](#using-results)
5. [Duplicate and Parallel Requests](#duplicate-and-parallel-requests)
    1. [Duplicate Request Handling](#duplicate-request-handling)
    2. [Parallel Request Handling](#parallel-request-handling)

---

# Gradle Dependency

Add this to your module's `build.gradle` file:

```gradle
dependencies {
    
  implementation 'com.afollestad:assent:1.0.1'
}
```

---

# The Basics

Runtime permissions on Android are completely reliant on the UI the user is in. Permission requests 
go in and out of Activities and Fragments. 

**Note**: *you need to have permissions declared in your AndroidManifest.xml too, otherwise Android 
 will always deny them.*

## With AssentActivity

The first way to use this library is to have Activities that request permissions extend 
`AssentActivity`. This handles the in-and-out dirty work internally.

```kotlin
import com.afollestad.assent.Assent.Companion.isAllGranted
import com.afollestad.assent.Assent.Companion.askForPermission
import com.afollestad.assent.Assent.Companion.askForPermissions
import com.afollestad.assent.Assent.Companion.runWithPermission
import com.afollestad.assent.Assent.Companion.runWithPermissions

class MainActivity : AssentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
 
    // Checks if one or more permissions are granted already, returns immediately
    val allGranted: Boolean = isAllGranted(WRITE_EXTERNAL_STORAGE, CAMERA)
 
    // Requests a single permission, sending a result to a callback
    askForPermission(WRITE_EXTERNAL_STORAGE) { result ->
      // Check the result, see the Using Results section
    }
    
    // Requests multiple permission, sending a result to a callback
    askForPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA)) { result ->
      // Check the result, see the Using Results section
    }
    
    // Requests a single permission and performs an action if it is granted
    runWithPermission(CAMERA) { 
      // Do something
    }
    
    // Requests multiple permissions and performs an action if all are granted
    runWithPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA)) { 
      // Do something
    }
  }
}
```  

All of the request methods above have an optional `requestCode` named parameter which you can use 
to customize the request code used when dispatching the permission request.

## Without AssentActivity

If you don't want to extend `AssentActivity`, you can use some of Assent's methods in order to
match the behavior:

```kotlin
import com.afollestad.assent.Assent.Companion.onPermissionsResponse
import com.afollestad.assent.Assent.Companion.setAssentActivity

class MyActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Updates Assent's context when the Activity is first created,
    // that way you can request permissions from within onCreate().
    // The first parameter is the caller (always this), second is the new context, also this here.
    setAssentActivity(this, this)

    // Do permission checks / stuff
  }

  override fun onResume() {
    super.onResume()
    
    // Updates Assent's context every time the Activity becomes visible again.
    // The first parameter is the caller (always this), second is the new context, also this here.
    setAssentActivity(this, this)
  }

  override fun onPause() {
    super.onPause()
    
    // Cleans up references of the Activity to avoid memory leaks.
    // The first parameter is the caller (always this), second is the new context, null here.
    if (isFinishing) {
      setAssentActivity(this, null)
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    
    // Lets Assent take over and notify respective callbacks
    onPermissionsResponse(
        permissions = permissions,
        grantResults = grantResults
    )
  }
}
```

---

# From Fragments

## With AssentFragment

If you use `Fragment`'s in your app, it's recommended that they extend `AssentFragment`. They will update
Context references in Assent, and handle Fragment permission results for you. Relying on the Fragment's
Activity can lead to occasional problems.

```kotlin
class MainFragment : AssentFragment() {

  // Use Assent the same way you would from an Activity like above
}
```

## Without AssentFragment

If you don't want to extend `AssentFragment`, you can use some of Assent's methods to match the 
behavior:

```kotlin
import com.afollestad.assent.Assent.Companion.onPermissionsResponse
import com.afollestad.assent.Assent.Companion.setAssentFragment

class MyFragment : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    setAssentFragment(this, this)

    // Do permission checks / stuff
  }

  override fun onResume() {
    super.onResume()
    
    setAssentFragment(this, this)
  }

  override fun onPause() {
    super.onPause()
    
    setAssentFragment(this, null)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    
    onPermissionsResponse(
        permissions = permissions,
        grantResults = grantResults
    )
  }
}
```

---

# Using Results

`AssentResult` is provided in callbacks. It has a few useful fields and methods:

```kotlin
val result: AssentResult = // ...

val permissions: Array<Permission> = result.permissions
val grantResults: IntArray = result.grantResults

// Takes a single permission and returns if this result contains it in its set
val containsPermission: Boolean = result.containsPermission(WRITE_EXTERNAL_STORAGE)

// You can pass multiple permissions as varargs
val permissionGranted: Boolean = result.isAllGranted(WRITE_EXTERNAL_STORAGE)

// You can pass multiple permissions as varargs
val permissionDenied: Boolean = result.isAllDenied(WRITE_EXTERNAL_STORAGE)
```

---

# Duplicate and Parallel Requests

## Duplicate Request Handling

If you were to do this...

```kotlin
askForPermission(WRITE_EXTERNAL_STORAGE) { _ -> }

askForPermission(WRITE_EXTERNAL_STORAGE) { _ -> }
```

...the permission would only be requested once, and both callbacks would be called at the same time.

An example situation where this would be useful: if you use tabs in your app, and multiple Fragments
which are created at the same request the same permission, the permission dialog would only be shown once
and both Fragments would be updated with the result.

## Parallel Request Handling

If you were to do this...

```kotlin
askForPermission(WRITE_EXTERNAL_STORAGE) { _ -> }

askForPermission(CALL_PHONE) { _ -> }
```

...Assent would wait until the first permission request is done before executing the second request.

This is important, because if you were you request different permissions at the same time without Assent,
the first permission request would be cancelled and denied and the second one would be shown immediately.