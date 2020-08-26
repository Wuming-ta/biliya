package com.jfeat.wechat.controller;

import com.jfeat.core.BaseController;
import com.jfeat.wechat.controller.bean.WechatAutoreply;
import com.jfeat.wechat.model.WechatSubscribeAutoreply;
import com.jfeat.wechat.service.WechatAutoreplyService;
import com.jfinal.aop.Enhancer;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Date;

/**
 * Created by kang on 2017/5/3.
 */
public class WechatSubscribeAutoreplyController extends BaseController {

    private WechatAutoreplyService wechatAutoreplyService = Enhancer.enhance(WechatAutoreplyService.class);

    @RequiresPermissions("wechat.edit")
    public void updateText() {
        WechatAutoreply wechatAutoreply = getBean(WechatAutoreply.class);
        WechatSubscribeAutoreply wechatSubscribeAutoreply = WechatSubscribeAutoreply.dao.findById(wechatAutoreply.getId());
        wechatSubscribeAutoreply.setContent(wechatAutoreply.getContent());
        wechatSubscribeAutoreply.update();
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.edit")
    public void enableText() {
        wechatAutoreplyService.enableOrDisableWechatSubscribeAutoreplyText(getParaToInt(), WechatAutoreplyService.ENABLED);
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.edit")
    public void disableText() {
        wechatAutoreplyService.enableOrDisableWechatSubscribeAutoreplyText(getParaToInt(), WechatAutoreplyService.DISABLED);
        redirect("/wechat_autoreply");
    }

    public void saveNews() {
        WechatAutoreply wechatAutoreply = getBean(WechatAutoreply.class);
        WechatSubscribeAutoreply wechatSubscribeAutoreply = new WechatSubscribeAutoreply();
        wechatSubscribeAutoreply.setType(WechatSubscribeAutoreply.Type.NEWS.toString());
        Long createTime = getParaToLong("createTime"); //createTime是秒，不是毫秒
        if (createTime != null) {
            Date d = new Date(createTime * 1000);
            wechatSubscribeAutoreply.setCreateTime(d);
        }
        wechatSubscribeAutoreply.setTitle(wechatAutoreply.getTitle());
        wechatSubscribeAutoreply.setDigest(wechatAutoreply.getDigest());
        wechatSubscribeAutoreply.setContent(wechatAutoreply.getContent());
        wechatSubscribeAutoreply.setUrl(wechatAutoreply.getUrl());
        wechatSubscribeAutoreply.setThumbUrl(wechatAutoreply.getThumb_url());
        wechatSubscribeAutoreply.save();
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.delete")
    public void deleteNews() {
        new WechatSubscribeAutoreply().deleteById(getParaToInt());
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.edit")
    public void enableNews() {
        wechatAutoreplyService.enableOrDisableWechatSubscribeAutoreplyNews(getParaToInt(), WechatAutoreplyService.ENABLED);
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.edit")
    public void disableNews() {
        wechatAutoreplyService.enableOrDisableWechatSubscribeAutoreplyNews(getParaToInt(), WechatAutoreplyService.DISABLED);
        redirect("/wechat_autoreply");
    }
}
