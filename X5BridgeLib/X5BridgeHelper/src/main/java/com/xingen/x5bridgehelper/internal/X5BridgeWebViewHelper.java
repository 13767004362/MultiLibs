package com.xingen.x5bridgehelper.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeUtil;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.github.lzyzsd.jsbridge.Message;
import com.github.lzyzsd.jsbridge.WebViewJavascriptBridge;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xingen.x5bridgehelper.common.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author HeXinGen
 * date 2019/2/1.
 *
 *
 * *  1. 初始化X5内核
 *  2. 重写X5内核的X5BridgeWebView
 *  3. 重写X5内核的X5BridgeWebViewClient
 */
public class X5BridgeWebViewHelper extends WebViewHelper<X5BridgeWebViewHelper.SingleX5BridgeWebView, WebResourceResponse> {

    public static X5BridgeWebViewHelper getInstance(){
        return Instance.instance;
    }
    private static final class Instance{
        private static final  X5BridgeWebViewHelper instance=new X5BridgeWebViewHelper();
    }
    private X5BridgeWebViewHelper(){

    }
    @Override
    protected void initSelfConfig() {
        try {
            //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
            QbSdk.PreInitCallback preInitCallback = new QbSdk.PreInitCallback() {
                @Override
                public void onCoreInitFinished() {
                    //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。
                    LogUtils.i(TAG, " x5内核已经加载成功");
                }

                @Override
                public void onViewInitFinished(boolean b) {
                    //X5内核初始化完成，true使用x5内核，false使用系统内核
                    LogUtils.i(TAG, "使用：" + (b ? "x5内核" : "系统内核"));
                }
            };
            //x5内核初始化接口
            QbSdk.initX5Environment(appContext, preInitCallback);
            //非wifi网络条件下是否允许下载内核，默认为false。当本地无可用内核会去主动下载内核，会产生24M左右的数据流量，为了节省用户流量，默认
            //只在wifi条件下才会去下载。开发者可以通过调用该接口设置为true,允许用户在非wifi条件下也下载内核
            QbSdk.setDownloadWithoutWifi(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected SingleX5BridgeWebView createWebComponent() {
        return new SingleX5BridgeWebView(appContext);
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
            Class<?> BridgeWebViewClass = webComponent.getClass();
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
    protected void clearHistory(boolean result) {
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


    private static class PreloadHelperImpl extends PreloadHelper<WebResourceResponse> {
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
     * 替换成x5内核中的WebView
     */
    @SuppressLint({"SetJavaScriptEnabled"})
    public static class SingleX5BridgeWebView extends WebView implements WebViewJavascriptBridge {
        private final String TAG = "X5BridgeWebView";
        public static final String toLoadJs = "WebViewJavascriptBridge.js";
        Map<String, CallBackFunction> responseCallbacks = new HashMap();
        Map<String, BridgeHandler> messageHandlers = new HashMap();
        BridgeHandler defaultHandler = new DefaultHandler();
        private List<Message> startupMessage = new ArrayList();
        private long uniqueId = 0L;

        public List<Message> getStartupMessage() {
            return this.startupMessage;
        }

        public void setStartupMessage(List<Message> startupMessage) {
            this.startupMessage = startupMessage;
        }

        public SingleX5BridgeWebView(Context context) {
            super(context);
            init();
        }

        public SingleX5BridgeWebView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            init();
        }

        public void setDefaultHandler(BridgeHandler handler) {
            this.defaultHandler = handler;
        }

        private void init() {
            this.setVerticalScrollBarEnabled(false);
            this.setHorizontalScrollBarEnabled(false);
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
            if (Build.VERSION.SDK_INT >= 19) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            //设置默认处理器
            setDefaultHandler(new DefaultHandler());
            //设置默认的WebViewClient
            this.setWebViewClient(this.generateBridgeWebViewClient());
        }

        protected SingleX5BridgeWebViewClient generateBridgeWebViewClient() {
            return new SingleX5BridgeWebViewClient(this);
        }

        void handlerReturnData(String url) {
            String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
            CallBackFunction f = responseCallbacks.get(functionName);
            String data = BridgeUtil.getDataFromReturnUrl(url);
            if (f != null) {
                f.onCallBack(data);
                this.responseCallbacks.remove(functionName);
            }
        }

        public void send(String data) {
            this.send(data, (CallBackFunction) null);
        }

        public void send(String data, CallBackFunction responseCallback) {
            this.doSend((String) null, data, responseCallback);
        }

        private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
            Message m = new Message();
            if (!TextUtils.isEmpty(data)) {
                m.setData(data);
            }
            if (responseCallback != null) {
                String callbackStr = String.format("JAVA_CB_%s", ++this.uniqueId + "_" + SystemClock.currentThreadTimeMillis());
                this.responseCallbacks.put(callbackStr, responseCallback);
                m.setCallbackId(callbackStr);
            }
            if (!TextUtils.isEmpty(handlerName)) {
                m.setHandlerName(handlerName);
            }
            this.queueMessage(m);
        }

        private void queueMessage(Message m) {
            if (this.startupMessage != null) {
                this.startupMessage.add(m);
            } else {
                this.dispatchMessage(m);
            }

        }

        void dispatchMessage(Message m) {
            String messageJson = m.toJson();
            messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
            messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
            messageJson = messageJson.replaceAll("(?<=[^\\\\])(')", "\\\\'");
            messageJson = messageJson.replaceAll("%7B", URLEncoder.encode("%7B"));
            messageJson = messageJson.replaceAll("%7D", URLEncoder.encode("%7D"));
            messageJson = messageJson.replaceAll("%22", URLEncoder.encode("%22"));
            String javascriptCommand = String.format("javascript:WebViewJavascriptBridge._handleMessageFromNative('%s');", messageJson);
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                this.loadUrl(javascriptCommand);
            }
        }

        void flushMessageQueue() {
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                this.loadUrl("javascript:WebViewJavascriptBridge._fetchQueue();", new CallBackFunction() {
                    public void onCallBack(String data) {
                        List list = null;

                        try {
                            list = Message.toArrayList(data);
                        } catch (Exception var9) {
                            var9.printStackTrace();
                            return;
                        }

                        if (list != null && list.size() != 0) {
                            for (int i = 0; i < list.size(); ++i) {
                                Message m = (Message) list.get(i);
                                String responseId = m.getResponseId();
                                CallBackFunction responseFunction;
                                final String callbackId;
                                if (!TextUtils.isEmpty(responseId)) {
                                    responseFunction = responseCallbacks.get(responseId);
                                    callbackId = m.getResponseData();
                                    responseFunction.onCallBack(callbackId);
                                    SingleX5BridgeWebView.this.responseCallbacks.remove(responseId);
                                } else {
                                    responseFunction = null;
                                    callbackId = m.getCallbackId();
                                    if (!TextUtils.isEmpty(callbackId)) {
                                        responseFunction = new CallBackFunction() {
                                            public void onCallBack(String data) {
                                                Message responseMsg = new Message();
                                                responseMsg.setResponseId(callbackId);
                                                responseMsg.setResponseData(data);
                                                queueMessage(responseMsg);
                                            }
                                        };
                                    } else {
                                        responseFunction = new CallBackFunction() {
                                            public void onCallBack(String data) {
                                            }
                                        };
                                    }

                                    BridgeHandler handler;
                                    if (!TextUtils.isEmpty(m.getHandlerName())) {
                                        handler = messageHandlers.get(m.getHandlerName());
                                    } else {
                                        handler = defaultHandler;
                                    }

                                    if (handler != null) {
                                        handler.handler(m.getData(), responseFunction);
                                    }
                                }
                            }

                        }
                    }
                });
            }
        }
        public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
            this.loadUrl(jsUrl);
            this.responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
        }
        public void registerHandler(String handlerName, BridgeHandler handler) {
            if (handler != null) {
                this.messageHandlers.put(handlerName, handler);
            }
        }
        public void unregisterHandler(String handlerName) {
            if (handlerName != null) {
                this.messageHandlers.remove(handlerName);
            }
        }
        public void callHandler(String handlerName, String data, CallBackFunction callBack) {
            this.doSend(handlerName, data, callBack);
        }

        public  void setSingleX5BridgeWebViewClient(SingleX5BridgeWebViewClient webViewClient){
            setWebViewClient(webViewClient);
        }
    }

    /**
     * 替换成x5内核中的WebViewClient
     */
    public static class SingleX5BridgeWebViewClient extends WebViewClient {
        private SingleX5BridgeWebView webView;
        private X5BridgeWebViewHelper helper;
        public SingleX5BridgeWebViewClient(SingleX5BridgeWebView webView) {
        this(webView,null);
        }
        public SingleX5BridgeWebViewClient(SingleX5BridgeWebView webView,String filePath) {
            this.webView = webView;
            helper=Instance.instance;
            helper.loadResource(filePath);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException var4) {
                var4.printStackTrace();
            }
            if (url.startsWith("yy://return/")) {
                this.webView.handlerReturnData(url);
                return true;
            } else if (url.startsWith("yy://")) {
                this.webView.flushMessageQueue();
                return true;
            } else {
                return this.onCustomShouldOverrideUrlLoading(url) ? true : super.shouldOverrideUrlLoading(view, url);
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

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (SingleX5BridgeWebView.toLoadJs != null) {
                String jsContent = BridgeUtil.assetFile2Str(view.getContext(), SingleX5BridgeWebView.toLoadJs);
                view.loadUrl(BridgeUtil.JAVASCRIPT_STR + jsContent);
            }
            if (this.webView.getStartupMessage() != null) {
                Iterator var3 = this.webView.getStartupMessage().iterator();

                while (var3.hasNext()) {
                    Message m = (Message) var3.next();
                    this.webView.dispatchMessage(m);
                }

                this.webView.setStartupMessage((List) null);
            }

            this.onCustomPageFinished(view, url);
        }

        protected boolean onCustomShouldOverrideUrlLoading(String url) {
            return false;
        }

        protected void onCustomPageFinished(WebView view, String url) {
        }
    }

}
