package com.jfeat.ext.plugin.vip.services.domain.model;

import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.JsonKit;

public class MemberApiResult extends ApiResult {


    public MemberApiResult(String json) {
        super(json);
    }

    public static MemberApiResult create(String json) {
        return new MemberApiResult(json);
    }

    public Member getData() {
        Object dataObject = get("data");
        String dataObjectStr = JsonKit.toJson(dataObject);
        return JsonKit.parseObject(dataObjectStr, Member.class);
    }

}
