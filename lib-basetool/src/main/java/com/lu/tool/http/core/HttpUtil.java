package com.lu.tool.http.core;

import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Author: luqihua
 * Time: 2017/11/6
 * Description: FileUploadUtil
 */

public class HttpUtil {
    /**
     * 根据文件后缀名回去mime类型
     *
     * @param file
     * @return
     */
    public static String getMimeTypeFromFile(File file) {
        /*默认是txt文件*/
        String mime = "text/plain";
        int dotIndex = file.getName().lastIndexOf(".");
        if (dotIndex > 0) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.getName().substring(dotIndex + 1).toLowerCase());
        }
        return mime;
    }
}
