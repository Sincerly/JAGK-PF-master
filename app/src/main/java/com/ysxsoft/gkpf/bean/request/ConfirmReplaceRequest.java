package com.ysxsoft.gkpf.bean.request;

import java.util.ArrayList;
import java.util.List;

public class ConfirmReplaceRequest extends BaseRequest {
    private String groupId;
    private List<String> flowNameList;

    public String getGroupId() {
        return groupId == null ? "" : groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getFlowNameList() {
        if (flowNameList == null) {
            return new ArrayList<>();
        }
        return flowNameList;
    }

    public void setFlowNameList(List<String> flowNameList) {
        this.flowNameList = flowNameList;
    }
}
