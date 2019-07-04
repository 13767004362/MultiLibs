package com.xingen.systemutils.statusbar;

import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author HeXinGen
 * date 2018/12/24.
 */
public class StatusBarUtils {


    /**
     *  修改StatusBar的背景颜色
     * @param window
     * @param color
     */
    public static void setStatusBar(Window window, String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}
