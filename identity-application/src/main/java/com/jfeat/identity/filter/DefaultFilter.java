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

package com.jfeat.identity.filter;

/**
 * Created by ehngjen on 9/1/2015.
 */
public enum DefaultFilter {
    api(ClientApiAuthcFilter.class);

    private Class<? extends ClientFilter> clazz;
    DefaultFilter(Class<? extends ClientFilter> filterClass) {
        clazz = filterClass;
    }

    public Class<? extends ClientFilter> getClazz() {
        return clazz;
    }
}
