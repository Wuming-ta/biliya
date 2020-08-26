/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.wechat.controller;

import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.wechat.model.WechatField;
import com.jfeat.wechat.model.WechatMessageType;
import com.jfeat.wechat.model.WechatMessageTypeProp;
import com.jfeat.wechat.model.WechatTemplateMessage;
import com.jfeat.wechat.service.WechatTemplateMessageService;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * Created by jackyhuang on 16/9/3.
 */
public class WechatTemplateMessageController extends BaseController {

    private WechatTemplateMessageService wechatTemplateMessageService = new WechatTemplateMessageService();

    @Override
    @Before(Flash.class)
    @RequiresPermissions(value = { "wechat.view", "sys.wechat_template_message.menu" }, logical = Logical.OR)
    public void index() {
        List<WechatMessageType> wechatMessageTypes = WechatMessageType.dao.findAll();
        setAttr("wechatMessageTypes", wechatMessageTypes);
    }

    @Override
    @RequiresPermissions("wechat.edit")
    public void add() {
        WechatMessageType wechatMessageType = WechatMessageType.dao.findById(getParaToInt());
        setAttr("wechatMessageType", wechatMessageType);
    }

    @Override
    @RequiresPermissions("wechat.edit")
    @Before(Tx.class)
    public void save() {
        WechatTemplateMessage wechatTemplateMessage = getModel(WechatTemplateMessage.class);
        wechatTemplateMessage.save();
        if (wechatTemplateMessage.getEnabled() == WechatTemplateMessage.ENABLED) {
            wechatTemplateMessageService.toggleEnable(wechatTemplateMessage, WechatTemplateMessage.ENABLED);
        }

        List<WechatMessageTypeProp> wechatMessageTypeProps = getModels(WechatMessageTypeProp.class);
        for (WechatMessageTypeProp wechatMessageTypeProp : wechatMessageTypeProps) {
            wechatMessageTypeProp.update();
        }

        List<WechatField> wechatFields = getModels(WechatField.class);
        for (WechatField wechatField : wechatFields) {
            if (StrKit.notBlank(wechatField.getName())) {
                wechatField.setTemplateMessageId(wechatTemplateMessage.getId());
                wechatField.save();
            }
        }
        setFlash("message", getRes().get("wechat.template.create.success"));
        redirect("/wechat_template_message");
    }

    @Override
    @RequiresPermissions("wechat.edit")
    public void edit() {
        setAttr("wechatTemplateMessage", WechatTemplateMessage.dao.findByIdJoin(getParaToInt()));
    }

    @Override
    @RequiresPermissions("wechat.edit")
    @Before(Tx.class)
    public void update() {
        WechatTemplateMessage wechatTemplateMessage = getModel(WechatTemplateMessage.class);
        wechatTemplateMessage.update();
        if (wechatTemplateMessage.getEnabled() == WechatTemplateMessage.ENABLED) {
            wechatTemplateMessageService.toggleEnable(wechatTemplateMessage, WechatTemplateMessage.ENABLED);
        }

        List<WechatMessageTypeProp> wechatMessageTypeProps = getModels(WechatMessageTypeProp.class);
        for (WechatMessageTypeProp wechatMessageTypeProp : wechatMessageTypeProps) {
            wechatMessageTypeProp.update();
        }

        List<WechatField> wechatFields = getModels(WechatField.class);
        new WechatField().deleteByField(WechatField.Fields.TEMPLATE_MESSAGE_ID.toString(), wechatTemplateMessage.getId());
        for (WechatField wechatField : wechatFields) {
            if (StrKit.notBlank(wechatField.getName())) {
                wechatField.setTemplateMessageId(wechatTemplateMessage.getId());
                wechatField.save();
            }
        }
        setFlash("message", getRes().get("wechat.template.update.success"));
        redirect("/wechat_template_message");
    }

    @Override
    @RequiresPermissions("wechat.delete")
    public void delete() {
        new WechatTemplateMessage().deleteByField(WechatTemplateMessage.Fields.ID.toString(), getParaToInt());
        setFlash("message", getRes().get("wechat.template.delete.success"));
        redirect("/wechat_template_message");
    }

    @RequiresPermissions("wechat.edit")
    public void enable() {
        wechatTemplateMessageService.toggleEnable(WechatTemplateMessage.dao.findById(getParaToInt()), WechatTemplateMessage.ENABLED);
        setFlash("message", getRes().get("wechat.template.enable.success"));
        redirect("/wechat_template_message");
    }

    @RequiresPermissions("wechat.edit")
    public void disable() {
        wechatTemplateMessageService.toggleEnable(WechatTemplateMessage.dao.findById(getParaToInt()), WechatTemplateMessage.DISABLED);
        setFlash("message", getRes().get("wechat.template.disable.success"));
        redirect("/wechat_template_message");
    }
}
