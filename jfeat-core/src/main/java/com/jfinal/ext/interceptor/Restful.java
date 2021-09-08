//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jfinal.ext.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import java.util.HashSet;
import java.util.Set;

/**
 * Copy from jfinal.
 * add the 'options' method.
 */
public class Restful implements Interceptor {
    private static final String isRestfulForwardKey = "_isRestfulForward";
    private Set<String> set = new HashSet() {
        private static final long serialVersionUID = 2717581127375143508L;

        {
            this.add("show");
            this.add("save");
            this.add("update");
            this.add("delete");
            this.add("options");
        }
    };

    public Restful() {
    }

    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        Boolean isRestfulForward = (Boolean)controller.getAttr("_isRestfulForward");
        String methodName = inv.getMethodName();
        if(this.set.contains(methodName) && isRestfulForward == null) {
            inv.getController().renderError(404);
        } else if(isRestfulForward != null && isRestfulForward.booleanValue()) {
            inv.invoke();
        } else {
            String controllerKey = inv.getControllerKey();
            String method = controller.getRequest().getMethod().toUpperCase();
            String urlPara = controller.getPara();
            if("GET".equals(method)) {
                if(urlPara != null && !"edit".equals(methodName)) {
                    controller.setAttr("_isRestfulForward", Boolean.TRUE);
                    controller.forwardAction(controllerKey + "/show/" + urlPara);
                    return;
                }
            } else {
                if("POST".equals(method)) {
                    controller.setAttr("_isRestfulForward", Boolean.TRUE);
                    controller.forwardAction(controllerKey + "/save");
                    return;
                }

                if("PUT".equals(method)) {
                    controller.setAttr("_isRestfulForward", Boolean.TRUE);
                    controller.forwardAction(controllerKey + "/update/" + urlPara);
                    return;
                }

                if("DELETE".equals(method)) {
                    controller.setAttr("_isRestfulForward", Boolean.TRUE);
                    controller.forwardAction(controllerKey + "/delete/" + urlPara);
                    return;
                }
                if ("OPTIONS".equals(method)) {
                    controller.setAttr("_isRestfulForward", Boolean.TRUE);
                    controller.forwardAction(controllerKey + "/options/" + urlPara);
                    return;
                }
            }

            inv.invoke();
        }
    }
}
