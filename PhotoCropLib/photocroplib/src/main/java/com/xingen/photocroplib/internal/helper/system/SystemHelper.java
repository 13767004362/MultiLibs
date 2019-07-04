package com.xingen.photocroplib.internal.helper.system;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.xingen.photocroplib.Task;

/**
 * @author HeXinGen
 * date 2018/12/18.
 */
public  interface SystemHelper {


      void init(Context context);
      void doTask(Object activityOrFragment,Task photoTask);
       Task handleResult(int requestCode, int resultCode, Intent intent);


}
