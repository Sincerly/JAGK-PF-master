package com.ysxsoft.gkpf.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lxj.xpopup.core.DrawerPopupView;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.api.ApiManager;
import com.ysxsoft.gkpf.bean.response.TaskListResponse;
import com.ysxsoft.gkpf.ui.MainActivity;
import com.ysxsoft.gkpf.ui.adapter.LeftPopupAdapter;
import com.ysxsoft.gkpf.utils.ShareUtils;
import com.ysxsoft.gkpf.utils.SystemUtils;
import com.ysxsoft.gkpf.ui.adapter.LeftAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainLeftPopupView extends DrawerPopupView {

    private Context context;
    private LeftPopupAdapter leftAdapter;
    private List<TaskListResponse> taskList;

    public MainLeftPopupView(@NonNull Context context, List<TaskListResponse> taskList, LeftPopupAdapter leftPopupAdapter) {
        super(context);
        this.context = context;
        this.taskList = taskList;
        this.leftAdapter = leftPopupAdapter;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.activity_main_left_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        //通过设置topMargin，可以让Drawer弹窗进行局部阴影展示
        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) getPopupContentView().getLayoutParams();
        params.topMargin = SystemUtils.getStatusHeight((Activity) context);

        TextView tv_type = findViewById(R.id.tv_type);
        String userType = ShareUtils.getUserType();
        if (!TextUtils.isEmpty(userType)) {
            tv_type.setText(userType.equals("1") ? "裁判员" : userType.equals("2") ? "助演" : "技术员");
        }
        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText(ShareUtils.getUserName());
        TextView tv_exit = findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(v -> {
            ApiManager.logout();//退出登录
        });

        RecyclerView recyclerView = findViewById(R.id.rv_activity_main_left_dialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(leftAdapter);
        leftAdapter.setNewData(taskList);
    }

}
