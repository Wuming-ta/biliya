package com.jfeat.ext.plugin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/6/13
 */
public class ApiResult implements Serializable {
    private Map<String, Object> attrs;
    private String json;

    public ApiResult(String json) {
        this.json = json;
        try {
            attrs = JsonKit.parseObject(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static ApiResult create(String json) {
        return new ApiResult(json);
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return getJson();
    }


    /**
     * APi 请求是否成功返回
     * @return {boolean}
     */
    public boolean isSucceed() {
        Integer errorCode = getCode();
        // errorCode 为 200 时也可以表示为成功，
        return (errorCode == 200);
    }

    public Integer getCode() {
        return getInt("code");
    }

    public String getMessage() {
        return (String)attrs.get("message");
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T)attrs.get(name);
    }

    public String getStr(String name) {
        return (String)attrs.get(name);
    }

    public Integer getInt(String name) {
        Number number = (Number) attrs.get(name);
        return number == null ? null : number.intValue();
    }

    public Long getLong(String name) {
        Number number = (Number) attrs.get(name);
        return number == null ? null : number.longValue();
    }

    public BigInteger getBigInteger(String name) {
        return (BigInteger)attrs.get(name);
    }

    public Double getDouble(String name) {
        return (Double)attrs.get(name);
    }

    public BigDecimal getBigDecimal(String name) {
        return (BigDecimal)attrs.get(name);
    }

    public Boolean getBoolean(String name) {
        return (Boolean)attrs.get(name);
    }

    @SuppressWarnings("rawtypes")
    public List getList(String name) {
        return (List)attrs.get(name);
    }

    @SuppressWarnings("rawtypes")
    public Map getMap(String name) {
        return (Map)attrs.get(name);
    }

    public Map<String, Object> getAttrs(){
        return this.attrs;
    }

}
