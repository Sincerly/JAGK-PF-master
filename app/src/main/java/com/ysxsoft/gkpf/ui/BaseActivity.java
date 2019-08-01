package com.ysxsoft.gkpf.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.ysxsoft.gkpf.utils.ToastUtils;
import com.ysxsoft.gkpf.view.AlertPopupView;
import com.zhy.autolayout.AutoLayoutActivity;

@SuppressLint("Registered")
public class BaseActivity extends AutoLayoutActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 返回按钮默认点击事件
     */
    public void onBack(View view) {
        finish();
    }

    /**
     * activity跳转（无参数）
     */
    public void startActivity(Class<?> className) {
        Intent intent = new Intent(this, className);
        startActivity(intent);
    }

    /**
     * activity跳转（有参数）
     */
    public void startActivity(Class<?> className, Bundle bundle) {
        Intent intent = new Intent(this, className);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 吐司
     */
    protected void showToast(String text) {
        runOnUiThread(() -> ToastUtils.showToast(BaseActivity.this, text, 0));
    }

    /**
     * 通用提示对话框，带title
     *
     * @param title
     * @param msg
     */
    protected void showAlert(String title, String msg) {
        new XPopup.Builder(this)
                .dismissOnTouchOutside(false) // 点击外部是否关闭弹窗，默认为true
                .asCustom(new AlertPopupView(this).setTitle(title).setMsg(msg))
                .show();
    }

    /**
     * 通用提示对话框，默认title
     *
     * @param msg
     */
    protected void showAlert(String msg) {
        new XPopup.Builder(this)
                .dismissOnTouchOutside(false) // 点击外部是否关闭弹窗，默认为true
                .asCustom(new AlertPopupView(this).setMsg(msg))
                .show();
    }

}
