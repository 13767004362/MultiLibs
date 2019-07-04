package com.xingen.systemnotification.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;

/**
 * @author HeXinGen
 * date 2018/12/22.
 * <p>
 * 处理icon:
 * 1. api26 以上AdaptiveIconDrawable
 * 2. api26以下BitmapDrawable
 */
public class AppIconUtils {

    public static Bitmap getAppIcon(Context context) {
        Bitmap icon = null;
        icon = get_Android_O_AppIcon(context);
        if (icon == null) { //防止没拿到对应的8.0以上的icon
            icon = get_Android_Old_AppIcon(context);
        }
        return icon;
    }

    private static Bitmap get_Android_Old_AppIcon(Context context) {
        Bitmap icon = null;
        try {
            Drawable drawable = context.getPackageManager().getApplicationIcon(context.getPackageName());
            icon = ((BitmapDrawable) drawable).getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icon;
    }

    /**
     * 获取8.0以上的
     *
     * @param context
     * @return
     */
    private static Bitmap get_Android_O_AppIcon(Context context) {
        Bitmap icon = null;
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                Drawable drawable = context.getPackageManager().getApplicationIcon(context.getPackageName());
                if (drawable instanceof AdaptiveIconDrawable) {
                    Drawable backgroundDrawable = ((AdaptiveIconDrawable) drawable).getBackground();
                    Drawable foregroundDrawable = ((AdaptiveIconDrawable) drawable).getForeground();
                    Drawable[] drawableArray=new Drawable[2];
                    drawableArray[0]=backgroundDrawable;
                    drawableArray[1]=foregroundDrawable;
                    LayerDrawable layerDrawable=new LayerDrawable(drawableArray);
                    int width=layerDrawable.getIntrinsicWidth();
                    int height=layerDrawable.getIntrinsicHeight();
                    Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
                    Canvas canvas=new Canvas(bitmap);
                    layerDrawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
                    layerDrawable.draw(canvas);
                    icon=bitmap;
                } else if (drawable instanceof BitmapDrawable) {
                    icon = ((BitmapDrawable) drawable).getBitmap();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return icon;
    }
}
