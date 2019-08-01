package com.ysxsoft.gkpf.bean.response;

public class ScoreNotifyResponse {

    /**
     * requestId : 1
     * instruction : str
     */

    private int requestId;
    private String instruction;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
