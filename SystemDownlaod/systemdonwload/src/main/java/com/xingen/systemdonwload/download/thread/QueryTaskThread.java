package com.xingen.systemdonwload.download.thread;

import android.app.DownloadManager;
import android.database.Cursor;

import com.xingen.systemdonwload.download.task.DownloadTask;

/**
 * @author HeXinGen
 * date 2018/12/14.
 *
 * 查询下载进度的工作线程
 */
public class QueryTaskThread implements Runnable {
    private MainThread mainThread;
    private DownloadTask downloadTask;
    private DownloadManager downloadManager;
    public QueryTaskThread(DownloadManager downloadManager, MainThread mainThread, DownloadTask downloadTask) {
        this.mainThread = mainThread;
        this.downloadTask = downloadTask;
        this.downloadManager = downloadManager;
    }

    @Override
    public void run() {
        if (downloadManager == null || downloadTask == null) {
            return;
        }
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadTask.getRequestId());
        boolean isQuery = true;
        try {
            //跨进程调用系统下载，会有一会儿的等待时间，因此，会查出多个DownloadManager.STATUS_PENDIN
            boolean isFirstStart=true;
            while (isQuery) {
                if (downloadTask.isCancel()) {
                    break;
                }
                Cursor cursor=null;
                try {
                  cursor = downloadManager.query(query);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (cursor != null && cursor.moveToFirst()) {
                    int state = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    Runnable runnable = null;
                    switch (state) {
                        case DownloadManager.STATUS_SUCCESSFUL://下载成功
                            isQuery = false;
                            runnable = () -> {
                                if (downloadTask.getDownloadListener() != null) {
                                    downloadTask.getDownloadListener().downloadFinish(downloadTask, downloadTask.getUrl(), downloadTask.getFilePath());
                                }
                            };
                            break;
                        case DownloadManager.STATUS_FAILED://下载失败
                            isQuery = false;
                            runnable = () -> {
                                if (downloadTask.getDownloadListener() != null) {
                                    downloadTask.getDownloadListener().downloadError(downloadTask, downloadTask.getUrl(), new Exception("下载失败"));
                                }
                                //移除SystemDownload中集合对其引用，防止指针泄露。
                                downloadTask.cancel();
                            };

                            break;
                        case DownloadManager.STATUS_RUNNING: {//下载中
                            int totalSize = cursor.getInt(
                                    cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            int currentSize = cursor.getInt(cursor.getColumnIndex(
                                    DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            int progress = (int) (((float) currentSize) / ((float) totalSize) * 100);
                            runnable = () -> {
                                if (downloadTask.getProgressListener() != null) {
                                    downloadTask.getProgressListener().progress(downloadTask, downloadTask.getUrl(), progress);
                                }
                            };
                        }
                        break;
                        case DownloadManager.STATUS_PAUSED://下载暂停，正在重试
                            break;
                        case DownloadManager.STATUS_PENDING://准备下载
                            if (isFirstStart){
                                isFirstStart=false;
                                runnable = () -> {
                                    if (downloadTask.getDownloadListener() != null) {
                                        downloadTask.getDownloadListener().downloadStart(downloadTask, downloadTask.getUrl());
                                    }
                                };
                            }
                            break;
                        default:
                            break;
                    }
                    if (downloadTask.isCancel()) {
                        break;
                    }
                    mainThread.execute(runnable);
                }
                if (cursor!=null){
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mainThread.execute(() -> {
                if (downloadTask.getDownloadListener() != null) {
                    downloadTask.getDownloadListener().downloadError(downloadTask, downloadTask.getUrl(), e);
                }
            });
        }
    }
}
