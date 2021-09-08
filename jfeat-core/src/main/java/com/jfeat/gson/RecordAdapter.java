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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.jfinal.plugin.activerecord.Record;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ehngjen on 10/13/2015.
 */
public class RecordAdapter implements JsonSerializer<Record>, JsonDeserializer<Record> {
    @Override
    public Record deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        Record r = new Record();
        Map<String, Object> m = context.deserialize(jsonElement, HashMap.class);
        r.setColumns(m);
        return r;
    }

    @Override
    public JsonElement serialize(Record record, Type type, JsonSerializationContext context) {
        return context.serialize(record.getColumns());
    }
}
