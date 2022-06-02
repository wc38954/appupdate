package com.ynce.code.appupdata.utils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static XDownloadUtils get() {
        if (downloadUtil == null) {
            downloadUtil = new XDownloadUtils();
        }
        return downloadUtil;
    }

    private XDownloadUtils() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * url 下载连接
     * saveDir 储存下载文件的SDCard目录
     * listener 下载监听
     */
    public void download(final String url, final String saveDir, final String filename, final OnDownloadListener listener) {
        Request request;
        try {
            request = new Request.Builder().url(url).build();
        } catch (Exception e){
            Log.d("okhttp","地址错误");
            d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> listener.onDownloadFailed());
            return;
        }
        if (d != null && !d.isDisposed()) d.dispose();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 下载失败
                Log.d("okhttp","下载失败:" + e);
                d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> listener.onDownloadFailed());
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
                res = file.delete();
                Log.d("okhttp","savePath: " + savePath + "   res:" + res);
                try {
                    ResponseBody body = response.body();
                    if (body == null){
                        Log.d("okhttp","ResponseBody为空");
                        d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> listener.onDownloadFailed());
                        return;
                    }
                    is = body.byteStream();
                    long total = body.contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> listener.onDownloading(progress));
                    }
                    fos.flush();
                    // 下载完成
                    d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> {
                        if (total>0){
                            listener.onDownloadSuccess(file);
                        } else {
                            Log.d("okhttp","文件为空");
                            listener.onDownloadFailed();
                        }
                    });
                } catch (Exception e) {
                    Log.d("okhttp","下载出错：" + e);
                    d = Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> listener.onDownloadFailed());
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

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }

}
