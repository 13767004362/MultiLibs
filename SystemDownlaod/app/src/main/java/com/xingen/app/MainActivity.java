package com.xingen.app;


import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xingen.systemdonwload.download.SystemDownload;
import com.xingen.systemdonwload.download.task.DownloadTask;
import com.xingen.systemdonwload.install.InstallUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_down_load_progress_btn).setOnClickListener(this);
        findViewById(R.id.main_down_load_broadcast_btn).setOnClickListener(this);
        //初始化配置
        SystemDownload.getInstance().init(this);
    }

    final String downloadUrl = "http://imtt.dd.qq.com/16891/08637F2F36C0225E9C9BE8EAFE668B59.apk?fsname=com.shoujiduoduo.ringtone_8.7.15.0_60087150.apk";
    final String filePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "shoujiduoduo.apk";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_down_load_progress_btn:
                startProgressDownloadTask(downloadUrl, filePath);
                break;
            case R.id.main_down_load_broadcast_btn:
                startBroadcastDownloadTask(downloadUrl, filePath);
                break;
        }

    }

    protected void startBroadcastDownloadTask(String downloadUrl, String filePath) {
        DownloadTask downloadTask = DownloadTask.create(downloadUrl)
                .setFilePath(filePath)
                .setTitle("铃声多多")
                .setBroadcastListener((DownloadTask task, String url, String path) -> {
                    Log.i(TAG, "广播监听到下载完成" + path + " " + new File(path).exists());
                    Toast.makeText(getApplicationContext(), "广播监听到下载完成", Toast.LENGTH_SHORT).show();
                    InstallUtils.install(getApplicationContext(), path);
                });
        SystemDownload.getInstance().startDownload(downloadTask);
    }

    protected void startProgressDownloadTask(String downloadUrl, String filePath) {
        ProgressFragmentDialog dialog = ProgressFragmentDialog.newInstance();
        DownloadTask downloadTask = DownloadTask.create(downloadUrl)
                .setFilePath(filePath)
                .setHideNotification()
                .setProgressListener((DownloadTask task, String url, int progress) -> {
                    Log.i(TAG, "下载进度 " + progress);
                    dialog.setProgress(progress);
                })
                .setDownloadListener(new SystemDownload.DownloadListener() {
                    @Override
                    public void downloadStart(DownloadTask downloadTask, String url) {
                        Log.i(TAG, "下载开始");
                        if (!dialog.isAdded()) {
                            dialog.show(getSupportFragmentManager(), ProgressFragmentDialog.TAG);
                        }
                    }
                    @Override
                    public void downloadError(DownloadTask downloadTask, String url, Exception e) {
                        Log.i(TAG, "下载异常 " + e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void downloadFinish(DownloadTask downloadTask, String url, String filePath) {
                        Log.i(TAG, "下载完成  " + filePath + " " + new File(filePath).exists());
                        dialog.dismiss();
                        InstallUtils.install(getApplicationContext(), filePath);
                    }
                });
        SystemDownload.getInstance().startDownload(downloadTask);
    }

}
