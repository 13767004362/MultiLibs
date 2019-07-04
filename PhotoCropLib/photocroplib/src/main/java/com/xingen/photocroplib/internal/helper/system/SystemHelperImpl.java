package com.xingen.photocroplib.internal.helper.system;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.xingen.photocroplib.Task;
import com.xingen.photocroplib.internal.utils.UriUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author HeXinGen
 * date 2018/12/18.
 * <p>
 * 拍照，图库，裁剪的实现类
 */
public class SystemHelperImpl implements SystemHelper {
    private List<Task> photoTaskList;
    private Context context;

    public SystemHelperImpl() {
        this.photoTaskList = new CopyOnWriteArrayList<>();
    }


    @Override
    public void init(Context context) {
        this.context = context;
    }

    @Override
    public void doTask(Object activityOrFragment, Task photoTask) {
        Fragment fragment = null;
        Activity activity = null;
        if (activityOrFragment instanceof Activity) {
            activity = (Activity) activityOrFragment;
        } else {
            fragment = (Fragment) activityOrFragment;
        }
        Intent intent = null;
        switch (photoTask.getType()) {
            case gallery:
                intent = GalleryUtils.createGalleryAction();
                break;
            case camera:
                intent = CameraUtils.createCameraAction(activity != null ? activity : fragment.getActivity(), photoTask.getFilePath());
                break;
            case crop:
                intent = CropUtils.createCropAction(activity != null ? activity : fragment.getActivity(), photoTask.getUri(), photoTask.getOutputX(), photoTask.getOutputY(), photoTask.getFilePath());
                break;
        }
        if (activity != null) {
            activity.startActivityForResult(intent, photoTask.getRequestCode());
        }else {
            fragment.startActivityForResult(intent, photoTask.getRequestCode());
        }
        photoTaskList.add(photoTask);
    }


    @Override
    public Task handleResult(int requestCode, int resultCode, Intent intent) {
        Task photoTask = filterTask(requestCode);
        if (photoTask != null) {
            photoTask.setSuccess(resultCode == Activity.RESULT_OK);
            switch (photoTask.getType()) {
                case crop: {
                }
                break;
                case camera: {
                }
                break;
                case gallery: {
                    if (requestCode==Activity.RESULT_OK){
                        Uri uri = intent.getData();
                        photoTask.setFilePath(GalleryUtils.uriConvertPath(context, uri));
                    }
                }
                break;
            }
        }
        return photoTask;
    }

    protected Task filterTask(int requestCode) {
        if (photoTaskList == null) return null;
        Task task = null;
        for (Task photoTask : photoTaskList) {
            if (photoTask.getRequestCode() == requestCode) {
                task = photoTask;
                photoTaskList.remove(photoTask);
                break;
            }
        }
        return task;
    }

    private static final class CameraUtils {
        public static Intent createCameraAction(Context context, String picturePath) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, UriUtils.getUriFromFile(context, picturePath));
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
            return intent;
        }
    }

    private final static class GalleryUtils {
        public static Intent createGalleryAction() {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            return intent;
        }

        /**
         * 从相册中返回的Uri查询到对应图片的Path
         *
         * @param context
         * @param uri
         * @return
         */
        public static String uriConvertPath(Context context, Uri uri) {
            String path;
            String scheme = uri.getScheme();
            if (scheme.equals("content")) {
                path = getPath(context, uri);
            } else {
                path = uri.getEncodedPath();
            }
            return path;
        }

        /**
         * <br>功能简述:4.4及以上获取图片的方法
         * <br>功能详细描述:
         * <br>注意:
         *
         * @param context
         * @param uri
         * @return
         */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        private static String getPath(final Context context, final Uri uri) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];


                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }


                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};


                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }


                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }

        private static String getDataColumn(Context context, Uri uri, String selection,
                                            String[] selectionArgs) {
            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return null;
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        private static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        private static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        private static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        private static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }
    }

    private static final class CropUtils {
        public static Intent createCropAction(Context context, Uri uri, int outputX, int outputY, String cropFilePath) {
            final String action = "com.android.camera.action.CROP";
            Intent intent = new Intent(action);
            // 7.0以上file协议，替换成content协议
            if (Build.VERSION.SDK_INT>=24&&uri.getScheme().contains("file")){
                if (uri.getEncodedPath().contains(context.getPackageName())){
                    Uri newUri=UriUtils.getUriFromFile(context,uri.getEncodedPath());
                    intent.setDataAndType(newUri ,"image/*");
                    UriUtils.grantUriPermission(context,intent,uri);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }else{
                    StringBuffer stringBuffer =new StringBuffer();
                    stringBuffer.append("content：//");
                    if (!TextUtils.isEmpty(uri.getAuthority())){
                           stringBuffer.append(uri.getAuthority());
                           stringBuffer.append("/");
                    }
                    stringBuffer.append(uri.getEncodedPath());
                    Uri newUri=Uri.parse(stringBuffer.toString());
                    intent.setDataAndType(newUri, "image/*");
                    UriUtils.grantUriPermission(context,intent,uri);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }else{
                intent.setDataAndType(uri, "image/*");
            }
            intent.putExtra("crop", "true");
            // 裁剪框的比例，1：1
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // 裁剪后输出图片的尺寸大小
            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
            // 图片格式
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
            // 取消人脸识别
            intent.putExtra("noFaceDetection", true);
            if (Build.VERSION.SDK_INT >= 24) {
                Uri saveFileUri = UriUtils.getUriFromFile(context, cropFilePath);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFileUri);
                UriUtils.grantUriPermission(context, intent, saveFileUri);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                //设置裁剪后文件保存路径
                Uri cropUri = Uri.fromFile(new File(cropFilePath));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
            }
            return intent;
        }
    }
}
