package com.xingen.systemdonwload.download.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xingen.systemdonwload.download.task.DownloadTask;
import com.xingen.systemdonwload.download.thread.MainThread;

import java.util.List;

/**
 * @author HeXinGen
 * date 2018/12/14.
 *
 * 下载完成的广播监听器
 */
public class DownloadReceiver extends BroadcastReceiver {
    private List<DownloadTask> downloadTaskList;
    private MainThread mainThread;
    public DownloadReceiver(Context context,MainThread mainThread,List<DownloadTask> downloadTaskList) {
        this. downloadTaskList=downloadTaskList;
        this.mainThread=mainThread;
        register(context);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        handlerDownload(intent);
    }

    /**
     * 接收到下载广播，且监听到对应的文件下载完成
     */
    private  void handlerDownload(Intent intent){
        long requestId=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (requestId==-1) return;
        for (DownloadTask downloadTask:downloadTaskList){
            if (downloadTask.getRequestId()==requestId){
                downloadTaskList.remove(downloadTask);
                mainThread.execute(()->{
                    if (downloadTask.getBroadcastListener()!=null){
                        downloadTask.getBroadcastListener().receiverDownloadFinish(downloadTask,downloadTask.getUrl(),downloadTask.getFilePath());
                    }
                });
                break;
            }
        }
    }
    public void register(Context context){
        IntentFilter intentFilter=new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE");
        context.registerReceiver(this,intentFilter);
    }
    public void unregister(Context context){
        context.unregisterReceiver(this);
        downloadTaskList=null;
        mainThread=null;
    }
}
