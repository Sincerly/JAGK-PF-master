package com.ysxsoft.gkpf.bean.request;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadScoreRequest  extends BaseRequest {

    /**
     * groupId : 1
     * userName : str
     * missionId : str
     * stepScores : {"flowName1":[{"score":5,"isConfirmed":false},{"score":5,"isConfirmed":false}],"flowName2":[{"score":5,"isConfirmed":true},{"score":5,"isConfirmed":false}]}
     */
    private String groupId;
    private String userName;
    private String missionId;
    private Map<String,List<Map<String,Object>>> stepScores;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public Map<String, List<Map<String, Object>>> getStepScores() {
        return stepScores;
    }

    public void setStepScores(Map<String, List<Map<String, Object>>> stepScores) {
        this.stepScores = stepScores;
    }

    public static void main(String[] args){
        UploadScoreRequest request=new UploadScoreRequest();
        request.setRequestId(1);
        request.setMissionId("2");
        request.setGroupId("1");
        request.setUserName("3");
        Map<String,List<Map<String,Object>>> stepMap=new HashMap<>();

        List<Map<String,Object>> flowName1=new ArrayList<>();
        Map<String,Object> objectMap=new HashMap<>();
        objectMap.put("score",2.0);
        objectMap.put("isConfirmed",false);
        flowName1.add(objectMap);
        flowName1.add(objectMap);
        stepMap.put("flowName1",flowName1);

        List<Map<String,Object>> flowName2=new ArrayList<>();
        Map<String,Object> objectMap2=new HashMap<>();
        objectMap2.put("score",5);
        objectMap2.put("isConfirmed",false);
        flowName2.add(objectMap2);
        flowName2.add(objectMap2);
        stepMap.put("flowName2",flowName2);

        request.setStepScores(stepMap);
        System.out.println("data:"+new Gson().toJson(request));
    }
}
