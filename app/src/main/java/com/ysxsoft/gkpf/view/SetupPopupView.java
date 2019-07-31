package com.ysxsoft.gkpf.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.lxj.xpopup.core.AttachPopupView;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.api.MessageSender;
import com.ysxsoft.gkpf.utils.ShareUtils;
import com.ysxsoft.gkpf.utils.ToastUtils;

import java.util.regex.Pattern;

/**
 * 设置弹窗
 */
public class SetupPopupView extends AttachPopupView {

    private Context context;
    private SetupPopupView instance;

    public SetupPopupView(@NonNull Context context) {
        super(context);
        this.context = context;
        instance = this;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.activity_login_setup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        final EditText etHost = findViewById(R.id.et_activity_login_setup_host);
        final EditText etPort = findViewById(R.id.et_activity_login_setup_port);
        final EditText etGroup = findViewById(R.id.et_activity_login_setup_group);
        etHost.setText(ShareUtils.getHost());
        etPort.setText(ShareUtils.getPort());
        etGroup.setText(ShareUtils.getGroup());
        //取消按钮
        findViewById(R.id.tv_activity_login_setup_qx).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.dismiss();
            }
        });
        //确认按钮
        findViewById(R.id.tv_activity_login_setup_qr).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.setHost(etHost.getText().toString());
                ShareUtils.setPort(etPort.getText().toString());
                ShareUtils.setGroup(etGroup.getText().toString());
                instance.dismiss();
            }
        });
    }
}
