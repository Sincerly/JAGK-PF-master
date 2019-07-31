package com.ysxsoft.gkpf.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.utils.ShareUtils;

/**
 * 设置弹窗
 */
public class AlertPopupView extends CenterPopupView {

    private Context context;
    private AlertPopupView instance;
    private String title;
    private String msg;

    public AlertPopupView(@NonNull Context context) {
        super(context);
        this.context = context;
        instance = this;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_alert;
    }

    public AlertPopupView setTitle(String title) {
        this.title = title;
        return this;
    }

    public AlertPopupView setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView tvTitle = findViewById(R.id.tv_dialog_alert_title);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        TextView tvMsg = findViewById(R.id.tv_dialog_alert_msg);
        if (!TextUtils.isEmpty(msg)) {
            tvMsg.setText(msg);
        }
        //确认按钮
        findViewById(R.id.tv_dialog_alert_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.dismiss();
            }
        });
    }
}
