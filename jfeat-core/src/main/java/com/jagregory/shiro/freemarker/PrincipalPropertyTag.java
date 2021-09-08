package com.jagregory.shiro.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ehngjen on 1/14/2016.
 */
public abstract class PrincipalPropertyTag extends SecureTag {

    protected abstract boolean showTagBody(String propertyValue);

    String getProperty(Map params) {
        return getParam(params, "property");
    }

    @Override
    public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
        String result = null;

        // Get the principal to print out
        Object principal = getSubject().getPrincipal();
        String property = getProperty(params);
        result = getPrincipalProperty(principal, property);
        if (showTagBody(result)) {
            renderBody(env, body);
        }
    }

    String getPrincipalProperty(Object principal, String property) throws TemplateModelException {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(principal.getClass());

            // Loop through the properties to get the string value of the specified property
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                if (propertyDescriptor.getName().equals(property)) {
                    Object value = propertyDescriptor.getReadMethod().invoke(principal, (Object[]) null);

                    return String.valueOf(value);
                }
            }

            // property not found, throw
            throw new TemplateModelException("Property ["+property+"] not found in principal of type ["+principal.getClass().getName()+"]");
        } catch (Exception ex) {
            throw new TemplateModelException("Error reading property ["+property+"] from principal of type ["+principal.getClass().getName()+"]", ex);
        }
    }

    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim()) || "null".equals(str.trim());
    }

    public static boolean notBlank(String str) {
        return !isBlank(str);
    }

}
