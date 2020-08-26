package com.jfeat.wechat.controller;

import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.wechat.controller.bean.WechatAutoreply;
import com.jfeat.wechat.model.WechatKeywordAutoreply;
import com.jfeat.wechat.model.WechatKeywordAutoreplyItem;
import com.jfeat.wechat.service.WechatAutoreplyService;
import com.jfeat.wechat.util.StringUtil;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Date;

/**
 * Created by kang on 2017/5/3.
 */
public class WechatKeywordAutoreplyController extends BaseController {

    private WechatAutoreplyService wechatAutoreplyService = Enhancer.enhance(WechatAutoreplyService.class);

    @Override
    @RequiresPermissions("wechat.edit")
    @Before(Flash.class)
    public void edit() {
        Integer id = getParaToInt();
        WechatKeywordAutoreply wechatKeywordAutoreply = null;
        if (id == null) {
            wechatKeywordAutoreply = new WechatKeywordAutoreply();
            wechatKeywordAutoreply.setName("未命名规则");
            wechatKeywordAutoreply.save();

            WechatKeywordAutoreplyItem wechatKeywordAutoreplyItemText = new WechatKeywordAutoreplyItem();
            wechatKeywordAutoreplyItemText.setType(WechatKeywordAutoreplyItem.Type.TEXT.toString());
            wechatKeywordAutoreplyItemText.setKeywordId(wechatKeywordAutoreply.getId());
            wechatKeywordAutoreplyItemText.save();
        } else {
            wechatKeywordAutoreply = WechatKeywordAutoreply.dao.findById(id);
        }
        setAttr("wechatKeywordAutoreply", wechatKeywordAutoreply);
    }

    @Override
    @RequiresPermissions("wechat.edit")
    public void update() {
        WechatKeywordAutoreply wechatKeywordAutoreply = getModel(WechatKeywordAutoreply.class);
        String[] keywordArr = getParaValues("keyword");
        if (keywordArr == null || keywordArr.length == 0) {
            wechatKeywordAutoreply.setKeyword(null);
        } else {
            wechatKeywordAutoreply.setKeyword(StringUtil.join("|", keywordArr));
        }
        wechatKeywordAutoreply.update();
        setFlash("message", getRes().get("save.success"));
        redirect("/wechat_keyword_autoreply/edit/" + wechatKeywordAutoreply.getId());
    }


    @RequiresPermissions("wechat.edit")
    public void updateText() {
        WechatAutoreply wechatAutoreply = getBean(WechatAutoreply.class);
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem = WechatKeywordAutoreplyItem.dao.findById(getParaToInt());
        wechatKeywordAutoreplyItem.setContent(wechatAutoreply.getContent());
        wechatKeywordAutoreplyItem.update();
        setFlash("message", getRes().get("save.success"));
        redirect("/wechat_keyword_autoreply/edit/" + wechatKeywordAutoreplyItem.getWechatKeywordAutoreply().getId());
    }

    @RequiresPermissions("wechat.edit")
    public void enableText() {
        wechatAutoreplyService.enableOrDisableWechatKeywordAutoreplyItemText(getParaToInt(), WechatAutoreplyService.ENABLED);
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem = WechatKeywordAutoreplyItem.dao.findFirstByField(WechatKeywordAutoreplyItem.Fields.TYPE.toString(), WechatKeywordAutoreplyItem.Type.TEXT.toString());
        setFlash("message", getRes().get("enable.success"));
        redirect("/wechat_keyword_autoreply/edit/" + wechatKeywordAutoreplyItem.getWechatKeywordAutoreply().getId());
    }

    @RequiresPermissions("wechat.edit")
    public void disableText() {
        wechatAutoreplyService.enableOrDisableWechatKeywordAutoreplyItemText(getParaToInt(), WechatAutoreplyService.DISABLED);
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem = WechatKeywordAutoreplyItem.dao.findFirstByField(WechatKeywordAutoreplyItem.Fields.TYPE.toString(), WechatKeywordAutoreplyItem.Type.TEXT.toString());
        setFlash("message", getRes().get("disable.success"));
        redirect("/wechat_keyword_autoreply/edit/" + wechatKeywordAutoreplyItem.getWechatKeywordAutoreply().getId());
    }

    public void saveNews() {
        WechatAutoreply wechatAutoreply = getBean(WechatAutoreply.class);
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem = new WechatKeywordAutoreplyItem();
        wechatKeywordAutoreplyItem.setKeywordId(getParaToInt());
        wechatKeywordAutoreplyItem.setType(WechatKeywordAutoreplyItem.Type.NEWS.toString());
        Long createTime = getParaToLong("createTime"); //createTime是秒，不是毫秒
        if (createTime != null) {
            Date d = new Date(createTime * 1000);
            wechatKeywordAutoreplyItem.setCreateTime(d);
        }
        wechatKeywordAutoreplyItem.setTitle(wechatAutoreply.getTitle());
        wechatKeywordAutoreplyItem.setDigest(wechatAutoreply.getDigest());
        wechatKeywordAutoreplyItem.setContent(wechatAutoreply.getContent());
        wechatKeywordAutoreplyItem.setUrl(wechatAutoreply.getUrl());
        wechatKeywordAutoreplyItem.setThumbUrl(wechatAutoreply.getThumb_url());
        wechatKeywordAutoreplyItem.save();
        setFlash("message", getRes().get("save.success"));
        redirect("/wechat_keyword_autoreply/edit/" + wechatKeywordAutoreplyItem.getWechatKeywordAutoreply().getId());
    }

    @RequiresPermissions("wechat.delete")
    public void deleteNews() {
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem = WechatKeywordAutoreplyItem.dao.findById(getParaToInt());
        wechatKeywordAutoreplyItem.delete();
        setFlash("message", getRes().get("delete.success"));
        redirect("/wechat_keyword_autoreply/edit/" + wechatKeywordAutoreplyItem.getWechatKeywordAutoreply().getId());
    }

    @RequiresPermissions("wechat.edit")
    public void enableNews() {
        wechatAutoreplyService.enableOrDisableWechatKeywordAutoreplyItemNews(getParaToInt(), WechatAutoreplyService.ENABLED);
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem = WechatKeywordAutoreplyItem.dao.findFirstByField(WechatKeywordAutoreplyItem.Fields.TYPE.toString(), WechatKeywordAutoreplyItem.Type.TEXT.toString());
        setFlash("message", getRes().get("enable.success"));
        redirect("/wechat_keyword_autoreply/edit/" + wechatKeywordAutoreplyItem.getWechatKeywordAutoreply().getId());
    }

    @RequiresPermissions("wechat.edit")
    public void disableNews() {
        wechatAutoreplyService.enableOrDisableWechatKeywordAutoreplyItemNews(getParaToInt(), WechatAutoreplyService.DISABLED);
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem = WechatKeywordAutoreplyItem.dao.findFirstByField(WechatKeywordAutoreplyItem.Fields.TYPE.toString(), WechatKeywordAutoreplyItem.Type.TEXT.toString());
        setFlash("message", getRes().get("disable.success"));
        redirect("/wechat_keyword_autoreply/edit/" + wechatKeywordAutoreplyItem.getWechatKeywordAutoreply().getId());
    }

    @Override
    @RequiresPermissions("wechat.delete")
    public void delete() {
        new WechatKeywordAutoreply().deleteById(getParaToInt());
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.edit")
    public void enable() {
        WechatKeywordAutoreply wechatKeywordAutoreply = WechatKeywordAutoreply.dao.findById(getParaToInt());
        wechatKeywordAutoreply.setEnabled(WechatAutoreplyService.ENABLED);
        wechatKeywordAutoreply.update();
        redirect("/wechat_autoreply");
    }

    @RequiresPermissions("wechat.edit")
    public void disable() {
        WechatKeywordAutoreply wechatKeywordAutoreply = WechatKeywordAutoreply.dao.findById(getParaToInt());
        wechatKeywordAutoreply.setEnabled(WechatAutoreplyService.DISABLED);
        wechatKeywordAutoreply.update();
        redirect("/wechat_autoreply");
    }
}
