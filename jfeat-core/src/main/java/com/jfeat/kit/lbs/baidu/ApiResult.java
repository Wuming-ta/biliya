package com.jfeat.kit.lbs.baidu;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by ehngjen on 1/11/2016.
 */
public class ApiResult {
    private Map<String, Object> attrs;
    private String json;

    /**
     * 通过 json 构造 ApiResult
     */
    public ApiResult(String jsonStr) {
        this.json = jsonStr;

        try {
            Map<String, Object> temp = JsonUtils.decode(jsonStr, Map.class);
            this.attrs = temp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过 json 创建 ApiResult 对象，等价于 new ApiResult(jsonStr)
     */
    public static ApiResult create(String jsonStr) {
        return new ApiResult(jsonStr);
    }


    public Map getAttrs() {
        return attrs;
    }

    public String getJson() {
        return json;
    }

    public String toString() {
        return getJson();
    }

    public boolean isSucceed() {
        return getStatus() == 0;
    }

    public Integer getStatus() {
        return (Integer)attrs.get("status");
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T)attrs.get(name);
    }

    public String getStr(String name) {
        return (String)attrs.get(name);
    }

    public Integer getInt(String name) {
        return (Integer)attrs.get(name);
    }

    public Long getLong(String name) {
        return (Long)attrs.get(name);
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
}
