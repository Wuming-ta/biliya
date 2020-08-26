package com.jfeat.wechat.controller;

import com.jfeat.core.BaseController;
import com.jfeat.wechat.controller.bean.WechatAutoreply;
import com.jfeat.wechat.model.WechatMessageAutoreply;
import com.jfeat.wechat.service.WechatAutoreplyService;
import com.jfinal.aop.Enhancer;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Date;

/**
 * Created by kang on 2017/5/3.
 */
public class WechatMessageAutoreplyController extends BaseController {

    private WechatAutoreplyService wechatAutoreplyService = Enhancer.enhance(WechatAutoreplyService.class);

    @RequiresPermissions("wechat.edit")
    public void updateText() {
        WechatAutoreply wechatAutoreply = getBean(WechatAutoreply.class);
        WechatMessageAutoreply wechatMessageAutoreply = WechatMessageAutoreply.dao.findById(wechatAutoreply.getId());
        wechatMessageAutoreply.setContent(wechatAutoreply.getContent());
        wechatMessageAutoreply.update();
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.edit")
    public void enableText() {
        wechatAutoreplyService.enableOrDisableWechatMessageAutoreplyText(getParaToInt(), WechatAutoreplyService.ENABLED);
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.edit")
    public void disableText() {
        wechatAutoreplyService.enableOrDisableWechatMessageAutoreplyText(getParaToInt(), WechatAutoreplyService.DISABLED);
        redirect("/wechat_autoreply");
    }

    public void saveNews() {
        WechatAutoreply wechatAutoreply = getBean(WechatAutoreply.class);
        WechatMessageAutoreply wechatMessageAutoreply = new WechatMessageAutoreply();
        wechatMessageAutoreply.setType(WechatMessageAutoreply.Type.NEWS.toString());
        Long createTime = getParaToLong("createTime"); //createTime是秒，不是毫秒
        if (createTime != null) {
            Date d = new Date(createTime * 1000);
            wechatMessageAutoreply.setCreateTime(d);
        }
        wechatMessageAutoreply.setTitle(wechatAutoreply.getTitle());
        wechatMessageAutoreply.setDigest(wechatAutoreply.getDigest());
        wechatMessageAutoreply.setContent(wechatAutoreply.getContent());
        wechatMessageAutoreply.setUrl(wechatAutoreply.getUrl());
        wechatMessageAutoreply.setThumbUrl(wechatAutoreply.getThumb_url());
        wechatMessageAutoreply.save();
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.delete")
    public void deleteNews() {
        new WechatMessageAutoreply().deleteById(getParaToInt());
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.edit")
    public void enableNews() {
        wechatAutoreplyService.enableOrDisableWechatMessageAutoreplyNews(getParaToInt(), WechatAutoreplyService.ENABLED);
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.edit")
    public void disableNews() {
        wechatAutoreplyService.enableOrDisableWechatMessageAutoreplyNews(getParaToInt(), WechatAutoreplyService.DISABLED);
        redirect("/wechat_autoreply");
    }
}
