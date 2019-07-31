package com.ysxsoft.gkpf.utils;

import android.util.Log;

public class Logutils {

    private final static String TAG = "TAG";
    private final static boolean isDebug = true;

    public static void e(String msg) {
        if (isDebug) {
            try {
                Log.e(TAG, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
