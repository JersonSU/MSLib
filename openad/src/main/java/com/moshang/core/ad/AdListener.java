package com.moshang.core.ad;

import android.view.View;

/**
 * Created by:Jerson, on 2021/7/5.
 * Describe:
 **/
public abstract class AdListener {

    public abstract void onShow(View view);
    public void onClick(){}
    public void onClose(){}
    public void onError(){}
    public void onDownload(){}
    public void onInstall(){}

} 
