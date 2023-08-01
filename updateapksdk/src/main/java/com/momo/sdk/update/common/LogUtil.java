package com.momo.sdk.update.common;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.util.Log;

public class LogUtil {
    public static final String LogVersion = "UpdateSDK";
    public static final String myLog = "UpdateSDK";
    public static boolean isDebug = true;

    public LogUtil() {
    }

    public static void k(String log) {
        if (isDebug) {
            Log.e("UpdateSDK", log);
        }

    }

    public static void i(String log) {
        if (isDebug) {
            Log.i("UpdateSDK", log);
        }

    }

    public static void e(String log) {
        if (isDebug) {
            Log.e("UpdateSDK", log);
        }

    }

    public static void v(String log) {
        if (isDebug) {
            Log.v("UpdateSDK", log);
        }

    }

    public static void d(String log) {
        if (isDebug) {
            Log.d("UpdateSDK", log);
        }

    }

    public static void w(String log) {
        if (isDebug) {
            Log.w("UpdateSDK", log);
        }

    }
}
