package com.ysxsoft.gkpf.bean.response;

public class AskResponse {

    /**
     * flowName : flowName1
     * groupId : 1
     * requestId : 1
     */

    private String flowName;
    private String groupId;
    private int requestId;

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}
