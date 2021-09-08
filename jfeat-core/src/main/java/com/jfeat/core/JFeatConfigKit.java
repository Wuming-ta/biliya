package com.jfeat.core;

/**
 * Created by huangjacky on 16/6/1.
 */
public class JFeatConfigKit {
    private JFeatConfig jFeatConfig;
    private static JFeatConfigKit me = new JFeatConfigKit();
    public static JFeatConfigKit me() {
        return me;
    }
    public void init(JFeatConfig jFeatConfig) {
        this.jFeatConfig = jFeatConfig;
    }
    public String getProperty(String key) {
        if (jFeatConfig == null) {
            throw new RuntimeException("JFeatConfig is not set.");
        }
        return jFeatConfig.getProperty(key);
    }
}
