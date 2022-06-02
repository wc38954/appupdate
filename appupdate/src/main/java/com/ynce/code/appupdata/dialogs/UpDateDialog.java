package com.ynce.code.appupdata.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.ynce.code.appupdata.R;

public class UpDateDialog extends XBaseDialog {
    private final Builder builder;
    private TextView tvContent;
    private TextView tvPositive;
    private TextView tvNegative;

    private UpDateDialog(@NonNull Context context ,Lifecycle lifecycle, @NonNull Builder builder) {
        super(context, lifecycle);
        this.builder = builder;
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
        if (!builder.showCancel){
            tvNegative.setVisibility(View.GONE);
        } else {
            tvNegative.setOnClickListener(v -> dismiss());
        }

        tvPositive.setOnClickListener(v -> {
            dismiss();
            if (builder.onPositive != null)
                builder.onPositive.onXClickListener(UpDateDialog.this);
        });
        setCancelable(false);
    }

    public static class Builder {
        private final Context context;
        private Lifecycle lifecycle;
        private String content;
        private XClickListener onPositive;
        private boolean showCancel = true;

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

        public Builder showCancel(boolean showCancel) {
            this.showCancel = showCancel;
            return this;
        }

        public Builder onPositive(XClickListener onPositive) {
            this.onPositive = onPositive;
            return this;
        }
        public UpDateDialog build() {
            return new UpDateDialog(context,lifecycle, this);
        }

        public void show() {
            UpDateDialog dialog = build();
            dialog.show();
        }
    }

    public interface XClickListener {
        void onXClickListener(UpDateDialog dialog);
    }
}
