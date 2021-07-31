package com.moshang.core.um;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.Map;
import java.util.TreeMap;

public class Umeng {

    /**
     * 友盟初始化
     * @param c
     * @param appkey
     * @param privacyGranted:是否已经同意隐私政策
     */
    public void init(Context c, String appkey,boolean privacyGranted){
        String channel=getChannel(c);
        UMConfigure.preInit(c,appkey,channel);
        UMConfigure.setLogEnabled(false);
        UMConfigure.setEncryptEnabled(!false);
        UMConfigure.setProcessEvent(!false);
        if(privacyGranted){
            UMConfigure.init(c, appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, "");
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        }
    }

    /**
     * AUTO模式下对非Activity页面进行埋点采集
     * @param s
     */
    public static void onPageStart(String s){
        MobclickAgent.onPageStart(s);
    }

    public static void onPageEnd(String s){
        MobclickAgent.onPageEnd(s);
    }

    /**
     * 自定义事件统计
     * @param context
     * @param event :事件名称
     * @param param :自定义参数
     */
    public static void count(Context context,String event, Map<String,String> param){
        MobclickAgent.onEvent(context,event,param);
    }

    private static String getChannel(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String channel=appInfo.metaData.getString("UMENG_CHANNEL");
            if(!TextUtils.isEmpty(channel)){
                return channel;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "default";
    }

}
