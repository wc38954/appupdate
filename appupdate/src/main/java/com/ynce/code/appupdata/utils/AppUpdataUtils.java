package com.ynce.code.appupdata.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Lifecycle;

import com.ynce.code.appupdata.dialogs.ProgressDialog;
import com.ynce.code.appupdata.dialogs.UpDateDialog;

import java.io.File;

public class AppUpdataUtils {

    private final Context context;
    private boolean showProgress;


    public AppUpdataUtils(Context context){
        this.context = context;
    }

    public static class Builder {
        private final Context context;
        private final String url;//下载地址
        private Lifecycle lifecycle;
        private String content = "新版本升级";//提示内容
        private boolean showProgress;//是否显示下载进度条
        private boolean forced;//是否强制更新

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
        public Builder onShowProgress(boolean showProgress) {
            this.showProgress = showProgress;
            return this;
        }
        public Builder onForced(boolean forced) {
            this.forced = forced;
            return this;
        }

        public void build() {
            try {
                new UpDateDialog.Builder(context)
                        .bindLifecycle(lifecycle)
                        .content(content)
                        .showCancel(!forced)
                        .onPositive((dialog) -> new AppUpdataUtils(context).DownLoadApk(url, showProgress))
                        .show();
            } catch (Exception ignored){ }

        }
    }

    /**
     * 判断网络是否连接
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isConnected() {
        if (context == null) {
            Log.d("NetworkUtils", "context no init");
            return false;
        }
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
    @SuppressLint("MissingPermission")
    private NetworkInfo getActiveNetworkInfo() {
        if (context == null) {
            Log.d("NetworkUtils", "context no init");
            return null;
        }
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }


    public void DownLoadApk(String url,boolean showProgress){
        if (!isConnected()){
            Toast.makeText(context, "无网络", Toast.LENGTH_SHORT).show();
            return;
        }
        this.showProgress = showProgress;
        showProgressDialog();
        String filepath = getApkCacheDir();
        XDownloadUtils.get().download(url, filepath, "newApp", new XDownloadUtils.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                dimissProgressDialog();
                installApk(file);
            }
            @Override
            public void onDownloading(int progress) {
                setProgress(progress);
            }

            @Override
            public void onDownloadFailed() {
                dimissProgressDialog();
            }
        });
    }
    private void installApk(File file) {
        String authority = context.getPackageName() + ".fileprovider";
        Uri apkUri = FileProvider.getUriForFile(context, authority, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
    private String getApkCacheDir() {
        if (context == null) return "";
        File file = context.getExternalFilesDir("apk");
        if (file == null) return "";
        return file.getAbsolutePath() + File.separator;
    }

    private ProgressDialog progressDialog;

    private void showProgressDialog(){
        if (!showProgress) return;
        if (progressDialog==null){
            progressDialog = new ProgressDialog.Builder(context).content("加载中").build();
        }
        progressDialog.show();
        progressDialog.setLoading("安装包下载中",100);
    }

    private void setProgress(int progress){
        if (!showProgress) return;
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.setProgress(progress);
        }
    }

    private void dimissProgressDialog(){
        if (!showProgress) return;
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
