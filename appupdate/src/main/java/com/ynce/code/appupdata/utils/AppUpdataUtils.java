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

import androidx.core.content.FileProvider;

import java.io.File;

public class AppUpdataUtils {

    private final Context context;

    public AppUpdataUtils(Context context){
        this.context = context;
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
        String filepath = getApkCacheDir();
        XDownloadUtils.get().download(url, filepath, "newApp.apk",showProgress,context, this::installApk);
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


}
