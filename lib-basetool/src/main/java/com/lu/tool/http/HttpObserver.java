package com.lu.tool.http;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lu.tool.http.error.CustomException;
import com.lu.tool.util.ToastUtil;
import com.lu.tool.widget.dialog.LoadingDialog;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Author: luqihua
 * Time: 2017/6/21
 * Description: HttpObserver
 */

public abstract class HttpObserver<T> implements Observer<HttpResult<T>> {

    private Context mContext;
    private String mShowMsg;
    private LoadingDialog mLoadingDialog;

    public HttpObserver() {
    }

    public HttpObserver(Context context) {
        this.mContext = context;
    }

    public HttpObserver(Context context, String showMsg) {
        this.mContext = context;
        this.mShowMsg = showMsg;
    }

    @Override
    public void onSubscribe(@NonNull Disposable disposable) {
        if (mContext != null) {
            mLoadingDialog = new LoadingDialog(mContext);
            mLoadingDialog.show(mShowMsg);
        }
        onBefore();
    }


    @Override
    public void onNext(HttpResult<T> value) {
        onSuccess(value.data, value.msg);
    }

    @Override
    public void onComplete() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        onAfter();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        if (e instanceof TimeoutException || e instanceof SocketTimeoutException) {
            ToastUtil.showShort("连接超时,请重试");
        } else {
            onCustomError(e);
        }
        onAfter();
    }


    /*==========================自定义操作方法=====================================*/

    protected void onBefore() {

    }

    protected abstract void onSuccess(T data, String message);

    //处理客户端自定义的错误
    protected void onCustomError(Throwable e) {
        if (e instanceof HttpException) {
            CustomException exception = CustomException.createHttpException(((HttpException) e).code());
            ToastUtil.showLong(exception.toString());
        } else {
            ToastUtil.showLong(e.getMessage());
        }
    }

    protected void onAfter() {
    }

}
