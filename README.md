使用方法:  
            new AppUpdataUtils.Builder(context,url)  
                    .onContent("有新版本APP下载")//提示内容  
                    .onShowProgress(false)//是否显示下载进度条  
                    .onForced(false)//是否强制更新  
                    .build();  