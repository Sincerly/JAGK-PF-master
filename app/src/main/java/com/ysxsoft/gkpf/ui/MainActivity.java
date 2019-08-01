package com.ysxsoft.gkpf.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.chong.widget.verticalviewpager.DummyViewPager;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.api.ApiManager;
import com.ysxsoft.gkpf.api.IMessageCallback;
import com.ysxsoft.gkpf.api.MessageCallbackMap;
import com.ysxsoft.gkpf.bean.response.FileResponse;
import com.ysxsoft.gkpf.bean.response.LogoutResponse;
import com.ysxsoft.gkpf.config.AppConfig;
import com.ysxsoft.gkpf.ui.adapter.ContentFragmentAdapter;
import com.ysxsoft.gkpf.ui.adapter.LeftAdapter;
import com.ysxsoft.gkpf.utils.FileUtils;
import com.ysxsoft.gkpf.utils.JsonUtils;
import com.ysxsoft.gkpf.utils.ToastUtils;
import com.ysxsoft.gkpf.view.MainLeftPopupView;

import java.util.ArrayList;

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

    private LeftAdapter leftAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initLeftData();
        initViewPager();

        MessageCallbackMap.reg("Main", this);
        //ApiManager.logout();//退出登录
        ApiManager.cache();//请求缓存
    }

    private void initView() {
        //左侧导航
        rvActivityMainLeft.setLayoutManager(new LinearLayoutManager(this));
        leftAdapter = new LeftAdapter(R.layout.activity_main_left_item);
        rvActivityMainLeft.setAdapter(leftAdapter);
    }

    private void initLeftData() {
        ArrayList<String> temp = new ArrayList<>();
        temp.add("");
        temp.add("");
        temp.add("");
        temp.add("");
        temp.add("");
        temp.add("");
        leftAdapter.setNewData(temp);
    }

    private void initViewPager() {
        //viewPager.setPageTransformer(false, new ZoomOutTransformer());
        //viewPager.setPageTransformer(true, new StackTransformer());
        String fileName = "Excel模板.xls";
        verticalViewpager.setAdapter(new ContentFragmentAdapter.Holder(getSupportFragmentManager())
                .add(ContentFragment.newInstance(fileName, 1, verticalViewpager))
                .add(ContentFragment.newInstance(fileName, 2, verticalViewpager))
                .add(ContentFragment.newInstance(fileName, 3, verticalViewpager))
                .add(ContentFragment.newInstance(fileName, 4, verticalViewpager))
                .add(ContentFragment.newInstance(fileName, 5, verticalViewpager))
                .set());
        //If you setting other scroll mode, the scrolled fade is shown from either side of display.
        verticalViewpager.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        verticalViewpager.setOffscreenPageLimit(5);
    }

    @OnClick(R.id.ll_activity_main_left)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_activity_main_left:
                new XPopup.Builder(this)
                        .popupPosition(PopupPosition.Left)//右边
                        .asCustom(new MainLeftPopupView(this))
                        .show();
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
//                ApiManager.confirmTaskState();
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
