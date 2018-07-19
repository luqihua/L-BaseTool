package com.lu.tool.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created  on 2017/5/5.
 * by luqihua
 */

public class PermissionUtil {

    private static Map<String, String> sPermissionMap;

    private static final int REQUEST_CODE = 0x11111;
    private OnPermissionResult onPermissionResult;

    static {
        sPermissionMap = new HashMap<>();
        /*android.permission-group.STORAGE*/
        sPermissionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读写权限");
        sPermissionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, "读写权限");

        /*android.permission-group.CALENDAR*/
        sPermissionMap.put(Manifest.permission.READ_CALENDAR, "读取日期权限");
        sPermissionMap.put(Manifest.permission.WRITE_CALENDAR, "编辑日期权限");

        /*android.permission-group.CAMERA*/
        sPermissionMap.put(Manifest.permission.CAMERA, "摄像头权限");

        /*android.permission-group.CONTACTS*/
        sPermissionMap.put(Manifest.permission.READ_CONTACTS, "读取联系人");
        sPermissionMap.put(Manifest.permission.WRITE_CONTACTS, "修改联系人");

         /*android.permission-group.LOCATION*/
        sPermissionMap.put(Manifest.permission.ACCESS_FINE_LOCATION, "获取精确位置");
        sPermissionMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, "获取大概位置");

         /*android.permission-group.MICROPHONE*/
        sPermissionMap.put(Manifest.permission.RECORD_AUDIO, "麦克风权限");

        /*android.permission-group.PHONE*/
        sPermissionMap.put(Manifest.permission.READ_PHONE_STATE, "获取手机状态权限");
        sPermissionMap.put(Manifest.permission.CALL_PHONE, "拨打电话权限");
        sPermissionMap.put(Manifest.permission.READ_CALL_LOG, "获取通讯记录权限");
        sPermissionMap.put(Manifest.permission.WRITE_CALL_LOG, "修改通话记录权限");
        sPermissionMap.put(Manifest.permission.ADD_VOICEMAIL, "语音信箱权限");
        sPermissionMap.put(Manifest.permission.USE_SIP, "use_sip");
        sPermissionMap.put(Manifest.permission.PROCESS_OUTGOING_CALLS, "修改通话记录权限");

        /*android.permission-group.SENSORS*/
        sPermissionMap.put(Manifest.permission.BODY_SENSORS, "访问传感器权限");

         /*android.permission-group.SMS*/
        sPermissionMap.put(Manifest.permission.SEND_SMS, "发送短信权限");
        sPermissionMap.put(Manifest.permission.RECEIVE_SMS, "接收短信权限");
        sPermissionMap.put(Manifest.permission.READ_SMS, "读取短信权限");
        sPermissionMap.put(Manifest.permission.RECEIVE_WAP_PUSH, "wap_push权限");
        sPermissionMap.put(Manifest.permission.RECEIVE_MMS, "receive_mms权限");
        sPermissionMap.put(Manifest.permission.BROADCAST_SMS, "broadcast_mms权限");

    }


    public PermissionUtil(OnPermissionResult onPermissionResult) {
        if (onPermissionResult == null) {
            throw new RuntimeException("PermissionUtil:  onPermissionResult must not be null");
        }
        this.onPermissionResult = onPermissionResult;
    }

    /**
     * 申请权限
     *
     * @param activity
     * @param permissions
     */
    public void requestPermission(Activity activity, final String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> needPermissions = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                final String permission = permissions[i];
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    if (activity.shouldShowRequestPermissionRationale(permission)) {
                        showMessageOKCancel(activity, "需要" + sPermissionMap.get(permissions[i]),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        needPermissions.add(permission);
                                    }
                                });
                    } else {
                        needPermissions.add(permission);
                    }
                }
            }

            if (needPermissions.size() > 0) {
                activity.requestPermissions(needPermissions.toArray(new String[0]), REQUEST_CODE);
            } else {
                onPermissionResult.onGrant();
            }
        } else {
            onPermissionResult.onGrant();
        }
    }

    /**
     * 申请权限
     *
     * @param fragment
     * @param permissions
     */
    public void requestPermission(Fragment fragment, String[] permissions) {
        final Activity activity = fragment.getActivity();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> needPermissions = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                final String permission = permissions[i];
                if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
                    if (!activity.shouldShowRequestPermissionRationale(permission)) {
                        showMessageOKCancel(activity, "需要" + sPermissionMap.get(permissions[i]),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        needPermissions.add(permission);
                                    }
                                });
                    } else {
                        needPermissions.add(permission);
                    }
                }
            }
            if (needPermissions.size() > 0) {
                fragment.requestPermissions((String[]) needPermissions.toArray(), REQUEST_CODE);
            } else {
                onPermissionResult.onGrant();
            }
        } else {
            onPermissionResult.onGrant();
        }
    }

    /**
     * 权限申请的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (onPermissionResult == null) return;
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    onPermissionResult.onDeny(sPermissionMap.get(permissions[i]));
                    return;
                }
            }
            onPermissionResult.onGrant();
        }
    }

    /**
     * 手动弹出权限申请对话框
     *
     * @param activity
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(Activity activity, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public interface OnPermissionResult {
        void onGrant();

        void onDeny(String message);
    }

}
