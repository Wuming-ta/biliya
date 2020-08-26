package com.jfeat.ext.plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jackyhuang
 * @date 2018/7/6
 */
public class ExtPluginHolder {

    private ExtPluginHolder() {

    }

    private static ExtPluginHolder me = new ExtPluginHolder();

    public static ExtPluginHolder me() {
        return me;
    }

    private Map<Class<? extends BasePlugin>, BasePlugin> map = new ConcurrentHashMap<>();
    private BasePlugin defaultPlugin = new BasePlugin(false, null, null) {
    };

    public void start(Class<? extends BasePlugin> clazz, BasePlugin plugin) {
        map.put(clazz, plugin);
    }

    public BasePlugin get(Class<? extends BasePlugin> clazz) {
        BasePlugin plugin = map.get(clazz);
        if (plugin != null) {
            return plugin;
        }
        return defaultPlugin;
    }
}
