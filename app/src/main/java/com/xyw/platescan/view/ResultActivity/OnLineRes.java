package com.xyw.platescan.view.ResultActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xyw.platescan.R;
import com.xyw.platescan.model.CarStatus;
import com.xyw.platescan.model.HttpObject;
import com.xyw.platescan.presenter.ResultView.ResultPresenter;
import com.xyw.platescan.presenter.StartView.StartCheckerPresenter;
import com.xyw.platescan.util.ExpandableAdapter;
import com.xyw.platescan.util.WebService;
import com.xyw.platescan.view.BaseActivity;

/**
 * Created by 31429 on 2018/1/11.
 */

public class OnLineRes extends BaseActivity implements OnlineViewInterface {

    private ProgressBar progressBar;

    private ExpandableListView listView;

    private TextView textView;

    private CarStatus carStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_avtivity);
        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.online_listview);
        textView = findViewById(R.id.textView6);
        StartCheckerPresenter.forceValidate(this, new WebService.webServiceCallback() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onValidateCompleted(boolean isValid) {
                if (!isValid) {
                    StartCheckerPresenter.wipeAuth(OnLineRes.this);
                }
            }

            @Override
            public void onTryCatchError(Exception e) {

            }

            @Override
            public void onQueryCompleted(HttpObject obj) {

            }

            @Override
            public void onRequestTimeout() {

            }
        });
        carStatus = (CarStatus) getIntent().getSerializableExtra("online");
        ResultPresenter presenter = new ResultPresenter(this);
        presenter.doQuery(carStatus);
    }


    @Override
    public void onLoading() {
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onQueryCompleted(HttpObject obj) {
        progressBar.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);

        carStatus = new Gson().fromJson(obj.getResultData(), CarStatus.class);
        String data = carStatus.getTxzbh() + "|" + carStatus.getHpzl() + "|" + carStatus.getHphm() + "|" + carStatus.getTxlx() + "|" + carStatus.getYxqs().substring(0, 10)
                + "|" + carStatus.getYxqz().substring(0, 10) + "|" + carStatus.getTxzlx() + "|" + carStatus.getCllx() + "|" + carStatus.getTxqy() + "|" + carStatus.getTxmd()
                + "|" + carStatus.getZxzd() + "|" + carStatus.getHwmc() + "|" + carStatus.getHwzl() + "|" + carStatus.getJsrxm() + "|" + carStatus.getSqrsfzmhm()
                + "|" + carStatus.getJszlxfs();
        String[] dataArray = data.split("\\|");
        String[][] childData = new String[][]{
                {dataArray[13], dataArray[14], dataArray[15]},
                {dataArray[0], dataArray[6], dataArray[2], dataArray[1], dataArray[7]},
                {dataArray[4], dataArray[5], dataArray[3], dataArray[8], dataArray[9], dataArray[11], dataArray[12]}
        };
        ExpandableAdapter expandableAdapter = new ExpandableAdapter(childData, OnLineRes.this);
        listView.setAdapter(expandableAdapter);
        for (int i = 0; i < expandableAdapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
    }

    @Override
    public void onTryCatchError(Exception e) {
        progressBar.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.INVISIBLE);
        textView.setText(e.getMessage());
    }

    @Override
    public void onRequestTimeout() {
        progressBar.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.INVISIBLE);
        textView.setText("网络超时,请重试");
    }
}
