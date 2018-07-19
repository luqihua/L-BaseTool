package com.lu.tool.http.core;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Author: luqihua
 * Time: 2017/9/26
 * Description: CustomerConverterFactory
 */

public class CustomerConverterFactory extends Converter.Factory {
    private final Gson gson;

    private CustomerConverterFactory(Gson gson) {
        this.gson = gson;
    }

    public static CustomerConverterFactory create() {
        return new CustomerConverterFactory(new Gson());
    }

    public static CustomerConverterFactory create(Gson gson) {
        return new CustomerConverterFactory(gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Log.d("CustomerConverterFactor", "type:" + type);
        return new GsonResponseBodyConverter<>(gson, type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new GsonRequestBodyConverter<>(gson);//创建请求转换器
    }
}
