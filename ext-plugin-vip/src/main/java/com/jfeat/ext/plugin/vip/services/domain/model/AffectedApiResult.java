package com.jfeat.ext.plugin.vip.services.domain.model;

import com.jfeat.ext.plugin.ApiResult;

//返回结果只有一个affected的ApiResult，比如：
//        {
//        code:200,
//        data:1,
//        message:'操作成功'
//        }
public class AffectedApiResult extends ApiResult {

    public AffectedApiResult(String json) {
        super(json);
    }

    public static AffectedApiResult create(String json) {
        return new AffectedApiResult(json);
    }

    public Integer getData() {
        return (Integer) get("data");
    }

}
