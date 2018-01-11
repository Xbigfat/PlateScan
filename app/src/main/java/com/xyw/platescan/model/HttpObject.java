package com.xyw.platescan.model;

/**
 * Soap 协议外层 http 数据对象
 * <p>
 * Created by 31429 on 2018/1/11.
 */

public class HttpObject {
    /**
     * 参数值数组
     */
    private String[] valueArray;
    /**
     * 调用方法名称
     */
    private String methodName;
    /**
     * 参数名字数组
     */
    private String[] keyArray;
    /**
     * 返回数据
     */
    private String resultData;

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public String[] getValueArray() {
        return valueArray;
    }

    public void setValueArray(String[] valueArray) {
        this.valueArray = valueArray;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getKeyArray() {
        return keyArray;
    }

    public void setKeyArray(String[] keyArray) {
        this.keyArray = keyArray;
    }
}
