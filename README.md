# Assent

Assent is designed to make Android's runtime permissions easier and take less code in your app to use.

[ ![jCenter](https://api.bintray.com/packages/drummer-aidan/maven/assent/images/download.svg) ](https://bintray.com/drummer-aidan/maven/assent/_latestVersion)
[![Build Status](https://travis-ci.org/afollestad/assent.svg)](https://travis-ci.org/afollestad/assent)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f1a2334c4c0349699760391bb71f763e)](https://www.codacy.com/app/drummeraidan_50/assent?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=afollestad/assent&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

## Table of Contents

1. [Gradle Dependency](#gradle-dependency)
2. [The Basics](#the-basics)
3. [Using Results](#using-results)
4. [Duplicate Request Handling](#duplicate-request-handling)
5. [Parallel Request Handling](#parallel-request-handling)

---

## Gradle Dependency

Add this to your module's `build.gradle` file:

```gradle
dependencies {
  
  implementation 'com.afollestad:assent:2.0.0'
}
```

---

## The Basics

Runtime permissions on Android are completely reliant on the UI the user is in. Permission requests 
go in and out of Activities and Fragments. This library provides its functionality as Kotlin 
extensions to Fragment Activities (e.g. `FragmentActivity`) and Support Library Fragments.

**Note**: *you need to have permissions declared in your `AndroidManifest.xml` too, otherwise 
Android will always deny them.*

```kotlin
class YourActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Set to true if one or more permissions are all granted
    val permissionsGranted: Boolean = isAllGranted(WRITE_EXTERNAL_STORAGE, CAMERA)
    
    // Requests one or more permissions, sending the result to a callback
    askForPermissions(WRITE_EXTERNAL_STORAGE, CAMERA) { result ->
      // Check the result, see the Using Results section
    }
    
    // Requests one or multiple permissions and performs an action if all are granted
    runWithPermissions(WRITE_EXTERNAL_STORAGE, CAMERA) { 
      // Do something
    }
  }
}
```  

All of the request methods above have an optional `requestCode` named parameter which you can use 
to customize the request code used when dispatching the permission request.

**These methods can all be called from within an Activity or a Fragment. It works the same way in 
both.**

---

## Using Results

`AssentResult` is provided in request callbacks. It has a few useful fields and methods:

```kotlin
val result: AssentResult = // ...

val permissions: List<Permission> = result.permissions
val grantResults: IntArray = result.grantResults

// Takes a single permission and returns if this result contains it in its set
val containsPermission: Boolean = result.containsPermission(WRITE_EXTERNAL_STORAGE)

// You can pass multiple permissions as varargs
val permissionGranted: Boolean = result.isAllGranted(WRITE_EXTERNAL_STORAGE)

// You can pass multiple permissions as varargs
val permissionDenied: Boolean = result.isAllDenied(WRITE_EXTERNAL_STORAGE)
```

---

## Duplicate Request Handling

If you were to do this...

```kotlin
askForPermissions(WRITE_EXTERNAL_STORAGE) { _ -> }

askForPermissions(WRITE_EXTERNAL_STORAGE) { _ -> }
```

...the permission would only be requested once, and both callbacks would be called at the same time.

An example situation where this would be useful: if you use tabs in your app, and multiple Fragments
which are created at the same request the same permission, the permission dialog would only be shown 
once and both Fragments would be updated with the result.

---

## Parallel Request Handling

If you were to do this...

```kotlin
askForPermissions(WRITE_EXTERNAL_STORAGE) { _ -> }

askForPermissions(CALL_PHONE) { _ -> }
```

...Assent would wait until the first permission request is done before executing the second request.

This is important, because if you were you request different permissions at the same time without 
Assent, the first permission request would be cancelled and denied and the second one would be 
shown immediately.