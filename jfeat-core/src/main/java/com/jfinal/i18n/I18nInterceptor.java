//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jfinal.i18n;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.render.Render;

public class I18nInterceptor implements Interceptor {
    private String localePara;
    private String resName;
    private boolean isSwitchView;

    public I18nInterceptor() {
        this.localePara = "_locale";
        this.resName = "_res";
        this.isSwitchView = false;
    }

    public I18nInterceptor(String localePara, String resName) {
        this.localePara = "_locale";
        this.resName = "_res";
        this.isSwitchView = false;
        if(StrKit.isBlank(localePara)) {
            throw new IllegalArgumentException("localePara can not be blank.");
        } else if(StrKit.isBlank(resName)) {
            throw new IllegalArgumentException("resName can not be blank.");
        } else {
            this.localePara = localePara;
            this.resName = resName;
        }
    }

    public I18nInterceptor(String localePara, String resName, boolean isSwitchView) {
        this(localePara, resName);
        this.isSwitchView = isSwitchView;
    }

    protected String getLocalePara() {
        return this.localePara;
    }

    protected String getResName() {
        return this.resName;
    }

    protected String getBaseName() {
        return I18n.defaultBaseName;
    }

    public void intercept(Invocation inv) {

        Controller c = inv.getController();
        String localePara = this.getLocalePara();
        String locale = c.getPara(localePara);
        if(StrKit.notBlank(locale)) {
            c.setCookie(localePara, locale, 999999999);
        } else {
            locale = c.getCookie(localePara);
            if(StrKit.isBlank(locale)) {
                locale = I18n.defaultLocale;
            }
        }

        if(this.isSwitchView) {
            this.switchView(locale, c);
        } else {
            Res res = I18n.use(this.getBaseName(), locale);
            c.setAttr(this.getResName(), res);
        }

        inv.invoke();
    }

    public void switchView(String locale, Controller c) {
        Render render = c.getRender();
        if(render != null) {
            String view = render.getView();
            if(view != null) {
                if(view.startsWith("/")) {
                    view = "/" + locale + view;
                } else {
                    view = locale + "/" + view;
                }

                render.setView(view);
            }
        }

    }
}
