package com.ysxsoft.gkpf.bean;

public class SocketHeartBean {
    private String version;
    private int ackId;
    private String groupId;

    public String getGroupId() {
        return groupId == null ? "" : groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version == null ? "1.0" : version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getAckId() {
        return ackId;
    }

    public void setAckId(int ackId) {
        this.ackId = ackId;
    }
}
