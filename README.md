<html lang="en"><head>
    <meta charset="UTF-8">
<body marginheight="0"><h2>引入</h2>
<pre><code class="lang-java">    
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
    implementation 'com.github.wc38954:appupdate:1.0.0'</code></pre>
</pre>
<h2>使用方法</h2>
<pre><code class="lang-java">    
        new AppUpdataUtils.Builder(context,url)//apk下载地址
            .onContent("有新版本APP下载")//提示内容
            .onShowProgress(false)//是否显示下载进度条
            .onForced(false)//是否强制更新
            .build();</code></pre>
</body></html>