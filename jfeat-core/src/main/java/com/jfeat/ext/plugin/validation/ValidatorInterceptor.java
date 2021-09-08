package com.jfeat.ext.plugin.validation;

import com.jfeat.core.BaseController;
import com.jfeat.core.RestController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by jackyhuang on 17/2/10.
 */
public class ValidatorInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        boolean matchResult = true;
        Method method = inv.getMethod();
        Validation validation = method.getAnnotation(Validation.class);
        if (validation != null) {
            Map<String, String> ruleMap = dealRule(validation.rules());
            for (String key : ruleMap.keySet()) {
                String value = getParaValue(inv, key);
                String rule = ruleMap.get(key);
                matchResult = ValidationRules.isMatch(rule, value);
                if (!matchResult) {
                    String message = key + ValidationRules.getErrorMessage(rule);
                    handleFailure(inv, validation.redirectUrl(), message);
                    break;
                }
            }
        }

        if (matchResult) {
            inv.invoke();
        }
    }

    private String getParaValue(Invocation invocation, String key) {
        Controller controller = invocation.getController();
        if (controller instanceof RestController) {
            RestController c = (RestController) controller;
            String methodName = invocation.getMethodName();
            if ("save".equalsIgnoreCase(methodName) || "update".equalsIgnoreCase(methodName)) {
                Map<String, Object> map = c.convertPostJsonToMap();
                if (map != null) {
                    Object object = map.get(key);
                    if (object != null) {
                        return object.toString();
                    }
                }
            }
        }

        return controller.getPara(key);

    }

    private void handleFailure(Invocation invocation, String redirectUrl, String message) {
        Controller controller = invocation.getController();
        if (controller instanceof RestController) {
            RestController c = (RestController) controller;
            c.renderFailure(message);
        }
        else if (controller instanceof BaseController) {
            BaseController c = (BaseController) controller;
            c.setFlash("message", message);
            c.redirect(redirectUrl);
        }
        else {
            controller.renderText(message);
        }
    }

    private Map<String, String> dealRule(String[] rules) {
        Map<String, String> map = new IdentityHashMap<>();
        if (rules != null && rules.length > 0) {
            for (String rule : rules) {
                String[] para = rule.split("=");
                if (para.length == 2) {
                    String key = para[0].trim();
                    String value = para[1].trim();
                    if (StrKit.notBlank(key) && StrKit.notBlank(value)) {
                        map.put(para[0].trim(), para[1].trim());
                    }
                }
            }
        }
        return map;
    }
}
