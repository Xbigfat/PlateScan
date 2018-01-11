package com.xyw.platescan.view.Start;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.xyw.platescan.R;
import com.xyw.platescan.model.HttpObject;
import com.xyw.platescan.presenter.Cherker;
import com.xyw.platescan.util.Parameters;
import com.xyw.platescan.view.BaseActivity;
import com.xyw.platescan.view.ViewInterface;
import com.xyw.platescan.zencryption.AES;

/**
 * 开始界面.程序主页
 * Created by 31429 on 2018/1/11.
 * <p>
 * 记录执行流程:<br>
 * BaseActivity.onCreate() -> <br>
 * controller.register() -> 注册管理器 <br>
 * StartActivity.onCreate() -> setContentView() -> <br>
 * initView() -> 绑定控件 <br>
 * functionInvalid() -> 设置控件不可用 <br>
 * api >= 23 checkPermission() -> requestPermission() ->  动态权限申请 <br>
 * quit()  -> 未获得权限退出 (onDenied) <br>
 * check.validate() -> 检查授权序列号 <br>
 * validate() callback -> 检查结果回调 <br>
 * <p>
 * <pre>
 * webservice | repost to | viewinterface
 * onError          ->      functionInvalid()
 * onQueryCompleted      ->      valid() or invalid()
 * onLoading        ->      inValid()
 * </pre>
 */

public class StartActivity extends BaseActivity implements ViewInterface, Cherker.checkListener, View.OnClickListener {

    private Cherker cherker;
    /**
     * 车辆类型spinner
     */
    private Spinner hpzlSpinner;
    /**
     * 号牌号码 editText
     */
    private EditText hphmEt;
    /**
     * 查询 btn_interact_selector
     */
    private Button queryBtn;
    /**
     * 扫一扫 btn_interact_selector
     */
    private Button scanBtn;
    /**
     * 提示信息 textView
     */
    private TextView tipsTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        initView();
        //首先将功能禁用
        setFunction(false, null);
        //初始化检查器
        cherker = new Cherker(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API >= 23 动态权限验证 -> 功能授权
            cherker.checkPermission();
        } else {
            // API < 23 直接进行功能授权
            cherker.validate();
        }
    }

    private void initView() {
        hpzlSpinner = findViewById(R.id.hpzl_spinner);
        hphmEt = findViewById(R.id.hphm_et);
        queryBtn = findViewById(R.id.query_btn);
        queryBtn.setOnClickListener(this);
        scanBtn = findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(this);
        tipsTv = findViewById(R.id.tip);
        TextView changeTv = findViewById(R.id.change_server_tv);
        changeTv.setOnClickListener(this);
        ArrayAdapter<String> hplxAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Parameters.hplx);
        hpzlSpinner.setAdapter(hplxAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cherker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void Denied() {
        makeToast(this, "未能获取到需要的权限,请尝试在系统菜单授权");
        controller.quit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.query_btn:
                //doQuery();
                break;
            case R.id.scan_btn:
                IntentIntegrator intentIntegrator = new IntentIntegrator(StartActivity.this);
                //intentIntegrator.setPrompt("     请将扫描区域对准二维码\n按下音量键开启或者关闭闪光灯").initiateScan();
                intentIntegrator.initiateScan();
                break;
            case R.id.change_server_tv:
                //changeServerGlobal(MainActivity.this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "用户取消扫描", Toast.LENGTH_LONG).show();
            } else {
                try {
                    String scanResult = result.getContents();
                    scanResult = AES.Decrypt(scanResult);
                    Log.i(Parameters.TAG, scanResult);
                    //Intent intent = new Intent(MainActivity.this, ScanProviderOffline.class);
                    //intent.putExtra("data", scanResult);
                    //startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "非法的二维码信息", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onLoading() {
        tipsTv.setText("正在获取授权...");
    }

    @Override
    public void onValidateCompleted(boolean isValid, String imei) {
        setFunction(isValid, imei);

    }

    @Override
    public void onTryCatchError(Exception e) {
        e.printStackTrace();
        tipsTv.setText("soap Fault !");
    }

    @Override
    public void onQueryCompleted(HttpObject obj) {

    }

    @Override
    public void onRequestTimeout() {
        tipsTv.setText("网络出了点小问题...");
    }

    public void setFunction(boolean b, String imei) {
        if (b) {
            scanBtn.setEnabled(true);
            queryBtn.setEnabled(true);
            tipsTv.setText("授权完成");
        } else {
            scanBtn.setEnabled(false);
            queryBtn.setEnabled(false);
            tipsTv.setText("授权失败了 -> " + (imei == null ? "" : imei));
        }


    }
}
