package com.moshang.core.um;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.umeng.commonsdk.UMConfigure;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Created by:Jerson, on 2021/4/1.
 * Describe:
 **/
public class Oaid {

    /**
     * 获取oaid并保存到本地
     * @param context
     */
    public static void init(Context context){
        String oaid=getOaid(context);
        if(TextUtils.isEmpty(oaid)){
            loadOaid(context);
        }
    }

    /**
     * 获取本地oaid
     * @param context
     * @return
     */
    public static String getOaid(Context context){
        String oaid="";
        SharedPreferences sp=context.getSharedPreferences(context.getPackageName(),0);
        if(sp!=null){
            sp.getString(context.getPackageName()+".oaid","");
        }
        if(TextUtils.isEmpty(oaid)){
            oaid=readDeviceID(context);
        }
        return oaid;
    }

    /**
     * 加载oaid并保存到本地
     * @param context
     */
    private static void loadOaid(Context context){
        UMConfigure.getOaid(context, s -> {
            String oaid;
            if(TextUtils.isEmpty(s)){
                oaid=getDeviceId(context);
            }else{
                oaid=getMD5(s,false);
            }
            if(!TextUtils.isEmpty(oaid)){
                SharedPreferences sp=context.getSharedPreferences(context.getPackageName(),0);
                if(sp!=null){
                    SharedPreferences.Editor e=sp.edit();
                    if(e!=null){
                        e.putString(context.getPackageName()+".oaid",oaid);
                    }
                }
                writeDeviceID(context,oaid);
            }
        });
    }

    /**
     * 获取设备唯一标识符
     * @param context
     * @return
     */
    private static String getDeviceId(Context context) {
        //用于生成最终的唯一标识符
        StringBuffer s = new StringBuffer();
        //获取IMEI(也就是常说的DeviceId)
        String deviceId = getIMEI(context);
        s.append(deviceId.trim());
        //获取设备的MACAddress地址 去掉中间相隔的冒号
        deviceId = getLocalMac().replace(":", "");
        s.append(deviceId.trim());
        deviceId = getAndroidId(context);
        s.append(deviceId.trim());
        //如果以上没有获取相应的内容,则自己生成相应的UUID作为相应设备唯一标识符
        if (s == null || s.length() <= 0) {
            UUID uuid = UUID.randomUUID();
            deviceId = uuid.toString().replace("-", "");
            s.append(deviceId);
        }
        //为了统一格式对设备的唯一标识进行md5加密 最终生成32位字符串
        String md5 = getMD5(s.toString(), false);
        return md5;
    }

    /**
     * 获取设备的DeviceId(IMEI) 这里需要相应的权限
     * 需要 READ_PHONE_STATE 权限
     * @param context
     * @return
     */
    private static String getIMEI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            return tm.getDeviceId();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Google解决方案：如果您的应用有追踪非登录用户的需求，可用ANDROID_ID来标识设备。
     * 1.ANDROID_ID生成规则：签名+设备信息+设备用户
     * 2.ANDROID_ID重置规则：设备恢复出厂设置时，ANDROID_ID将被重置
     * @param context
     * @return
     */
    private static String getAndroidId(Context context) {
        try {
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return androidId;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取设备MAC 地址 由于 6.0 以后 WifiManager 得到的 MacAddress得到都是 相同的没有意义的内容
     * 所以采用以下方法获取Mac地址
     * @return
     */
    private static String getLocalMac() {
        String macAddress;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
            return macAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 对挺特定的 内容进行 md5 加密
     * @param message   加密明文
     * @param upperCase 加密以后的字符串是是大写还是小写 true 大写 false 小写
     * @return
     */
    private static String getMD5(String message, boolean upperCase) {
        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] input = message.getBytes();
            byte[] buff = md.digest(input);
            md5str = bytesToHex(buff, upperCase);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    private static String bytesToHex(byte[] bytes, boolean upperCase) {
        StringBuffer md5str = new StringBuffer();
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        if (upperCase) {
            return md5str.toString().toUpperCase();
        }
        return md5str.toString().toLowerCase();
    }

    /**
     * 读取固定的文件中的内容,这里就是读取sd卡中保存的设备唯一标识符
     * @param context
     * @return
     */
    private static String readDeviceID(Context context) {
        File file = getDevicesDir(context);
        if(file==null || !file.exists() || !file.isFile()){
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            Reader in = new BufferedReader(isr);
            int i;
            while ((i = in.read()) > -1) {
                buffer.append((char) i);
            }
            in.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 保存 内容到 SD卡中, 这里保存的就是 设备唯一标识符
     * @param context
     * @param str
     */
    private static void writeDeviceID(Context context, String str) {
        File file = getDevicesDir(context);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统一处理设备唯一标识 保存的文件的地址
     * @param context
     * @return
     */
    private static File getDevicesDir(Context context) {
        File mCropFile;
        /**
         * 对应手机设置->应用->详情->清除数据:(应用卸载数据不删除)
         * context.getCacheDir();// 获取/data/data/<application package>/cache/目录
         * context.getFilesDir();// 获取/data/data/<application package>/files/目录
         * 对应手机设置->应用->详情->清除缓存:(应用卸载数据删除)
         * context.getExternalCacheDir("");// 获取SDCard/Android/data/<application package>/cache/目录(一般存放临时缓存数据)
         * context.getExternalFilesDir();// 获取SDCard/Android/data/<application package>/files/目录(一般放一些长时间保存的数据)
         */
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            mCropFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), ".device");
        } else {
            mCropFile = new File(context.getFilesDir(), ".device");
        }
        return mCropFile;
    }
} 
