# Assent

Assent is designed to make Marshmallow's runtime permissions easier to use. Have the flexibility of
 request permissions and receiving results through callback interfaces.

# Table of Contents

1. [Gradle Dependency](https://github.com/afollestad/assent#gradle-dependency)
    1. [Repository](https://github.com/afollestad/assent#repository)
    2. [Dependency](https://github.com/afollestad/assent#dependency)
2. [Basics](https://github.com/afollestad/assent#basics)
3. [Without AssentActivity](https://github.com/afollestad/assent#without-assentactivity)
4. [Using PermissionResultSet](https://github.com/afollestad/assent#using-permissionresultset)
5. [Fragments](https://github.com/afollestad/assent#fragments)
6. [Duplicate and Simultaneous Requests](https://github.com/afollestad/assent#duplicate-and-simultaneous-requests)
    1. [Duplicate Request Handling](https://github.com/afollestad/assent#duplicate-request-handling)
    2. [Simultaneous Request Handling](https://github.com/afollestad/assent#simultaneous-request-handling)
7. [AfterPermissionResult Annotation](https://github.com/afollestad/assent#afterpermissionresult-annotation)

---

# Gradle Dependency

[![Release](https://jitpack.io/v/afollestad/assent.svg)](https://jitpack.io/#afollestad/assent)
[![Build Status](https://travis-ci.org/afollestad/assent.svg)](https://travis-ci.org/afollestad/assent)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

#### Repository

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

#### Dependency

Add this to your module's `build.gradle` file:

```gradle
dependencies {
    ...
    compile('com.github.afollestad:assent:0.2.0') {
        transitive = true
    }
}
```

--

# Basics

**Note**: *you need to have needed permissions in your AndroidManifest.xml too, otherwise Android will 
 always deny them, even on Marshmallow.*

The first way to use this library is to have Activities which request permissions extend `AssentActivity`.
AssentActivity will handle some dirty work internally, so all that you have to do is use the `requestPermissions` method:

```java
public class MainActivity extends AssentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            // The if statement checks if the permission has already been granted before
            Assent.requestPermissions(new AssentCallback() {
                @Override
                public void onPermissionResult(PermissionResultSet result) {
                    // Permission granted or denied
                }
            }, 69, Assent.WRITE_EXTERNAL_STORAGE);
        }
    }
}
```

`requestPermissions` has 3 parameters: a callback, a request code, and a list of permissions. You
can pass multiple permissions in your request like this:

```java
Assent.requestPermissions(callback, 
    requestCode,
    Assent.WRITE_EXTERNAL_STORAGE, Assent.ACCESS_FINE_LOCATION);
```

---

# Without AssentActivity

If you don't want to extend `AssentActivity`, you can use some of Assent's other methods in order to
mimic the behavior:

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Updates the activity when the Activity is first created
        // That way you can request permissions from within onCreate()
        Assent.setActivity(this, this);
        
        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            // The if statement checks if the permission has already been granted before
        	Assent.requestPermissions(new AssentCallback() {
	            @Override
            	public void onPermissionResult(PermissionResultSet result) {
	                // Permission granted or denied
            	}
        	}, 69, Assent.WRITE_EXTERNAL_STORAGE);
    	}
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Updates the activity every time the Activity becomes visible again
        Assent.setActivity(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cleans up references of the Activity to avoid memory leaks
        if (isFinishing())
            Assent.setActivity(this, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Lets Assent handle permission results, and contact your callbacks
        Assent.handleResult(permissions, grantResults);
    }
}
```

---

# Using PermissionResultSet

`PermissionResultSet` is returned in callbacks. It has a few useful methods:

```java
PermissionResultSet result = // ...

String[] permissions = result.getPermissions();

boolean granted = result.isGranted(permissions[0]);

Map<String, Boolean> grantedMap = result.getGrantedMap();

boolean allGranted = result.allPermissionsGranted();
```

---

# Fragments

A huge plus to using callbacks rather than relying on `onRequestPermissionsResult` is that you
 can request permission from Fragments and receive the result right in the Fragment, as long as
 the Activity your Fragment is in handles results.
 
```java
public class MainFragment extends Fragment {

    // ... view creation logic and other stuff here
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
        	Assent.requestPermissions(new AssentCallback() {
            	@Override
            	public void onPermissionResult(PermissionResultSet result) {
	                // Permission granted or denied
            	}
        	}, 69, Assent.WRITE_EXTERNAL_STORAGE);
    	}
    }
}
```

---

# Duplicate and Simultaneous Requests

#### Duplicate Request Handling

If you were to do this...

```java
Assent.requestPermissions(new AssentCallback() {
    @Override
    public void onPermissionResult(PermissionResultSet result) {
        // Permission granted or denied
    }
}, 69, Assent.WRITE_EXTERNAL_STORAGE);

Assent.requestPermissions(new AssentCallback() {
    @Override
    public void onPermissionResult(PermissionResultSet result) {
        // Permission granted or denied
    }
}, 69, Assent.WRITE_EXTERNAL_STORAGE);
```

...the permission would only be requested once, and both callbacks would be called at the same time.

An example situation where this would be useful: if you use tabs in your app, and multiple Fragments
which are created at the same request the same permission, the permission dialog would only be shown once
and both Fragments would be updated with the result.

#### Simultaneous Request Handling

If you were to do this...

```java
Assent.requestPermissions(new AssentCallback() {
    @Override
    public void onPermissionResult(PermissionResultSet result) {
        // Permission granted or denied
    }
}, 34, Assent.WRITE_EXTERNAL_STORAGE);

Assent.requestPermissions(new AssentCallback() {
    @Override
    public void onPermissionResult(PermissionResultSet result) {
        // Permission granted or denied
    }
}, 69, Assent.ACCESS_FINE_LOCATION);
```

...Assent would wait until the first permission request is done before executing the second request.

This is important, because if you were you request different permissions at the same time without Assent,
the first permission request would be cancelled and denied and the second one would be shown immediately.

---

# AfterPermissionResult Annotation

As a convenience, you can use the `AfterPermissionResult` annotation to have Assent invoke a method in 
any class when a specific set of permissions is granted or denied.

```java
public class MainActivity extends AssentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Request WRITE_EXTERNAL_STORAGE and ACCESS_FINE_LOCATION permissions, with the current class as the target
        Assent.requestPermissions(MainActivity.this, 69, 
            Assent.WRITE_EXTERNAL_STORAGE, Assent.ACCESS_FINE_LOCATION);
    }
    
    @AfterPermissionResult(permissions = { Assent.WRITE_EXTERNAL_STORAGE, Assent.ACCESS_FINE_LOCATION })
    public void onPermissionResult(PermissionResultSet result) {
        // Use PermissionResultSet
    }
}
```

Behind the scenes, Assent is actually using a callback. When the callback is received, it finds the 
first `AfterPermissionResult` annotated method in the target class object (with a matching permission 
set) and invokes it.
 
The target class couldn't be any object. It even works like this:

```java
public class OtherClass {

    @AfterPermissionResult(permissions = {Assent.WRITE_EXTERNAL_STORAGE, Assent.ACCESS_FINE_LOCATION})
    public void onPermissionResult(PermissionResultSet result) {
        // Use permission result
    }
}

public class MainActivity extends AssentActivity {

    private OtherClass mOther;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mOther = new OtherClass();
        
        // Request WRITE_EXTERNAL_STORAGE and ACCESS_FINE_LOCATION permissions, with mOther as the target
        Assent.requestPermissions(mOther, 69, 
            Assent.WRITE_EXTERNAL_STORAGE, Assent.ACCESS_FINE_LOCATION);
    }
}
```

---

# [LICENSE](/LICENSE.md)

#### Copyright 2016 Aidan Follestad

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
