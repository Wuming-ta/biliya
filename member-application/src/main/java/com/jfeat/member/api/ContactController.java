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

package com.jfeat.member.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.member.model.Contact;
import com.jfinal.ext.route.ControllerBind;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Created by jingfei on 2016/4/1.
 */
@ControllerBind(controllerKey = "/rest/contact")
public class ContactController extends RestController {


    private Integer getCurrentUserId() {
        Subject currentUser = SecurityUtils.getSubject();
        return ((ShiroUser) currentUser.getPrincipal()).id;
    }

    public void index() {
        renderSuccess(Contact.dao.findByUserId(getCurrentUserId()));
    }

    /**
     * {
     * "contact_user": "Mr Huang",
     * "phone": "1380000000",
     * "zip": "510000",
     * "province": "GD",
     * "city": "GZ",
     * "district": "Tiahne",
     * "street": "jianzhong road",
     * "street_number": "50",
     * "detail": "6F",
     * "is_default": 1
     * }
     */
    public void save() {
        Contact contact = getPostJson(Contact.class);
        contact.setUserId(getCurrentUserId());
        contact.save();
        renderSuccess("contact.saved");
    }

    public void update() {
        Contact originalContact = Contact.dao.findById(getParaToInt());
        if (originalContact == null) {
            render404Rest("contact.not.found");
            return;
        }
        if (!originalContact.getUserId().equals(getCurrentUserId())) {
            renderError(401);
            return;
        }
        Contact contact = getPostJson(Contact.class);
        contact.remove(Contact.Fields.ID.toString());
        contact.remove(Contact.Fields.USER_ID.toString());
        originalContact._setAttrs(contact);
        originalContact.update();
        renderSuccess(originalContact);
    }

    public void delete() {
        Contact originalContact = Contact.dao.findById(getParaToInt());
        if (originalContact == null) {
            render404Rest("contact.not.found");
            return;
        }
        if (!originalContact.getUserId().equals(getCurrentUserId())) {
            renderError(401);
            return;
        }
        originalContact.delete();
        renderSuccessMessage("contact.deleted");
    }

}
