package com.jfeat.ext.plugin.store.bean;

import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.JsonKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryStoresApiResult extends ApiResult {

    public QueryStoresApiResult(String json) {
        super(json);
    }

    public static QueryStoresApiResult create(String json) {
        return new QueryStoresApiResult(json);
    }

    public Object getData() {
        return get("data");
    }

    public Map<String, Object> getDataMap() {
        if (getData() == null || getData() instanceof String) {
            return new HashMap<>();
        }
        String dataStr = JsonKit.toJson(getData());
        return JsonKit.parseObject(dataStr, Map.class);
    }

    public Assistant getAssistant() {
        return JsonKit.parseObject(JsonKit.toJson(getData()), Assistant.class);
    }

    public List<Store> getRecords() {
        Map<String, Object> dataMap = getDataMap();
        if (dataMap == null || dataMap.isEmpty()) {
            return new ArrayList<>();
        }
        Object records = dataMap.get("stores");
        String recordsStr = JsonKit.toJson(records);
        return JsonKit.parseArray(recordsStr, Store.class);
    }

}
