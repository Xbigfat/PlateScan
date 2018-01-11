package com.xyw.platescan.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.xyw.platescan.model.Object;
import com.xyw.platescan.util.WebService;
import com.xyw.platescan.view.ViewInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * 此类负责
 * 1.动态权限申请
 * 2.检查更新
 * Created by 31429 on 2018/1/11.
 */

public class Cherker implements WebService.webServiceCallback {

    private Context mContext;
    private ViewInterface mViewHandler;
    private checkListener mlistener;

    private static String[] PERMISSIONS = new String[]{
            //危险权限组 read camera,imei
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
    };
    private String imei;

    /**
     * checker 对象,传入的 object 下转型为3个对象使用
     *
     * @param object 对象
     */
    public Cherker(java.lang.Object object) {
        mContext = (Context) object;
        mViewHandler = (ViewInterface) object;
        mlistener = (checkListener) object;
    }

    /**
     * 运行时需要主动调用检查权限
     */
    public void checkPermission() {
        //检查未授权,加到 need 列表中
        List<String> needPer = new ArrayList<>();
        for (String s : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(mContext, s) != PackageManager.PERMISSION_GRANTED) {
                needPer.add(s);
            }
        }
        if (needPer.size() == 0) {
            this.validate();
            return;
        }
        ActivityCompat.requestPermissions(
                (Activity) mContext,
                needPer.toArray(new String[needPer.size()]),
                1);
    }

    @SuppressWarnings("all")
    public void validate() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getImei();
        if (readMyAuth()) {
            mViewHandler.functionValid("授权完成");
            return;
        } else {
            //此处开始联网验证
            //方法名:GetCodeId 参数名:CodeID 参数值:imei
            Object httpObject = new Object();
            httpObject.setMethodName("GetCodeId");
            httpObject.setKeyArray(new String[]{"CodeId"});
            httpObject.setValueArray(new String[]{imei});
            WebService webService = new WebService(mContext, httpObject);
            webService.doRequest(this);
        }

    }

    @SuppressLint("all")
    public void forceValidate() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getImei();
        Object httpObject = new Object();
        httpObject.setMethodName("GetCodeId");
        httpObject.setKeyArray(new String[]{"CodeId"});
        httpObject.setValueArray(new String[]{imei});
        WebService webService = new WebService(mContext, httpObject);
        webService.doRequest(this);
    }

    /**
     * 功能授权,首先读取本地授权文件
     * 读取到授权文件后,返回true
     */
    private boolean readMyAuth() {
        SharedPreferences s = mContext.getSharedPreferences("auth", Context.MODE_PRIVATE);
        if (s != null) {
            //取出本地 auth 值
            String auth = s.getString("auth", "invalid");
            if (imei.equals(auth)) {
                return true;
            }
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                //权限获取成功
                if (grantResults.length > 0) {
                    for (int i : grantResults) {
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            mlistener.Denied();
                            return;
                        }
                    }
                    this.validate();
                } else {
                    mlistener.Denied();
                }
            }
        }
    }

    @Override
    public void onLoading() {
        mViewHandler.functionInvalid("正在检查中,请稍后");
        wipeAuth();
    }


    @Override
    public void onQueryCompleted(Object obj) {
        if (obj.getResultData().equals("0")) {
            mViewHandler.functionValid("权限检查成功!");
            writeAuth();
        } else {
            mViewHandler.functionInvalid("未获得权限-->" + imei);
            wipeAuth();
        }
    }

    private void wipeAuth() {
        SharedPreferences s = mContext.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s.edit();
        editor.clear();
        editor.apply();
    }

    private void writeAuth() {
        SharedPreferences s = mContext.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s.edit();
        editor.putString("auth", imei);
        editor.apply();
    }

    public interface checkListener {
        void Denied();
    }

    @Override
    public void onValidateCompleted(boolean isValid) {

    }

    @Override
    public void onErrorOccurs(Object e) {

    }
}
