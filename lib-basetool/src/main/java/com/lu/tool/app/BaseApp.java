package com.lu.tool.app;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.lu.tool.http.RetrofitUtil;
import com.lu.tool.util.ActivityStackUtil;
import com.lu.tool.util.FileUtil;
import com.lu.tool.util.ResourceUtil;
import com.lu.tool.util.SystemUtil;
import com.lu.tool.util.ToastUtil;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

/**
 * 基础Application,一些初始化操作放在这里执行
 *
 * @author lqh
 */
public class BaseApp extends Application {

    public static Application sInstance;
    /*---当前版本名称------*/
    public static String sVersionName;
    public static int sVersionCode;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        if (SystemUtil.isCurrentProcess(this)) {
            initVersionInfo();
            initIconify();
            ToastUtil.init(this);
            RetrofitUtil.init(this);
            FileUtil.init(this);
            ResourceUtil.init(this);
            ActivityStackUtil.getInstance().init(this);
        }
    }

    /**
     * 获取当前版本信息
     */
    private void initVersionInfo() {
        if (TextUtils.isEmpty(sVersionName)) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                sVersionName = packageInfo.versionName;
                sVersionCode = packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private void initIconify() {
        Iconify.with(new FontAwesomeModule());
    }
}
