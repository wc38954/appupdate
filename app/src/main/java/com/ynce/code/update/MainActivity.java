package com.ynce.code.update;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ynce.code.appupdata.dialogs.UpDateDialog;
import com.ynce.code.appupdata.utils.AppUpdataUtils;

public class MainActivity extends AppCompatActivity {

    private UpDateDialog upDateDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvHello = findViewById(R.id.tv_hello);
        //"https://downloads.gradle-dn.com/distributions/gradle-7.5-milestone-1-all.zip"
        //"http://47.106.9.55:80/photo/app-release.apk"
        tvHello.setOnClickListener(view -> {
            if (upDateDialog == null){
                upDateDialog =  new UpDateDialog.Builder(this,"http://47.106.9.55:80/photo/app-release.apk")//app下载地址
                        .onContent("有新版本APP下载")//提示
                        .onShowProgress(true)//是否显示下载进度条
                        .onForced(false)//是否强制更新
                        .build();
            }
            upDateDialog.show();
        });
    }
}