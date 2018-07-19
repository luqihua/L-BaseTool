package com.lu.tool.util;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * Author: luqihua
 * Time: 2017/12/22
 * Description: FileUtil
 */

public class FileUtil {
    private static String mAppCacheDir = "";
    private static String rootDir = "tmp";
    private static boolean hasInitialize = false;

    public static void init(Context context) {
        if (!hasInitialize) {
            hasInitialize = true;
            rootDir = context.getPackageName();
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mAppCacheDir = Environment.getExternalStorageDirectory()
                        .getAbsolutePath();
            } else {
                mAppCacheDir = context.getCacheDir().getAbsolutePath();
            }
        }
    }

    /**
     * app external adCard root file path
     *
     * @return
     */
    private static File getAppRootFile() {

        if (!hasInitialize) {
            throw new RuntimeException("please invoke FileStorageUtil.getInstance().init(context) first");
        }
        File file = new File(mAppCacheDir, rootDir);

        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File newFileByName(String fileName) {
        return new File(getAppRootFile(), fileName);
    }

    /**
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        if (!file.exists()) return 0;
        long size = 0;
        if (file.isFile()) {
            size = size + file.length();
        } else {
            for (File childFile : file.listFiles()) {
                size = size + getFileSize(childFile);
            }
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param file
     * @param deleteThisPath
     */
    public static void deleteFile(File file, boolean deleteThisPath) {
        if (!file.exists()) return;
        if (!file.isDirectory()) {
            if (deleteThisPath) {
                file.delete();
            }
        } else {// 如果下面还有文件
            for (File childFile : file.listFiles()) {
                deleteFile(childFile, true);
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 流转成字符串
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String stream2str(InputStream is) throws IOException {
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toString();
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }
}
