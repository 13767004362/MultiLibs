package com.xingen.app;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xingen.systemdonwload.dialog.NotCancelDialog;

/**
 * @author HeXinGen
 * @date 2018/12/14.
 */
public class ProgressFragmentDialog extends NotCancelDialog {
    public static final String TAG = ProgressFragmentDialog.class.getSimpleName();
    private ProgressBar progressBar;
    private TextView progress_tv;
    public static ProgressFragmentDialog newInstance() {
        return new ProgressFragmentDialog();
    }
    @Override
    public int getLayoutId() {
        return R.layout.dialog_progress;
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        this.progressBar = findViewById(R.id.download_progress);
        this.progress_tv = findViewById(R.id.download_progress_tv);
    }
    public void setProgress(int progress){
        if (progressBar==null) return;
        this.progressBar.setProgress(progress);
        this.progress_tv.setText(progress+"%");
    }
}
