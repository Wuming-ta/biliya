package com.jfeat.ext.plugin.wms;

import com.alibaba.fastjson.JSONObject;

/**
 * @author jackyhuang
 * @date 2018/12/18
 */
public interface WmsHandler {
    public void handle(JSONObject data);
}
