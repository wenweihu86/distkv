package com.github.wenweihu86.distkv.example.vo;

/**
 * Created by wenweihu86 on 2017/6/10.
 */
public class KVGetVo {
    private int resCode;
    private String resMsg;
    private String key;
    private String value;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
