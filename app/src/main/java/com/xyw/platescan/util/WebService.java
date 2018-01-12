package com.xyw.platescan.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.xyw.platescan.model.HttpObject;
import com.xyw.platescan.zencryption.AES;

import org.ksoap2.SoapEnvelope;
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

    //WebService 请求结束 -> return online query metadata
    private static final int QUERY_COMPLETED = 0;
    //WebService 请求中 -> validating or querying ,return null
    private static final int LOADING = 1;
    //WebService 请求时出现的未知错误 -> return Exception e
    private static final int TRY_CATCH_ERROR = -1;
    //Validation 授权结果 -> return boolean
    private static final int VALIDATE_COMPLETED = -2;
    //自定义超时错误 -> ruturn void
    private static final int REQUEST_TIMEOUT = -3;
    private static final ExecutorService executorPool = Executors.newFixedThreadPool(3);
    private HttpObject httpObject;
    private String endPoint;
    private SoapObject request;

    public WebService(Context context, HttpObject httpObject) {
        this.httpObject = httpObject;
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

    public void doRequest(final webServiceCallback w) {
        @SuppressLint("HandlerLeak") final android.os.Handler mHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                    case QUERY_COMPLETED:
                        w.onQueryCompleted((HttpObject) msg.obj);
                        break;

                    case LOADING:
                        w.onLoading();
                        break;

                    case TRY_CATCH_ERROR:
                        w.onTryCatchError((Exception) msg.obj);
                        break;

                    case VALIDATE_COMPLETED:
                        w.onValidateCompleted((Boolean) msg.obj);
                        break;

                    case REQUEST_TIMEOUT:
                        w.onRequestTimeout();
                        break;
                }
            }
        };
        executorPool.submit(new Runnable() {
            @Override
            public void run() {
                //请求开始
                Message msg1 = mHandler.obtainMessage();
                msg1.what = WebService.LOADING;
                mHandler.sendMessage(msg1);

                try {
                    request = new SoapObject(Parameters.nameSpace + "/", httpObject.getMethodName());
                    for (int i = 0; i < httpObject.getKeyArray().length; i++) {
                        request.addProperty(httpObject.getKeyArray()[i], httpObject.getValueArray()[i]);
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
                            ht.call(Parameters.nameSpace + "/" + httpObject.getMethodName(), envelope);
                            break;
                        } catch (Exception e) {

                            //此错误不会终止请求,但是会在控制台打印出出错的原因,同时会将exception发送到调用的presenter
                            e.printStackTrace();
                            Log.i(Parameters.TAG, "the " + String.valueOf(i + 1) + " times retry");
                            Message msg2 = mHandler.obtainMessage();
                            msg2.what = WebService.TRY_CATCH_ERROR;
                            msg2.obj = e;
                            mHandler.sendMessage(msg2);

                        }
                        i++;
                        if (i == 3) {

                            //此错误表示已经进行了3次 webservice 请求,都失败,终止此次调用,回调 onTimeOut()
                            Message msg3 = mHandler.obtainMessage();
                            msg3.what = WebService.REQUEST_TIMEOUT;
                            mHandler.sendMessage(msg3);
                            return;

                        }
                    }
                    httpObject.setResultData(envelope.getResponse().toString());
                    String data = httpObject.getResultData();

                    if (data.length() == 1) {

                        //判断数据长度,如果长度为1 表示是验证请求,直接回调 validate
                        Message msg4 = mHandler.obtainMessage();
                        msg4.what = WebService.VALIDATE_COMPLETED;
                        msg4.obj = Integer.valueOf(data) == 0;
                        mHandler.sendMessage(msg4);
                        return;
                    }

                    //如果数据长度不为1,表示请求了别的数据,收到的数据先解密,然后放到 httpObject中返回
                    //httpObject中还封装了此次请求的参数的键值对,方法名
                    httpObject.setResultData(AES.Decrypt(data));
                    Message msg5 = mHandler.obtainMessage();
                    msg5.what = QUERY_COMPLETED;
                    msg5.obj = httpObject;
                    mHandler.sendMessage(msg5);

                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg6 = mHandler.obtainMessage();
                    msg6.what = TRY_CATCH_ERROR;
                    msg6.obj = e;
                    mHandler.sendMessage(msg6);
                }
            }
        });
    }

    public interface webServiceCallback {

        //验证过程中
        void onLoading();

        //验证授权
        void onValidateCompleted(boolean isValid);

        //出现错误
        void onTryCatchError(Exception e);

        //查询结果回调
        void onQueryCompleted(HttpObject obj);

        //超时回调
        void onRequestTimeout();

    }
}


