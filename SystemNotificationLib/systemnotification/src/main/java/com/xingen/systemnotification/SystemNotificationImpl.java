package com.xingen.systemnotification;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import com.xingen.systemnotification.utils.AppIconUtils;
import com.xingen.systemnotification.utils.RomUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ${HeXinGen} on 2018/12/16.
 * blog博客:http://blog.csdn.net/hexingen
 */
class SystemNotificationImpl extends SystemNotification {
    /**
     * Value signifying that the user has not expressed an importance.
     *
     * This value is for persisting preferences, and should never be associated with
     * an actual notification.
     */
    public static final int IMPORTANCE_UNSPECIFIED = -1000;
    private static SystemNotificationImpl instance;
    private NotificationManager notificationManager;
    private Context appContext;

    static {
        instance = new SystemNotificationImpl();
    }
    private SystemNotificationImpl() {
    }
    public static SystemNotificationImpl getInstance() {
        return instance;
    }

    @Override
    public void init(Context context) {
        if (notificationManager == null) {
            this.appContext=context.getApplicationContext();
            notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            RomAdapter.adaptationRoom(context);

        }
    }
    @Override
    public boolean checkNotificationEnable() {
        if (appContext==null) return false;
        final String CHECK_OP_NO_THROW = "checkOpNoThrow";
        final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 24) {
            return notificationManager.areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOps = (AppOpsManager) appContext.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = appContext.getApplicationInfo();
            String pkg = appContext.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | RuntimeException e) {
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    public void openNotificationSet(Context context) {
        Intent intent;
        if (Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.O) {//跳转到设置的通知管理界面
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        } else {//跳转app设置详情界面
            intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
        }
        context.startActivity(intent);
    }
    @Override
    public void showNotification( int id, Notification notification) {
        if (notification==null|| notificationManager==null) return;
        //防止在5.0以上icon展示灰白错误
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP){
             try {
                Field mLargeIconField=notification.getClass().getDeclaredField("mLargeIcon");
                mLargeIconField.setAccessible(true);
                 if (mLargeIconField.get(notification)==null){
                     //静态内部类，用$链接
                     Class<?> BuilderClass=Class.forName("android.app.Notification$Builder");
                     Constructor<?> BuilderConstructor= BuilderClass.getConstructor(Context.class,Notification.class);
                     BuilderConstructor.setAccessible(true);
                     Notification.Builder BuilderObject= (Notification.Builder) BuilderConstructor.newInstance(appContext,notification);
                     Bitmap bitmap= AppIconUtils.getAppIcon(appContext);
                     BuilderObject.setLargeIcon(bitmap);
                     notification=BuilderObject.build();
                 }
             }catch (Exception e){
              e.printStackTrace();
             }
        }
        /**
         * 在8.0 以上需要加入 NotificationChannel
         */
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (TextUtils.isEmpty(notification.getChannelId())){
                String channelID = "default_channel_id";
                String channelName = "channel_name";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
                notificationManager.createNotificationChannel(channel);
                try {
                    Field mChannelId_Field= notification.getClass().getDeclaredField("mChannelId");
                    mChannelId_Field.setAccessible(true);
                    mChannelId_Field.set(notification,channelID);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        notificationManager.notify(id,notification);
    }

    @Override
    public void cancelNotification( int id) {
       if (notificationManager==null) return;
       notificationManager.cancel(id);
    }

    @Override
    public int getImportance() {
        if (Build.VERSION.SDK_INT >= 24) {
            return notificationManager==null?IMPORTANCE_UNSPECIFIED:notificationManager.getImportance();
        } else {
            return IMPORTANCE_UNSPECIFIED;
        }
    }
    /**
     * 机型适配
     */
    private final static class RomAdapter {
        public static void adaptationRoom(Context context) {
            String launcherPkg = RomUtils.getLauncherPackageName(context);
            switch (launcherPkg) {
                case RomUtils.Device.ROM_ZUK:
                    adaptationZUK(context);
                    break;
                case RomUtils.Device.ROM_OPPO: {
                    if ("OPPO A57".equalsIgnoreCase(RomUtils.getModel())) {
                        adaptationOppoA57(context);
                    }
                }
                break;
                default:

                    break;
            }
        }
        /**
         * 适配a57 ，需要关闭，重启第三次才能开启权限。
         *
         * @param context
         */
        private static void adaptationOppoA57(Context context) {
            //content://com.oppo.notification_center/powers
            String packageName = context.getPackageName();
            try {
                Intent intent = new Intent("oppo.safecenter.intent.action.CHANGE_NOTIFICATION_STATE");
                Bundle bundle = new Bundle();
                bundle.putString("package_name", packageName);
                bundle.putBoolean("allow_notify", true);
                intent.putExtras(bundle);
                context.sendBroadcast(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /**
         * 对ZUK进行适配
         *
         * @param context
         */
        private static void adaptationZUK(Context context) {
            String packageName = context.getPackageName();
            int uid = -1;
            try {
                uid = context.getPackageManager().getApplicationInfo(packageName, 41472).uid;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Intent intent = new Intent("com.zui.notificationsetting.service");
                intent.setPackage("com.zui.homesettings");
                Bundle bundle = new Bundle();
                bundle.putInt("op", 1);//1是开启，2是拒收
                bundle.putString("packagename", packageName);
                bundle.putInt("uid", uid);
                intent.putExtras(bundle);
                context.startService(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
