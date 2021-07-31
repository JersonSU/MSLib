package com.moshang.core.base;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.moshang.core.R;
import com.moshang.core.realm.Migration;
import com.moshang.core.util.NetUtil;
import com.moshang.core.util.Util;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.zhy.http.okhttp.OkHttpUtils;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class BaseApp extends Application {

    private static Context mContext;
    private static int width,height;

    protected abstract void onStart();

    static {
        //TODO::全局设置Header&Footer
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.transparent, R.color.gray);
            ClassicsHeader header = new ClassicsHeader(context);
            header.setSpinnerStyle(SpinnerStyle.Translate);
            header.setEnableLastTime(false);
            return header;
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getContext();
        int[] pixels= Util.getPixels(this);
        width=pixels[0];
        height=pixels[1];
        onStart();
    }

    protected void initOkHttp(){
        Interceptor interceptor = chain -> {
            if(NetUtil.isProxy()){
                return new Response.Builder()
                        .code(400)
                        .protocol(Protocol.HTTP_2)
                        .message("Proxy network!")
                        .body(ResponseBody.create(MediaType.parse("text/html;charset=utf-8"),""))
                        .request(chain.request())
                        .build();
            }
            Request original = chain.request();
            Request request=original.newBuilder()
                    .header("Accept","*/*")
                    .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        };
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .proxy(Proxy.NO_PROXY)//设置无代理模式(防止抓包,需要抓包时将该行代码注释掉)
                .addInterceptor(interceptor)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    protected void initRealm(int vcode){
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(getPackageName() + ".realm")
                .schemaVersion(vcode)
                .deleteRealmIfMigrationNeeded()
                .migration(new Migration(vcode))
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static Context getContext(){
        return mContext;
    }

    public static int getWidth(){
        return width;
    }

    public static int getHeight(){
        return height;
    }

}
