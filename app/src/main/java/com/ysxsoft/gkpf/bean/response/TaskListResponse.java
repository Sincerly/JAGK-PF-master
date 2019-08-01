package com.ysxsoft.gkpf.bean.response;

import com.google.gson.Gson;
import com.ysxsoft.gkpf.bean.request.UploadScoreRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskListResponse {
    private String groupId;
    private String missionId;
    private int taskState;
    private String taskName;
    private String flowName;


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

    public int getTaskState() {
        return taskState;
    }

    public void setTaskState(int taskState) {
        this.taskState = taskState;
    }

    public String getTaskName() {
        return taskName == null ? "" : taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getFlowName() {
        return flowName == null ? "" : flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }
}
