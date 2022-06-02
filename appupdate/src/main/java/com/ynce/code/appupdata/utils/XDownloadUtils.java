package com.ynce.code.appupdata.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ynce.code.appupdata.dialogs.ProgressDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class XDownloadUtils {
    private static XDownloadUtils downloadUtil;
    private final OkHttpClient okHttpClient;
    private Disposable d;
    private boolean res = false;
    private int lastProgress = 0;
    private boolean showProgress;

    public static XDownloadUtils get() {
        if (downloadUtil == null) {
            downloadUtil = new XDownloadUtils();
        }
        return downloadUtil;
    }

    private XDownloadUtils() {
        okHttpClient = new OkHttpClient();
    }

    private ProgressDialog progressDialog;

    private void showProgressDialog(Context context){
        if (!showProgress) return;
        if (progressDialog==null || lastProgress == -1){
            progressDialog = new ProgressDialog.Builder(context).content("加载中").build();
        }
        progressDialog.show();
        progressDialog.setLoading("安装包下载中",100);
        progressDialog.setProgress(lastProgress==-1?0:lastProgress);
    }

    private void setProgress(int progress){
        if (!showProgress) return;
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.setProgress(progress);
        }
    }

    private void dimissProgressDialog(){
        lastProgress = 0;
        if (!showProgress) return;
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     * url 下载连接
     * saveDir 储存下载文件的SDCard目录
     * listener 下载监听
     */
    private String context;
    public void download(final String url, final String saveDir, final String filename, final boolean showProgress, Context context, final OnDownloadListener listener) {
        if (!Objects.equals(this.context, context.toString())){
            this.context = context.toString();
            lastProgress = -1;
        }
        this.showProgress = showProgress;
        showProgressDialog(context);
        if (lastProgress>0&&lastProgress<100){
            return;
        }
        Request request;
        try {
            request = new Request.Builder().url(url).build();
        } catch (Exception e){
            Toast.makeText(context, "下载地址错误", Toast.LENGTH_SHORT).show();
            dimissProgressDialog();
            return;
        }
        if (d != null && !d.isDisposed()) d.dispose();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 下载失败
                d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> {
                    Toast.makeText(context, "下载失败" + e, Toast.LENGTH_SHORT).show();
                    dimissProgressDialog();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                File file = new File(savePath, filename);
                try {
                    ResponseBody body = response.body();
                    if (body == null){
                        d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> dimissProgressDialog());
                        return;
                    }
                    long total = body.contentLength();
                    if (total>0 && file.length() == total){
                        d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> {
                            dimissProgressDialog();
                            listener.onDownloadSuccess(file);
                        });
                        return;
                    } else {
                        res = file.delete();
                    }
                    is = body.byteStream();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    Thread.sleep(100);//保证重新开始时，上一次的下载已经被干掉
                    lastProgress = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        if (lastProgress==-1){
                            return;
                        }
                        if (lastProgress!=progress){
                            lastProgress = progress;
                            // 下载中
                            d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> setProgress(progress));
                        }
                    }
                    fos.flush();
                    // 下载完成
                    d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> {
                        dimissProgressDialog();
                        if (total>0 && file.length() == total){
                            listener.onDownloadSuccess(file);
                        } else {
                            Toast.makeText(context, "下载文件错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> {
                        Toast.makeText(context, "下载出错" + e, Toast.LENGTH_SHORT).show();
                        dimissProgressDialog();
                    });
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException ignored) { }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException ignored) { }
                }
            }
        });
    }

    /**
     * saveDir
     * 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            res = downloadFile.createNewFile();
        }
        return downloadFile.getAbsolutePath();
    }


    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(File file);
    }

}
