package com.jfeat.ext.plugin.wms.services.domain.model;

import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.JsonKit;

import java.util.List;
import java.util.Map;

public class GetInventoryApiResult extends ApiResult {

    public GetInventoryApiResult(String json) {
        super(json);
    }

    public static GetInventoryApiResult create(String json) {
        return new GetInventoryApiResult(json);
    }

    public Object getData() {
        return get("data");
    }


    public Map<String, Object> getDataMap() {
        String dataStr = JsonKit.toJson(getData());
        return JsonKit.parseObject(dataStr, Map.class);
    }

    public int getCurrent() {
        Map<String, Object> dataMap = getDataMap();
        return (int) dataMap.get("current");
    }

    public int getTotal() {
        Map<String, Object> dataMap = getDataMap();
        return (int) dataMap.get("total");
    }

    public int getSize() {
        Map<String, Object> dataMap = getDataMap();
        return (int) dataMap.get("size");
    }

    public int getPages() {
        Map<String, Object> dataMap = getDataMap();
        return (int) dataMap.get("pages");
    }

    public List<Inventory> getRecords() {
        Map<String, Object> dataMap = getDataMap();
        Object records = dataMap.get("records");
        String recordsStr = JsonKit.toJson(records);
        return JsonKit.parseArray(recordsStr, Inventory.class);
    }

}
