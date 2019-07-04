package com.xingen.systemnotification;

import android.app.Notification;
import android.content.Context;

/**
 * Created by ${HeXinGen} on 2018/12/16.
 * blog博客:http://blog.csdn.net/hexingen
 *
 *
 */

public abstract  class SystemNotification {

    public static SystemNotification getInstance(){
        return  SystemNotificationImpl.getInstance();
    }

    /**
     * 初始化
     * @param context
     */
    public abstract void init(Context context);


    /**
     * 检查Notification是否被禁止
     * @return
     */
    public  abstract  boolean checkNotificationEnable();

    /**
     * 用户手动开启Notification设置界面
     * @param context
     */
    public abstract void openNotificationSet(Context context);

    /**
     * 显示notification
     * @param id
     * @param notification
     */
    public abstract  void  showNotification( int id, Notification notification);

    /**
     * 取消notification
     * @param id
     */
    public abstract  void cancelNotification( int id);

    /**
     * 获取notification 的 importance级别
     * @return
     */
    public abstract  int getImportance();

}
