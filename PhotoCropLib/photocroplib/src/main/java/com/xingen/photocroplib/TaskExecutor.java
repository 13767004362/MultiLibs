package com.xingen.photocroplib;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import com.xingen.photocroplib.internal.listener.BitmapListener;
import com.xingen.photocroplib.internal.listener.PermissionListener;

/**
 * Created by ${HeXinGen} on 2018/12/16.
 * blog博客:http://blog.csdn.net/hexingen
 */

public abstract class TaskExecutor {
    public static TaskExecutor getInstance(){
        return TaskExecutorImpl.getInstance();
    }

    /**
     * 初始化
     * @param context
     */
    public  abstract void init(Context context);
    /**
     * 执行任务
     * @param task
     * @param context
     */
    public  abstract  void execute(Activity context, Task task);

    public  abstract  void execute(Fragment fragment, Task task);
    /**
     * 查询任务
     * @param requestCode
     * @param resultCode
     * @param intent
     * @return
     */
    public abstract Task queryTask(int requestCode, int resultCode, Intent intent);

    /**
     * 处理读写权限的结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public abstract  void handleWritePermission(int requestCode, String[] permissions, int[] grantResults, PermissionListener listener);


    /**
     * 请求读写权限
     * @param activity
     * @return
     */
    public abstract boolean requestWritePermission(Activity activity);

    public abstract boolean requestWritePermission(Fragment fragment);
    /**
     * 同步加载
     * @param filePath
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public  abstract Bitmap synLoadBitmap(String filePath, int targetWidth, int targetHeight);

    /**
     *  异步加载，主线程回调
     * @param filePath
     * @param targetWidth
     * @param targetHeight
     * @param bitmapListener
     */
    public  abstract void   asynLoadBitmap(String filePath, int targetWidth, int targetHeight, BitmapListener bitmapListener);

}
