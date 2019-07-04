package com.xingen.systemutils.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author HeXinGen
 * date 2018/12/27.
 */
public class GalleryUtils {

    /**
     * 系统相册目录,
     * 6.0以上需要android.permission.WRITE_EXTERNAL_STORAGE
     * @return
     */
    public static String getGalleryRootFilePath(){
        String galleryPath= Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                +File.separator+"Camera"+File.separator;
        return galleryPath;
    }

    /**
     * 通知系统相册,插入新的图片
     * @param context
     * @param filePath
     * @param fileName
     * @throws FileNotFoundException
     */
    public static void notifyGallery(Context context, String filePath, String fileName) throws FileNotFoundException {
        String newUri= MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.parse(newUri);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }


}
