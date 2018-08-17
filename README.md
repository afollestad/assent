# Assent

Assent is designed to make Android's runtime permissions easier and take less code in your app to use.

[ ![jCenter](https://api.bintray.com/packages/drummer-aidan/maven/assent/images/download.svg) ](https://bintray.com/drummer-aidan/maven/assent/_latestVersion)
[![Build Status](https://travis-ci.org/afollestad/assent.svg)](https://travis-ci.org/afollestad/assent)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f1a2334c4c0349699760391bb71f763e)](https://www.codacy.com/app/drummeraidan_50/assent?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=afollestad/assent&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

# Table of Contents

1. [Gradle Dependency](#gradle-dependency)
2. [Using from Activities](#using-from-activities)
    1. [With AssentActivity](#with-assentactivity)
    2. [Without AssentActivity](#without-assentactivity)
3. [Using from Fragments](#using-from-fragments)
    1. [With AssentFragment](#with-assentfragment)
    2. [Without AssentFragment](#without-assentfragment)
5. [Using Results](#using-results)
6. [Duplicate and Simultaneous Requests](#duplicate-and-simultaneous-requests)
    1. [Duplicate Request Handling](#duplicate-request-handling)
    2. [Simultaneous Request Handling](#simultaneous-request-handling)

---

# Gradle Dependency

Add this to your module's `build.gradle` file:

```gradle
dependencies {
    
  implementation 'com.afollestad:assent:1.0.0'
}
```

--

# Using from Activities

**Note**: *you need to have needed permissions in your AndroidManifest.xml too, otherwise Android will 
 always deny them, even on Marshmallow.*

### With AssentActivity

The first way to use this library is to have Activities which request permissions extend `AssentActivity`.
This handle dirty work internally, so all that you have to do is use the `request` method:

```kotlin
class MainActivity : AssentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (!Assent.isAllGranted(WRITE_EXTERNAL_STORAGE)) {
      // The if statement checks if the permission has already been granted before
      
      Assent.request(WRITE_EXTERNAL_STORAGE) { result ->
        // Permission granted or denied 
      }
    }
  }
}
```

`request` has three parameters: a permission, an optional request code, and a callback. If you want 
to request multiple permissions, you can pass an array in the first parameter: 

```kotlin
Assent.request(arrayOf(WRITE_EXTERNAL_STORAGE, CALL_PHONE)) { result ->
  // Permission granted or denied
}
```

Note that `isAllGranted` can also accept multiple values as well:

```kotlin
Assent.isAllGranted(WRITE_EXTERNAL_STORAGE, CALL_PHONE)
```

### Without AssentActivity

If you don't want to extend `AssentActivity`, you can use some of Assent's methods in order to
match the behavior:

```kotlin
class MyActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Updates Assent's context when the Activity is first created,
    // that way you can request permissions from within onCreate().
    // The first parameter is the caller (always this), second is the new context, also this here.
    Assent.setActivity(this, this)

    if (!Assent.isAllGranted(WRITE_EXTERNAL_STORAGE)) {
      // The if statement checks if the permission has already been granted before

      Assent.request(WRITE_EXTERNAL_STORAGE) { result ->
        // Permission granted or denied 
      }
    }
  }

  override fun onResume() {
    super.onResume()
    // Updates Assent's context every time the Activity becomes visible again.
    // The first parameter is the caller (always this), second is the new context, also this here.
    Assent.setActivity(this, this)
  }

  override fun onPause() {
    super.onPause()
    // Cleans up references of the Activity to avoid memory leaks.
    // The first parameter is the caller (always this), second is the new context, null here.
    if (isFinishing) {
      Assent.setActivity(this, null)
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    // Lets Assent take over and notify respective callbacks
    Assent.response(
        permissions = permissions,
        grantResults = grantResults
    )
  }
}
```

---

# Using from Fragments

### With AssentFragment

If you use `Fragment`'s in your app, it's recommended that they extend `AssentFragment`. They will update
Context references in Assent, and handle Fragment permission results for you. Relying on the Fragment's
Activity can lead to occasional problems.

```kotlin
class MainFragment : AssentFragment() {

  // Use Assent the same way you would in an Activity
}
```

### Without AssentFragment

If you don't want to extend `AssentFragment`, you can use some of Assent's methods to match the 
behavior:

```kotlin
class MyFragment : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Assent.setFragment(this, this)

    if (!Assent.isAllGranted(WRITE_EXTERNAL_STORAGE)) {
      // The if statement checks if the permission has already been granted before
      Assent.request(WRITE_EXTERNAL_STORAGE) { result ->
        // Permission granted or denied 
      }
    }
  }

  override fun onResume() {
    super.onResume()
    Assent.setFragment(this, this)
  }

  override fun onPause() {
    super.onPause()
    Assent.setFragment(this, null)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    Assent.response(
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

# Duplicate and Simultaneous Requests

### Duplicate Request Handling

If you were to do this...

```kotlin
Assent.request(WRITE_EXTERNAL_STORAGE) { result ->
  // Permission granted or denied
}

Assent.request(WRITE_EXTERNAL_STORAGE) { result ->
  // Permission granted or denied
}
```

...the permission would only be requested once, and both callbacks would be called at the same time.

An example situation where this would be useful: if you use tabs in your app, and multiple Fragments
which are created at the same request the same permission, the permission dialog would only be shown once
and both Fragments would be updated with the result.

### Simultaneous Request Handling

If you were to do this...

```kotlin
Assent.request(WRITE_EXTERNAL_STORAGE) { result ->
  // Permission granted or denied
}

Assent.request(CALL_PHONE) { result ->
  // Permission granted or denied
}
```

...Assent would wait until the first permission request is done before executing the second request.

This is important, because if you were you request different permissions at the same time without Assent,
the first permission request would be cancelled and denied and the second one would be shown immediately.

---

---

# [LICENSE](/LICENSE.md)

### Copyright 2018 Aidan Follestad

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
