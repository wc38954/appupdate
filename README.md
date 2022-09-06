<html lang="en"><head>
    <meta charset="UTF-8">
<body marginheight="0"><h2>引入</h2>
<pre><code class="lang-java">    
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
    implementation 'com.github.wc38954:appupdate:1.0.4'</code></pre>
</pre>
<h2>使用方法</h2>
<pre><code class="lang-java">
    private UpDateDialog upDateDialog;
            if (upDateDialog == null){
                upDateDialog =  new UpDateDialog.Builder(this,"http://47.106.9.55:80/photo/app-release.apk")//app下载地址
                        .onContent("有新版本APP下载")//提示内容
                        .onShowProgress(true)//是否显示下载进度条
                        .onForced(false)//是否强制更新
                        .build();
            }
            upDateDialog.show();
</code></pre>
</body></html>