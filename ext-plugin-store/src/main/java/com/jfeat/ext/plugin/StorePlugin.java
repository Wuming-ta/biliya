package com.jfeat.ext.plugin;

import com.jfeat.core.ServiceContext;
import com.jfeat.ext.plugin.store.AppointmentPayService;
import com.jfeat.ext.plugin.store.AppointmentPayServiceImpl;

/**
 * @author jackyhuang
 * @date 2018/6/14
 */
public class StorePlugin extends BasePlugin {

    public StorePlugin(boolean enabled, String baseUrl, String encodedKey) {
        super(enabled, baseUrl, encodedKey);
        ServiceContext.me().register(AppointmentPayService.name, new AppointmentPayServiceImpl());
    }
}
