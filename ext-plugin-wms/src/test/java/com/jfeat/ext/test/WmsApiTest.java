package com.jfeat.ext.test;

import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.WmsPlugin;
import com.jfeat.ext.plugin.wms.WmsApi;
import com.jfeat.ext.plugin.wms.services.domain.model.QueryWarehouseResult;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jackyhuang
 * @date 2018/11/30
 */
public class WmsApiTest {


    @BeforeClass
    public static void beforeClass() {
        WmsPlugin wmsPlugin = new WmsPlugin(true,
                "http://120.79.77.207:8080",
                "L7A/6zARSkK1j7Vd5SDD9pSSqZlqF7mAhiOgRbgv9Smce6tf4cJnvKOjtKPxNNnWQj+2lQEScm3XIUjhW+YVZg==");
        ExtPluginHolder.me().start(WmsPlugin.class, wmsPlugin);
    }

    @Test
    @Ignore
    public void testQueryWarehouses() {
        WmsApi wmsApi = new WmsApi();
        QueryWarehouseResult result = wmsApi.queryWarehouse();
        System.out.println(result.getWarehouses());
    }
}
