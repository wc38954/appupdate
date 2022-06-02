package com.ynce.code.appupdata.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.ynce.code.appupdata.R;
import com.ynce.code.appupdata.utils.AppUpdataUtils;

public class UpDateDialog extends XBaseDialog {
    private final Builder builder;
    private final AppUpdataUtils updataUtils;
    private TextView tvContent;
    private TextView tvPositive;
    private TextView tvNegative;

    private UpDateDialog(@NonNull Context context ,Lifecycle lifecycle, @NonNull Builder builder) {
        super(context, lifecycle);
        this.builder = builder;
        updataUtils = new AppUpdataUtils(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(context, R.layout.dialog_x_update, null);
        setContentView(view);
        tvContent = view.findViewById(R.id.tv_content);
        tvNegative = view.findViewById(R.id.tv_negative);
        tvPositive = view.findViewById(R.id.tv_positive);
        initView();
    }


    private void initView() {
        if (!TextUtils.isEmpty(builder.content)) tvContent.setText(builder.content);
        if (builder.forced){
            tvNegative.setVisibility(View.GONE);
        } else {
            tvNegative.setOnClickListener(v -> dismiss());
        }

        tvPositive.setOnClickListener(v -> {
            dismiss();
            updataUtils.DownLoadApk(builder.url, builder.showProgress);
        });
        setCancelable(false);
    }

    public static class Builder {
        private final Context context;
        private final String url;//下载地址
        private Lifecycle lifecycle;
        private String content;
        private boolean showProgress = true;//是否显示下载进度条
        private boolean forced;

        public Builder(@NonNull Context context,@NonNull String url) {
            this.context = context;
            this.url = url;
        }

        public Builder bindLifecycle(Lifecycle lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        public Builder onContent(String content) {
            this.content = content;
            return this;
        }

        public Builder onForced(boolean forced) {
            this.forced = forced;
            return this;
        }
        public Builder onShowProgress(boolean showProgress) {
            this.showProgress = showProgress;
            return this;
        }

        public UpDateDialog build() {
            return new UpDateDialog(context,lifecycle, this);
        }

    }

}
