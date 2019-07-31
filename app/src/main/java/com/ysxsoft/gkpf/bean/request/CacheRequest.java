package com.ysxsoft.gkpf.bean.request;

public class CacheRequest extends BaseRequest{
    private String userName;
    private String groupId;

    public String getUserName() {
        return userName == null ? "" : userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupId() {
        return groupId == null ? "" : groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
