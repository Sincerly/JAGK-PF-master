package com.ysxsoft.gkpf.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.YsxApplication;

/**
 * Created by zhaozhipeng on 17/6/27.
 */

public class ToastUtils {

    private static Context context = YsxApplication.getInstance();
    private static Toast toast;

    /**
     *
     * @param msg
     * @param time  0:short  1:long
     */
    public static void showToast(String msg , int time){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, time);
        LinearLayout layout = (LinearLayout)toast.getView();
        layout.removeAllViews();
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setBackgroundResource(R.drawable.toast_bg);
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.CENTER);
//		加粗
		tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTextSize(30);
        tv.setText(msg);
        layout.addView(tv);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
