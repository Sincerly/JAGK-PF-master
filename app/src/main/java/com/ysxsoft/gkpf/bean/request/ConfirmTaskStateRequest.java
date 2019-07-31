package com.ysxsoft.gkpf.bean.request;

public class ConfirmTaskStateRequest extends BaseRequest {
    private String groupId;
    private String missionId;
    public String getGroupId() {
        return groupId == null ? "" : groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMissionId() {
        return missionId == null ? "" : missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }
}
