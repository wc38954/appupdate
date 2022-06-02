package com.ynce.code.appupdata.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.ynce.code.appupdata.R;


public class ProgressDialog extends XBaseDialog {
    private final Builder builder;
    private ProgressBar pbDownload;
    private ProgressBar pbRotation;
    private TextView tvProgress;
    private TextView tvTips;
    private int max = 100;

    public ProgressDialog(@NonNull Context context, Lifecycle lifecycle, @NonNull Builder builder) {
        super(context, lifecycle);
        this.builder = builder;
    }

    public void setLoading(String text,int max){
        pbDownload.setVisibility(View.VISIBLE);
        tvProgress.setVisibility(View.VISIBLE);
        pbRotation.setVisibility(View.GONE);
        this.max = max;
        tvTips.setText(text);
        pbDownload.setMax(max);
    }

    @SuppressLint("SetTextI18n")
    public void setProgress(int progress) {
        pbDownload.setProgress(progress);
        tvProgress.setText(progress + "/" + max);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.dialog_download_progress, null);
        setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            //获得窗体的属性
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            //将属性设置给窗体
            dialogWindow.setAttributes(lp);
        }
        setCancelable(true);
        pbDownload = view.findViewById(R.id.pb_download);
        pbRotation = view.findViewById(R.id.pb_rotation);
        tvProgress = view.findViewById(R.id.tv_progress);
        tvTips = view.findViewById(R.id.tv_tips);

        initView();
    }

    private void initView(){
        pbDownload.setVisibility(View.GONE);
        tvProgress.setVisibility(View.GONE);
        pbRotation.setVisibility(View.VISIBLE);
        tvTips.setText(builder.content);
    }

    public static class Builder {
        private final Context context;
        private Lifecycle lifecycle;
        private String content;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder bindLifecycle(Lifecycle lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }
        public ProgressDialog build() {
            return new ProgressDialog(context,lifecycle, this);
        }
    }

}
