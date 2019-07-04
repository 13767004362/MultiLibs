package com.xingen.systemdonwload.download;

import android.content.Context;

import com.xingen.systemdonwload.download.task.DownloadTask;

/**
 * @author HeXinGen
 * date 2018/12/14.
 */
public abstract class SystemDownload {

    public static SystemDownload getInstance() {
        return SystemDownloadImpl.getInstance();
    }
    /**
     * 初始化
     * @param context
     */
    public abstract void init(Context context);

    /**
     * 开始下载
     * @param downloadTask
     */
    public  abstract  <T extends DownloadTask>  void startDownload(T downloadTask);


    /**
     *  取消任务
     * @param requestId
     */

    public abstract  void  cancelDownload(long requestId);

    /**
     * 销毁的方法与init()对应
     */
    public abstract  void destroy();

    /**
     * 进度监听器
     */
    public interface  ProgressListener{
        void progress(DownloadTask downloadTask,String url,int progress);
    }
    /**
     *  下载结果监听器
     */
    public interface  DownloadListener{
        void downloadStart(DownloadTask downloadTask,String url);
        void downloadError(DownloadTask downloadTask,String url,Exception e);
        void downloadFinish(DownloadTask downloadTask,String url,String filePath);
    }
    /**
     * 下载完成的广播监听器
     */
    public interface  BroadcastListener{
        void receiverDownloadFinish(DownloadTask downloadTask,String url,String filePath);
    }
}
