# Assent

Assent is designed to make Android's runtime permissions easier and take less code in your app to use.

<img src="https://raw.githubusercontent.com/afollestad/assent/master/showcase2.png" width="750" />

[![Android CI](https://github.com/afollestad/assent/workflows/Android%20CI/badge.svg)](https://github.com/afollestad/assent/actions?query=workflow%3A%22Android+CI%22)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f1a2334c4c0349699760391bb71f763e)](https://www.codacy.com/app/drummeraidan_50/assent?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=afollestad/assent&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

## Table of Contents

1. [Gradle Dependency](#gradle-dependency)
2. [The Basics](#the-basics)
3. [Using Results](#using-results)
4. [Permanently Denied](#permanently-denied)
5. [Request Debouncing](#request-debouncing)
6. [Rationales](#rationales)
7. [Coroutines](#coroutines)

---

## Core

[ ![Core](https://img.shields.io/maven-central/v/com.afollestad.assent/core?style=flat&label=Core) ](https://repo1.maven.org/maven2/com/afollestad/assent/core)

Add this to your module's `build.gradle` file:

```gradle
dependencies {
  
  implementation 'com.afollestad.assent:core:3.0.0-RC4'
}
```

---

### The Basics

Runtime permissions on Android are completely reliant on the UI the user is in. Permission requests 
go in and out of Activities and Fragments. This library provides its functionality as Kotlin 
extensions to Fragment Activities (e.g. `AppCompatActivity`) and AndroidX Fragments.

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

### Using Results

`AssentResult` is provided in request callbacks. It has a few useful fields and methods:

```kotlin
val result: AssentResult = // ...

val permissions: List<Permission> = result.permissions
val grantResults: List<GrantResult> = result.grantResults

// Takes a single permission and returns if this result contains it in its set
val containsPermission: Boolean = result.containsPermission(WRITE_EXTERNAL_STORAGE)

// You can pass multiple permissions as varargs
val permissionGranted: Boolean = result.isAllGranted(WRITE_EXTERNAL_STORAGE)

// You can pass multiple permissions as varargs
val permissionDenied: Boolean = result.isAllDenied(WRITE_EXTERNAL_STORAGE)

// Returns GRANTED, DENIED, or PERMANENTLY_DENIED
val writeStorageGrantResult: GrantResult = result[WRITE_EXTERNAL_STORAGE]

val granted: Set<Permission> = result.granted()

val denied: Set<Permission> = result.denied()

val permanentlyDenied: Set<Permission> = result.permanentlyDenied()
```

---

### Permanently Denied

Assent detects when the user of your app has permanently denied a permission. Once a permission
is permanently denied, the Android system will no longer show the permission dialog for that
permission. At this point, the only way to get them to grant the permission is to explain why you
_really_ need the permission and then launch system app details page for your app.

```kotlin
val result: AssentResult = // ...

if (result[WRITE_EXTERNAL_STORAGE] == PERMANENTLY_DENIED) {
  // NOTE: You should show a dialog of some sort before doing this!
  showSystemAppDetailsPage()
}
```

---

### Request Debouncing

If you were to do this...

```kotlin
askForPermissions(WRITE_EXTERNAL_STORAGE) { _ -> }

askForPermissions(WRITE_EXTERNAL_STORAGE) { _ -> }
```

...the permission would only be requested once, and both callbacks would be called at the same time.

If you were to do this...

```kotlin
askForPermissions(WRITE_EXTERNAL_STORAGE) { _ -> }

askForPermissions(CALL_PHONE) { _ -> }
```

...Assent would wait until the first permission request is done before executing the second request.

---

## Rationales

[ ![Rationales](https://img.shields.io/maven-central/v/com.afollestad.assent/rationales?style=flat&label=Rationales) ](https://repo1.maven.org/maven2/com/afollestad/assent/rationales)

Add this to your module's `build.gradle` file:

```gradle
dependencies {

  implementation 'com.afollestad.assent:rationales:3.0.0-RC4'
}
```

---

Google recommends showing rationales for permissions when it may not be obvious to the user why 
you need them. 

Assent supports extensible rationale handlers, it comes with two out-of-the-box: 
* `SnackBarRationaleHandler`
* `AlertDialogRationaleHandler`

```kotlin
// Could also use createDialogRationale(...) here, 
// or provide your own implementation of RationaleHandler. 
val rationaleHandler = createSnackBarRationale(rootView) {
  onPermission(READ_CONTACTS, "Test rationale #1, please accept!")
  onPermission(WRITE_EXTERNAL_STORAGE, "Test rationale #1, please accept!")
  onPermission(READ_SMS, "Test rationale #3, please accept!")
}

askForPermissions(
    READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_SMS,
    rationaleHandler = rationaleHandler
) { result ->
  // Use result
}
```

---

## Coroutines

[ ![Coroutines](https://img.shields.io/maven-central/v/com.afollestad.assent/coroutines?style=flat&label=Coroutines) ](https://repo1.maven.org/maven2/com/afollestad/assent/coroutines)

Add this to your module's `build.gradle` file:

```gradle
dependencies {

  implementation 'com.afollestad.assent:coroutines:3.0.0-RC4'
}
```

---

Kotlin coroutines enable Assent to work without callbacks. If you do not know the basics of
coroutines, you should research them first.

First, `awaitPermissionsResult(...)` is the coroutines equivalent to `askForPermissions(...)`:

```kotlin
// Launch a coroutine in some scope...
launch {
   val result: AssentResult = awaitPermissionsResult(
       READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_SMS,
       rationaleHandler = rationaleHandler
   )
   // Use the result...
}
```

And second, `awaitPermissionsGranted(...)` is the coroutines equivalent to `runWithPermissions(...)`:

```kotlin
// Launch a coroutine in some scope...
launch {
   awaitPermissionsGranted(
       READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_SMS,
       rationaleHandler = rationaleHandler
   )
   // All three permissions were granted...
}
```
