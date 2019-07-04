package com.xingen.x5bridgehelper.internal;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;


import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author HeXinGen
 * date 2019/2/1.
 */
public class BridgeWebViewHelper extends WebViewHelper<BridgeWebViewHelper.SingleBridgeWebView, WebResourceResponse> {
    public static BridgeWebViewHelper getInstance(){
        return Instance.instance;
    }
    private static final class Instance{
        private static final BridgeWebViewHelper instance=new BridgeWebViewHelper();
    }
    private BridgeWebViewHelper(){ }
    @Override
    protected void initSelfConfig() { }

    @Override
    protected SingleBridgeWebView createWebComponent() {
        return new SingleBridgeWebView(appContext);
    }
    @Override
    protected PreloadHelper<WebResourceResponse> createPreloadComponent() {
        return new PreloadHelperImpl();
    }
    @Override
    public void unbind() {
        if (webComponent != null) {
            //清除父容器
            ViewGroup viewGroup = (ViewGroup) webComponent.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(webComponent);
                writerLog("释放资源，将WebView从Parent容器移除");
            }
            webComponent.setWebViewClient(null);
            webComponent.setWebChromeClient(null);
            //清除注册的 BridgeHandler 和 CallBackFunction
            Class<?> BridgeWebViewClass = webComponent.getClass().getSuperclass();
            try {
                Field responseCallbacksField = BridgeWebViewClass.getDeclaredField("responseCallbacks");
                responseCallbacksField.setAccessible(true);
                Map<String, CallBackFunction> responseCallbacks = (Map<String, CallBackFunction>) responseCallbacksField.get(webComponent);
                if (responseCallbacks.size() > 0) {
                    responseCallbacks.clear();
                    writerLog("释放资源，清空WebView中  BridgeHandler");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Field messageHandlersField = BridgeWebViewClass.getDeclaredField("messageHandlers");
                messageHandlersField.setAccessible(true);
                Map<String, BridgeHandler> messageHandlers = (Map<String, BridgeHandler>) messageHandlersField.get(webComponent);
                if (messageHandlers.size() > 0) {
                    messageHandlers.clear();
                    writerLog("释放资源，清空WebView中  BridgeHandler");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clearHistory(boolean result) {
        if (webComponent != null) {
            if (result) {
                webComponent.clearHistory();
                webComponent.setTag(true);
            } else {
                webComponent.setTag(false);
            }
        }

    }


    @Override
    public void destroy() {
        try {
            if (webComponent != null) {
                unbind();
                webComponent.destroy();
            }
            if (preloadComponent != null) {
                preloadComponent.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载WebView需要的资源
     */
    private static final class PreloadHelperImpl extends PreloadHelper<WebResourceResponse> {
        @Override
        protected WebResourceResponse createResponse(String mime, String filePath) {
            WebResourceResponse response = null;
            try {
                final String encoding = "UTF-8";
                response = new WebResourceResponse(mime, encoding, new FileInputStream(new File(filePath)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    /**
     * 自定义WebView组件，封装一些通性
     */
    public static class SingleBridgeWebView extends BridgeWebView {
        public SingleBridgeWebView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }
        public SingleBridgeWebView(Context context) {
            super(context);
            init();
        }
        private void init() {
            //设置常用配置
            WebSettings webSetting = getSettings();
            webSetting.setJavaScriptEnabled(true);
            webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
            webSetting.setAllowFileAccess(true);
            webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webSetting.setBuiltInZoomControls(false);
            webSetting.setSupportZoom(false);
            webSetting.setUseWideViewPort(true);
            webSetting.setSupportMultipleWindows(true);
            webSetting.setAppCacheEnabled(true);
            webSetting.setDomStorageEnabled(true);
            webSetting.setGeolocationEnabled(true);
            webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
            webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
            webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
            //设置默认处理器
            setDefaultHandler(new DefaultHandler());
            //设置默认的WebViewClient
            setSingleBridgeWebViewClient(new SingleBridgeWebViewClient(this));
        }
        public void  setSingleBridgeWebViewClient(SingleBridgeWebViewClient client){
            setWebViewClient(client);
        }
    }
    public static class SingleBridgeWebViewClient extends BridgeWebViewClient {
        protected BridgeWebViewHelper helper;
        public SingleBridgeWebViewClient(BridgeWebView webView) {
         this(webView,null);
        }
        public SingleBridgeWebViewClient(BridgeWebView webView,String localResourcePath) {
            super(webView);
            helper=Instance.instance;
            helper.loadResource(localResourcePath);
        }
        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            try {
                if (helper != null) {
                    if (!helper.isNeedClearHistory()) {
                        helper.clearHistory(true);
                        helper.writerLog("清除历史，清除操作成功");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            WebResourceResponse response = null;
            if (helper != null) {
                response = helper.preloadResource(url);
            }
            return response == null ? super.shouldInterceptRequest(view, url) : response;
        }
    }





}
