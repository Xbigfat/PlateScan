package com.xyw.platescan.presenter.StartView;

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

import com.xyw.platescan.model.HttpObject;
import com.xyw.platescan.util.WebService;
import com.xyw.platescan.view.Start.StartActivityInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * 此类负责
 * 1.动态权限申请
 * 2.检查更新
 * Created by 31429 on 2018/1/11.
 */

public class StartCheckerPresenter implements WebService.webServiceCallback {

    private static String[] PERMISSIONS = new String[]{
            //危险权限组 read camera,imei
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
    };
    private Context mContext;
    private StartActivityInterface mViewHandler;
    private String imei;

    /**
     * checker 对象,传入的 object 下转型为3个对象使用
     *
     * @param object 对象
     */
    public StartCheckerPresenter(Object object) {
        mContext = (Context) object;
        mViewHandler = (StartActivityInterface) object;
    }

    /**
     * 静态方法,强制进行版本更新检查
     *
     * @param context            检查的上下文对象
     * @param webServiceCallback 回调
     */
    @SuppressLint("all")
    public static void forceValidate(Context context, WebService.webServiceCallback webServiceCallback) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getImei();
        HttpObject httpHttpObject = new HttpObject();
        httpHttpObject.setMethodName("GetCodeId");
        httpHttpObject.setKeyArray(new String[]{"CodeId"});
        httpHttpObject.setValueArray(new String[]{imei});
        WebService webService = new WebService(context, httpHttpObject);
        webService.doRequest(webServiceCallback);
    }

    public static void wipeAuth(Context context) {
        SharedPreferences s = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s.edit();
        editor.clear();
        editor.apply();
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
            mViewHandler.onValidateCompleted(true, imei);
            return;
        } else {
            //此处开始联网验证
            //方法名:GetCodeId 参数名:CodeID 参数值:imei
            HttpObject httpHttpObject = new HttpObject();
            httpHttpObject.setMethodName("GetCodeId");
            httpHttpObject.setKeyArray(new String[]{"CodeId"});
            httpHttpObject.setValueArray(new String[]{imei});
            WebService webService = new WebService(mContext, httpHttpObject);
            webService.doRequest(this);
        }

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
                            mViewHandler.onPermissionDenied();
                            return;
                        }
                    }
                    this.validate();
                } else {
                    mViewHandler.onPermissionDenied();
                }
            }
        }
    }

    private void writeAuth() {
        SharedPreferences s = mContext.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s.edit();
        editor.putString("auth", imei);
        editor.apply();
    }

    @Override
    public void onLoading() {
        mViewHandler.onLoading();
    }

    @Override
    public void onValidateCompleted(boolean isValid) {
        mViewHandler.onValidateCompleted(isValid, imei);
        if (isValid) {
            writeAuth();
        } else {
            wipeAuth(mContext);
        }
    }

    @Override
    public void onTryCatchError(Exception e) {
        mViewHandler.onTryCatchError(e);
    }

    @Override
    public void onQueryCompleted(HttpObject obj) {
    }

    @Override
    public void onRequestTimeout() {
        mViewHandler.onRequestTimeout();
    }

}
