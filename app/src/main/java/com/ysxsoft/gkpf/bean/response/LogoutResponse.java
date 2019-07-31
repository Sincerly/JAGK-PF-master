package com.ysxsoft.gkpf.bean.response;

public class LogoutResponse {

    /**
     * groupId : 1
     * password : 1
     * requestId : 2
     * result : true
     * userName : 1
     * uuid : {f5e77f8f-000d-4d54-b5ea-587dbfc9b6a8}
     */

    private String groupId;
    private String password;
    private int requestId;
    private boolean result;
    private String userName;
    private String uuid;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
