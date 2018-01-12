package com.xyw.platescan.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.xyw.platescan.R;
import com.xyw.platescan.presenter.StartView.StartCheckerPresenter;
import com.xyw.platescan.view.Start.StartActivity;

import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 31429 on 2018/1/11.
 */

public class Utils {

    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    /**
     * 判定是否为合法的IPv4地址
     *
     * @param ip ip地址
     * @return 合法返回true
     */
    public static boolean isIPv4Address(final String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * 全局修改系统服务的地址信息
     *
     * @param context 修改的上下文位置
     */
    public static void changeServerGlobal(final Context context, final StartCheckerPresenter presenter) {
        final StartActivity mContext = (StartActivity) context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View serverDialog = layoutInflater.inflate(R.layout.dialog_change_server, null);
        final SharedPreferences serviceip = context.getSharedPreferences("ip", MODE_PRIVATE);
        final EditText setIp = (EditText) serverDialog.findViewById(R.id.dialog_server_adress);
        final EditText setPort = (EditText) serverDialog.findViewById(R.id.dialog_server_port);
        final EditText setService = (EditText) serverDialog.findViewById(R.id.dialog_method_name);
        if (serviceip == null) {
            setIp.setText(Parameters.ip);
            setPort.setText(Parameters.port);
            setService.setText(Parameters.service);
        } else {
            if (!serviceip.contains("ip") || !serviceip.contains("port") || !serviceip.contains("service")) {
                setIp.setText(Parameters.ip);
                setPort.setText(Parameters.port);
                setService.setText(Parameters.service);
            } else {
                setIp.setText(serviceip.getString("ip", Parameters.ip));
                setPort.setText(serviceip.getString("port", Parameters.port));
                setService.setText(serviceip.getString("service", Parameters.service));
            }
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(serverDialog)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = setIp.getText().toString().trim();
                String port = setPort.getText().toString().trim();
                String service = setService.getText().toString().trim();
                if (!isIPv4Address(ip)) {
                    mContext.makeToast(mContext, "IP地址不合法！");
                    return;
                }
                if ("".equals(port) || "".equals(service)) {
                    mContext.makeToast(mContext, "配置出错，请检查！");
                    return;
                }
                try {
                    SharedPreferences.Editor editor = serviceip.edit();
                    editor.putString("ip", ip);
                    editor.putString("port", port);
                    editor.putString("service", service);
                    editor.apply();
                } catch (Exception e) {
                    mContext.makeToast(mContext, e.getMessage());
                } finally {
                    alertDialog.dismiss();
                }
                mContext.makeToast(mContext, "服务器地址成功更改为：\n http://" + ip + ":" + port + "/" + service + "\n请重新打开应用");
                mContext.getController().quit();
                presenter.wipeAuth(mContext);
            }
        });
    }

}
