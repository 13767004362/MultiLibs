package com.xingen.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.xingen.x5bridgehelper.internal.WebViewHelper;
import com.xingen.x5bridgehelper.internal.X5BridgeWebViewHelper;


/**
 * @author HeXinGen
 * date 2019/1/31.
 */
public class X5BridgeWebViewActivity extends AppCompatActivity {
    private ProgressBar loadingProgress;

    private X5BridgeWebViewHelper.SingleX5BridgeWebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initConfig();
        initView();
    }

    private void initConfig() {
        WebViewHelper.initX5Bridge(this, true);
    }

    private void initView() {
        loadingProgress = findViewById(R.id.loading_progress);
        LinearLayout rootLayout = findViewById(R.id.activity_web_view_content_layout);
        webView = X5BridgeWebViewHelper.getInstance().bind();
        rootLayout.addView(webView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        webView.setWebChromeClient(new MyWebChromeClient());
        //加载本地的html
        final String url = "file:///android_asset/" + "test.html";
        webView.loadUrl(url);
        JsCallJava();
        webView.setSingleX5BridgeWebViewClient(new X5BridgeWebViewHelper.SingleX5BridgeWebViewClient(webView) {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                JavaCallJS();
            }
        });
    }
    private void JavaCallJS() {
        //java调用js
        final String params="X5BridgeLib ,JavaCallJS() ";
        webView.callHandler("functionInJs",params, (String s) ->{

        });
    }

    private void JsCallJava() {
        //Js端调用Android原生
        webView.registerHandler("submitFromWeb", (String s, CallBackFunction callBackFunction) -> {
            Toast.makeText(this.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            String response = " X5BridgeLib ,JsCallJava() ";
            callBackFunction.onCallBack(response);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        X5BridgeWebViewHelper.getInstance().unbind();
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView webView, String s) {
            super.onReceivedTitle(webView, s);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                //加载完毕进度条消失
                loadingProgress.setVisibility(View.GONE);
            } else {
                //更新进度
                loadingProgress.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }


    public static void openActivity(Context context) {
        Intent intent = new Intent(context, X5BridgeWebViewActivity.class);
        context.startActivity(intent);
    }
}
