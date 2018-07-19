package com.lu.tool.http.core;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import okhttp3.Cache;

/**
 * Author: luqihua
 * Time: 2017/8/3
 * Description: OkCache
 */

public class OkCache {

    private static final long DEFAULT_CACHE_SIZE = 20 * 1024 * 1024;
    private static final String DEFAULT_CACHE_DIR = "okcache";


    private Context mContext;
    private String mCacheDirs;
    private long mCacheSize;

    public OkCache(Context context) {
        this(context, null);
    }

    public OkCache(Context context, String cacheDirs) {
        this(context, cacheDirs, DEFAULT_CACHE_SIZE);
    }

    public OkCache(Context context, String cacheDirs, long cacheSize) {
        if (context == null)
            throw new RuntimeException("OkCache: context must not be null");
        this.mContext = context;
        this.mCacheDirs = cacheDirs;
        this.mCacheSize = cacheSize;
    }

    /**
     * default cache config
     *
     * @return
     */
    public Cache createCache() {
        File file = null;

        if (mCacheDirs != null && mCacheDirs.length() > 0) {
            file = new File(mCacheDirs);
        } else {
            /*默认的缓存相对路径为  com.xxx.xx/okcache    */
            final String cachePath = mContext.getPackageName() + File.separator + DEFAULT_CACHE_DIR;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                file = new File(Environment.getExternalStorageDirectory(), cachePath);
            } else {
                file = new File(mContext.getExternalCacheDir(), cachePath);
            }
        }

        if (!file.exists()) {
            boolean success = file.mkdirs();
            if (!success) return null;
        }

        return new Cache(file, mCacheSize);
    }

}
