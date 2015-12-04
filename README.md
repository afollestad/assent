# Assent

Assent is designed to make Marshmallow's runtime permissions easier to use. Have the flexibility of
 request permissions and receiving results through callback interfaces.

# Gradle Dependency

[![Release](https://img.shields.io/github/release/afollestad/assent.svg?label=jitpack)](https://jitpack.io/#afollestad/assent)

### Repository

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

### Dependency

Add this to your module's `build.gradle` file:

```gradle
dependencies {
    ...
    compile('com.github.afollestad:assent:0.1.1') {
        transitive = true
    }
}
```

--

# Basics

The first way to use this library is to have Activities which request permissions extend `AssentActivity`.
AssentActivity will handle some dirty work internally, so all that you have to do is use the `requestPermissions` method:

```java
public class MainActivity extends AssentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Assent.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Assent.requestPermissions(new AssentCallback() {
                @Override
                public void onPermissionResult(PermissionResultSet result) {
                    // Permission granted or denied
                }
            }, 69, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
}
```

`requestPermissions` has 3 parameters: a callback, a request code, and a list of permissions. You
can pass multiple permissions in your request like this:

```java
Assent.requestPermissions(callback, 
    requestCode,
    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION);
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
        
        if (!Assent.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        	Assent.requestPermissions(new AssentCallback() {
	            @Override
            	public void onPermissionResult(PermissionResultSet result) {
	                // Permission granted or denied
            	}
        	}, 69, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    	}
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Updates the activity every time the Activity becomes visible again
        Assent.setActivity(this, this);
    }

    @Override
    protected void onStop() {
        // Cleans up references of the Activity to avoid memory leaks
        Assent.setActivity(this, null);
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Lets Assent handle permission results and contact your callbacks
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
        
        if (!Assent.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        	Assent.requestPermissions(new AssentCallback() {
            	@Override
            	public void onPermissionResult(PermissionResultSet result) {
	                // Permission granted or denied
            	}
        	}, 69, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    	}
    }
}
```

---

# Duplicate Request Handling

If you were to do this:

```java
Assent.requestPermissions(new AssentCallback() {
    @Override
    public void onPermissionResult(PermissionResultSet result) {
        // Permission granted or denied
    }
}, 69, Manifest.permission.WRITE_EXTERNAL_STORAGE);

Assent.requestPermissions(new AssentCallback() {
    @Override
    public void onPermissionResult(PermissionResultSet result) {
        // Permission granted or denied
    }
}, 69, Manifest.permission.WRITE_EXTERNAL_STORAGE);
```

The permission would only be requested once, and both callbacks would be called at the same time.

An example situation where this would be useful: if you use tabs in your app, and multiple Fragments
which are created at the same request the same permission, the permission dialog would only be shown once
and both Fragments would be updated with the result.

---

# [LICENSE](/LICENSE.md)

###### Copyright 2016 Aidan Follestad

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
