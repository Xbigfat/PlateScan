package com.xyw.platescan.presenter.ResultView;


import android.content.Context;

import com.xyw.platescan.model.CarStatus;
import com.xyw.platescan.model.HttpObject;
import com.xyw.platescan.util.WebService;
import com.xyw.platescan.view.ResultActivity.OnlineViewInterface;

/**
 * Created by 31429 on 2018/1/11.
 */

public class ResultPresenter {

    private OnlineViewInterface onlineViewInterface;


    public ResultPresenter(Object object) {
        this.onlineViewInterface = (OnlineViewInterface) object;
    }

    public void doQuery(final CarStatus carStatus) {
        HttpObject obj = new HttpObject();
        obj.setKeyArray(new String[]{"hpzl", "hphm"});
        obj.setValueArray(new String[]{carStatus.getHpzl(), carStatus.getHphm()});
        obj.setMethodName("sel_txzxx_by_hphm");
        WebService webService = new WebService((Context) onlineViewInterface, obj);
        webService.doRequest(new WebService.webServiceCallback() {
            @Override
            public void onLoading() {
                onlineViewInterface.onLoading();
            }

            @Override
            public void onValidateCompleted(boolean isValid) {

            }

            @Override
            public void onTryCatchError(Exception e) {
                onlineViewInterface.onTryCatchError(e);
            }

            @Override
            public void onQueryCompleted(HttpObject obj) {
                onlineViewInterface.onQueryCompleted(obj);
            }

            @Override
            public void onRequestTimeout() {
                onlineViewInterface.onRequestTimeout();
            }
        });
    }

}
