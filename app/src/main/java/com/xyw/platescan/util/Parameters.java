package com.xyw.platescan.util;

/**
 * 系统默认参数表
 * Created by 31429 on 2018/1/11.
 */

public class Parameters {

    public static final String TAG = "scan plate log tag";
    /**
     * 命名空间
     */
    public static String nameSpace = "http://tempuri.org";
    /**
     * 默认IP地址
     */
    public static String ip = "60.174.83.217";
    /**
     * 默认端口号
     */
    public static String port = "9095";
    /**
     * 默认服务名称
     */
    public static String service = "vehpassweb";
    /**
     * 服务文件
     */
    public static String path = "/SearchService.asmx";
    /**
     * 默认服务地址
     */
    public static String defaultEndPoint = "http://" + ip + ":" + port + "/" + service + path;

    public static final String[] hplx = {"01 大型汽车", "13	低速车", "15	挂车", "22	临时行驶车", "51	大型新能源汽车"};
}
