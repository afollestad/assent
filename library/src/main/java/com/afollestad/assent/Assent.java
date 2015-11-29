package com.afollestad.assent;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Assent extends AssentBase {

    private static Assent mAssent;
    private Activity mContext;
    private final Map<String, ArrayList<AssentCallback>> mRequestQueue;

    private Assent() {
        mRequestQueue = new HashMap<>();
    }

    private static Assent instance() {
        // Singleton reduces static variables and prevents pre-mature garbage collection
        if (mAssent == null)
            mAssent = new Assent();
        return mAssent;
    }

    public static void setActivity(Activity context) {
        instance().mContext = context;
        if (context == null)
            LOG("Activity set to (null)");
        else
            LOG("Activity set to %s", context.getClass().getSimpleName());
    }

    private static Activity invalidateActivity() {
        final Activity context = instance().mContext;
        if (context == null) throw new IllegalStateException("Must set an Activity to Assent.");
        return context;
    }

    private static Map<String, ArrayList<AssentCallback>> requestQueue() {
        return instance().mRequestQueue;
    }

    public static void handleResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
        synchronized (requestQueue()) {
            final String cacheKey = getCacheKey(permissions);
            final ArrayList<AssentCallback> callbacks = requestQueue().get(cacheKey);
            if (callbacks == null) return;
            final PermissionResultSet result = PermissionResultSet.create(permissions, grantResults);
            for (AssentCallback cb : callbacks)
                cb.onPermissionResult(result);
            requestQueue().remove(cacheKey);
            LOG("Result for %s handled to %d callbacks; new queue size: %d",
                    cacheKey, callbacks.size(), requestQueue().size());
        }
    }

    public static boolean isPermissionGranted(@NonNull String permission) {
        return ContextCompat.checkSelfPermission(invalidateActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(@NonNull AssentCallback callback,
                                          @IntRange(from = 1, to = Integer.MAX_VALUE) int requestCode,
                                          @NonNull String... permissions) {
        synchronized (requestQueue()) {
            final String cacheKey = getCacheKey(permissions);
            ArrayList<AssentCallback> stack = requestQueue().get(cacheKey);
            if (stack != null) {
                stack.add(callback);
                LOG("Added callback to stack for %s; new stack size: %d", cacheKey, stack.size());
            } else {
                stack = new ArrayList<>(2);
                stack.add(callback);
                requestQueue().put(cacheKey, stack);
                LOG("Added NEW callback stack for %s", cacheKey);
                ActivityCompat.requestPermissions(invalidateActivity(), permissions, requestCode);
            }
        }
    }
}
