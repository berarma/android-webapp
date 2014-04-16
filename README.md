# Android-WebApp

This is a library project to help build better webapp clients for Android. The
library handles the generic details so that it's possible to create a webapp
client with minimal effort.

# Installation

Just clone this repository to any location accessible from your Android build environment.

# Usage

Add the library to your Android project. Once done, your Activities can extend
the `com.bernatarlandis.android.webapp.WebAppActivity` class. You only need to
define the `getHomeUrl` method but any method can be overriden.

This is an example Activity:

```Java
package com.mycompany.mywebapp;

import com.bernatarlandis.android.webapp.WebAppActivity;

public class MainActivity extends WebAppActivity {

    @Override
    protected String getHomeUrl() {
        return "http://example.com";
    }
}
```

This is an example AndroidManifest.xml:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.mycomppany.mywebapp"
    android:versionCode="1"
    android:versionName="1.0">
    <uses-permission android:name="android.permission.INTERNET" />
    <application android:label="@string/app_name" android:icon="@drawable/icon">
        <activity android:name="MainActivity"
            android:label="@string/app_name"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:theme="@android:style/Theme.NoTitleBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
    <uses-sdk
        android:minSdkVersion="8"
        android:targetSdkVersion="19" />
</manifest>
```

You can override the library resource strings in your project as needed, just
declare them again with the same name.

The webapp will behave mostly like a browser but without the interface. Check
the source code of WebAppActivity to see other methods you can override to
change the default behavior.

A few examples:

* Override the `getUserAgentString` method if you want to change the default.
* Override the `isInternalUrl` to redefine what's an internal URL. Links to
  external URLs open in a browser window.
* Override the `saveUrl/restoreUrl` methods in case you don't want to start the
  webapp in the last visited page.

# TODO

* Add permanent caching of JS/CSS resources.

# License

This library is released under the GPLv2 license. Most common uses of this
library won't constitute a derived work, thus, you may use it and include it in
your app independently of the license you've choosen for your code as long as
the library itself is still distributed with its original license. But using
this library from another library that provides modified or improved
functionality may constitute a derived work and thus require you to use the
same license for your library.

