package com.ysxsoft.gkpf.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.ysxsoft.gkpf.bean.CacheBean;
import com.ysxsoft.gkpf.bean.request.CacheRequest;
import com.ysxsoft.gkpf.bean.request.UploadScoreRequest;
import com.ysxsoft.gkpf.bean.response.AskResponse;
import com.ysxsoft.gkpf.bean.response.CacheResponse;
import com.ysxsoft.gkpf.bean.response.FileResponse;
import com.ysxsoft.gkpf.bean.response.LogoutResponse;
import com.ysxsoft.gkpf.bean.response.ScoreNotifyResponse;
import com.ysxsoft.gkpf.bean.response.TaskListResponse;
import com.ysxsoft.gkpf.config.AppConfig;
import com.ysxsoft.gkpf.ui.adapter.ContentFragmentAdapter;
import com.ysxsoft.gkpf.ui.adapter.LeftAdapter;
import com.ysxsoft.gkpf.ui.adapter.LeftPopupAdapter;
import com.ysxsoft.gkpf.utils.FileUtils;
import com.ysxsoft.gkpf.utils.JsonUtils;
import com.ysxsoft.gkpf.utils.Logutils;
import com.ysxsoft.gkpf.utils.ShareUtils;
import com.ysxsoft.gkpf.view.MainLeftPopupView;
import com.ysxsoft.gkpf.view.MultipleStatusView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_ASK_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_CACHE_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_EXAMEND_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_EXAMSTART_NOTIFY;
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
    @BindView(R.id.logLayout)
    LinearLayout logLayout;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.tv_curr_page_name)
    TextView tvCurrPageName;
    @BindView(R.id.multipleStatusView)
    MultipleStatusView multipleStatusView;

    private ContentFragmentAdapter fragmentAdapter;
    private LeftAdapter leftAdapter;
    private LeftPopupAdapter leftPopupAdapter;
    private LeftItemClickListener leftItemClick;
    List<TaskListResponse> taskList;
    //List<CacheResponse.DataBean> cacheResponses;
    Map<String, List<CacheResponse>> cacheResponses;
    private String missionId = "";//TODO:考试ID

    ContentFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();

        MessageCallbackMap.reg("Main", this);
//        ApiManager.logout();//退出登录
        //ApiManager.cache();//请求缓存
//        initList("");
        //initList("");
//        initCache("");
    }

    /**
     * 任务状态变化通知  处理服务器返回值
     *
     * @param json
     * @return
     */
    private String initList(String json) {
        String missionId = this.missionId;//missionId
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        taskList.clear();
//        json = "{\"groupId\":\"1\",\"requestId\":1,\"missionId\":\"333\",\"taskInfoList\":[{\"taskName\":\"taskName1\",\"taskState\":1,\"flowNameList\":[\"aaaaaa\",\"aaaaaa.xls\"]},{\"taskName\":\"taskName2\",\"taskState\":2,\"flowNameList\":[\"Excel模板.xls\",\"Excel模板.xls\"]}]}";
        try {
            JSONObject jsonObject = new JSONObject(json);
            String groupId = jsonObject.optString("groupId");
            int requestId = jsonObject.optInt("requestId");
            missionId = jsonObject.optString("missionId");

            JSONArray array = jsonObject.optJSONArray("taskInfoList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.optJSONObject(i);
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
            writeLog(e.getMessage(), 2);
        }
        //初始化ViewPager
        runOnUiThread(() -> {
            //TODO 重复下发任务状态数据会崩溃，原因待查？
            if (leftAdapter.getData().size() == 0) {
                initViewPager();
                initLeftData();
            }
        });
        return missionId;
    }

    /**
     * 请求缓存 处理服务器返回值
     */
    private void initCache(String json) {
        if (cacheResponses == null) {
            cacheResponses = new HashMap<>();
        }
        //json = "{\"groupId\":\"1\",\"requestId\":1,\"flowName1\":[{\"score\":1.5,\"isConfirmed\":false},{ \"score\":5,\"isConfirmed\":false}],\"flowName2\":[{\"score\":5,\"isConfirmed\":true},{\"score\":5, \"isConfirmed\":false}]}";
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if ("groupId".equals(key)) {
                    String groupId = jsonObject.optString("groupId");
                } else if ("requestId".equals(key)) {
                    int requestId = jsonObject.optInt("requestId");
                } else {
                    try {
                        JSONArray array = jsonObject.optJSONArray(key);
                        List<CacheResponse> list = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.optJSONObject(i);
                            Object score = object.get("score");
                            boolean isConfirmed = (boolean) object.get("isConfirmed");
                            //返回值封装数据
                            CacheResponse response = new CacheResponse();
                            response.setScore(score);
                            response.setConfirmed(isConfirmed);
                            list.add(response);
                        }
                        cacheResponses.put(key, list);//放进map
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //拿到缓存列表
            Set<String> key = cachePageBeanMap.keySet();
            Iterator<String> iterator = key.iterator();
            while (iterator.hasNext()) {
                String fileName = iterator.next();
                //拿到缓存的页面值
                CacheBean cacheBean = cachePageBeanMap.get(fileName);
                int size = 0;
                if (cacheBean != null) {
                    size = cacheBean.getSize();//缓存数量
                }
                List<CacheResponse> responses = null;
                if (cacheResponses.containsKey(fileName)) {
                    responses = createEmptyList(cacheResponses.get(fileName), size);
                } else {
                    responses = createEmptyList(new ArrayList<>(), size);
                }
                cacheResponses.put(fileName, responses);//替换追加空的map
                updatePageCache(fileName);//刷新页面
            }
            //拿到缓存page列表
            Log.e("tag", "cacheList:" + new Gson().toJson(cacheResponses));
            Log.e("tag", "cacheBeanMap:" + new Gson().toJson(cachePageBeanMap));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建同size大小的list
     *
     * @param data
     * @return
     */
    private List<CacheResponse> createEmptyList(List<CacheResponse> data, int size) {
        if (data == null) {
            data = new ArrayList<>();
        }
        List<CacheResponse> d = null;
        if (data.size() < size) {
            //向集合追加空
            d = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                if(i<data.size()){
                    //小于追加进去
                    CacheResponse response = data.get(i);
                    d.add(response);
                }else{
                    CacheResponse response = new CacheResponse();
                    response.setScore(0);
                    response.setConfirmed(false);
                    d.add(response);
                }
            }
        } else {
            d = new ArrayList<>(size);
            //后台返回比前台多1个
            for (int i = 0; i < size; i++) {
                d.add(data.get(i));
            }
        }
        return d;
    }

    public void updatePageCache(String fileName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (verticalViewpager != null) {
                    CacheBean cacheBean = cachePageBeanMap.get(fileName);
                    if (cacheBean == null) {
                        return;
                    }
                    int position = cacheBean.getPosition();//缓存页面下表标
                    ContentFragment contentFragment = (ContentFragment) fragmentAdapter.getItem(position);
                    if (cacheResponses != null && cacheResponses.containsKey(fileName)) {
                        contentFragment.responseCache(cacheResponses.get(fileName));
                    }
                }
            }
        });
    }

    /**
     * 请求缓存
     *
     * @param position 页面下标
     * @param fileName 页面文件名
     * @param size     页面行数
     */
    public void requestCache(int position, String fileName, int size) {
        if (cachePageBeanMap == null) {
            cachePageBeanMap = new HashMap<>();
        }
        if (cachePageBeanMap.containsKey(fileName)) {
            CacheBean cacheBean = cachePageBeanMap.get(fileName);
            cacheBean.setFileName(fileName);
            cacheBean.setPosition(position);
            cacheBean.setSize(size);
        } else {
            CacheBean cacheBean = new CacheBean();
            cacheBean.setFileName(fileName);
            cacheBean.setPosition(position);
            cacheBean.setSize(size);
            cachePageBeanMap.put(fileName, cacheBean);
        }
        ApiManager.cache();
    }

    private Map<String, CacheBean> cachePageBeanMap = new HashMap<>();//文件名为key 封装页面信息


    /**
     * 上传分数
     */
    private void upload() {
        UploadScoreRequest request = new UploadScoreRequest();
        request.setMissionId(missionId);
        request.setGroupId(ShareUtils.getGroup());
        request.setUserName(ShareUtils.getUserName());
        Map<String, List<LinkedHashMap<String, Object>>> stepMap = new HashMap<>();

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
            stepMap.put(key, flowName1);
        }
        request.setStepScores(stepMap);
        Logutils.e("data:" + new Gson().toJson(request));
        ApiManager.uploadScore(request);
    }

    /**
     * fragment单项上传
     */
    public void uploadByPosition(String fileName, int p, Object object, boolean isConfirm) {
        Logutils.e("单项上传："+ fileName+"p");
        if (cacheResponses != null) {
            List<CacheResponse> list = cacheResponses.get(fileName);
            if (list == null) {
                return;
            }
            boolean isEdited = false;
            for (int i = 0; i < list.size(); i++) {
                if (i == p) {
                    list.get(i).setConfirmed(isConfirm);
                    list.get(i).setScore(object);
                }
            }
            upload();
        }
    }

    Map<String, LinkedHashMap<String, String>> replaceMap;

    private void initReplaceNotify(String json) {
        if (replaceMap == null) {
            replaceMap = new HashMap<>();
        } else {
            replaceMap.clear();
        }
        json = "{\"groupId\":\"1\",\"requestId\":1,\"flowName1\":{\"replaceholder1\":\"替换1\",\"replaceholder2\":\"替换2\",\"replaceholder3\":\"替换33\"},\"flowName2\":{\"replaceholder4\":\"替换4\",\"replaceholder5\":\"替换5\",\"replaceholder6\":\"替换6\"}}";
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();
            List<String> list = new ArrayList<>();
            while (keys.hasNext()) {
                String key = keys.next();
                if ("groupId".equals(key)) {
                    String groupId = jsonObject.optString("groupId");
                } else if ("requestId".equals(key)) {
                    int requestId = jsonObject.optInt("requestId");
                } else {
                    try {
                        LinkedHashMap<String, String> map = new LinkedHashMap();
                        JSONObject flowObject = jsonObject.getJSONObject(key);
                        Iterator<String> flowKeyList = flowObject.keys();
                        while (flowKeyList.hasNext()) {
                            String flowKey = flowKeyList.next();
                            String placeHolderStr = flowObject.optString(flowKey);
                            map.put(flowKey, placeHolderStr);//替换内容
                        }
                        list.add(key);//放进key
                        replaceMap.put(key, map);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                 }
            }
            ApiManager.confirmReplace(list);
            Log.e("tag", "replaceList:" + new Gson().toJson(replaceMap));
        } catch (JSONException e) {
            e.printStackTrace();
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
        if (taskList != null) {
            leftAdapter.setNewData(taskList);
            leftPopupAdapter.setNewData(taskList);
        }
    }

    private void initViewPager() {
        //viewPager.setPageTransformer(false, new ZoomOutTransformer());
        //viewPager.setPageTransformer(true, new StackTransformer());
        ContentFragmentAdapter.Holder holder = new ContentFragmentAdapter.Holder(getSupportFragmentManager());
        for (int i = 0; i < taskList.size(); i++) {
            holder.add(ContentFragment.newInstance(taskList.get(i).getFlowName(), i, verticalViewpager));
        }
        fragmentAdapter = holder.set();
        verticalViewpager.setAdapter(fragmentAdapter);
        //If you setting other scroll mode, the scrolled fade is shown from either side of display.
        verticalViewpager.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        verticalViewpager.setOffscreenPageLimit(taskList.size());
        verticalViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                leftAdapter.setCurrPage(i);
                leftPopupAdapter.setCurrPage(i);
                tvCurrPageName.setText(((ContentFragment) fragmentAdapter.getItem(i)).getFileName());
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

    @OnClick({R.id.ll_activity_main_left, R.id.upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_activity_main_left:
                new XPopup.Builder(this)
                        .popupPosition(PopupPosition.Left)//右边
                        .asCustom(new MainLeftPopupView(this, taskList, leftPopupAdapter))
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
        writeLog(MessageSender.parsePacketType(type) + ":" + json, 1);
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
                            showToast("登出成功！");
                            startActivity(LoginActivity.class);
                            finish();
                        }
                    });
                } else {
                    //退出失败
                }
                break;
            case MSG_MANUALSCORE_EXAMSTART_NOTIFY:
                Log.e("tag", "考试开始通知");
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    missionId = jsonObject.optString("missionId");
                    ApiManager.confirmExamStart(missionId);
                } catch (Exception e) {
                    e.printStackTrace();
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
                initReplaceNotify(json);
                break;
            case MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY:
                //任务状态变化通知
                Log.e("tag", "任务状态变化通知");
                missionId = initList(json);
                ApiManager.confirmTaskState(missionId);
                break;
            case MSG_MANUALSCORE_ASK_NOTIFY:
                //问询通知
                Log.e("tag", "问询通知");
                AskResponse uploadResponse = JsonUtils.parseByGson(json, AskResponse.class);
                if (uploadResponse != null) {
                    String flowName = uploadResponse.getFlowName();
                    ApiManager.confirmAsk(flowName);
                }
                break;
            case MSG_MANUALSCORE_UPLOADSCORE_REPLY:
                //评分上传反馈
                Log.e("tag", "评分上传反馈");
//                UploadResponse uploadResponse=JsonUtils.parseByGson(json,UploadResponse.class);
//                if(uploadResponse!=null){
//                    if(uploadResponse.isResult()){
//                        //上传成功
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtils.showToast(MainActivity.this, "上传成功！", 300);
//                            }
//                        });
//                    }else{
//                        //上传失败
//                    }
//                }
                break;
            case MSG_MANUALSCORE_EXAMEND_NOTIFY:
                //考试结束通知
                Log.e("tag", "考试结束通知");
                ApiManager.confirmExamAck(missionId);//考试结束接收确认
                break;
            case MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY:
                //定位不同反馈
                Log.e("tag", "定位不同反馈");
                break;
            case MSG_TOTALGRADEFORM_REPLY:
                //总成绩单反馈
                Log.e("tag", "总成绩单反馈");
                break;
            case MSG_MANUALSCORE_INSTRUCTION_NOTIFY:
                //评分说明通知
                Log.e("tag", "评分说明通知");
                ScoreNotifyResponse scoreNotifyResponse = JsonUtils.parseByGson(json, ScoreNotifyResponse.class);
                if (scoreNotifyResponse != null) {
                    String instruction = scoreNotifyResponse.getInstruction();//说明通知
                    ShareUtils.setInstruction(instruction);
                    ApiManager.confirmInstruction();//评分说明接收确认
                }
                break;
        }
    }

    public void writeLog(final String strLog, final int nType) {
        Log.e("tag", (nType == 0 ? "发送" : (nType == 2) ? "解析异常" : "接收") + strLog);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                TextView logTextView = null;
                if (nType == 0) {
                    //发送的数据
                    logTextView = createTextView("SEND>>:" + strLog, Color.WHITE);
                } else {
                    //接收到的数据
                    logTextView = createTextView("RECEIVED>>:" + strLog, Color.RED);
                }
                if (logLayout.getChildCount() > 50) {
                    logLayout.removeAllViews();
                }
                logLayout.addView(logTextView);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public TextView createTextView(String str, int textColor) {
        TextView textView = new TextView(this);
        textView.setText(new SimpleDateFormat("yyyy-MM-dd HH:hh:ss:sss").format(new Date()) + " " + str);
        textView.setTextColor(textColor);
        textView.setTextSize(15);
        textView.setPadding(8, 0, 8, 0);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    private boolean isExit = false;

    @SuppressLint("HandlerLeak")
    @Override
    public void onBackPressed() {
        if (!isExit) {
            isExit = true;
            showToast("再次点击退出应用");
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    isExit = false;
                }
            }.sendEmptyMessageDelayed(0, 3000);
        } else {
            super.onBackPressed();
        }

    }
}
