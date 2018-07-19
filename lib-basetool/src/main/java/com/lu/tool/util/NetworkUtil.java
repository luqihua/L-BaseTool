package com.lu.tool.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络监测工具
 */
public class NetworkUtil {
    /**
     * 网络是否可用
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnect;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = manager.getAllNetworks();
            boolean bEthernet = false;//有线网是否可用
            boolean bWifi = false;//wifi无线是否可用
            boolean bMobile = false;//移动网是否可用
            for (Network network : networks) {
                NetworkInfo info = manager.getNetworkInfo(network);
                if (info == null) continue;
                if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    bEthernet = info.isConnected();
                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    bWifi = info.isConnected();
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    bMobile = info.isConnected();
                }
            }
            isConnect = bEthernet || bWifi || bMobile;
        } else {
            NetworkInfo info = manager.getActiveNetworkInfo();
            isConnect = info != null && info.isConnected();
        }

        return isConnect;
    }

    /**
     * 获取手机网络ip
     *
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//移动网络
                try {
                    Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                    while (en.hasMoreElements()) {
                        Enumeration<InetAddress> enumIpAddr = en.nextElement().getInetAddresses();
                        while (enumIpAddr.hasMoreElements()) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ip = wifiInfo.getIpAddress();
                String ipAddress = (ip & 0xFF) + "." +
                        ((ip >> 8) & 0xFF) + "." +
                        ((ip >> 16) & 0xFF) + "." +
                        (ip >> 24 & 0xFF);//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

}
