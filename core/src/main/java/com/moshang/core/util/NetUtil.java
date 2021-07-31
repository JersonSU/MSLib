package com.moshang.core.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.Build;
import android.text.TextUtils;
import com.moshang.core.base.BaseApp;

/**
 * Created by:Jerson, on 2020/12/26.
 * Describe:
 **/
public class NetUtil {

    public static boolean check(Context context){
        if(!isAvailable(context)){
            Util.toast("无网络连接");
            return false;
        }else{
            return true;
        }
    }
    /**
     * 判断网络是否可用
     * @param context
     * @return
     */
    public static boolean isAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null) {
                return info.isConnected();
            }
        }catch (Exception e){
        }
        return  false;
    }

    /**
     * 网络连接类型
     * @param context
     * @return
     */
    public static int getType(Context context) {
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                    return mNetworkInfo.getType();
                }
            }
        }catch (Exception e){
        }
        return -1;
    }

    /**
     * 判断当前网络是否使用了代理
     * @return
     */
    public static boolean isProxy() {
        try{
            Context context= BaseApp.getContext();
            final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
            String proxyAddress;
            int proxyPort;
            if (IS_ICS_OR_LATER) {
                proxyAddress = System.getProperty("http.proxyHost");
                String portStr = System.getProperty("http.proxyPort");
                proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
            } else {
                proxyAddress = Proxy.getHost(context);
                proxyPort = Proxy.getPort(context);
            }
            return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
        }catch (Exception e){
        }
        return false;
    }

}
