package com.xingen.systemdonwload.download;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;


import android.os.Process;
import android.util.Log;


import com.xingen.systemdonwload.download.receiver.DownloadReceiver;
import com.xingen.systemdonwload.download.task.DownloadTask;
import com.xingen.systemdonwload.download.thread.MainThread;
import com.xingen.systemdonwload.download.thread.QueryTaskThread;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author HeXinGen
 * date 2018/12/14.
 */
public class SystemDownloadImpl extends SystemDownload {
    private static SystemDownloadImpl instance;
    private List<DownloadTask> downloadTaskList = new CopyOnWriteArrayList<>();
    private DownloadManager downloadManager;
    private Looper workLooper;
    private MainThread mainThread;
    private Handler workThread;
    private Context appContext;
    private DownloadReceiver downloadReceiver;

    static {
        instance = new SystemDownloadImpl();
    }

    private SystemDownloadImpl() {
        this.mainThread = new MainThread();
        HandlerThread handlerThread = new HandlerThread(HandlerThread.class.getSimpleName(), Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        workLooper = handlerThread.getLooper();
        workThread = new Handler(workLooper);
    }

    public static SystemDownloadImpl getInstance() {
        return instance;
    }

    @Override
    public void init(Context context) {
        if (downloadManager == null && context != null) {
            this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            this.appContext = context.getApplicationContext();
            this.downloadReceiver = new DownloadReceiver(this.appContext, this.mainThread, this.downloadTaskList);

        }
    }

    @Override
    public void startDownload(DownloadTask downloadTask) {
        if (downloadTask == null && downloadManager == null) return;
        downloadTask.setSystemDownload(this);
        long requestId = downloadManager.enqueue(downloadTask);
        downloadTask.setRequestId(requestId);
        workThread.post(new QueryTaskThread(downloadManager, mainThread, downloadTask));
        if (downloadTask.getMonitor() == DownloadTask.Monitor.broadcast) {
            downloadTaskList.add(downloadTask);
        }
    }

    @Override
    public void cancelDownload(long requestId) {
        if (downloadManager == null) return;
//Log.i(SystemDownloadImpl.class.getSimpleName()," cancelDownload ");
        downloadManager.remove(requestId);
        for (DownloadTask downloadTask : downloadTaskList) {
            if (downloadTask.getRequestId() == requestId) {
                downloadTaskList.remove(downloadTask);
                break;
            }
        }
    }

    @Override
    public void destroy() {
        if (workLooper != null) {
            try {
                if (Build.VERSION.SDK_INT >= 18) {
                    workLooper.quitSafely();
                } else {
                    workLooper.quit();
                }
                workLooper = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        downloadManager = null;
        downloadTaskList.clear();
        if (downloadReceiver != null) {
            this.downloadReceiver.unregister(this.appContext);
        }
    }
}
