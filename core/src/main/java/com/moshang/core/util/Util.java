package com.moshang.core.util;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.moshang.core.base.BaseApp;

/**
 * Created by:Jerson, on 2021/7/5.
 * Describe:
 **/
public class Util {

    public final static int[] getPixels(Context context) {
        int sp[] = new int[2];
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        sp[0] = metrics.widthPixels;
        sp[1] = metrics.heightPixels;
        return sp;
    }

    public static boolean isEmpty(String s){
        return TextUtils.isEmpty(s) || s.equals("null");
    }

    public final static int px2dp(Context context,float px){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px/scale+0.5f);
    }

    public final static void toast(String s){
        try {
            Toast toast=Toast.makeText(BaseApp.getContext(),null,Toast.LENGTH_SHORT);
            toast.setText(s);
            toast.show();
        }catch (Exception e){
            try {
                if(e.getMessage().contains("has not called Looper.prepare")){
                    Looper.prepare();
                    Toast toast=Toast.makeText(BaseApp.getContext(),null,Toast.LENGTH_SHORT);
                    toast.setText(s);
                    toast.show();
                    Looper.loop();
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }
    }

    public static void log(String s){
        Log.e("debug",s);
    }

} 
