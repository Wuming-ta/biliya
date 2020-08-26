package com.jfeat.wechat.sdk.api;

import com.jfinal.weixin.sdk.api.ReturnCode;
import com.jfinal.weixin.sdk.utils.JsonUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/7/20
 */
public class AuthResult {
    private Map<String, Object> attrs;
    private String json;

    public AuthResult(String jsonStr) {
        this.json = jsonStr;

        try {
            Map e = (Map) JsonUtils.parse(jsonStr, Map.class);
            this.attrs = e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static AuthResult create(String jsonStr) {
        return new AuthResult(jsonStr);
    }

    public String getJson() {
        return this.json;
    }

    @Override
    public String toString() {
        return this.getJson();
    }

    public boolean isSucceed() {
        Integer errorCode = this.getErrorCode();
        return errorCode == null || errorCode.intValue() == 0;
    }

    public Integer getErrorCode() {
        return this.getInt("errcode");
    }

    public String getErrorMsg() {
        Integer errorCode = this.getErrorCode();
        if(errorCode != null) {
            String result = ReturnCode.get(errorCode.intValue());
            if(result != null) {
                return result;
            }
        }

        return (String)this.attrs.get("errmsg");
    }

    public String getStr(String name) {
        return (String)this.attrs.get(name);
    }

    public Integer getInt(String name) {
        Number number = (Number)this.attrs.get(name);
        return number == null?null:Integer.valueOf(number.intValue());
    }

    public Long getLong(String name) {
        Number number = (Number)this.attrs.get(name);
        return number == null?null:Long.valueOf(number.longValue());
    }

    public BigInteger getBigInteger(String name) {
        return (BigInteger)this.attrs.get(name);
    }

    public Double getDouble(String name) {
        return (Double)this.attrs.get(name);
    }

    public BigDecimal getBigDecimal(String name) {
        return (BigDecimal)this.attrs.get(name);
    }

    public Boolean getBoolean(String name) {
        return (Boolean)this.attrs.get(name);
    }

    public List getList(String name) {
        return (List)this.attrs.get(name);
    }

    public Map getMap(String name) {
        return (Map)this.attrs.get(name);
    }
}