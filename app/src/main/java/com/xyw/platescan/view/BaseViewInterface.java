package com.xyw.platescan.view;

/**
 * View相关的基类接口,最基本的3个接口,每个view都需要其基本实现
 * Created by 31429 on 2018/1/12.
 */

public interface BaseViewInterface {
    //验证过程中
    void onLoading();

    //出现错误 -> 此 exception 是来自 WebService 中的 try catch error, 基本上都是 soap fault
    void onTryCatchError(Exception e);

    //超时回调 -> 超时表示 3次 webservice 请求都失败了,debug可看到具体原因.
    void onRequestTimeout();

}
