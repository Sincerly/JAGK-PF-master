package com.ysxsoft.gkpf.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ysxsoft.gkpf.YsxApplication;

public class ShareUtils {

    private static String HOST_KEY = "HOST_KEY";
    private static String PORT_KEY = "PORT_KEY";
    private static String GROUP_KEY = "GROUP_KEY";
    private static String USER_TYPE_KEY = "USER_TYPE_KEY";
    private static String USER_NAME_KEY = "USER_NAME_KEY";
    private static String USER_PWD_KEY= "USER_PWD_KEY";
    private static String INSTRUCTION_KEY= "INSTRUCTION_KEY";

    public static void setHost(String host) {
        setValue(HOST_KEY, host);
    }

    public static String getHost() {
        return getValue(HOST_KEY);
    }

    public static void setPort(String port) {
        setValue(PORT_KEY, port);
    }

    public static String getPort() {
        return getValue(PORT_KEY);
    }

    public static void setGroup(String group) {
        setValue(GROUP_KEY, group);
    }

    public static String getGroup() {
        return getValue(GROUP_KEY);
    }

    public static void setUserType(String userType) {
        //1(裁判端)、2(助演)、3(技术员)
        setValue(USER_TYPE_KEY, userType);
    }

    public static String getUserType() {
        return getValue(USER_TYPE_KEY);
    }

    public static void setUserName(String userName) {
        setValue(USER_NAME_KEY, userName);
    }

    public static String getUserName() {
        return getValue(USER_NAME_KEY);
    }

    public static void setUserPwd(String userName) {
        setValue(USER_PWD_KEY, userName);
    }

    public static String getUserPwd() {
        return getValue(USER_PWD_KEY);
    }

    public static void setInstruction(String instruction) {
        setValue(INSTRUCTION_KEY, instruction);
    }

    public static String getInstruction() {
        return getValue(INSTRUCTION_KEY);
    }


    private static String getValue(String key) {
        SharedPreferences sp = YsxApplication.getInstance().getSharedPreferences("USER", Context.MODE_PRIVATE);
        String value = sp.getString(key, "");
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }

    private static void setValue(String key, String value) {
        SharedPreferences sp = YsxApplication.getInstance().getSharedPreferences("USER", Context.MODE_PRIVATE);
        //获取到edit对象
        SharedPreferences.Editor edit = sp.edit();
        //通过editor对象写入数据
        edit.putString(key, value);
        //提交数据存入到xml文件中
        edit.apply();
    }

}
