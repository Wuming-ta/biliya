package com.jfeat.wechat.controller;

import com.google.common.collect.Lists;
import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.wechat.global.ErrorMessage;
import com.jfinal.aop.Before;
import com.jfinal.kit.JsonKit;
import com.jfinal.upload.UploadFile;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;
import java.util.Map;

/**
 * Created by kang on 2017/4/6.
 */
public class WechatCustomerServiceController extends BaseController {
    private int SUCCESS = 0;

    @Override
    @Before(Flash.class)
    @RequiresPermissions("wechat.view")
    public void index() {
        ApiResult apiResult = CustomServiceApi.getKfList();
        List<Map<String, Object>> list = apiResult.getList("kf_list");
        logger.debug("kf_list: apiResult = {}", JsonKit.toJson(apiResult));
        setAttr("wechatCustomerServices", list);
        apiResult = CustomServiceApi.getOnlineKFList();
        logger.debug("kf_online_list: apiResult = {}", JsonKit.toJson(apiResult));
        List<Map<String, Object>> onlineWechatCustomerServices = apiResult.getList("kf_online_list");
        List<Integer> onlineKfIds = Lists.newLinkedList();
        if (onlineWechatCustomerServices != null) {
            for (Map<String, Object> onlineWechatCustomerService : onlineWechatCustomerServices) {
                onlineKfIds.add((Integer) onlineWechatCustomerService.get("kf_id"));
            }
        }
        setAttr("onlineKfIds", onlineKfIds);
    }

    @Override
    @RequiresPermissions("wechat.edit")
    public void add() {

    }

    @Override
    @RequiresPermissions("wechat.edit")
    public void save() {
        ApiResult apiResult = CustomServiceApi.addKfAccount(getPara("kf_account"), getPara("nickname"), getPara("password"));
        if (apiResult.getErrorCode().equals(SUCCESS)) {
            setFlash("message", "成功添加客服账号!");
        } else {
            setFlash("message", ErrorMessage.getInstance().getErrorMsg(apiResult.getErrorCode()));
        }
        redirect("/wechat_customer_service");
    }

    @RequiresPermissions("wechat.edit")
    public void invite() {
        ApiResult apiResult = com.jfeat.wechat.sdk.api.CustomServiceApi.inviteWorker(getPara("kf_account"), getPara("kf_wx"));
        if (apiResult.getErrorCode().equals(SUCCESS)) {
            setFlash("message", "成功发出绑定邀请，正等待客服登录微信进行确认!");
        } else {
            setFlash("message", ErrorMessage.getInstance().getErrorMsg(apiResult.getErrorCode()));
        }
        redirect("/wechat_customer_service");
    }

    @Override
    @RequiresPermissions("wechat.edit")
    public void edit() {
        Integer kfId = getParaToInt();
        ApiResult apiResult = CustomServiceApi.getKfList();
        List<Map<String, Object>> list = apiResult.getList("kf_list");
        for (Map<String, Object> map : list) {
            if (kfId.equals((Integer) map.get("kf_id"))) {
                setAttr("wechatCustomerService", map);
            }
        }
    }

    @Override
    @RequiresPermissions("wechat.edit")
    public void update() {
        UploadFile uploadedFile = getFile("kf_headimgurl", System.getProperty("java.io.tmpdir"));
        if (uploadedFile != null && uploadedFile.getFile() != null) {
            ApiResult apiResult = com.jfeat.wechat.sdk.api.CustomServiceApi.uploadKfAccountHeadImg(getPara("kf_account"), uploadedFile.getFile());
            if (!apiResult.getErrorCode().equals(SUCCESS)) {
                setFlash("message", ErrorMessage.getInstance().getErrorMsg(apiResult.getErrorCode()));
                redirect("/wechat_customer_service");
                return;
            }
        }
        ApiResult apiResult = CustomServiceApi.updateKfAccount(getPara("kf_account"), getPara("nickname"), getPara("password"));
        if (apiResult.getErrorCode().equals(SUCCESS)) {
            setFlash("message", "成功更新客服账号信息!");
        } else {
            setFlash("message", ErrorMessage.getInstance().getErrorMsg(apiResult.getErrorCode()));
        }
        redirect("/wechat_customer_service");
    }

    @Override
    @RequiresPermissions("wechat.delete")
    public void delete() {
        ApiResult apiResult = CustomServiceApi.delKfAccount(getPara());
        if (apiResult.getErrorCode().equals(SUCCESS)) {
            setFlash("message", "成功删除客服账号!");
        } else {
            setFlash("message", ErrorMessage.getInstance().getErrorMsg(apiResult.getErrorCode()));
        }
        redirect("/wechat_customer_service");
    }
}
