package com.jfeat.ext.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2019/1/26
 */
public abstract class BaseApi {
    private BasePlugin plugin;

    public void init(BasePlugin plugin) {
        this.plugin = plugin;
    }

    public String getBaseUrl() {
        return plugin.getBaseUrl();
    }

    public Map<String, String> getAuthorizationHeader() {
        return getAuthorizationHeader(null, null, "");
    }

    public Map<String, String> getAuthorizationHeader(Long tenantId, Long userId, String account) {
        if (plugin == null) {
            return null;
        }
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + this.plugin.getAccessToken(tenantId, userId, account));
        return header;
    }
}
