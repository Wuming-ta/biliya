package com.jfeat.ext.test;

import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.StorePlugin;
import com.jfeat.ext.plugin.store.AppointmentApi;
import com.jfeat.ext.plugin.store.bean.Appointment;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jackyhuang
 * @date 2018/8/28
 */
public class AppointmentApiTest {

    @BeforeClass
    public static void beforeClass() {
        StorePlugin storePlugin = new StorePlugin(true,
                "http://120.79.77.207:8080",
                "L7A/6zARSkK1j7Vd5SDD9pSSqZlqF7mAhiOgRbgv9Smce6tf4cJnvKOjtKPxNNnWQj+2lQEScm3XIUjhW+YVZg==");
        ExtPluginHolder.me().start(StorePlugin.class, storePlugin);
    }

    @Test
    @Ignore
    public void testGetAppointment() {
        AppointmentApi api = new AppointmentApi();
        Appointment appointment = api.getAppointment("APT2018270800027Zqo");
        System.out.println(appointment);
    }

    @Test
    @Ignore
    public void testPayAppointment() {
        AppointmentApi api = new AppointmentApi();
        api.payNotify("APT2018270800027Zqo", "WECHAT", "11101");
    }
}
