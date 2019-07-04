package com.xingen.x5bridgehelper.internal;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.text.TextUtils;

import com.xingen.x5bridgehelper.common.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author HeXinGen
 * date 2019/2/1.
 */
public abstract class PreloadHelper<R> {
    private final String tag;
    private final Handler workThread;
    private volatile WebLocalData data;

    public PreloadHelper() {
        tag = this.getClass().getSimpleName();
        workThread = createWorkThread(tag);
    }

    private Handler createWorkThread(String tag) {
        HandlerThread handlerThread = new HandlerThread(tag, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        return new Handler(handlerThread.getLooper());
    }


    /**
     * @param dir
     */
    public void localLocalResource(String dir) {
        if (TextUtils.isEmpty(dir)) return;
        data = WebLocalData.create().setDir(dir).setLocalResourceList(new CopyOnWriteArrayList<>());
        workThread.post(() -> {
            try {
                List<String> filesPathList = new ArrayList<>();
                FileUtils.queryFilesPath(data.getDir(), filesPathList);
                for (String filePath : filesPathList) {
                    String fileRelativePath = filePath.substring(data.getDir().length(), filePath.length());
                    data.getLocalResourceList().add(fileRelativePath);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public R preload(String url) {
        R resource = null;
        try {
            if (data != null && data.getDir() != null
                    && data.getLocalResourceList() != null
                    && data.getLocalResourceList().size() > 0
                    && !TextUtils.isEmpty(url)) {
                String filePath = queryResourceIfExist(url);
                boolean needLocalResource = !TextUtils.isEmpty(filePath);
                if (needLocalResource) {
                    String mime = null;
                    if (url.endsWith(".png")) {
                        mime = "image/png";
                    } else if (url.endsWith(".gif")) {
                        mime = "image/gif";
                    } else if (url.endsWith(".jpg")) {
                        mime = "image/jepg";
                    } else if (url.endsWith(".jepg")) {
                        mime = "image/jepg";
                    } else if (url.endsWith(".js")) {
                        mime = "text/javascript";
                    } else if (url.endsWith(".css")) {
                        mime = "text/css";
                    } else if (url.endsWith(".html")) {
                        mime = "text/html";
                    }
                    if (!TextUtils.isEmpty(mime)) {
                        resource = createResponse(mime, filePath);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resource;
    }

    /**
     * 根据url匹配本地资源
     *
     * @param url
     * @return
     */
    private String queryResourceIfExist(String url) {
        String filePath = null;
        if (data != null && data.getLocalResourceList() != null) {
            for (String fileRelativePath : data.getLocalResourceList()) {
                if (url.contains(fileRelativePath)) {
                    filePath = data.getDir() + fileRelativePath;
                    break;
                }
            }
        }
        return filePath;
    }

    public void destroy() {
        try {
            if (workThread != null) {
                workThread.getLooper().quit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract R createResponse(String mime, String filePath);


}
