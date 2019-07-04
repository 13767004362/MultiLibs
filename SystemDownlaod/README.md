
**前言**：
>SystemDownload是一个基于系统DwonloadManager的增强库，可以自由设定Notification的显示，查询下载进度，广播监听，具备安装功能。



**SystemDownload库的好处**：

- 自由设定Notification的是否显示
- 异步查询下载进度，主线程监听回调。
- 自由取消下载任务
- 已经内置动态广播监听，监听器回调下载完成通知。
- 处理了7.0 的FileProvider问题
- 处理8.0的安装权限问题

#### **SystemDownload库的 API**：

**SystemDownload类**

一个下载管理类，用于初始化，开始下载，移除下载任务，销毁初始化的api。除吃之外，包含三个监听器。

下载进度的监听器
```

    /**
     * 进度监听器
     */
    public interface  ProgressListener{
        void progress(DownloadTask downloadTask,String url,int progress);
    }
```
下载状态的监听器，监听开始下载，下载完成，下载异常。
```
   /**
     *  下载结果监听器
     */
    public interface  DownloadListener{
        void downloadStart(DownloadTask downloadTask,String url);
        void downloadError(DownloadTask downloadTask,String url,Exception e);
        void downloadFinish(DownloadTask downloadTask,String url,String filePath);
    }
```
监听系统下载完成的监听器。
```
   /**
     * 下载完成的广播监听器
     */
    public interface  BroadcastListener{
        void receiverDownloadFinish(DownloadTask downloadTask,String url,String filePath);
    }
```

**DownloadTask类**

DownloadTask是DownalodManager.Request的子类，新增取消方法,和进度监听器，和广播监听器。


### **前期准备**

项目的gradle的依赖：
```
compile 'com.xingen:SystemDownload:1.0.0'
```
别忘了，还有联网权限` <uses-permission android:name="android.permission.INTERNET" />`。

### **使用指南**

**1.初始化**

在Application或者Activit或者service中进行初始化。
```
   //初始化配置
   SystemDownload.getInstance().init(this);
```

**2. 选择进度监听器**

设置不显示消息栏，异步查询下载状态和进度，主线程回调监听。一般使用在强制版本更新的场景。
```
  protected void startProgressDownloadTask(String downloadUrl, String filePath) {
        ProgressFragmentDialog dialog = ProgressFragmentDialog.newInstance();
        DownloadTask downloadTask = DownloadTask.create(downloadUrl)
                .setFilePath(filePath)
                .setHideNotification()
                .setProgressListener((DownloadTask task, String url, int progress) -> {
                    Log.i(TAG, "下载进度 " + progress);
                    dialog.setProgress(progress);
                })
                .setDownloadListener(new SystemDownload.DownloadListener() {
                    @Override
                    public void downloadStart(DownloadTask downloadTask, String url) {
                        Log.i(TAG, "下载开始");
                        if (!dialog.isAdded()) {
                            dialog.show(getSupportFragmentManager(), ProgressFragmentDialog.TAG);
                        }
                    }
                    @Override
                    public void downloadError(DownloadTask downloadTask, String url, Exception e) {
                        Log.i(TAG, "下载异常 " + e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void downloadFinish(DownloadTask downloadTask, String url, String filePath) {
                        Log.i(TAG, "下载完成  " + filePath + " " + new File(filePath).exists());
                        dialog.dismiss();
                        InstallUtils.install(getApplicationContext(), filePath);
                    }
                });
        SystemDownload.getInstance().startDownload(downloadTask);
    }

```

**3. 选择广播监听**


设置notification消息栏的标题，信息。监听，系统下载完成发送的广播。一般用在自由下载的场景。

```
   protected void startBroadcastDownloadTask(String downloadUrl, String filePath) {
        DownloadTask downloadTask = DownloadTask.create(downloadUrl)
                .setFilePath(filePath)
                .setTitle("铃声多多")
                .setBroadcastListener((DownloadTask task, String url, String path) -> {
                    Log.i(TAG, "广播监听到下载完成" + path + " " + new File(path).exists());
                    Toast.makeText(getApplicationContext(), "广播监听到下载完成", Toast.LENGTH_SHORT).show();
                    InstallUtils.install(getApplicationContext(), path);
                });
        SystemDownload.getInstance().startDownload(downloadTask);
    }
```
**4. 取消下载任务**


调用DownloadTask 的`cancel()`方法，会结束下载任务。

**5. 调用安装，一行搞定7.0,8.0存在的坑。**

传入cotext和安装路径，调用系统安装器进行安装。
```
   InstallUtils.install(getApplicationContext(), filePath);
```

**资源参考**：


- [DownloadManager强制版本更新](https://blog.csdn.net/hexingen/article/details/52276969)
- [Android官方DownloadManage资料](https://developer.android.com/reference/android/app/DownloadManager.html)


License
-------

    Copyright 2018 HeXinGen.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.