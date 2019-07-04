package com.xingen.systemutils.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.xingen.systemutils.bitmap.scale.VolleyScaleUtils;

/**
 * @author HeXinGen
 * date 2018/12/24.
 */
public class BitmapUtils {
    /**
     *  同步锁，防止同一时刻，多线程生成bitmap，从而导致内存溢出
     */
    private final static Object lock = new Object();

    /**
     *  从File中生成Bitmap
     * @param filePath
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public  static Bitmap decodeFile(String filePath, int targetWidth, int targetHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        final int actualHeight = options.outHeight;
        final int actualWidth = options.outWidth;
        options.inSampleSize = VolleyScaleUtils.calculateBitmapScaleValue(targetWidth, targetHeight, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        //防止同时解压多个，造成内存溢出
        synchronized (lock) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            return FileRotateUtils.repairBitmapRotate(bitmap, filePath);
        }
    }



    /**
     * 用于处理某些手机拍照后，图片旋转问题
     */
    private final static class FileRotateUtils {
        /**
         * 修复某些图片旋转问题，从而调整
         * 这种情况，存在于某些手机拍照，生成反向的图片
         *
         * @param bitmap
         * @param path
         * @return
         */
        private static Bitmap repairBitmapRotate(Bitmap bitmap, String path) {
            int rotate = getBitmapRotate(path);
            Bitmap normalBitmap;
            switch (rotate) {
                case 90:
                case 180:
                case 270:
                    try {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(rotate);
                        normalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        normalBitmap = bitmap;
                    }
                    break;
                default:
                    normalBitmap = bitmap;
                    break;
            }
            return normalBitmap;
        }

        /**
         * ExifInterface ：这个类为jpeg文件记录一些image 的标记
         * 这里，获取图片的旋转角度
         * <p>
         * Exif可以附加于JPEG、TIFF、RIFF等文件之中, PNG，WebP这类的图片就不会有这些数据。
         *
         * @param path
         * @return
         */
        private static int getBitmapRotate(String path) {
            int degree = 0;
            try {
                if (path.contains(".jpeg") || path.contains(".JPEG")) {
                    ExifInterface exifInterface = new ExifInterface(path);
                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            degree = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            degree = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            degree = 270;
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return degree;
        }
    }

}
