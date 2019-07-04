package com.xingen.photocroplib.internal.helper.bitmap;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

import com.xingen.photocroplib.internal.listener.BitmapListener;
import com.xingen.photocroplib.internal.utils.FileBitmapUtils;

/**
 * @author HeXinGen
 * date 2018/12/18.
 */
public class FileBitmapHelper {
    private Handler workThread;
    private Looper looper;
    private Handler mainThread;
    public FileBitmapHelper() {
        HandlerThread handlerThread = new HandlerThread(HandlerThread.class.getSimpleName(), Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        looper = handlerThread.getLooper();
        workThread = new Handler(looper);
        mainThread = new Handler(Looper.getMainLooper());
    }
    public Bitmap synLoadBitmap(String filePath, int targetWidth, int targetHeight) {
        return FileBitmapUtils.createBitmap(filePath, targetWidth, targetHeight);
    }
    public void asynLoadBitmap(String filePath, int targetWidth, int targetHeight, BitmapListener bitmapListener) {
        if (bitmapListener == null) return;
        workThread.post(() -> {
            Bitmap bitmap = FileBitmapUtils.createBitmap(filePath, targetWidth, targetHeight);
            mainThread.post(() -> {
                if (bitmapListener == null) return;
                bitmapListener.result(filePath, bitmap);
            });
        });
    }

    ;
}
