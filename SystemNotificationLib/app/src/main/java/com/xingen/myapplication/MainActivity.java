package com.xingen.myapplication;

import android.app.Notification;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xingen.systemnotification.SystemNotification;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SystemNotification.getInstance().init(this);
        initView();
    }

    private TextView textView;

    private void initView() {
        textView = findViewById(R.id.main_send_message_btn);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == textView) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("SystemNotification测试");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentText("SystemNotification测试内容");
            SystemNotification.getInstance().showNotification(111, builder.build());
        }

    }

}
