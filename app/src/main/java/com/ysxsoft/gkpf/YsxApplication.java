package com.ysxsoft.gkpf;

import android.support.multidex.MultiDexApplication;

public class YsxApplication extends MultiDexApplication {

    private static YsxApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
    }

    public static YsxApplication getInstance(){
        return application;
    }

}
