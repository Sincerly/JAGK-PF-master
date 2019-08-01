package com.ysxsoft.gkpf.ui.adapter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.bean.response.TaskListResponse;

public class LeftAdapter extends BaseQuickAdapter<TaskListResponse, BaseViewHolder> {

    private int currPage = 0;

    public LeftAdapter(int layoutResId) {
        super(layoutResId);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void convert(BaseViewHolder helper, TaskListResponse item) {
        int currPos = helper.getLayoutPosition() + 1;
        TextView tv_num = helper.getView(R.id.tv_activity_main_left_item_num);
        LinearLayout ll_bg = helper.getView(R.id.ll_activity_main_left_item_bg);

        tv_num.setText("" + currPos);

        switch (item.getTaskState()) {
            case 1:
                tv_num.setTextColor(mContext.getColor(R.color.white));
                ll_bg.setBackgroundResource(R.drawable.activity_main_left_bg_black);
                break;
            case 2:
                tv_num.setTextColor(mContext.getColor(R.color.white));
                ll_bg.setBackgroundResource(R.drawable.activity_main_left_bg_org);
                break;
            case 3:
                tv_num.setTextColor(mContext.getColor(R.color.main_left_black));
                ll_bg.setBackgroundResource(R.drawable.activity_main_left_bg_gray);
                break;
        }

        if(currPos==currPage){
            tv_num.setTextColor(mContext.getColor(R.color.white));
            ll_bg.setBackgroundResource(R.drawable.activity_main_left_bg_blue);
        }

    }

    public void setCurrPage(int page) {
        this.currPage = page;
        this.notifyDataSetChanged();
    }

}
