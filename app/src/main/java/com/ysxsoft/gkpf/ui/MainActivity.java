package com.ysxsoft.gkpf.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chong.widget.verticalviewpager.DummyViewPager;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.api.ApiManager;
import com.ysxsoft.gkpf.api.IMessageCallback;
import com.ysxsoft.gkpf.api.MessageCallbackMap;
import com.ysxsoft.gkpf.api.MessageSender;
import com.ysxsoft.gkpf.bean.request.UploadScoreRequest;
import com.ysxsoft.gkpf.bean.response.CacheResponse;
import com.ysxsoft.gkpf.bean.response.FileResponse;
import com.ysxsoft.gkpf.bean.response.LogoutResponse;
import com.ysxsoft.gkpf.bean.response.TaskListResponse;
import com.ysxsoft.gkpf.config.AppConfig;
import com.ysxsoft.gkpf.ui.adapter.ContentFragmentAdapter;
import com.ysxsoft.gkpf.ui.adapter.LeftAdapter;
import com.ysxsoft.gkpf.ui.adapter.LeftPopupAdapter;
import com.ysxsoft.gkpf.utils.FileUtils;
import com.ysxsoft.gkpf.utils.JsonUtils;
import com.ysxsoft.gkpf.utils.ShareUtils;
import com.ysxsoft.gkpf.utils.ToastUtils;
import com.ysxsoft.gkpf.view.MainLeftPopupView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_ASK_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_CACHE_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_EXAMEND_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_FILESEND;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_INSTRUCTION_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_LOGINOUT_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_REPLACE_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_UPLOADSCORE_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_TOTALGRADEFORM_REPLY;

public class MainActivity extends BaseActivity implements IMessageCallback {

    @BindView(R.id.rv_activity_main_left)
    RecyclerView rvActivityMainLeft;
    @BindView(R.id.vertical_viewpager)
    DummyViewPager verticalViewpager;
    @BindView(R.id.upload)
    TextView upload;

    private LeftAdapter leftAdapter;
    private LeftPopupAdapter leftPopupAdapter;
    private LeftItemClickListener leftItemClick;
    List<TaskListResponse> taskList;
    //List<CacheResponse.DataBean> cacheResponses;
    Map<String, List<CacheResponse>> cacheResponses;
    private String missionId = "";//TODO:考试ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();

        MessageCallbackMap.reg("Main", this);
//        ApiManager.logout();//退出登录
        //ApiManager.cache();//请求缓存
        initList("");
        initCache("");
    }

    /**
     * 任务状态变化通知  处理服务器返回值
     *
     * @param json
     * @return
     */
    private String initList(String json) {
        String missionId = "";//missionId
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        taskList.clear();
        json = "{\"groupId\":\"1\",\"requestId\":1,\"missionId\":\"333\",\"taskInfoList\":[{\"taskName\":\"taskName1\",\"taskState\":1,\"flowNameList\":[\"Excel模板.xls\",\"Excel模板.xls\"]},{\"taskName\":\"taskName2\",\"taskState\":2,\"flowNameList\":[\"Excel模板.xls\",\"Excel模板.xls\"]}]}";
        try {
            JSONObject jsonObject = new JSONObject(json);
            String groupId = jsonObject.optString("groupId");
            int requestId = jsonObject.optInt("requestId");
            missionId = jsonObject.optString("missionId");

            JSONArray array = jsonObject.optJSONArray("taskInfoList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String taskName = item.optString("taskName");
                int taskState = item.optInt("taskState");

                JSONArray flowNameList = item.optJSONArray("flowNameList");

                for (int j = 0; j < flowNameList.length(); j++) {
                    String flowName = flowNameList.getString(j);
                    Log.e("tag", "flowName:" + flowName);

                    TaskListResponse response = new TaskListResponse();
                    response.setMissionId(missionId);
                    response.setGroupId(groupId);
                    response.setTaskState(taskState);
                    response.setTaskName(taskName);
                    response.setFlowName(flowName);
                    taskList.add(response);
                }
            }
            Log.e("tag", "json:" + new Gson().toJson(taskList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //初始化ViewPager
        initLeftData();
        initViewPager();
        return missionId;
    }

    /**
     * 请求缓存 处理服务器返回值
     */
    private void initCache(String json) {
        if (cacheResponses == null) {
            cacheResponses = new HashMap<>();
        } else {
            cacheResponses.clear();
        }
        json = "{\"groupId\":\"1\",\"requestId\":1,\"flowName1\":[{\"score\":1.5,\"isConfirmed\":false},{ \"score\":5,\"isConfirmed\":false}],\"flowName2\":[{\"score\":5,\"isConfirmed\":true},{\"score\":5, \"isConfirmed\":false}]}";
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if ("groupId".equals(key)) {
                    String groupId = jsonObject.getString("groupId");
                } else if ("requestId".equals(key)) {
                    int requestId = jsonObject.getInt("requestId");
                } else {
                    JSONArray array = jsonObject.getJSONArray(key);
                    List<CacheResponse> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Object score = object.get("score");
                        boolean isConfirmed = (boolean) object.get("isConfirmed");
                        //返回值封装数据
                        CacheResponse response=new CacheResponse();
                        response.setScore(score);
                        response.setConfirmed(isConfirmed);
                        list.add(response);
                    }
                    cacheResponses.put(key,list);//放进map
                }
            }
            Log.e("tag","cacheList:"+new Gson().toJson(cacheResponses));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传分数
     */
    private void upload(){
        UploadScoreRequest request=new UploadScoreRequest();
        request.setMissionId(missionId);
        request.setGroupId(ShareUtils.getGroup());
        request.setUserName(ShareUtils.getUserName());
        Map<String,List<LinkedHashMap<String,Object>>> stepMap=new HashMap<>();

        Set<String> set = cacheResponses.keySet();
        Iterator<String> keys = set.iterator();
        //放文件
        while (keys.hasNext()) {
            String key = keys.next();
            List<LinkedHashMap<String, Object>> flowName1 = new ArrayList<>();
            List<CacheResponse> item = cacheResponses.get(key);
            if (item != null) {
                for (int i = 0; i < item.size(); i++) {
                    CacheResponse cacheResponse = item.get(i);
                    LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
                    objectMap.put("score", cacheResponse.getScore());
                    objectMap.put("isConfirmed", cacheResponse.isConfirmed());
                    flowName1.add(objectMap);
                }
            }
            stepMap.put(key,flowName1);
        }
        request.setStepScores(stepMap);
        System.out.println("data:"+new Gson().toJson(request));
        ApiManager.uploadScore(request);
    }

    /**
     * fragment单项上传
     */
    public void uploadByPosition(String fileName, int p, Object object, boolean isConfirm) {
        if (cacheResponses != null) {
            List<CacheResponse> list=cacheResponses.get(fileName);
            for (int i = 0; i <list.size(); i++) {
                if(i==p){
                    list.get(i).setConfirmed(isConfirm);
                    list.get(i).setScore(object);
                }
            }
            upload();
        }
    }

    private void initView() {
        //左侧导航
        rvActivityMainLeft.setLayoutManager(new LinearLayoutManager(this));
        leftAdapter = new LeftAdapter(R.layout.activity_main_left_item);
        rvActivityMainLeft.setAdapter(leftAdapter);
        leftItemClick = new LeftItemClickListener();
        leftAdapter.setOnItemClickListener(leftItemClick);
        leftPopupAdapter = new LeftPopupAdapter(R.layout.activity_main_left_dialog_item);
        leftPopupAdapter.setOnItemClickListener(leftItemClick);
    }

    private void initLeftData() {
        leftAdapter.setNewData(taskList);
    }

    private void initViewPager() {
        //viewPager.setPageTransformer(false, new ZoomOutTransformer());
        //viewPager.setPageTransformer(true, new StackTransformer());
        ContentFragmentAdapter.Holder holder = new ContentFragmentAdapter.Holder(getSupportFragmentManager());
        for (int i = 0; i < taskList.size(); i++) {
            holder.add(ContentFragment.newInstance(taskList.get(i).getFlowName(), i, verticalViewpager));
        }
        verticalViewpager.setAdapter(holder.set());
        //If you setting other scroll mode, the scrolled fade is shown from either side of display.
        verticalViewpager.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        verticalViewpager.setOffscreenPageLimit(taskList.size());
        verticalViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                leftAdapter.setCurrPage(i);
                leftPopupAdapter.setCurrPage(i);
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    public class LeftItemClickListener implements BaseQuickAdapter.OnItemClickListener {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            verticalViewpager.setCurrentItem(position, true);
        }
    }

    @OnClick({R.id.ll_activity_main_left,R.id.upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_activity_main_left:
                new XPopup.Builder(this)
                        .popupPosition(PopupPosition.Left)//右边
                        .asCustom(new MainLeftPopupView(this, taskList,leftPopupAdapter))
                        .show();
                break;
            case R.id.upload:
                //上传接口
                upload();
                break;
        }
    }

    /**
     * TODO
     * 网络响应接收方法
     *
     * @param type
     * @param json
     * @param rawData
     */
    @Override
    public void onMessageResponse(short type, String json, byte[] rawData) {
        switch (type) {
            case MSG_MANUALSCORE_LOGINOUT_REPLY:
                //登出反馈
                Log.e("tag", "登出回复");
                LogoutResponse response = JsonUtils.parseByGson(json, LogoutResponse.class);
                if (response.isResult()) {
                    //退出成功
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            MessageSender.close();
                            ToastUtils.showToast(MainActivity.this, "登出成功！", 300);
                            finish();
                        }
                    });
                } else {
                    //退出失败
                }
                break;
            case MSG_MANUALSCORE_CACHE_REPLY:
                //缓存反馈
                Log.e("tag", "缓存反馈");
                initCache(json);
                break;
            case MSG_MANUALSCORE_FILESEND:
                //评分表文件发送
                Log.e("tag", "评分表文件发送");
                FileResponse fileResponse = JsonUtils.parseByGson(json, FileResponse.class);
                String fileContent = fileResponse.getFileContents();
                int length = fileContent.length();
                byte[] bytes = fileContent.getBytes();
                for (int i = 0; i < length; i++) {
                    bytes[i] = (byte) fileContent.charAt(i);
                }
                String fileName = fileResponse.getFileName() + ".xls";
                FileUtils.byte2File(bytes, AppConfig.BASE_PATH, fileName);
                //发送接收文件确认
                ApiManager.confirmFile(fileName);
                break;
            case MSG_MANUALSCORE_REPLACE_NOTIFY:
                //占位符替换数据通知
                Log.e("tag", "占位符替换数据通知");
                //{"flowName1":{"replaceholder1":"","replaceholder2":"","replaceholder3":""},"flowName2":{"replaceholder1":"","replaceholder2":"","replaceholder3":""},"groupId":1,"requestId":1}
                //flowName1动态key
//                ApiManager.confirmReplace("");
                break;
            case MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY:
                //任务状态变化通知
                Log.e("tag", "任务状态变化通知");
                String missionId = initList(json);
                if (!"".equals(missionId)) {
                    ApiManager.confirmTaskState(missionId);
                }
                break;
            case MSG_MANUALSCORE_ASK_NOTIFY:
                //问询通知
                Log.e("tag", "问询通知");
//                ApiManager.confirmAsk();
                break;
            case MSG_MANUALSCORE_UPLOADSCORE_REPLY:
                //评分上传反馈
//                Log.e("tag", "评分上传反馈");
                break;
            case MSG_MANUALSCORE_EXAMEND_NOTIFY:
                //考试结束通知
                Log.e("tag", "考试结束通知");
//                ApiManager.confirmExamAck();//考试结束接收确认
                break;
            case MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY:
                //定位不同反馈
//                Log.e("tag", "定位不同反馈");
                break;
            case MSG_TOTALGRADEFORM_REPLY:
                //总成绩单反馈
//                Log.e("tag", "总成绩单反馈");
                break;
            case MSG_MANUALSCORE_INSTRUCTION_NOTIFY:
                //评分说明通知
                Log.e("tag", "评分说明通知");
//                ApiManager.confirmInstruction();//评分说明接收确认
                break;
        }
    }
}
