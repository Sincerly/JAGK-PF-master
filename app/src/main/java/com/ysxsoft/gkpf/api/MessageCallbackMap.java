package com.ysxsoft.gkpf.api;

import java.util.HashMap;

public class MessageCallbackMap {
    private static HashMap<String, IMessageCallback> pageHashMap;

    public static void reg(String clazzName, IMessageCallback target) {
        if (pageHashMap == null) {
            pageHashMap = new HashMap<>();
        }
        if (pageHashMap.containsValue(target)) {
            pageHashMap.remove(clazzName);
        }
        pageHashMap.put(clazzName, target);
    }

    public static void notifyPage(String clazzName, short packet, String json, byte[] rawData) {
        if (pageHashMap == null) return;
        if (pageHashMap.containsKey(clazzName)) {
            pageHashMap.get(clazzName).onMessageResponse(packet, json,rawData);
        }
    }

    public static void removePage(String clazzName) {
        if (pageHashMap == null) {
            pageHashMap = new HashMap<>();
        }
        if (pageHashMap.containsValue(clazzName)) {
            pageHashMap.remove(clazzName);
        }
    }
}
