package com.xingen.x5bridgehelper.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author HeXinGen
 * date 2019/1/31.
 */
public class FileUtils {

    /**
     * 获取文件夹下的全部文件。
     *
     * @param dir
     * @param filePathList 用于存储文件路径的list
     */
    public static void queryFilesPath(String dir, List<String> filePathList) {
        try {
            File dirFile = new File(dir);
            if (dirFile != null && dirFile.exists()) {
                if (dirFile.isDirectory()) {
                    //遍历dir下的文件和目录，放在File数组中
                    File[] fileArray = dirFile.listFiles();
                    if (fileArray == null && fileArray.length == 0) {
                        //文件夹不存在或者为空
                    } else {
                        for (File file : fileArray) {
                            if (file.isDirectory()) {// 文件夹
                                queryFilesPath(file.getAbsolutePath(), filePathList);
                            } else {
                                filePathList.add(file.getAbsolutePath());
                            }
                        }
                    }
                } else {//该路径是一个文件，而不是文件夹，防止传入错误。
                    filePathList.add(dirFile.getAbsolutePath());
                }
            } else {
                //文件不存在
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝数据到文件中
     *
     * @param inputStream
     * @param existFilePath 存在文件的路径
     */
    public static void writeToFile(InputStream inputStream, String existFilePath) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(new File(existFilePath));
            byte[] bytes = new byte[1024];
            int i;
            while ((i = inputStream.read(bytes)) != -1)
                out.write(bytes, 0, i);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
