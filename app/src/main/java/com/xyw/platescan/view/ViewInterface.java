package com.xyw.platescan.view;

import com.xyw.platescan.model.HttpObject;

/**
 * Created by 31429 on 2018/1/11.
 */

public interface ViewInterface {

    //验证过程中
    void onLoading();

    //验证授权
    void onValidateCompleted(boolean isValid, String imei);

    //出现错误
    void onTryCatchError(Exception e);

    //查询结果回调
    void onQueryCompleted(HttpObject obj);

    //超时回调
    void onRequestTimeout();

}
