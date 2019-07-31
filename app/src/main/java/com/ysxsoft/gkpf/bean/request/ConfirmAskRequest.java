package com.ysxsoft.gkpf.bean.request;

public class ConfirmAskRequest extends BaseRequest {
    private String groupId;
    private String flowName;

    public String getGroupId() {
        return groupId == null ? "" : groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFlowName() {
        return flowName == null ? "" : flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }
}
