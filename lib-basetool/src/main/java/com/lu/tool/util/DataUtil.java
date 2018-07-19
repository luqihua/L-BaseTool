package com.lu.tool.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据转换类
 *
 * @author lqh
 */
public class DataUtil {
    /**
     * 将字符串转成数组
     *
     * @param data
     * @return
     */
    public static ArrayList<String> parseStr2List(String data) {
        ArrayList<String> list = new ArrayList<>();

        if (TextUtils.isEmpty(data)) return list;

        if (data.contains("[") && data.contains("]")) {
            list = new Gson().fromJson(data, new TypeToken<List<String>>() {
            }.getType());
        } else {
            String[] strings = data.split(",");

            list.addAll(Arrays.asList(strings));
        }

        return list;
    }

    /**
     * 将string集合转成字符串
     *
     * @param list
     * @return
     */
    public static String parseList2Str(List<String> list) {

        if (list == null || list.size() == 0) return "";
        if (list.size() == 1) return list.get(0);

        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (!TextUtils.isEmpty(list.get(i))) {
                buffer.append(",").append(list.get(i));
            }
        }
        return buffer.substring(1).toString();
    }

    /**
     * 将string数组转成字符串
     *
     * @param list
     * @return
     */
    public static String parseArray2Str(String[] list) {

        if (list == null || list.length == 0) return "";
        if (list.length == 1) return list[0];

        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < list.length; i++) {
            if (!TextUtils.isEmpty(list[i])) {
                buffer.append(",").append(list[i]);
            }
        }
        return buffer.substring(1).toString();
    }

}
