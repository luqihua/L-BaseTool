package com.lu.tool.http;


import com.lu.tool.http.core.RxLifecycle;
import com.lu.tool.http.error.CustomException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: luqihua
 * Time: 2017/9/21
 * Description: HttpTransformer
 */

public class BaseHttpTransformer<T> implements ObservableTransformer<HttpResult<T>, HttpResult<T>> {

    private ObservableSource<RxLifecycle> mLifecycleSubject;

    public BaseHttpTransformer() {
    }

    public BaseHttpTransformer(ObservableSource<RxLifecycle> lifecycleSubject) {
        this.mLifecycleSubject = lifecycleSubject;
    }

    @Override
    public ObservableSource<HttpResult<T>> apply(Observable<HttpResult<T>> upstream) {
        if (mLifecycleSubject != null) {
            upstream = upstream.takeUntil(mLifecycleSubject);
        }
        return upstream
                .filter(new Predicate<HttpResult<T>>() {
                    @Override
                    public boolean test(HttpResult<T> tHttpResult) throws Exception {
                        if (tHttpResult.isSuccess()) {
                            return true;
                        } else {
                            throw new CustomException(tHttpResult.code, tHttpResult.msg);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
