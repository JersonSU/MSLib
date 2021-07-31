package com.moshang.core.ad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.moshang.core.base.BaseApp;
import com.moshang.core.util.Util;

public class Cvideo extends AppCompatActivity {

    private TTRewardVideoAd ad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        String codeid="";
        if(getIntent()!=null){
            codeid=getIntent().getStringExtra("codeid");
        }
        if(Util.isEmpty(codeid)){
            onFinish();
            return;
        }
        int width= BaseApp.getWidth();
        int height=BaseApp.getHeight();
        try {
            TTAdManager manager= TTAdSdk.getAdManager();
            manager.requestPermissionIfNecessary(this);
            TTAdNative ttAdNative=manager.createAdNative(this);
            AdSlot adSlot=new AdSlot.Builder()
                    .setCodeId(codeid)
                    .setSupportDeepLink(true)
                    .setExpressViewAcceptedSize(width,height)
                    .setImageAcceptedSize(width,height)
                    .setUserID(getPackageName().hashCode()+"")
                    .setOrientation(TTAdConstant.VERTICAL)
                    .build();
            ttAdNative.loadRewardVideoAd(adSlot,adListener);
        }catch (Exception e){
        }
    }

    @Override
    public void onBackPressed() {
        //TODO::
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ad!=null){
            ad=null;
        }
    }

    private TTAdNative.RewardVideoAdListener adListener=new TTAdNative.RewardVideoAdListener() {
        @Override
        public void onError(int i, String s) {
            onFinish();
        }

        @Override
        public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
            ad=ttRewardVideoAd;
            ad.setRewardAdInteractionListener(interactionListener);
        }

        @Override
        public void onRewardVideoCached() {
            if(ad!=null){
                ad.showRewardVideoAd(Cvideo.this);
            }
        }

        @Override
        public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {

        }
    };

    private TTRewardVideoAd.RewardAdInteractionListener interactionListener=new TTRewardVideoAd.RewardAdInteractionListener() {
        @Override
        public void onAdShow() {

        }

        @Override
        public void onAdVideoBarClick() {

        }

        @Override
        public void onAdClose() {
            onFinish();
        }

        @Override
        public void onVideoComplete() {

        }

        @Override
        public void onVideoError() {

        }

        @Override
        public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {

        }

        @Override
        public void onSkippedVideo() {

        }
    };

    private void onFinish(){
        Intent i=getIntent();
        i.putExtra("state",0);
        setResult(20201,i);
        finish();
    }

    public static Intent getIntent(Context context, String codeid){
        Intent i=new Intent(context,Cvideo.class);
        i.putExtra("codeid",codeid);
        return i;
    }

}
