package com.xingen.photocroplib;

import android.net.Uri;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by ${HeXinGen} on 2018/12/16.
 * blog博客:http://blog.csdn.net/hexingen
 */

public class Task {
    private final int requestCode;
    private Type type;
    private boolean success;
    private Uri uri;
    private int outputX;
    private int outputY;
    private String filePath;
    public static Task createCameraTask(String filePath) {
        Task task = new Task();
        task.filePath = filePath;
        task.type = Type.camera;
        return task;
    }
    public static Task createGalleryTask() {
        Task task = new Task();
        task.type = Type.gallery;
        return task;
    }
    public static Task createCropTask(Uri uri, int outputX, int outputY, String cropFilepath) {
        Task task = new Task();
        task.uri=uri;
        task.filePath = cropFilepath;
        task.outputX = outputX;
        task.outputY = outputY;
        task.type = Type.crop;
        return task;
    }
    private Task() {
        requestCode = Integer.valueOf(RandomUtils.getRandom(4));
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public Type getType() {
        return type;
    }

    public boolean isSuccess() {
        return success;
    }

    public Uri getUri() {
        return uri;
    }

    public int getOutputX() {
        return outputX;
    }

    public int getOutputY() {
        return outputY;
    }

    public String getFilePath() {
        return filePath;
    }

    /**
     * 任务类型，拍照，图库，裁剪,加载bitmap
     */
    public enum Type {
        camera,
        gallery,
        crop
    }

    private final static class RandomUtils {
        /**
         * 生成随机数纯数字
         *
         * @param length // 随机数的长度
         * @return
         */
        public static String getRandom(int length) {
            Random random = new Random();
            String rr = "";
            Set set = new HashSet();
            while (set.size() < length) {
                set.add(random.nextInt(10));
            }
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                rr = rr + iterator.next();
            }
            return rr;
        }
    }
}
