package com.xyw.platescan.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.xyw.platescan.util.ActivityController;

/**
 * Created by 31429 on 2018/1/11.
 */

public class BaseActivity extends AppCompatActivity {
    private static Toast mToast;
    protected ActivityController controller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = ActivityController.getController();
        controller.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.remove(this);
    }

    public ActivityController getController() {
        return controller;
    }

    /**
     * 展示土司
     *
     * @param activity 需要展示吐司的界面
     * @param msg      提示内容
     */
    @SuppressLint("ShowToast")
    public void makeToast(Activity activity, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(activity, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

}
