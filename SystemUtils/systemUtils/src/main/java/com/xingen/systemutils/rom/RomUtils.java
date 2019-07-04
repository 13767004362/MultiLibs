package com.xingen.systemutils.rom;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;

/**
 * @author HeXinGen
 * date 2018/12/24.
 *
 * 国产ROM工具类
 */
public class RomUtils {

    /**
     * 获取launcher包名，从而获取哪个系统
     * @param context
     * @return
     */
    public static String getLauncherPackageName(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res == null || res.activityInfo == null) {
            return "";
        }
        if ("android".equals(res.activityInfo.packageName)) {
            return "";
        } else {
            return res.activityInfo.packageName;
        }
    }
    /**
     * 获取机型
     */
    public static String getModel() {
        try {
            String s= Build.MODEL;
            return s;
        } catch (Throwable throwable) {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
        return "";
    }
    /**
     * 国产手机设备系统
     */
    public static final  class Device{
        /**
         * 华为
         */
        public static final String ROM_HUAWEI="huawei";
        /**
         * 金立
         */
        public static final String ROM_JING_LI="gionee";
        /**
         * 魅族
         */
        public static final  String ROM_MEIZU="flyme";
        /**
         * 联想
         */
        public  static final  String ROM_LIAN_XIANG="lenovo";
        /**
         * 联想zuk
         */
        public  static final String ROM_ZUK="zui";
        /**
         *  锤子
         */
        public static final  String ROM_CUI_ZI="smartisanos";
        /**
         * 小辣椒
         */
        public  static final String ROM_XIAO_LA_JIAO="ila";
        /**
         * oppo
         */
        public static final  String ROM_OPPO="oppo";
        /**
         *  vivo
         */
        public static final  String ROM_VIVO="bbk";
        /**
         * 三星
         */
        public  static final  String ROM_Samsung="sec";
        /**
         * 诺基亚
         */
        public static final  String ROM_Nokia  ="evenwell";
        /**
         * 努比亚
         */
        public static final String ROM_Nubia="nubia";
    }
}
