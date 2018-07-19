package com.lu.tool.http.core;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.lu.tool.http.HttpResult;
import com.lu.tool.http.error.CustomException;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Author: luqihua
 * Time: 2017/9/24
 * Description: GsonResponseBody
 */

public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private Gson gson;
    private Type mType;//返回结果类型


    public GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.mType = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
            //正常解析返回结果，通过catch捕获解析异常
            return gson.fromJson(response, mType);
        } catch (RuntimeException e) {
            //考虑到当结果不正确时
            // 1.响应字符串不是json格式
            // 2.类型不匹配，有时data字段被服务器定义为null，而期望值是数组[]，会发生解析错误
            JSONObject obj = null;
            try {
                obj = new JSONObject(response);

                //解析HttpResult中定义的code和msg属性对应的json解析的别名
                //code可能是：code,status

                Field codeField = HttpResult.class.getDeclaredField("code");
                SerializedName codeSerializedName = codeField.getAnnotation(SerializedName.class);
                Set<String> codeAlias = new HashSet<>();
                codeAlias.add("code");
                codeAlias.add(codeSerializedName.value());
                codeAlias.addAll(Arrays.asList(codeSerializedName.alternate()));
                //msg 可能是：msg,message

                Field msgField = HttpResult.class.getDeclaredField("msg");
                SerializedName msgSerializedName = msgField.getAnnotation(SerializedName.class);
                Set<String> msgAlias = new HashSet<>();
                msgAlias.add("msg");
                msgAlias.add(msgSerializedName.value());
                msgAlias.addAll(Arrays.asList(msgSerializedName.alternate()));

                //获取resultCode
                int resultCode = -1;
                for (String alias : codeAlias) {
                    if (obj.has(alias)) {
                        resultCode = obj.getInt(alias);
                        break;
                    }
                }

                //获取resultMsg
                String resultMsg = "";
                for (String alias : msgAlias) {
                    if (obj.has(alias)) {
                        resultMsg = obj.getString(alias);
                        break;
                    }
                }

                if (resultCode == HttpResult.SUCCESS_CODE) {
                    //业务码返回是正确的值，那么错误就是data的类型解析不正确
                    throw new CustomException("data解析不正确");
                } else {
                    //业务码返回的不是正确的值，那么只需要关心code和msg即可
                    throw new CustomException(resultCode, resultMsg);
                }
            } catch (JSONException e1) {
                throw new CustomException("返回结果不是json格式");
            } catch (NoSuchFieldException e1) {
                throw new CustomException("接收对象不含有code或者msg属性");
            }
        }
    }
}
