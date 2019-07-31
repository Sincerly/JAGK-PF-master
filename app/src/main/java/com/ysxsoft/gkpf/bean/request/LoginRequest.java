package com.ysxsoft.gkpf.bean.request;

import com.google.gson.Gson;

public class LoginRequest extends BaseRequest {
    private String userName;
    private String password;
    private String groupId;


    public String getUserName() {
        return userName == null ? "" : userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroupId() {
        return groupId == null ? "" : groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
