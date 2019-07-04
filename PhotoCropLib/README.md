**前言**
>android 自身提供了调用相机拍照，图库获取图片，裁剪图片的功能，但不同系统API存在版本差异。因此，针对这些情况，适配和结合各种常见业务，封装出PhotoCrop库。

**处理的若干问题**：

- android 6.0以上的读写权限问题
- android7.0 FileProvider问题，相机拍照带Uri和裁剪带Uri问题
- 华为手机图库获取不到图片问题
- 部分手机，拍照图片旋转问题
- 异步加载，按照Volley中图片压缩算法，避免ORM问题。

**PhotoCrop库使用介绍**
--

在项目builde.gradle中添加依赖：
```java
compile 'com.xingen:PhotoCrop:1.0.0'
```
**1.初始化**：
```
  TaskExecutor.getInstance().init(this);
```
**2. 在Activity或者Fragment中请求读写权限**：

先检查权限，返回权限结果若是没有权限会自动请求。
```
boolean result = TaskExecutor.getInstance().requestWritePermission(this);

```

在`onRequestPermissionsResult()`中处理权限结果：
```
  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        TaskExecutor.getInstance().handleWritePermission(requestCode, permissions, grantResults, result -> {
            Toast.makeText(getApplicationContext(), "读写权限" + (result ? "授权成功" : "被拒绝"), Toast.LENGTH_SHORT).show();
            
            //若是权限请求成功，进行拍照，图库，裁剪等其他业务操作
        });
    }
```

**3.1 进行拍照**
```
  TaskExecutor.getInstance().execute(this, Task.createCameraTask(FileUtils.createPhotoPath(this)));
```

**3.2 打开图库获取图片**
```
   TaskExecutor.getInstance().execute(this, Task.createGalleryTask());
```

**3.3 裁剪图片**
```
  private void  cropTest(Uri uri){
        TaskExecutor.getInstance().execute(this, Task.createCropTask(uri, 100, 100, FileUtils.createPhotoPath(this)));
  }
```

因拍照，图库，裁剪都是调用系统app完成，因此需要在`onActivityResult()`处理返回结果：
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        Task task = TaskExecutor.getInstance().queryTask(requestCode, resultCode, data);
        if (task!=null&& task.isSuccess()) {
            if (task.getType() == Task.Type.gallery) {
                //获取图库中相片成功，进行加载图片UI操作
            }else if (task.getType()== Task.Type.camera){
               //拍照成功，进行加载图片UI操作
            }
        }
        
    }
```


**4. 异步获取图片，采用Volley中压缩算法，避免ORM问题**：
```
   if (task != null && task.isSuccess()) {//加载裁剪后的图片
            TaskExecutor.getInstance().asynLoadBitmap(task.getFilePath(), imageView.getWidth(), imageView.getHeight(), (String filePath, Bitmap bitmap) -> {
                Log.i(TAG, " 加载的图片路径是： " + filePath);
                imageView.setImageBitmap(bitmap);
            });
        }
```
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