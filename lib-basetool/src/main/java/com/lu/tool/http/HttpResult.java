package com.lu.tool.http;

import com.google.gson.annotations.SerializedName;

/**
 * Author: luqihua
 * Time: 2017/7/19
 * Description: HttpResult
 */

public class HttpResult<T> {
    /**
     * 定义表示成功的值
     */
    public static int SUCCESS_CODE = 0;

    @SerializedName(value = "code", alternate = {"status"})
    public int code;

    @SerializedName(value = "msg", alternate = {"message"})
    public String msg;

    public T data;

    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }
}
