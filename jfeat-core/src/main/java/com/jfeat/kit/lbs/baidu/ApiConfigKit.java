package com.jfeat.kit.lbs.baidu;

/**
 * Created by ehngjen on 1/11/2016.
 */
public class ApiConfigKit {
    private static ApiConfigKit me = new ApiConfigKit();

    private ApiConfig apiConfig;

    private ApiConfigKit() {
        apiConfig = new ApiConfig();
    }

    public static ApiConfigKit me() {
        return me;
    }

    public ApiConfig getApiConfig() {
        return apiConfig;
    }

    public void setAk(String ak) {
        apiConfig.setAk(ak);
    }

    public void setSk(String sk) {
        apiConfig.setSk(sk);
    }
}
