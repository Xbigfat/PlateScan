package com.xyw.platescan.view.Start;

import com.xyw.platescan.view.BaseViewInterface;

/**
 * Created by 31429 on 2018/1/11.
 */

public interface StartActivityInterface extends BaseViewInterface {

    //验证授权
    void onValidateCompleted(boolean isValid, String imei);

    //动态权限申请失败了...大概是要退出程序了吧~
    void onPermissionDenied();
    //TODO 此接口是否考虑增加一个 VersionCheck 功能啊?
}
