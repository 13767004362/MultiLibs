package com.xingen.systemdonwload.download.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * @author HeXinGen
 * date 2018/12/14.
 *
 * 主线程
 */
public class MainThread implements Executor {
    private Handler uiHandler;

    public MainThread() {
        uiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute( Runnable command) {
        if (command == null) {
            return;
        }
        uiHandler.post(command);
    }
}
