package com.ysxsoft.gkpf;

import android.support.multidex.MultiDexApplication;

/**
 * git配置测试
 */
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
