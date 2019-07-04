package com.xingen.systemdonwload.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author HeXinGen
 * date 2018/12/14.
 *
 *  1.点击back键不取消
 *  2.点击dialog外部不可取消
 */
public abstract class NotCancelDialog extends DialogFragment {
    protected View rootView;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        //设置dialog的背景色
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        window.setLayout(getDialogWidth(), getDialogHeight());
        window.setGravity(getGravity());
        //设置back键,不取消
        setCancelable(false);
        //设置触摸dialog外部不取消
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        initView(savedInstanceState);
        return rootView;
    }

    protected final <T extends View> T findViewById(int id) {
        return rootView.findViewById(id);
    }

    protected int getGravity() {
        return Gravity.CENTER;
    }

    /**
     * 设置宽度
     *
     * @return
     */
    protected int getDialogWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    /**
     * 设置高度
     *
     * @return
     */
    protected int getDialogHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public abstract int getLayoutId();

    protected abstract void initView(Bundle savedInstanceState);
}
