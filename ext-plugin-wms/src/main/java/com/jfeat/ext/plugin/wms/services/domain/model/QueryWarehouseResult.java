package com.jfeat.ext.plugin.wms.services.domain.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.JsonKit;

import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/11/30
 */
public class QueryWarehouseResult extends ApiResult {
    public QueryWarehouseResult(String json) {
        super(json);
    }

    public static QueryWarehouseResult create(String json) {
        return new QueryWarehouseResult(json);
    }

    public List<Warehouse> getWarehouses() {
        JSONArray records = get("data");
        String recordsStr = JsonKit.toJson(records);
        return JsonKit.parseArray(recordsStr, Warehouse.class);
    }
}
