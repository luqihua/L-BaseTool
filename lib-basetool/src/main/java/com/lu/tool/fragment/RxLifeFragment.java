package com.lu.tool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.lu.tool.http.BaseHttpTransformer;
import com.lu.tool.http.HttpResult;
import com.lu.tool.http.core.RxLifecycle;
import com.lu.tool.mvp.BaseView;

import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author luzeyan
 * @time 2018/3/13 上午10:15
 * @description
 */


public class RxLifeFragment extends Fragment implements BaseView {


    private BehaviorSubject<RxLifecycle> lifecycleSubject = BehaviorSubject.create();

    @Override
    public <T> ObservableTransformer<HttpResult<T>, HttpResult<T>> getTransformer(RxLifecycle lifecycle) {
        return new BaseHttpTransformer<>(bindLife(lifecycle));
    }

    @Override
    public <T> ObservableTransformer<HttpResult<T>, HttpResult<T>> getDefaultTransformer() {
        return getTransformer(RxLifecycle.DESTROY);
    }

    @Override
    public ObservableSource<RxLifecycle> bindLife(final RxLifecycle lifecycle) {
        return lifecycleSubject.filter(new Predicate<RxLifecycle>() {
            @Override
            public boolean test(RxLifecycle lifecycle1) throws Exception {
                return lifecycle1 == lifecycle;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(RxLifecycle.CREATE);
    }


    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(RxLifecycle.START);
    }


    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(RxLifecycle.RESUME);
    }


    @Override
    public void onPause() {
        super.onPause();
        lifecycleSubject.onNext(RxLifecycle.PAUSE);
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycleSubject.onNext(RxLifecycle.STOP);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(RxLifecycle.DESTROY);
    }
}
