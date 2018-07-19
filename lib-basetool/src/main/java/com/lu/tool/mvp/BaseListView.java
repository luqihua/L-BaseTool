package com.lu.tool.mvp;

import java.util.List;

/**
 * @Author: luqihua
 * @Time: 2018/4/18
 * @Description: BaseListView
 */

public interface BaseListView<T> extends BaseMVPView {
    void loadDataSuccess(int count, List<T> dataList);
}
