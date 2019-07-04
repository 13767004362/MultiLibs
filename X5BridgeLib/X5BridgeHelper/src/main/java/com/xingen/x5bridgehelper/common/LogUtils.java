package com.xingen.x5bridgehelper.common;

import android.util.Log;

/**
 * @author HeXinGen
 * date 2019/1/31.
 */
public class LogUtils {
    private volatile static boolean hasLog;
    public static void openLog(boolean isOpen){
        hasLog=isOpen;
    }
    public static void i(String tag,String content){
        if (hasLog){
            Log.i(tag,content);
        }
    }
}
