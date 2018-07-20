package com.lu.tool.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lu.tool.http.BaseHttpTransformer;
import com.lu.tool.http.HttpResult;
import com.lu.tool.http.core.RxLifecycle;
import com.lu.tool.mvp.BaseView;

import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Author: luqihua
 * Time: 2017/12/25
 * Description: RxLifeActivity
 */

public abstract class RxLifeActivity   extends AppCompatActivity implements BaseView {

    private BehaviorSubject<RxLifecycle> lifecycleSubject = BehaviorSubject.create();

    @Override
    public <E> ObservableTransformer<HttpResult<E>,HttpResult<E>> getTransformer(RxLifecycle lifecycle) {
        return new BaseHttpTransformer<>(bindLife(lifecycle));
    }

    @Override
    public <E> ObservableTransformer<HttpResult<E>, HttpResult<E>> getDefaultTransformer() {
        return getTransformer(RxLifecycle.DESTROY);
    }

    @Override
    public ObservableSource<RxLifecycle> bindLife(final RxLifecycle lifecycle) {
        return lifecycleSubject.filter(new Predicate<RxLifecycle>() {
            @Override
            public boolean test(RxLifecycle lifecycle1) throws Exception {
                return lifecycle1==lifecycle;
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(RxLifecycle.CREATE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(RxLifecycle.START);
    }


    @Override
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(RxLifecycle.RESUME);
    }


    @Override
    protected void onPause() {
        super.onPause();
        lifecycleSubject.onNext(RxLifecycle.PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifecycleSubject.onNext(RxLifecycle.STOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(RxLifecycle.DESTROY);
    }
}
