package com.ysxsoft.gkpf.bean;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ysxsoft.gkpf.bean.response.TaskListResponse;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        List<TaskListResponse> taskList=new ArrayList<>();
        String json="{\"groupId\":\"1\",\"requestId\":1,\"missionId\":\"\",\"taskInfoList\":[{\"taskName\":\"taskName1\",\"taskState\":1,\"flowNameList\":[\"flowName1\",\"flowName2\"]},{\"taskName\":\"taskName2\",\"taskState\":1,\"flowNameList\":[\"flowName3\",\"flowName4\"]}]}";
        try {
            JSONObject jsonObject=new JSONObject(json);
            String groupId=jsonObject.optString("groupId");
            int requestId=jsonObject.optInt("requestId");
            String missionId=jsonObject.optString("missionId");

            JSONArray array=jsonObject.optJSONArray("taskInfoList");
            for (int i = 0; i <array.length() ; i++) {
                JSONObject item=array.getJSONObject(i);
                String taskName=item.optString("taskName");
                int taskState=item.optInt("taskState");

                JSONArray flowNameList=item.optJSONArray("flowNameList");
                List<String> flowNames=new ArrayList<>();
                for (int j = 0; j <flowNameList.length(); j++) {
                    String flowName= (String) flowNameList.get(i);
                    flowNames.add(flowName);
                    TaskListResponse response=new TaskListResponse();
                    response.setMissionId(missionId);
                    response.setGroupId(groupId);
                    response.setTaskState(taskState);
                    response.setTaskName(taskName);

                    taskList.add(response);
                }
            }
            System.out.println(new Gson().toJson(taskList));
        }catch(JSONException e) {
            e.printStackTrace();
        }

    }
}
