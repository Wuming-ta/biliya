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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ehngjen on 9/1/2015.
 */
public class ClientFilterManager {
    private static ClientFilterManager me = new ClientFilterManager();

    private Map<String, ClientFilter> filters;

    private ClientFilterManager() {
        filters = new LinkedHashMap<>();
        for (DefaultFilter filter : DefaultFilter.values()) {
            try {
                filters.put(filter.name(), filter.getClazz().newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static ClientFilterManager me() {
        return  me;
    }

    public void add(String name, ClientFilter filter) {
        filters.put(name, filter);
    }

    public Collection<ClientFilter> getFilters() {
        return filters.values();
    }

    public Map<String, ClientFilter> getFilterMap() {
        return filters;
    }

    public ClientFilter getFilter(String name) {
        return filters.get(name);
    }
}
