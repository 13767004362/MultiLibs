package com.xingen.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.main_x5_bridge_web_view_btn).setOnClickListener(this);
        findViewById(R.id.main_single_bridge_web_view_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_x5_bridge_web_view_btn: {
                       X5BridgeWebViewActivity.openActivity(this);
            }
            break;
            case R.id.main_single_bridge_web_view_btn:{
                    SingleBridgeWebViewActivity.openActivity(this);
            }
            break;

        }
    }
}
