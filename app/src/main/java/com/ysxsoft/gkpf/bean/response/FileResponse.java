package com.ysxsoft.gkpf.bean.response;

public class FileResponse {
    /**
     * fileContents : ��ࡱ�
     * fileName : Excel模板
     * fileSize : 13312
     * groupId : 1
     * requestId : 2
     */

    private String fileContents;
    private String fileName;
    private long fileSize;
    private int groupId;
    private int requestId;

    public String getFileContents() {
        return fileContents;
    }

    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}
