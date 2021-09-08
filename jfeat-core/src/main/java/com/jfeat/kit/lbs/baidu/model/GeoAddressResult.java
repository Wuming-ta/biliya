package com.jfeat.kit.lbs.baidu.model;

import com.jfeat.kit.lbs.baidu.ApiResult;
import com.jfeat.kit.lbs.baidu.JsonUtils;

import java.util.Map;

/**
 * Created by ehngjen on 1/22/2016.
 */
public class GeoAddressResult extends ApiResult {
    /**
     * 通过 json 构造 ApiResult
     *
     * @param jsonStr
     */
    public GeoAddressResult(String jsonStr) {
        super(jsonStr);
    }

    public String getFormattedAddress() {
        Map result = get("result");
        if (result != null) {
            return JsonUtils.toJson(result.get("formatted_address"));
        }
        return null;
    }

    public Object getFormattedAddressObject() {
        Map result = get("result");
        if (result != null) {
            return result.get("formatted_address");
        }
        return null;
    }

    public String getAddressComponent() {
        Map result = get("result");
        if (result != null) {
            return JsonUtils.toJson(result.get("addressComponent"));
        }
        return null;
    }

    public Object getAddressComponentObject() {
        Map result = get("result");
        if (result != null) {
            return result.get("addressComponent");
        }
        return null;
    }
}
