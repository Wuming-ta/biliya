package com.jfeat.wechat.controller;

import com.jfeat.config.model.Config;
import com.jfeat.core.BaseController;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.wechat.config.WechatConfig;
import com.jfinal.upload.UploadFile;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by sing on 2016/8/30.
 */
public class WechatCertController extends BaseController {

    @Override
    @RequiresPermissions(value = { "wechat.view", "sys.wechat_cert.menu" }, logical = Logical.OR)
    public void index() {
        Config config = Config.dao.findByKey("wx.cert_path");
        if (config != null) {
            String cert_path = config.getValueToStr();
            setAttr("cert_path", cert_path);
        }
        Config appConfig = Config.dao.findByKey("wx.app_cert_path");
        if (appConfig != null) {
            String cert_path = appConfig.getValueToStr();
            setAttr("app_cert_path", cert_path);
        }
    }

    @Override
    @RequiresPermissions("wechat.edit")
    public void update() {
        UploadFile uploadFile = getFile("uploadFile", WechatConfig.me().getCertUploadPath());
        if (uploadFile == null) {
            renderError(500);
            return;
        }
        String path = uploadFile.getFile().getAbsolutePath();
        Config config = Config.dao.findByKey("wx.cert_path");
        config.setValue(path);
        config.update();
        redirect("/wechat_cert");
    }

    @RequiresPermissions("wechat.edit")
    public void appUpdate() {
        UploadFile uploadFile = getFile("uploadAppFile", WechatConfig.me().getCertUploadPath());
        if (uploadFile == null) {
            renderError(500);
            return;
        }
        String path = uploadFile.getFile().getAbsolutePath();
        Config config = Config.dao.findByKey("wx.app_cert_path");
        config.setValue(path);
        config.update();
        redirect("/wechat_cert");
    }
}
