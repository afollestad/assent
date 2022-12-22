3.0.1

It's been a while! Going right to 3.0.1:

* Updated dependencies.
* Add new SDK 33 permissions to Permission.kt (#25 from @stephanepechard).
* Update internal lifecycle utilities to use `DefaultLifecycleObserver` instead of deprecated reflection-based annotations.

---

3.0.0-RC4

* Fixed https://github.com/afollestad/assent/issues/16

---

3.0.0-RC3

* Quick fix-up around permanently denied detection! No longer reliant on rationale handler either.
* Added more methods to `AssentResult` to get granted, denied, and permanently denied permissions.
* More internal cleanup.

---

3.0.0-RC2

* Detect permanently denied permissions! Changes to `AssentResult`'s structure, it also allows you
to access `GrantResult`'s for specific permissions.
* Some internal cleanup and restructuring.

---

3.0.0-RC1

* The library is now split into 3 separate modules. Core, Rationales, and Coroutines.
* Coroutines support! No callbacks.
* Upgrade dependencies and project structure.
* Add `ACCESS_BACKGROUND_LOCATION` to the `Permission` enum.
* Got rid of Timber for logging (doesn't really make sense for a library). Log to the logcat directly.
