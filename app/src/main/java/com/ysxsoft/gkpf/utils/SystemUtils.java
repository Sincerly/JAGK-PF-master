package com.ysxsoft.gkpf.utils;

import android.app.Activity;
import android.graphics.Rect;

public class SystemUtils {
    /**
     * @param activity
     * @return > 0 success; <= 0 fail
     */
    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | IllegalArgumentException | SecurityException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 判断当前值的类型
     *
     * @param content
     * @return
     */
    public static Object getValueType(String content) {
        try {
            //评判结果是正数还是负数
            int jieguo = Integer.valueOf(content);
            return jieguo;
        } catch (Exception e) {
            try {
                double jieguo = Double.valueOf(content);
                return jieguo;
            } catch (Exception e1) {
                e1.printStackTrace();
                return content;
            }
        }
    }

}
