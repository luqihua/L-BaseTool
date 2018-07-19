package com.lu.tool.mvp;

import com.lu.tool.http.HttpResult;
import com.lu.tool.http.core.RxLifecycle;

import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 * Author: luqihua
 * Time: 2017/12/25
 * Description: BaseView
 */

public interface BaseView{

   <E> ObservableTransformer<HttpResult<E>,HttpResult<E>> getTransformer(RxLifecycle lifecycle);

   <E> ObservableTransformer<HttpResult<E>,HttpResult<E>> getDefaultTransformer();

   ObservableSource<RxLifecycle> bindLife(RxLifecycle lifecycle);
}
