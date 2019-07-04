package com.xingen.systemutils.stream;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author HeXinGen
 * date 2018/12/24.
 * <p>
 * IO stream 工具类
 */
public class StreamUtils {

    /**
     * file转成byte
     *
     * @param file
     * @return
     */
    public static byte[] fileToByteArray(File file) {
        byte[] bytes = null;
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            FileChannel fileChannel = fileInputStream.getChannel();
            byteArrayOutputStream = new ByteArrayOutputStream();
            WritableByteChannel writableByteChannel = Channels.newChannel(byteArrayOutputStream);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                int i = fileChannel.read(buffer);
                if (i == 0 || i == -1) {
                    break;
                }
                buffer.flip();
                writableByteChannel.write(buffer);
                buffer.clear();
            }
            bytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            bytes = null;
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e2) {
            }
        }
        return bytes;
    }
    /**
     * 将stream转成String
     *
     * @param inputStream
     * @return
     */
    public static String streamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        String result = null;
        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int length;
            while ((length = bufferedInputStream.read(b)) > 0) {
                byteArrayOutputStream.write(b, 0, length);
            }
            result = byteArrayOutputStream.toString("utf-8");
        }
        finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
