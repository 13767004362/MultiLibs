package com.xingen.x5bridgehelper.common;

import android.app.Application;
import android.content.Context;

/**
 * @author HeXinGen
 * date 2019/1/31.
 */
public class ContextUtils {

    /**
     * 防止持有组件的生命周期，只持有Application
     *
     * @param context
     * @return
     */
   public  static Context createApplication(Context context) {
        return context instanceof Application ? context : context.getApplicationContext();
    }
}
