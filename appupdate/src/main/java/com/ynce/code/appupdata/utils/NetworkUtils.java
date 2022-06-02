package com.ynce.code.appupdata.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 *  2018/12/10.
 * 网络相关工具类
 */
public class NetworkUtils {
    public static Context context;

    private NetworkUtils() {
    }

    public enum NetworkType {
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    /**
     * 打开网络设置界面
     */
    public static void openWirelessSettings() {
        if (context == null) {
            Log.d("NetworkUtils", "context no init");
            return;
        }
        context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * 获取活动网络信息
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return NetworkInfo
     */
    @SuppressLint("MissingPermission")
    private static NetworkInfo getActiveNetworkInfo() {
        if (context == null) {
            Log.d("NetworkUtils", "context no init");
            return null;
        }
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }




    /**
     * 判断移动数据是否打开
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean getDataEnabled() {
        if (context == null) {
            Log.d("NetworkUtils", "context no init");
            return false;
        }
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {
        }
        return false;

    }

    /**
     * 打开或关闭移动数据
     * <p>需系统应用 需添加权限{@code <uses-permission android:name="android.permission.MODIFY_PHONE_STATE"/>}</p>
     *
     * @param enabled {@code true}: 打开<br>{@code false}: 关闭
     */
    public static void setDataEnabled(final boolean enabled) {
        if (context == null) {
            Log.d("NetworkUtils", "context no init");
            return;
        }
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            if (null != setMobileDataEnabledMethod) {
                setMobileDataEnabledMethod.invoke(tm, enabled);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 判断网络是否是4G
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean is4G() {
        if (context == null) {
            Log.d("NetworkUtils", "context no init");
            return false;
        }
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * 判断wifi是否打开
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean getWifiEnabled() {
        if (context == null) {
            Log.d("NetworkUtils", "context no init");
            return false;
        }
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 打开或关闭wifi
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>}</p>
     *
     * @param enabled {@code true}: 打开<br>{@code false}: 关闭
     */
    @SuppressLint("MissingPermission")
    public static void setWifiEnabled(final boolean enabled) {
        if (context == null) {
            Log.d("NetworkUtils", "context no init");
            return;
        }
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (enabled) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } else {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    /**
     * 判断wifi是否连接状态
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return {@code true}: 连接<br>{@code false}: 未连接
     */
    @SuppressLint("MissingPermission")
    public static boolean isWifiConnected() {
        if (context == null) {
            Log.d("NetworkUtils", "context no init");
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

}
