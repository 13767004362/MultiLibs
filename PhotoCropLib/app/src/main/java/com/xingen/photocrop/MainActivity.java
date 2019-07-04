package com.xingen.photocrop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.xingen.photocroplib.Task;
import com.xingen.photocroplib.TaskExecutor;
import com.xingen.photocroplib.internal.listener.BitmapListener;
import com.xingen.photocroplib.internal.utils.FileUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TaskExecutor.getInstance().init(this);
        initView();
        initWritePermission();
    }


    private void initView() {
        imageView = findViewById(R.id.main_show_iv);
        findViewById(R.id.main_camera_btn).setOnClickListener(this);
        findViewById(R.id.main_gallery_btn).setOnClickListener(this);
    }

    private void initWritePermission() {
        boolean result = TaskExecutor.getInstance().requestWritePermission(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_camera_btn:
                TaskExecutor.getInstance().execute(this, Task.createCameraTask(FileUtils.createPhotoPath(this)));
                break;

            case R.id.main_gallery_btn:
                TaskExecutor.getInstance().execute(this, Task.createGalleryTask());
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TaskExecutor.getInstance().handleWritePermission(requestCode, permissions, grantResults, result -> {
            Toast.makeText(getApplicationContext(), "读写权限" + (result ? "授权成功" : "被拒绝"), Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Task task = TaskExecutor.getInstance().queryTask(requestCode, resultCode, data);
        if (task!=null&& task.isSuccess()) {//当图库获取图片或者拍照成功后，进行裁剪操作。
            if (task.getType() == Task.Type.gallery) {
                cropTest(data.getData());
                return;
            }else if (task.getType()== Task.Type.camera){
                  cropTest(Uri.fromFile(new File(task.getFilePath())));
                return;
            }
        }
        if (task != null && task.isSuccess()) {//加载裁剪后的图片
            TaskExecutor.getInstance().asynLoadBitmap(task.getFilePath(), imageView.getWidth(), imageView.getHeight(), (String filePath, Bitmap bitmap) -> {
                Log.i(TAG, " 加载的图片路径是： " + filePath);
                imageView.setImageBitmap(bitmap);
            });
        }
    }

    private void  cropTest(Uri uri){
        TaskExecutor.getInstance().execute(this, Task.createCropTask(uri, 100, 100, FileUtils.createPhotoPath(this)));
    }
}
