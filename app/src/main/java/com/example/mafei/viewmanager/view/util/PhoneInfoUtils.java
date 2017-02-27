package com.example.mafei.viewmanager.view.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by jicool on 2017/2/13.
 */

public class PhoneInfoUtils {
    private static DisplayMetrics mMetrics;

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.widthPixels;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = null;

        // 获取系统服务时有可能错误，此处不崩溃
        try {
            Display display = null;
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            display = wm.getDefaultDisplay();

            if (display == null) {
                // 取出来的对象可能为空，http://bugly.qq.com/detail?app=900007917&pid=1&ii=15629#stack
                dm = context.getResources().getDisplayMetrics();
            } else {
                if (mMetrics == null) {
                    mMetrics = new DisplayMetrics();
                }
                display.getMetrics(mMetrics);
                dm = mMetrics;
            }
        } catch (Exception e) {
        }
        return dm;
    }
}
