package com.lu.tool.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.FileProvider;

import com.google.gson.Gson;
import com.lu.tool.app.BaseApp;
import com.lu.tool.widget.util.LDialogBuilder;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;


/**
 * 检测升级工具类
 * 为了方便移植   网络请求采用了最基本的HttpUrlConnection
 * Created by lqh on 2016/8/19.
 */
public class UpdateVersionUtil {

    public static final int GET_UNKNOWN_APP_SOURCES = 0x99;
    private final String mSaveName = "ecarbusiness.apk";//更改为自己app的名称
    private final String FILE_PROVIDER = "com.pyec.ecarbusiness.fileprovider";//fileprovider

    private String update_url = "";//更新地址

    private UpdateInfo mUpdateInfo;

    private Activity mActivity;

    private boolean mIsCancel;

    private int mVersionCode = 1;

    private ProgressDialog mDownloadDialog;
    private File apkFile;
    private OnUpdateListener onUpdateListener;

    private Handler mHandler;


    private static class MainHandler extends Handler {

        private WeakReference<UpdateVersionUtil> weakReference;

        public MainHandler(UpdateVersionUtil updateVersionUtil) {
            super(Looper.getMainLooper());
            this.weakReference = new WeakReference<>(updateVersionUtil);
        }

        @Override
        public void handleMessage(Message msg) {
            UpdateVersionUtil util = weakReference.get();
            if (util == null) return;
            switch (msg.what) {
                case 0:
                    util.isNeedUpdate();
                    break;
            }
        }
    }

    private UpdateVersionUtil(Activity context) {
        this.mActivity = context;
        this.mVersionCode = getVersionCode(context);
        this.mHandler = new MainHandler(this);
    }

    public static UpdateVersionUtil newInstance(Activity context) {
        return new UpdateVersionUtil(context);
    }


    public void checkUpdate(final String url) {
        this.update_url = url;
        mIsCancel = false;

        new Thread() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(update_url);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(10000);
                    conn.connect();

                    OutputStream writer = conn.getOutputStream();
                    postParams(writer);//检测版本需要传递的参数
                    writer.flush();
                    writer.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200) {
                        ToastUtil.showShort("网络出错,请稍后再试");
                        return;
                    }

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    InputStream in = conn.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        bos.write(buffer, 0, len);
                    }

                    String response = bos.toString();
                    bos.close();
                    JSONObject object = new JSONObject(response);

                    Logger.d(object);
                    final int code = object.getInt("code");
                    if (code == 0) {
                        String dataStr = object.getString("data");
                        mUpdateInfo = new Gson().fromJson(dataStr, UpdateInfo.class);
                        if (mUpdateInfo != null) {
                            mHandler.obtainMessage(0).sendToTarget();
                            return;
                        }
                    } else {
                        final String msg = object.getString("msg");
                        ToastUtil.showShort(msg);
                    }
                } catch (IOException | JSONException e) {
                    ToastUtil.showShort("数据解析出错");
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }.start();
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    /**
     * 判断是否需要更新版本
     */
    public void isNeedUpdate() {
        if (mUpdateInfo.version_id > mVersionCode) {
            showNotifyDialog();
        }
    }

    private void showNotifyDialog() {

        new LDialogBuilder(mActivity)
                .setTitle("更新提醒")
                .setContent("版本:" + mUpdateInfo.version_name + "\n" + mUpdateInfo.update_note, 16, false)
                .setPositiveClickListener(new LDialogBuilder.BtnClickListener() {
                    @Override
                    public void click(Dialog dialog) {
                        new DownTask().execute();
                    }
                })
                .setNegativeClickListener(new LDialogBuilder.BtnClickListener() {
                    @Override
                    public void click(Dialog dialog) {
                        if (onUpdateListener != null) {
                            onUpdateListener.onError("取消更新");
                            return;
                        }
                        if (mActivity instanceof Activity) {
                            if (mUpdateInfo.force == 1) {
                                ((Activity) mActivity).finish();
                            }
                        }
                    }
                })
                .setCanceledOnTouchOutside(false)
                .build().show();
    }

    /**
     * 显示正在下载的对话框
     */
    private void showDownloadD() {

        mDownloadDialog = new ProgressDialog(mActivity);
        mDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.setMax(100);
        mDownloadDialog.setButton(Dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIsCancel = true;
                mDownloadDialog.dismiss();
            }
        });
        mDownloadDialog.show();
    }

    private void installApk() {
        if (!apkFile.exists())
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(mActivity, FILE_PROVIDER, apkFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            mActivity.startActivity(intent);
        } else {
            apkUri = Uri.fromFile(apkFile);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            mActivity.startActivity(intent);
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * 判断是不是8.0+系统
     */
    private void checkInstall() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            boolean canInstall = mActivity.getPackageManager().canRequestPackageInstalls();
//            if (canInstall) {
//                new RxPermissions(mActivity)
//                        .request(Manifest.permission.REQUEST_INSTALL_PACKAGES)
//                        .subscribe(new Consumer<Boolean>() {
//                            @Override
//                            public void accept(Boolean aBoolean) throws Exception {
//                                if (aBoolean) {
//                                    installApk();
//                                } else if (mActivity.shouldShowRequestPermissionRationale(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
//                                    //引导用户去打开权限
//                                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
//                                    mActivity.startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
//                                } else {
//                                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
//                                    mActivity.startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
//                                }
//
//                            }
//                        });
//            } else {
//                installApk();
//            }
//        } else {
//            installApk();
//        }
        installApk();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_UNKNOWN_APP_SOURCES && resultCode == Activity.RESULT_OK) {
            checkInstall();
        }
    }

    /*=================================================================*/

    class DownTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            showDownloadD();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String filePath = Environment.getExternalStorageDirectory() + "/" + mActivity.getPackageName();
                    File dir = new File(filePath);
                    if (!dir.exists())
                        dir.mkdirs();

                    //下载文件
                    URL url = new URL(mUpdateInfo.getUpdate_url());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    if (conn.getResponseCode() == 200) {
                        is = conn.getInputStream();
                        int length = conn.getContentLength();

                        apkFile = new File(filePath, mSaveName);
                        fos = new FileOutputStream(apkFile);

                        int count = 0;

                        byte[] buffer = new byte[1024];
                        int readL;

                        while (!mIsCancel) {
                            readL = is.read(buffer);
                            if (readL < 0) {
                                break;
                            }
                            fos.write(buffer, 0, readL);
                            count += readL;
                            /*更新进度*/
                            publishProgress((int) (count * 1.0f / length * 100));
                        }
                        fos.flush();
                        return true;
                    } else {
                        ToastUtil.showShort("下载资源出错！");
                    }
                } else {
                    ToastUtil.showShort("当前SD卡不可用！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                    if (is != null)
                        is.close();
                    if (conn != null)
                        conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mDownloadDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean isDown) {
            mDownloadDialog.dismiss();
            if (isDown && !mIsCancel) {
                checkInstall();
                return;
            }
            if (onUpdateListener != null) {
                onUpdateListener.onError("下载失败");
                return;
            }

            ToastUtil.showShort("下载失败");
            mActivity.finish();
        }
    }

    /**
     * 获取当前版本信息
     */
    private int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 写入数据
     *
     * @param out
     * @throws IOException
     */
    private void postParams(OutputStream out) throws IOException {
        Map<String, String> params = new TreeMap<>();
        //======添加需要传递的参数============
        params.put("device", "android");
        params.put("version", "a" + BaseApp.sVersionName);
        //======================================================

        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            data.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        params.put("secret", "merchant_!@#$%^&*_11qaz23wqsz6");

        String plainText = SecurityUtil.formatParams(params);

        String sign = SecurityUtil.getMD5(plainText);

        data.append("signature=").append(sign);

        out.write(data.toString().getBytes());

    }

    public static class UpdateInfo {
        /**
         * version_id : 314
         * version_name :
         * os : android
         * project : check
         * force : 0
         * update_time : 1484567522
         * update_url : http://update.fecar.com/check/android/eCarCheck3.1.3.apk
         * update_note : 修复检测项卡顿问题
         * create_time : 1484567522
         * <p>
         * PHPSESSID
         */

        int version_id;
        String version_name;
        String os;
        String project;
        int force;
        String update_time;
        String update_url;
        String update_note;
        String create_time;


        public int getVersion_id() {
            return version_id;
        }

        public void setVersion_id(int version_id) {
            this.version_id = version_id;
        }

        public String getVersion_name() {
            return version_name;
        }

        public void setVersion_name(String version_name) {
            this.version_name = version_name;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public int getForce() {
            return force;
        }

        public void setForce(int force) {
            this.force = force;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getUpdate_url() {
            return update_url;
        }

        public void setUpdate_url(String update_url) {
            this.update_url = update_url;
        }

        public String getUpdate_note() {
            return update_note;
        }

        public void setUpdate_note(String update_note) {
            this.update_note = update_note;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
    }


    public interface OnUpdateListener {
        void onError(String message);
    }
}
