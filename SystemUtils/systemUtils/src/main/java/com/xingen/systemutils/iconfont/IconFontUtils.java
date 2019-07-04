package com.xingen.systemutils.iconfont;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author HeXinGen
 * date 2018/12/25.
 *
 * 阿里矢量图库中iconfont工具类
 */
public class IconFontUtils {

    /**
     *  根据 rootView，为其子TextView设置 IconFont
     *  applyFont(context, findViewById(R.id.activity_root), "fonts/YourCustomFont.ttf");
     * @param context
     * @param rootView
     * @param fontFileName
     */

    public  static void applyFont(final Context context, final View rootView,final String fontFileName){
            try {
                if (rootView instanceof ViewGroup){
                    ViewGroup viewGroup=(ViewGroup) rootView;
                    for (int i=0;i<viewGroup.getChildCount();++i){
                        applyFont(context,viewGroup.getChildAt(i),fontFileName);
                    }
                }else if (rootView instanceof TextView){
                    ( (TextView) rootView).setTypeface(Typeface.createFromAsset(context.getAssets(),fontFileName));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
    }
}
