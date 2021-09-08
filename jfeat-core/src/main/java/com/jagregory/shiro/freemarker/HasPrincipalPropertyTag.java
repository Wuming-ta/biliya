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
public class HasPrincipalPropertyTag extends PrincipalPropertyTag {

    @Override
    protected boolean showTagBody(String propertyValue) {
        return notBlank(propertyValue);
    }
}
