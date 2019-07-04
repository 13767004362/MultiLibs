package com.xingen.photocroplib.internal.helper.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.xingen.photocroplib.internal.listener.PermissionListener;

/**
 * @author HeXinGen
 * date 2018/12/17.
 *
 * 权限辅助类
 */
public  class WritePermissionHelper {

    /**
     * 检查是否获取到读写权限
     * @param context
     * @return
     */
   public boolean checkWritePermission(Context context){
       return PermissionUtils.hasPermission(context,Permission.PERMS_WRITE);
   }
   public boolean requestWritePermission(Object context){
       int requestCode=Permission.write_permission_code;
       if (context instanceof Activity && Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
           Activity activity = (Activity) context;
           boolean result= checkWritePermission(activity);
           if (!result){
               activity.requestPermissions(Permission.PERMS_WRITE,requestCode);
           }
           return result;
       }else if (context instanceof Fragment  && Build.VERSION.SDK_INT > Build.VERSION_CODES.M ){
           Fragment fragment=(Fragment) context;
           boolean result= checkWritePermission(fragment.getContext());
           if (!result){
               fragment.requestPermissions(Permission.PERMS_WRITE,requestCode);
           }
           return result;
       }else {
           return false;
       }
   }
   public void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults, PermissionListener listener){
       if (requestCode != Permission.write_permission_code) return;
       boolean result = true;
       for (int permission : grantResults) {
           if (permission != PackageManager.PERMISSION_GRANTED) {
               result = false;
               break;
           }
       }
       if (listener!=null){
           listener.result(result);
       }
   }
   private final static class  PermissionUtils{
       /**
        * 检查申请到权限
        * @param context
        * @param permissions
        * @return
        */
       public static boolean hasPermission(Context context, String ...permissions){
           if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
               return true;
           }
           for (String permission:permissions){
               if (ContextCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED){
                   return false;
               }
           }
           return true;
       }
   }
   public  final static class  Permission{
        /**
         * 写入权限的请求code,提示语，和权限码
         */
        public final  static String[] PERMS_WRITE ={Manifest.permission.WRITE_EXTERNAL_STORAGE};
        public final static  int write_permission_code=11111;
    }
}
