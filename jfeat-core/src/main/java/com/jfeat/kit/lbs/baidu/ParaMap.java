package com.jfeat.kit.lbs.baidu;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ehngjen on 1/11/2016.
 */
public class ParaMap {
    private Map<String, String> data = new LinkedHashMap<String, String>();
    private ParaMap() {}

    public static ParaMap create() {
        return new ParaMap();
    }

    public static ParaMap create(String key, String value) {
        return create().put(key, value);
    }

    public ParaMap put(String key, String value) {
        data.put(key, value);
        return this;
    }

    public Map<String, String> getData() {
        return data;
    }
}
