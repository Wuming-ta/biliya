package com.jfeat.plugintest.validation;

import com.jfeat.core.BaseController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.ext.plugin.validation.ValidatorInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

/**
 * Created by jackyhuang on 17/2/10.
 */
@Before(ValidatorInterceptor.class)
public class TestController extends Controller {

    protected boolean failed = false;

    @Validation(rules = {"a = required", "b = number"})
    public void pass() {

    }

    @Validation(rules = {"a = required", "b = email"})
    public void fail() {

    }

    public String getPara(String key) {
        return "123";
    }

    public void renderText(String data) {
        failed = true;
    }
}
