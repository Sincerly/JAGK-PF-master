package com.ysxsoft.gkpf.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lxj.xpopup.core.DrawerPopupView;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.utils.SystemUtils;
import com.ysxsoft.gkpf.ui.adapter.LeftAdapter;

import java.util.ArrayList;

public class MainLeftPopupView extends DrawerPopupView {

    private Context context;
    private LeftAdapter leftAdapter;

    public MainLeftPopupView(@NonNull Context context) {
        super(context);
        this.context=context;
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

        RecyclerView recyclerView =findViewById(R.id.rv_activity_main_left_dialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        leftAdapter = new LeftAdapter(R.layout.activity_main_left_dialog_item);
        recyclerView.setAdapter(leftAdapter);
        initData();
    }

    private void initData() {
        ArrayList<String> temp = new ArrayList<>();
        temp.add("");
        temp.add("");
        temp.add("");
        temp.add("");
        temp.add("");
        temp.add("");
        leftAdapter.setNewData(temp);
    }

}
