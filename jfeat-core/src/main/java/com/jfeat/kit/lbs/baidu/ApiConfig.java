package com.jfeat.kit.lbs.baidu;

/**
 * Created by ehngjen on 1/11/2016.
 */
public class ApiConfig {
    private String ak;
    private String sk;

    public ApiConfig() {

    }

    public ApiConfig(String ak, String sk) {
        this.ak = ak;
        this.sk = sk;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }
}
