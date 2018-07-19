package com.lu.tool.http;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lu.tool.http.cookie.CookieJarImp;
import com.lu.tool.http.core.CustomerConverterFactory;
import com.lu.tool.http.core.OkCache;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by lqh on 2016/7/26.
 */
public class RetrofitUtil {
    private static final int TIME_OUT = 10;
    private static OkHttpClient sOkHttpClient;

    /**
     * 初始化  OkHttpClient.Builder
     */
    public static void init(Context context) {
        sOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .cookieJar(new CookieJarImp(context))
                .cache(new OkCache(context).createCache())
                .addInterceptor(loggingInterceptor())
                .build();
    }

    public static void init(OkHttpClient client) {
        sOkHttpClient = client;
    }

    public static OkHttpClient.Builder newClientBuilder() {
        return sOkHttpClient.newBuilder();
    }

    /**
     * 创建一个新的Retrofit.Builder
     *
     * @param baseUrl
     * @return
     */
    public static Retrofit.Builder newBuilder(@NonNull String baseUrl) {
        return new Retrofit.Builder()
                .client(sOkHttpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    /**
     * 创建一个新的Retrofit
     *
     * @param baseUrl
     * @return
     */
    public static Retrofit newRetrofit(@NonNull String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(CustomerConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(sOkHttpClient)
                .build();
    }

    /**
     * 提供logger
     *
     * @return
     */
    private static HttpLoggingInterceptor loggingInterceptor() {
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if (message.contains("{") && message.contains("}")) {
                    Logger.json(message);
                } else if (message.contains("http")) {
                    Logger.d(message);
                }
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY);
    }

}
