package com.lu.tool.http.cookie;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Author: luqihua
 * Time: 2017/6/12
 * Description: CookieJarImp
 */

public class CookieJarImp implements CookieJar {

    private PersistentCookieStore cookieStore;

    public CookieJarImp(Context context) {
        this.cookieStore = new PersistentCookieStore(context.getApplicationContext());
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        cookieStore.add(url, cookies);
    }

    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        return cookieStore.get(url);
    }
}
