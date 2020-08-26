package com.jfeat.ext.plugin;

/**
 * @author jackyhuang
 * @date 2018/6/13
 */
public abstract class BasePlugin {

    private JWTService jwtService;
    private boolean enabled = false;
    private String baseUrl;

    public boolean isEnabled() {
        return enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAccessToken() {
        if (!enabled) {
            return null;
        }
        return jwtService.createToken(null, null, "");
    }

    public String getAccessToken(Long tenantId, Long userId, String account) {
        return jwtService.createToken(tenantId, userId, account);
    }

    public BasePlugin(boolean enabled, String baseUrl, String encodedKey) {
        this.enabled = enabled;
        this.baseUrl = baseUrl;
        jwtService = new JWTService();
        jwtService.init(encodedKey);
    }

    public BasePlugin(boolean enabled, String baseUrl, String encodedKey, Long ttlMillis) {
        this.enabled = enabled;
        this.baseUrl = baseUrl;
        jwtService = new JWTService();
        jwtService.init(encodedKey, ttlMillis);
    }

}
