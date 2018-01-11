package com.xyw.platescan.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.xyw.platescan.model.Object;
import com.xyw.platescan.zencryption.AES;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebService请求类
 * Created by 31429 on 2018/1/11.
 */

public class WebService {

    private Object object;
    private String endPoint;
    private SoapObject request;
    private static final int QueryCompleted = 0;
    private static final int Loading = 1;
    private static final int ErrorOccurs = -1;
    private static final int ValidateCompleted = -2;

    public interface webServiceCallback {

        //验证过程中
        void onLoading();

        //验证授权
        void onValidateCompleted(boolean isValid);

        //出现错误
        void onErrorOccurs(Object e);

        //查询结果回调
        void onQueryCompleted(Object obj);

    }

    public WebService(Context context, Object object) {
        this.object = object;
        SharedPreferences serviceIp = context.getSharedPreferences("ip", Context.MODE_PRIVATE);
        if (serviceIp == null) {
            endPoint = Parameters.defaultEndPoint;
        } else {
            if (!serviceIp.contains("ip") || !serviceIp.contains("port") || !serviceIp.contains("service")) {
                endPoint = Parameters.defaultEndPoint;
            } else {
                endPoint = "http://" + serviceIp.getString("ip", Parameters.ip) + ":"
                        + serviceIp.getString("port", Parameters.port) + "/"
                        + serviceIp.getString("service", Parameters.service)
                        + Parameters.path;
            }
        }
    }

    private static final ExecutorService executorPool = Executors.newFixedThreadPool(3);

    public void doRequest(final webServiceCallback w) {
        @SuppressLint("HandlerLeak") final android.os.Handler mHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case QueryCompleted:
                        w.onQueryCompleted((Object) msg.obj);
                        break;
                    case ErrorOccurs:
                        w.onErrorOccurs((Object) msg.obj);
                        break;
                    case Loading:
                        w.onLoading();
                        break;
                    case ValidateCompleted:
                        w.onValidateCompleted(msg.arg1 == 1);
                }
            }
        };
        executorPool.submit(new Runnable() {
            @Override
            public void run() {
                Message onLoading = mHandler.obtainMessage();
                onLoading.what = Loading;
                mHandler.sendMessage(onLoading);
                try {
                    request = new SoapObject(Parameters.nameSpace + "/", object.getMethodName());
                    for (int i = 0; i < object.getKeyArray().length; i++) {
                        request.addProperty(object.getKeyArray()[i], object.getValueArray()[i]);
                    }
                    request.addProperty("name", AES.Encrypt("aHhlSHPaDmin"));
                    request.addProperty("passwd", AES.Encrypt("aHhlSHPaDmin3662709"));
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.bodyOut = request;
                    HttpTransportSE ht = new HttpTransportSE(endPoint, 4000);
                    int i = 0;
                    while (i < 3) {
                        try {
                            ht.call(Parameters.nameSpace + "/" + object.getMethodName(), envelope);
                            break;
                        } catch (SoapFault e) {
                            e.printStackTrace();
                            Log.i(Parameters.TAG, "the " + String.valueOf(i + 1) + " times retry");
                        }
                        i++;
                        if (i == 3) {
                            Message errorOccurs = mHandler.obtainMessage();
                            errorOccurs.what = WebService.ErrorOccurs;
                            errorOccurs.obj = "请求webservice第三次失败";
                            mHandler.sendMessage(errorOccurs);
                            return;
                        }
                    }
                    object.setResultData(envelope.getResponse().toString());
                    String data = object.getResultData();
                    if (data.length() == 1) {
                        Message validateStatus = mHandler.obtainMessage();
                        validateStatus.what = ValidateCompleted;
                        validateStatus.arg1 = Integer.valueOf(data);
                        mHandler.sendMessage(validateStatus);
                        return;
                    }
                    object.setResultData(AES.Decrypt(data));
                    Message msg = mHandler.obtainMessage();
                    msg.what = QueryCompleted;
                    msg.obj = object;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message error = mHandler.obtainMessage();
                    error.what = ErrorOccurs;
                    error.obj = e;
                    mHandler.sendMessage(error);
                }
            }
        });
    }
}


