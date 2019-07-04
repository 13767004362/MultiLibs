package com.xingen.x5bridgehelper.internal;

import android.content.Context;
import android.view.View;

import com.xingen.x5bridgehelper.common.ContextUtils;
import com.xingen.x5bridgehelper.common.LogUtils;

/**
 * @author HeXinGen
 * date 2019/2/1.
 *
 * 一个基类，共性和抽象行为
 *
 */
public abstract  class WebViewHelper<T extends View,R>{
    public static void initBridge(Context context,boolean hasLog){
        LogUtils.openLog(hasLog);
        BridgeWebViewHelper.getInstance().init(context);
    }
    public static void initX5Bridge(Context context,boolean hasLog){
        X5BridgeWebViewHelper.getInstance().init(context);
        LogUtils.openLog(hasLog);
    }
    protected Context appContext;
    protected T webComponent;
    protected  String TAG;
    protected PreloadHelper<R> preloadComponent;
    /**
     * 初始化入口
     * @param context
     */
    public  void init(Context context){
        if (appContext==null){
            appContext= ContextUtils.createApplication(context);
            initSelfConfig();
            webComponent = createWebComponent();
            preloadComponent=createPreloadComponent();
            TAG=this.getClass().getSimpleName();
        }
    }
    protected boolean isNeedClearHistory(){
        boolean isNeed=false;
        if (webComponent!=null){
           if (webComponent.getTag()instanceof  Boolean){
                isNeed=(Boolean) webComponent.getTag();
           }
        }
        return isNeed;
    }

    /**
     * 根据url，匹配本地资源，实现预加载
     * @param url
     * @return
     */
    protected R preloadResource(String url){
        return preloadComponent==null?null:preloadComponent.preload(url);
    }

    /**
     * 根据本地预加载资源路径，获取本地资源
     * @return
     */
    protected void loadResource(String filePath){
        if (preloadComponent!=null){
            preloadComponent.localLocalResource(filePath);
        }
    };
    protected   void writerLog(String content) {
        LogUtils.i(TAG, content);
    }

    /**
     * 创建WebView 组件
     * @return
     */
    protected abstract T createWebComponent();

    /**
     * 创建预加载的组件
     * @return
     */
    protected  abstract PreloadHelper<R> createPreloadComponent();

    /**
     * 初始化私有配置
     */
    protected abstract void initSelfConfig();

    /**
     *  与组件绑定
     */
    public   T bind(){
        unbind();
        //绑定标识，用于标记需要清除历史
        clearHistory(false);
        return webComponent;
    };
    /**
     * 与组件解绑
     */
    public abstract  void unbind();

    /**
     * 清除历史
     */
   protected abstract void clearHistory(boolean result);



    /**
     * 销毁
     */
    public abstract void destroy();




}
