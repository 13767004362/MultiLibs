package com.xingen.systemutils.keyboard;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author HeXinGen
 * date 2019/1/3.
 */
public class KeyBoardUtils {


    /**
     * 用于管理软键盘，
     * @param active 显示或者关闭
     * @param activity
     */
    public static void handleKeyBoard(boolean active, Activity activity) {
        if (activity == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) return;
        View focusView = activity.getCurrentFocus();
        if (active) {//显示
            if (focusView != null) {
                //有焦点打开
                inputMethodManager.showSoftInput(focusView, 0);
            } else {
                //无焦点打开
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        } else {//关闭
            if (focusView != null) {//有焦点
                inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                //无焦点关闭
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        }
    }

}
