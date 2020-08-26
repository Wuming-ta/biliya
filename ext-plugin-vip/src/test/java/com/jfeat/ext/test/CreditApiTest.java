package com.jfeat.ext.test;

import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.VipPlugin;
import com.jfeat.ext.plugin.vip.CreditApi;
import com.jfeat.ext.plugin.vip.VipApi;
import com.jfeat.ext.plugin.vip.bean.VipAccount;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author jackyhuang
 * @date 2018/8/28
 */
public class CreditApiTest {

    @BeforeClass
    public static void beforeClass() {
        VipPlugin vipPlugin = new VipPlugin(true,
                "http://120.79.77.207:8080",
                "L7A/6zARSkK1j7Vd5SDD9pSSqZlqF7mAhiOgRbgv9Smce6tf4cJnvKOjtKPxNNnWQj+2lQEScm3XIUjhW+YVZg==");
        ExtPluginHolder.me().start(VipPlugin.class, vipPlugin);
    }

    @Test
    @Ignore
    public void testUpdateCreditByCharge() {
        CreditApi api = new CreditApi();
        ApiResult apiResult = api.updateAccountCreditCharge("13922112130", BigDecimal.valueOf(23));
        System.out.println(apiResult);
    }

}
