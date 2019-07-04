package com.xingen.photocroplib;

import android.app.Activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;


import com.xingen.photocroplib.internal.helper.bitmap.FileBitmapHelper;
import com.xingen.photocroplib.internal.helper.permission.WritePermissionHelper;
import com.xingen.photocroplib.internal.helper.system.SystemHelperImpl;
import com.xingen.photocroplib.internal.helper.system.SystemHelper;
import com.xingen.photocroplib.internal.listener.BitmapListener;
import com.xingen.photocroplib.internal.listener.PermissionListener;

/**
 * Created by ${HeXinGen} on 2018/12/16.
 * blog博客:http://blog.csdn.net/hexingen
 */

class TaskExecutorImpl extends TaskExecutor {
    private static TaskExecutorImpl instance;
    private WritePermissionHelper permissionHelper;
    private FileBitmapHelper fileBitmapHelper;
    private SystemHelper systemHelper;
    private Context appContext;
    static {
        instance = new TaskExecutorImpl();
    }
    private TaskExecutorImpl() {
        this.permissionHelper = new WritePermissionHelper();
        this.fileBitmapHelper = new FileBitmapHelper();
        this.systemHelper = new SystemHelperImpl();
    }
    public static TaskExecutorImpl getInstance() {
        return instance;
    }
    @Override
    public void init(Context context) {
        if (appContext == null) {
            this.appContext = context.getApplicationContext();
            this.systemHelper.init(appContext);
        }
    }
    @Override
    public void execute(Activity context, Task task) {
        this.systemHelper.doTask(context,task);
    }
    @Override
    public void execute(Fragment fragment, Task task) {
        this.systemHelper.doTask(fragment,task);
    }
    @Override
    public Task queryTask(int requestCode, int resultCode, Intent intent) {
        return this.systemHelper.handleResult(requestCode,resultCode,intent);
    }
    @Override
    public void handleWritePermission(int requestCode, String[] permissions, int[] grantResults, PermissionListener listener) {
        this.permissionHelper.handlePermissionResult(requestCode,permissions,grantResults,listener);
    }

    @Override
    public boolean requestWritePermission(Activity activity) {
        return this.permissionHelper.requestWritePermission(activity);
    }
    @Override
    public boolean requestWritePermission(Fragment fragment) {
        return this.permissionHelper.requestWritePermission(fragment);
    }
    @Override
    public Bitmap synLoadBitmap(String filePath, int targetWidth, int targetHeight) {
        return this.fileBitmapHelper.synLoadBitmap(filePath, targetWidth, targetHeight);
    }

    @Override
    public void asynLoadBitmap(String filePath, int targetWidth, int targetHeight, BitmapListener bitmapListener) {
        this.fileBitmapHelper.asynLoadBitmap(filePath, targetWidth, targetHeight, bitmapListener);
    }
}
