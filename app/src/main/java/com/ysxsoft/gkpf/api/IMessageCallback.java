package com.ysxsoft.gkpf.api;

public interface IMessageCallback {
    void onMessageResponse(short type, String json, byte[] rawData);
}
