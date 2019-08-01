package com.ysxsoft.gkpf.bean;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ysxsoft.gkpf.bean.request.UploadScoreRequest;
import com.ysxsoft.gkpf.bean.response.CacheResponse;
import com.ysxsoft.gkpf.bean.response.TaskListResponse;
import com.ysxsoft.gkpf.utils.ShareUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
//        List<CacheResponse.DataBean> cacheResponses=new ArrayList<>();
//        String cache="{\"groupId\":\"1\",\"requestId\":1,\"flowName1\":[{\"score\":1.5,\"isConfirmed\":false},{ \"score\":5,\"isConfirmed\":false}],\"flowName2\":[{\"score\":5,\"isConfirmed\":true},{\"score\":5, \"isConfirmed\":false}]}";
//        try {
//            JSONObject jsonObject=new JSONObject(cache);
//            Iterator<String> keys=jsonObject.keys();
//            while (keys.hasNext()){
//                String key=keys.next();
//                if ("groupId".equals(key)){
//                    String groupId=jsonObject.getString("groupId");
//                }else if ("requestId".equals(key)){
//                    int requestId=jsonObject.getInt("requestId");
//                }else {
//                    JSONArray array=jsonObject.getJSONArray(key);
//                    for (int i = 0; i <array.length() ; i++) {
//                        JSONObject object=array.getJSONObject(i);
//                        Object score=object.get("score");
//                        boolean isConfirmed=(boolean)object.get("isConfirmed");
//
//                        CacheResponse.DataBean response=new CacheResponse.DataBean();
//                        response.setConfirmed(isConfirmed);//是否确认
//                        response.setFlowName(key);//文件名
//                        response.setScore(score);//分数
//                        cacheResponses.add(response);
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

}
