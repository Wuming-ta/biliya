package com.jfeat.ext.plugin.vip.services.domain.model;

import com.jfeat.ext.plugin.ApiResult;

//返回结果是一个number的ApiResult，比如：
//        {
//        code:200,
//        data:33.2,
//        message:'操作成功'
//        }
public class NumberApiResult extends ApiResult {


    public NumberApiResult(String json) {
        super(json);
    }

    public static NumberApiResult create(String json) {
        return new NumberApiResult(json);
    }

    public Number getData() {
        return (Number) get("data");
    }

    public Byte getDataByte() {
        return getData().byteValue();
    }

    public Short getDataShort() {
        return getData().shortValue();
    }

    public Integer getDataInt() {
        return getData().intValue();
    }

    public Long getDataLong() {
        return getData().longValue();
    }

    public Float getDataFloat() {
        return getData().floatValue();
    }

    public Double getDataDouble() {
        return getData().doubleValue();
    }

}
