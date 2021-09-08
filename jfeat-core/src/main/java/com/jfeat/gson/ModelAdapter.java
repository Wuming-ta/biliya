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

package com.jfeat.gson;

import com.google.gson.*;
import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ehngjen on 10/13/2015.
 */
public class ModelAdapter implements JsonSerializer<Model>, JsonDeserializer<Model> {
    @Override
    public JsonElement serialize(Model model, Type type, JsonSerializationContext context) {
        return context.serialize(CPI.getAttrs(model));
    }

    @Override
    public Model deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        Model model = null;
        try {
            model = (Model) ((Class)type).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (model != null) {
            Map<String, Object> m = context.deserialize(jsonElement, HashMap.class);
            model.put(m);
        }
        return model;
    }
}
