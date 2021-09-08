package com.jfeat.core.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * Created by jackyhuang on 17/2/8.
 */
public class ProductNameInterceptor implements Interceptor {

    private String name;
    private String version;
    private String theme = "default";
    private String logo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public ProductNameInterceptor() {

    }

    public ProductNameInterceptor(String name) {
        this.name = name;
    }

    public ProductNameInterceptor(String name, String version) {
        this(name);
        this.version = version;
    }

    @Override
    public void intercept(Invocation invocation) {
        invocation.getController().setAttr("productName", name);
        invocation.getController().setAttr("productVersion", version);
        invocation.getController().setAttr("theme", theme);
        invocation.getController().setAttr("logo", logo);
        invocation.invoke();
    }
}