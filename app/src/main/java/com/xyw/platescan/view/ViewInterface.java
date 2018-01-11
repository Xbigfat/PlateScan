package com.xyw.platescan.view;

import com.xyw.platescan.model.CarStatus;

/**
 * Created by 31429 on 2018/1/11.
 */

public interface ViewInterface {

    //权限获取失败
    void functionInvalid(String msg);

    //权限获取成功
    void functionValid(Object obj);

}
