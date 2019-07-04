package com.xingen.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.xingen.x5bridgehelper.common.FileUtils;
import com.xingen.x5bridgehelper.common.ZipUtils;
import com.xingen.x5bridgehelper.internal.BridgeWebViewHelper;
import com.xingen.x5bridgehelper.internal.WebViewHelper;

import java.io.File;

/**
 * @author HeXinGen
 * date 2019/1/31.
 */
public class SingleBridgeWebViewActivity extends AppCompatActivity {
    private ProgressBar loadingProgress;
    private String assetFile = "H5LocalResource.zip";
    private String dir;
    private BridgeWebViewHelper.SingleBridgeWebView webView;

    

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        queryUnZipDir();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initConfig();
        initView();
    }

    private void initConfig() {
        WebViewHelper.initBridge(this, true);
    }

    private void initView() {
        loadingProgress =  findViewById(R.id.loading_progress);
        LinearLayout rootLayout = findViewById(R.id.activity_web_view_content_layout);
        webView = BridgeWebViewHelper.getInstance().bind();
        rootLayout.addView(webView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        webView.setSingleBridgeWebViewClient(new BridgeWebViewHelper.SingleBridgeWebViewClient(webView,dir){});
        webView.setWebChromeClient(new MyWebChromeClient());
        final String url = "https://www.baidu.com/";
        webView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BridgeWebViewHelper.getInstance().unbind();
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

    private void queryUnZipDir() {

        File dirFile = new File(getFilesDir() + File.separator + assetFile.substring(0, assetFile.indexOf(".")));
        try {
            if (!dirFile.exists()) {
                File zipFile = new File(getFilesDir() + File.separator + assetFile);
                if (!zipFile.exists()) {
                    FileUtils.writeToFile(getAssets().open(assetFile), zipFile.getAbsolutePath());
                }
                ZipUtils.unZipFolder(zipFile.getAbsolutePath(), dirFile.getAbsolutePath());
                zipFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dir = dirFile.getAbsolutePath();
    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, SingleBridgeWebViewActivity.class);
        context.startActivity(intent);
    }
}
