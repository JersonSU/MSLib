package com.moshang.core.ad;

import android.app.Activity;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.moshang.core.util.Util;

import org.jetbrains.annotations.NotNull;

/**
 * Created by:Jerson, on 2021/7/5.
 * Describe:
 **/
public class Csplash {

    private static Csplash mIntance;
    private Activity mActivity;
    private AdListener mListener;
    private boolean show=false;
    private boolean click=false;
    private boolean down=false;
    private boolean instl=false;

    public static Csplash init(Activity activity){
        if(mIntance==null){
            mIntance=new Csplash(activity);
        }
        return mIntance;
    }

    public Csplash(Activity a) {
        this.mActivity = a;
    }

    public void load(int width, int height, String cid,@NotNull AdListener listener){
        this.mListener=listener;
        TTAdManager manager= TTAdSdk.getAdManager();
        manager.requestPermissionIfNecessary(mActivity);
        TTAdNative tnative=manager.createAdNative(mActivity);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(cid)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(width,height)
                .build();
        tnative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            public void onError(int i, String s) {
                listener.onError();
            }
            @Override
            public void onTimeout() {
                listener.onError();
            }
            @Override
            public void onSplashAdLoad(TTSplashAd ttSplashAd) {
                if(ttSplashAd==null || ttSplashAd.getSplashView()==null){
                    listener.onError();
                }else{
                    show(ttSplashAd);
                    listener.onShow(ttSplashAd.getSplashView());
                }
            }
        }, 5000);
    }

    private void show(TTSplashAd ad){
        ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
            @Override
            public void onAdShow(View view, int i) {
                //TODO::统计展示量
                if(!show){
                    show=true;
                }
            }
            @Override
            public void onAdClicked(View view, int i) {
                //TODO::统计点击量
                if(!click){
                    click=true;
                    if(mListener!=null){
                        mListener.onClick();
                    }
                }
            }

            @Override
            public void onAdSkip() {
                if(mListener!=null){
                    mListener.onClose();
                }
            }

            @Override
            public void onAdTimeOver() {
                if(mListener!=null){
                    mListener.onClose();
                }
            }
        });
        if(ad.getInteractionType()== TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            ad.setDownloadListener(new TTAppDownloadListener() {
                @Override
                public void onIdle() {}
                @Override
                public void onDownloadActive(long l, long l1, String s, String s1) {}
                @Override
                public void onDownloadPaused(long l, long l1, String s, String s1) {}
                @Override
                public void onDownloadFailed(long l, long l1, String s, String s1) {}
                @Override
                public void onDownloadFinished(long l, String s, String s1) {
                    if(!down && click){
                        down=true;
                        if(mListener!=null){
                            mListener.onDownload();
                        }
                    }
                }
                @Override
                public void onInstalled(String s, String s1) {
                    if(down && !instl){
                        instl=true;
                        if(mListener!=null){
                            mListener.onInstall();
                        }
                    }
                }
            });
        }
    }
}
