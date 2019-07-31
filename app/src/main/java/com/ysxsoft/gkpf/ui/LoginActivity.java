package com.ysxsoft.gkpf.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.api.ApiManager;
import com.ysxsoft.gkpf.api.IMessageCallback;
import com.ysxsoft.gkpf.api.MessageCallbackMap;
import com.ysxsoft.gkpf.api.MessageSender;
import com.ysxsoft.gkpf.bean.response.LoginResponse;
import com.ysxsoft.gkpf.config.AppConfig;
import com.ysxsoft.gkpf.utils.JsonUtils;
import com.ysxsoft.gkpf.utils.ShareUtils;
import com.ysxsoft.gkpf.utils.ToastUtils;
import com.ysxsoft.gkpf.view.AlertPopupView;
import com.ysxsoft.gkpf.view.MultipleStatusView;
import com.ysxsoft.gkpf.view.SetupPopupView;

import java.io.File;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class LoginActivity extends BaseActivity implements IMessageCallback {

    @BindView(R.id.iv_activity_login_setup)
    ImageView ivActivityLoginSetup;
    @BindView(R.id.iv_activity_login_saoma)
    ImageView ivActivityLoginSaoma;
    @BindView(R.id.iv_activity_login_name)
    ImageView ivActivityLoginName;
    @BindView(R.id.et_activity_login_name)
    EditText etActivityLoginName;
    @BindView(R.id.iv_activity_login_name_del)
    ImageView ivActivityLoginNameDel;
    @BindView(R.id.ll_activity_login_name)
    LinearLayout llActivityLoginName;
    @BindView(R.id.iv_activity_login_pwd)
    ImageView ivActivityLoginPwd;
    @BindView(R.id.et_activity_login_pwd)
    EditText etActivityLoginPwd;
    @BindView(R.id.iv_activity_login_pwd_del)
    ImageView ivActivityLoginPwdDel;
    @BindView(R.id.ll_activity_login_pwd)
    LinearLayout llActivityLoginPwd;
    @BindView(R.id.tv_activity_login_button)
    TextView tvActivityLoginButton;
    @BindView(R.id.multipleStatusView)
    MultipleStatusView multipleStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        requestPermissions();
        MessageCallbackMap.reg("Login", this);
    }

    private void initView() {
        //添加焦点监听
        etActivityLoginName.setOnFocusChangeListener(new inputFocusChange());
        etActivityLoginPwd.setOnFocusChangeListener(new inputFocusChange());
        //添加文本监听
        etActivityLoginName.addTextChangedListener(new inputTextChange());
        etActivityLoginPwd.addTextChangedListener(new inputTextChange());
    }

    @OnClick({R.id.iv_activity_login_name_del, R.id.iv_activity_login_pwd_del, R.id.iv_activity_login_saoma, R.id.iv_activity_login_setup, R.id.tv_activity_login_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_activity_login_name_del:   //清除账号
                etActivityLoginName.setText("");
                break;
            case R.id.iv_activity_login_pwd_del:    //清除密码
                etActivityLoginPwd.setText("");
                break;
            case R.id.iv_activity_login_saoma:      //扫码登录
                break;
            case R.id.iv_activity_login_setup:      //设置
                new XPopup.Builder(this)
                        .atView(ivActivityLoginSetup)
                        .dismissOnBackPressed(false) // 按返回键是否关闭弹窗，默认为true
                        .dismissOnTouchOutside(false) // 点击外部是否关闭弹窗，默认为true
                        .asCustom(new SetupPopupView(this))
                        .show();
                break;
            case R.id.tv_activity_login_button:     //登录
                if (TextUtils.isEmpty(ShareUtils.getHost()) || TextUtils.isEmpty(ShareUtils.getPort()) || TextUtils.isEmpty(ShareUtils.getGroup())) {
                    showToast("请完善配置信息");
                    return;
                }
//               showAlert("登录失败，用户名或密码错误！");
                //隐藏软键盘
                multipleStatusView.showLoading();
                Pattern pattern = Pattern.compile("^\\d+$");
                if (!"".equals(ShareUtils.getPort()) && pattern.matcher(ShareUtils.getPort()).matches()) {
                    MessageSender.connect(ShareUtils.getHost(), Integer.parseInt(ShareUtils.getPort()));
                    handler.sendEmptyMessageDelayed(0x01,300);
                } else {
                    ToastUtils.showToast(LoginActivity.this, "请检查配置！", 300);
                }
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0x01:
                    if(MessageSender.manager!=null&&MessageSender.manager.isConnect()){
                       handler.removeCallbacksAndMessages(null);
                       ApiManager.login(etActivityLoginName.getText().toString(),etActivityLoginPwd.getText().toString(),ShareUtils.getGroup());
                    }else{
                        handler.sendEmptyMessageDelayed(0x01,300);
                    }
                    break;
            }
        }
    };

    @Override
    public void onMessageResponse(short type, final String json, byte[] rawData) {
        switch (type) {
            case ApiManager.MSG_MANUALSCORE_LOGIN_REPLY:
                //登录回馈
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        LoginResponse response = JsonUtils.parseByGson(json, LoginResponse.class);
                        if (response != null && response.isResult()) {
                            //登录成功
                            ShareUtils.setUserType(String.valueOf(response.getUserType()));
                            ShareUtils.setUserName(response.getUserName());
                            ShareUtils.setUserPwd(response.getPassword());
                            multipleStatusView.hideLoading();
                            showToast("登陆成功！");
                            startActivity(MainActivity.class);
                            finish();
                        } else {
                            //登录失败
                            showToast("登陆失败！");
                        }
                    }
                });
                break;
        }
    }

    //焦点变化监听
    class inputFocusChange implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == etActivityLoginName.getId()) {
                if (hasFocus) {
                    llActivityLoginName.setBackgroundResource(R.drawable.activity_login_input_select);
                    ivActivityLoginName.setImageResource(R.mipmap.activity_login_name_select);
                    if (etActivityLoginName.getText().toString().length() > 0) {
                        ivActivityLoginNameDel.setVisibility(View.VISIBLE);
                    } else {
                        ivActivityLoginNameDel.setVisibility(View.INVISIBLE);
                    }
                } else {
                    llActivityLoginName.setBackgroundResource(R.drawable.activity_login_input_unselect);
                    ivActivityLoginName.setImageResource(R.mipmap.activity_login_name_unselect);
                    ivActivityLoginNameDel.setVisibility(View.INVISIBLE);
                }
            } else if (v.getId() == etActivityLoginPwd.getId()) {
                if (hasFocus) {
                    llActivityLoginPwd.setBackgroundResource(R.drawable.activity_login_input_select);
                    ivActivityLoginPwd.setImageResource(R.mipmap.activity_login_pwd_select);
                    if (etActivityLoginPwd.getText().toString().length() > 0) {
                        ivActivityLoginPwdDel.setVisibility(View.VISIBLE);
                    } else {
                        ivActivityLoginPwdDel.setVisibility(View.INVISIBLE);
                    }
                } else {
                    llActivityLoginPwd.setBackgroundResource(R.drawable.activity_login_input_unselect);
                    ivActivityLoginPwd.setImageResource(R.mipmap.activity_login_pwd_unselect);
                    ivActivityLoginPwdDel.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    //文字输入监听
    class inputTextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            View currView = getCurrentFocus();
            if (currView.getId() == etActivityLoginName.getId()) {
                if (etActivityLoginName.getText().toString().length() > 0) {
                    ivActivityLoginNameDel.setVisibility(View.VISIBLE);
                    if (etActivityLoginPwd.getText().toString().length() > 0) {
                        tvActivityLoginButton.setEnabled(true);
                    }
                } else {
                    ivActivityLoginNameDel.setVisibility(View.INVISIBLE);
                    tvActivityLoginButton.setEnabled(false);
                }
            } else if (currView.getId() == etActivityLoginPwd.getId()) {
                if (etActivityLoginPwd.getText().toString().length() > 0) {
                    ivActivityLoginPwdDel.setVisibility(View.VISIBLE);
                    if (etActivityLoginName.getText().toString().length() > 0) {
                        tvActivityLoginButton.setEnabled(true);
                    }
                } else {
                    ivActivityLoginPwdDel.setVisibility(View.INVISIBLE);
                    tvActivityLoginButton.setEnabled(false);
                }
            }
        }
    }

    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @SuppressLint("CheckResult")
    private void requestPermissions() {
        RxPermissions re = new RxPermissions(this);
        re.requestEach(BASIC_PERMISSIONS).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    // 用户已经同意该权限
                    if (permission.name.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        createFile();
                    }
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                } else {
                    // 用户拒绝了该权限，并且选中『不再询问』
                }
            }
        });
    }

    private void createFile() {
        File f = new File(AppConfig.BASE_PATH);
        if (!f.exists()) {
            f.mkdirs();
        }
    }
}



