package com.xyw.platescan.util;

import java.util.regex.Pattern;

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


}
