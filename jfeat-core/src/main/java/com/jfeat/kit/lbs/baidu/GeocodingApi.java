package com.jfeat.kit.lbs.baidu;

import com.jfeat.kit.lbs.baidu.model.GeoAddressResult;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ehngjen on 1/11/2016.
 */
public class GeocodingApi {
    private static String url = "http://api.map.baidu.com/geocoder/v2/";

    public static ApiResult location(double latitude, double longitude) throws UnsupportedEncodingException {
        ParaMap pm = ParaMap.create();
        pm.put("location", convert(latitude, longitude));
        pm.put("output", "json");
        pm.put("ak", ApiConfigKit.me().getApiConfig().getAk());
        String sn = SnCal.calc(pm.getData(), ApiConfigKit.me().getApiConfig().getSk());
        pm.put("sn", sn);
        return new GeoAddressResult(HttpKit.get(url, pm.getData()));
    }

    public static ApiResult address(String address) throws UnsupportedEncodingException {
        ParaMap pm = ParaMap.create();
        pm.put("address", address);
        pm.put("output", "json");
        pm.put("ak", ApiConfigKit.me().getApiConfig().getAk());
        String sn = SnCal.calc(pm.getData(), ApiConfigKit.me().getApiConfig().getSk());
        pm.put("sn", sn);
        return new ApiResult(HttpKit.get(url, pm.getData()));
    }

    private static String convert(double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();
        builder.append(latitude);
        builder.append(",");
        builder.append(longitude);
        return builder.toString();
    }

}
