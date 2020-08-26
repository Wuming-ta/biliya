package com.jfeat.ext.test;

import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.VipPlugin;
import com.jfeat.ext.plugin.vip.VipApi;
import com.jfeat.ext.plugin.vip.bean.Grade;
import com.jfeat.ext.plugin.vip.bean.VipAccount;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jackyhuang
 * @date 2018/8/28
 */
public class VipApiTest {

    @BeforeClass
    public static void beforeClass() {
        VipPlugin vipPlugin = new VipPlugin(true,
                "http://120.79.77.207:8080",
                "L7A/6zARSkK1j7Vd5SDD9pSSqZlqF7mAhiOgRbgv9Smce6tf4cJnvKOjtKPxNNnWQj+2lQEScm3XIUjhW+YVZg==");
        ExtPluginHolder.me().start(VipPlugin.class, vipPlugin);
    }

    @Test
    @Ignore
    public void testCreateVipAccount() {
        VipApi api = new VipApi();
        VipAccount vipAccount = new VipAccount();
        vipAccount.setAccount("13922112130").setVipName("Huang").setRegisterMobile("13922112130");
        ApiResult apiResult = api.createVipAccount(vipAccount);
        System.out.println(apiResult);
    }

    @Test
    @Ignore
    public void testUpdateVipAccount() {
        VipApi api = new VipApi();
        VipAccount vipAccount = new VipAccount();
        vipAccount.setAccount("13922112130").setVipName("Huang");
        ApiResult apiResult = api.updateVipAccount(vipAccount);
        System.out.println(apiResult);
    }

    @Test
    @Ignore
    public void testGetVipAccount() {
        VipApi api = new VipApi();
        VipAccount vipAccount = api.getVipAccount("13922112130");
        System.out.println(vipAccount);
    }

    @Test
    @Ignore
    public void testGetGrade() {
        VipApi api = new VipApi();
        Grade grade = api.getGrade(1L);
        System.out.println(grade);
    }
}
