package com.ysxsoft.gkpf.bean.request;

public class ConfirmFileRequest extends BaseRequest {
    private String groupId;
    private String fileName;

    public String getGroupId() {
        return groupId == null ? "" : groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFileName() {
        return fileName == null ? "" : fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
