package com.ysxsoft.gkpf.bean.response;

public class UploadResponse {
    private String groupId;
    private int requestId;
    private boolean result;

    public String getGroupId() {
        return groupId == null ? "" : groupId;
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

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
