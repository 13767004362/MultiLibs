package com.xingen.systemutils.uri;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author HeXinGen
 * date 2018/12/24.
 *
 * 处理FileProvider问题
 */
public class UriUtils {

    /**
     * 生成File文件路径对应的Uri
     *
     * 7.0 及其以上使用FileProvider替换'file://'访问
     * @param context
     * @param filePath
     * @return
     */
    public static Uri getUriFromFile(Context context, String filePath){
        if (Build.VERSION.SDK_INT>=24){
            return FileProvider.getUriForFile(context, queryApplicationId(context) + ".provider", new File(filePath));
        }else{
            return  Uri.fromFile(new File(filePath));
        }
    }
    /**
     * 查询BuildConfig类中的APPLICATION_ID
     *
     * @param context
     * @return
     */
    private static String queryApplicationId(Context context) {
        String applicationId;
        try {
            String packageName = context.getPackageName();
            Class<?> BuildConfigClass = Class.forName(packageName + ".BuildConfig");
            Field APPLICATION_ID_Filed = BuildConfigClass.getDeclaredField("APPLICATION_ID");
            APPLICATION_ID_Filed.setAccessible(true);
            applicationId = (String) APPLICATION_ID_Filed.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            applicationId = context.getPackageName();
        }
        return applicationId;
    }


    /**
     * 为Intent中文件路径的Uri，授予读写的权限,
     * 适用于裁剪，带起始路径和裁剪后保存路径
     * @param context
     * @param intent
     * @param uri
     */
    public static  void grantUriPermission(Context context , Intent intent, Uri uri){
        List<ResolveInfo> resolveInfoList=context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo:resolveInfoList){
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }
}
