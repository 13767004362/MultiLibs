package com.xingen.systemdonwload.install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author HeXinGen
 * date 2018/12/14.
 */
public class InstallUtils {
    /**
     * 调用系统安装器的版本问题：
     * <p>
     * Android 7.0 :
     * 处理android.os.FileUriExposedException
     * <p>
     * Android 8.0 :
     * 未知来源应用，在应用权限设置的“特殊访问权限”中，需要 android.permission.REQUEST_INSTALL_PACKAGES
     */


    public static void install(Context context, String filePath) {
        Uri downloadFileUri;
        if (Build.VERSION.SDK_INT >= 24) {
            downloadFileUri = FileProvider.getUriForFile(context.getApplicationContext(), queryApplicationId(context) + ".provider", new File(filePath));
        } else {
            downloadFileUri = Uri.parse("file://" + new File(filePath).getAbsolutePath());
        }
        install(context, downloadFileUri);
    }

    public static void install(Context context, Uri downloadFileUri) {
        Intent installIntent = new Intent();
        installIntent.setAction(Intent.ACTION_VIEW);
        // 在Boradcast中启动活动需要添加Intent.FLAG_ACTIVITY_NEW_TASK
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
        if (Build.VERSION.SDK_INT >= 24) {
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        context.startActivity(installIntent);
    }

    /**
     * 查询BuildConfig类中的APPLICATION_ID
     *
     * @param context
     * @return
     */
    public static String queryApplicationId(Context context) {
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
}
