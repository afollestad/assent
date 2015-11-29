package com.afollestad.assent;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * @author Aidan Follestad (afollestad)
 */
class AssentBase {

    protected static String getCacheKey(@NonNull String... permissions) {
        StringBuilder result = new StringBuilder();
        for (String perm : permissions) {
            result.append(perm);
            result.append("\0");
        }
        return result.toString();
    }

    protected static void LOG(String message, Object... args) {
        if (args != null)
            Log.d("Assent", String.format(message, args));
        else
            Log.d("Assent", message);
    }
}
