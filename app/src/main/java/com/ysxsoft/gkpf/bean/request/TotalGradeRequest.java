package com.ysxsoft.gkpf.bean.request;

public class TotalGradeRequest extends BaseRequest {

    /**
     * groupId : 1
     * missionId : str
     */

    private String groupId;
    private String missionId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }
}
