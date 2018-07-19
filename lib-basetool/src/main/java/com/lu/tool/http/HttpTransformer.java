package com.lu.tool.http;


import com.lu.tool.http.core.RxLifecycle;
import com.lu.tool.http.error.CustomException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: luqihua
 * Time: 2017/9/21
 * Description: HttpTransformer
 */

public class HttpTransformer<T> implements ObservableTransformer<HttpResult<T>, T> {

    private ObservableSource<RxLifecycle> mLifecycleObservable;

    public HttpTransformer() {
    }

    public HttpTransformer(ObservableSource<RxLifecycle> lifecycleObservable) {
        this.mLifecycleObservable = lifecycleObservable;
    }

    @Override
    public ObservableSource<T> apply(Observable<HttpResult<T>> upstream) {
        if (mLifecycleObservable != null) {
            upstream = upstream.takeUntil(mLifecycleObservable);
        }
        return upstream
                .flatMap(new Function<HttpResult<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(HttpResult<T> httpResult) throws Exception {
                        if (httpResult.isSuccess()) {
                            return Observable.just(httpResult.data);
                        } else {
                            return Observable.error(new CustomException(httpResult.code, httpResult.msg));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
