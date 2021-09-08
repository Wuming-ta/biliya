package com.jagregory.shiro.freemarker;

/**
 * Created by ehngjen on 1/14/2016.
 */
public class LacksPrincipalPropertyTag extends PrincipalPropertyTag {
    @Override
    protected boolean showTagBody(String propertyValue) {
        return isBlank(propertyValue);
    }
}
