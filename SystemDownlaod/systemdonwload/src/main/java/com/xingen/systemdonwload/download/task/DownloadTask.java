package com.xingen.systemdonwload.download.task;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.xingen.systemdonwload.download.SystemDownload;

import java.io.File;

/**
 * @author HeXinGen
 * date 2018/12/14.
 *
 *
 */
public class DownloadTask extends DownloadManager.Request {

    public enum Monitor {
        query,//查询 监听
        broadcast;//广播监听
    }
    /**
     * 下载地址
     */
    private String url;
    /**
     * 文件存储地址
     */
    private String filePath;
    /**
     * Download.Request的id
     */
    private long requestId;

    private SystemDownload systemDownload;
    private boolean cancel;
    private SystemDownload.ProgressListener progressListener;
    private SystemDownload.DownloadListener downloadListener;
    private SystemDownload.BroadcastListener broadcastListener;

    private Monitor monitor;


    public static DownloadTask create(String url){
        return    new DownloadTask(Uri.parse(url)).setUrl(url);
    }

    /**
     * @param uri the HTTP or HTTPS URI to download.
     */
    private DownloadTask(Uri uri) {
        super(uri);
        this.cancel = false;
        monitor=Monitor.query;
    }

    /**
     *   隐藏notification
     */
    public DownloadTask setHideNotification(){
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DownloadTask setUrl(String url) {
        this.url = url;
        return this;
    }
    public String getFilePath() {
        return filePath;
    }

    public DownloadTask setFilePath(String filePath) {
        this.filePath = filePath;
        super.setDestinationUri(Uri.fromFile(new File(filePath)));
        return this;
    }

    public long getRequestId() {
        return requestId;
    }

    public DownloadTask setRequestId(long requestId) {
        this.requestId = requestId;
        return this;
    }



    public void cancel() {
        if (systemDownload == null) return;
        synchronized (this) {
            cancel = true;
        }
        this.systemDownload.cancelDownload(requestId);
    }

    public boolean isCancel() {
        synchronized (this) {
            return cancel;
        }
    }


    @Override
    public DownloadTask setTitle(CharSequence title) {
          super.setTitle(title);
        return this;
    }

    @Override
    public DownloadTask setNotificationVisibility(int visibility) {
        super.setNotificationVisibility(visibility);
        return this;
    }

    public void setSystemDownload(SystemDownload systemDownload) {
        this.systemDownload = systemDownload;
    }

    public SystemDownload.ProgressListener getProgressListener() {
        return progressListener;
    }

    public DownloadTask setProgressListener(SystemDownload.ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public SystemDownload.DownloadListener getDownloadListener() {
        return downloadListener;
    }

    public DownloadTask setDownloadListener(SystemDownload.DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    public SystemDownload.BroadcastListener getBroadcastListener() {
        return broadcastListener;
    }

    public DownloadTask  setBroadcastListener(SystemDownload.BroadcastListener broadcastListener) {
        this.monitor=Monitor.broadcast;
        this.broadcastListener = broadcastListener;
        return this;
    }

    public Monitor getMonitor() {
        return monitor;
    }


    @Override
    public DownloadTask setDescription(CharSequence description) {
        super.setDescription(description);
        return this;
    }

    @Override
    public DownloadTask setAllowedNetworkTypes(int flags) {
        super.setAllowedNetworkTypes(flags);
        return this;
    }

    @Override
    public DownloadTask setAllowedOverRoaming(boolean allowed) {
        super.setAllowedOverRoaming(allowed);
        return this;
    }

    @Override
    public DownloadTask setDestinationInExternalPublicDir(String dirType, String subPath) {
        super.setDestinationInExternalPublicDir(dirType, subPath);

        return this;
    }
    @Override
    public DownloadTask setMimeType(String mimeType) {
           super.setMimeType(mimeType);
        return this;
    }

    @Override
    public   DownloadTask setRequiresDeviceIdle(boolean requiresDeviceIdle) {
         super.setRequiresDeviceIdle(requiresDeviceIdle);
        return this;
    }

    @Override
    public DownloadTask setRequiresCharging(boolean requiresCharging) {
        super.setRequiresCharging(requiresCharging);
        return  this;
    }
    @Override
    public DownloadTask setDestinationInExternalFilesDir(Context context, String dirType, String subPath) {
       super.setDestinationInExternalFilesDir(context, dirType, subPath);
       return  this;
    }

    @Override
    public DownloadTask setDestinationUri(Uri uri) {
       super.setDestinationUri(uri);
        return this;
    }
}
