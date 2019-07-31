package com.ysxsoft.gkpf.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxj.xpopup.core.AttachPopupView;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.utils.ShareUtils;

/**
 * 评分弹窗
 */
public class PingFenPopupView extends AttachPopupView {

    private Context context;
    private PingFenPopupView instance;
    private String[] tempValue;
    private ItemClickResult itemClickResult;

    public PingFenPopupView(@NonNull Context context, String[] tempValue, ItemClickResult itemClickResult) {
        super(context);
        this.context = context;
        instance = this;
        this.tempValue = tempValue;
        this.itemClickResult = itemClickResult;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.activity_main_pingfen_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        LinearLayout baseView = findViewById(R.id.ll_activity_main_pingfen_dialog_item);
        for (int i = 0; i < tempValue.length; i++) {
            View itemView = View.inflate(context, R.layout.activity_main_pingfen_dialog_item, null);
            TextView itemText = itemView.findViewById(R.id.tv_activity_main_pingfen_dialog_item);
            itemText.setText(tempValue[i]);
            itemText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickResult.resultValue(((TextView)v).getText().toString());
                    instance.dismiss();
                }
            });
            baseView.addView(itemView);
        }
    }

    public interface ItemClickResult {
        void resultValue(String value);
    }

}
