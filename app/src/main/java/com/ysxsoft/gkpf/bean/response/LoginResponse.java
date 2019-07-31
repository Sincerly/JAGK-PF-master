package com.ysxsoft.gkpf.bean.response;

public class LoginResponse {

    /**
     * groupId : 1
     * password : 1
     * requestId : 1
     * result : true
     * userName : 1
     * userType : 1
     * uuid : {e7333d11-44f8-4d71-aa1f-632037260f7e}
     */

    private String groupId;
    private String password;
    private int requestId;
    private boolean result;
    private String userName;
    private int userType;
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

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
