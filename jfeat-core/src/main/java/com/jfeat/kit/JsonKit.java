/*
 * Copyright (C) 2014-2015 by ehngjen @ www.jfeat.com
 *
 *  The program may be used and/or copied only with the written permission
 *  from JFeat.com, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */

package com.jfeat.kit;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;

/**
 * Created by jacky on 8/16/14.
 */
public class JsonKit {
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertToMap(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object>[] convertToMapArray(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Map[].class);
    }
}
