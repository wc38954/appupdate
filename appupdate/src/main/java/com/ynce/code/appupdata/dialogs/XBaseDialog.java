package com.ynce.code.appupdata.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.ynce.code.appupdata.R;

public class XBaseDialog extends Dialog {
    protected Context context;
    protected View view;
    protected Lifecycle lifecycle;

    public XBaseDialog(@NonNull Context context) {
        super(context, R.style.DialogBgTranslucentStyle);
        this.context = context;
    }

    public XBaseDialog(@NonNull Context context, Lifecycle lifecycle) {
        super(context, R.style.DialogBgTranslucentStyle);
        this.context = context;
        this.lifecycle = lifecycle;
        bindToLifecycle(lifecycle);
    }

    public XBaseDialog(@NonNull Context context, @StyleRes int themeResId, Lifecycle lifecycle) {
        super(context, themeResId);
        this.context = context;
        this.lifecycle = lifecycle;
        bindToLifecycle(lifecycle);
    }
    /**
     * 绑定生命周期
     *
     * @param lifecycle Lifecycle
     */
    public void bindToLifecycle(Lifecycle lifecycle) {
        if (lifecycle == null) return;
        lifecycle.addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                if (isShowing()) dismiss();
            }
        });
    }
}
