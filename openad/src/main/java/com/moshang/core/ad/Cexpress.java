package com.moshang.core.ad;

import android.app.Activity;
import android.view.View;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.moshang.core.util.Util;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class Cexpress {

    private static Cexpress mExpress;
    private Activity mActivity;
    private AdListener mListener;
    private boolean clicked=false;
    private boolean download=false;
    private boolean installed=false;

    public Cexpress(Activity activity) {
        this.mActivity = activity;
    }

    public static Cexpress init(Activity activity){
        if(mExpress==null){
            mExpress=new Cexpress(activity);
        }
        return mExpress;
    }

    public void load(int width, String codeid, @NotNull AdListener listener){
        this.mListener=listener;
        int w= Util.px2dp(mActivity,width);
        TTAdManager manager= TTAdSdk.getAdManager();
        manager.requestPermissionIfNecessary(mActivity);
        TTAdNative adNative=manager.createAdNative(mActivity);
        AdSlot adSlot=new AdSlot.Builder()
                .setCodeId(codeid)
                .setAdCount(1)
                .setSupportDeepLink(true)
                .setIsAutoPlay(true)
                .setOrientation(TTAdConstant.VERTICAL)
                .setExpressViewAcceptedSize(w,0)
                .build();
        adNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int i, String s) {
                listener.onError();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                if(list!=null && list.size()>0){
                    TTNativeExpressAd ad=list.get(0);
                    if(ad!=null){
                        show(ad);
                        return;
                    }
                }
                listener.onError();
            }
        });
    }

    private void show(TTNativeExpressAd ad){
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdShow(View view, int i) {
            }
            @Override
            public void onAdClicked(View view, int i) {
                if(!clicked){
                    clicked=true;
                    if(mListener!=null){
                        mListener.onClick();
                    }
                }
            }
            @Override
            public void onRenderSuccess(View view, float v, float v1) {
                if(mListener!=null){
                    mListener.onShow(view);
                }
            }
            @Override
            public void onRenderFail(View view, String s, int i) {
                if(mListener!=null){
                    mListener.onError();
                }
            }
        });
        //dislike
        ad.setDislikeCallback(mActivity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {}
            @Override
            public void onSelected(int i, String s, boolean b) {
                if (mListener!=null){
                    mListener.onClose();
                }
            }
            @Override
            public void onCancel() {}
        });
        //download
        if(ad.getInteractionType()==TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
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
                    if(!download && clicked){
                        download=true;
                        if(mListener!=null){
                            mListener.onDownload();
                        }
                    }
                }
                @Override
                public void onInstalled(String s, String s1) {
                    if(!installed && download){
                        installed=true;
                        if(mListener!=null){
                            mListener.onInstall();
                        }
                    }
                }
            });
        }
        ad.render();
    }
}
