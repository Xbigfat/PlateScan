package com.xyw.platescan.view.ResultActivity;

import com.xyw.platescan.model.HttpObject;
import com.xyw.platescan.view.BaseViewInterface;

/**
 * Created by 31429 on 2018/1/12.
 */

public interface OnlineViewInterface extends BaseViewInterface {

    //查询结果回调
    void onQueryCompleted(HttpObject obj);

}
