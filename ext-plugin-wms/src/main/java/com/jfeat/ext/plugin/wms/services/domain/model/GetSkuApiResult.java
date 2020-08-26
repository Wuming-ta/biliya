package com.jfeat.ext.plugin.wms.services.domain.model;

import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.JsonKit;

public class GetSkuApiResult extends ApiResult {

    public GetSkuApiResult(String json) {
        super(json);
    }

    public static GetSkuApiResult create(String json) {
        return new GetSkuApiResult(json);
    }

    public Sku getData() {
        Object dataObject = get("data");
        String dataObjectStr = JsonKit.toJson(dataObject);
        return JsonKit.parseObject(dataObjectStr, Sku.class);
    }

}
