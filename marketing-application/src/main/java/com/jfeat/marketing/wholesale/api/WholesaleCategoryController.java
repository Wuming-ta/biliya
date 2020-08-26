package com.jfeat.marketing.wholesale.api;

import com.google.common.collect.Maps;
import com.jfeat.core.RestController;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.marketing.wholesale.model.WholesaleCategory;
import com.jfeat.member.model.Contact;
import com.jfinal.ext.route.ControllerBind;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.util.List;
import java.util.Map;

/**
 * Created by kang on 2017/6/7.
 */
@ControllerBind(controllerKey = "/rest/wholesale_category")
public class WholesaleCategoryController extends RestController {

    public void index() {
        Subject subject = SecurityUtils.getSubject();
        ShiroUser user = (ShiroUser) subject.getPrincipal();
        List<WholesaleCategory> wholesaleCategories = WholesaleCategory.dao.findAll();
        Map<String, Object> resultMap = Maps.newLinkedHashMap();
        resultMap.put("categories", wholesaleCategories);
        resultMap.put("contact", Contact.dao.findDefaultByUserId(user.id));
        renderSuccess(resultMap);
    }
}
