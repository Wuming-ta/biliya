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

package com.jfeat.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfeat.core.handler.HtmlFilter;
import com.jfeat.kit.JsonKit;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.Restful;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Before(Restful.class)
public abstract class RestController extends BaseController {

    private static final String STATUS_CODE = "status_code";
    private static final String MESSAGE = "message";
    private static final String DATA = "data";
    private static final int SUCCESS = 0;
    private static final int FAILURE_DEFAULT = 1;

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final Gson gson = createGson();
    private String inputJson;
    private Object jsonObject;

    public static Gson createGson() {
        final GsonBuilder builder = new GsonBuilder();
        //builder.registerTypeAdapter(Record.class, new RecordAdapter());
        //builder.registerTypeHierarchyAdapter(Model.class, new ModelAdapter());
        builder.setDateFormat(DEFAULT_DATE_PATTERN);
        return builder.create();
    }

    public void renderRest(Object data) {
        renderRest(data, HttpServletResponse.SC_OK);
    }

    public void renderRest(Object data, int statusCode) {
        getResponse().setStatus(statusCode);
        renderJson(data);
    }

    public void renderSuccessMessage(Object message) {
        Map<String, Object> result = new HashMap<>();
        result.put(STATUS_CODE, SUCCESS);
        result.put(MESSAGE, message);
        renderJson(result);
    }

    public void renderSuccess(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put(STATUS_CODE, SUCCESS);
        result.put(DATA, data);
        renderJson(result);
    }

    public void renderFailure(Object message) {
        renderFailure(message, FAILURE_DEFAULT);
    }

    public void renderFailure(Object message, int code) {
        Map<String, Object> result = new HashMap<>();
        result.put(STATUS_CODE, code);
        result.put(MESSAGE, message);
        renderJson(result);
    }

    public void render400Rest(String message) {
        render400Rest(message, HttpServletResponse.SC_BAD_REQUEST);
    }

    public void render400Rest(String message, int statusCode) {
        getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
        renderErrorRest(message, statusCode);
    }

    public void render404Rest(String message) {
        getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
        renderErrorRest(message, HttpServletResponse.SC_NOT_FOUND);
    }

    public void renderErrorRest(String message, int statusCode) {
        Map<String, Object> result = new HashMap<>();
        result.put(STATUS_CODE, statusCode);
        if (!StrKit.isBlank(message)) {
            result.put(MESSAGE, message);
        }

        renderJson(result);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPostJson(Class<T> clazz) {
        if (inputJson == null) {
            try {
                inputJson = IOUtils.toString(getRequest().getInputStream(), "UTF-8");
                inputJson = HtmlFilter.getBasicHtmlandimage(inputJson);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            logger.debug("POST JSON: {}", inputJson);
        }

        if (jsonObject == null) {
            try {
                if (Model.class.isAssignableFrom(clazz)) {
                    Map<String, Object> map = JsonKit.convertToMap(inputJson);
                    Model model = (Model) injectModel(clazz, map, true);
                    jsonObject = model;
                }
                else {
                    jsonObject = gson.fromJson(inputJson, clazz);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return (T) jsonObject;
    }


    public Map<String, Object> convertPostJsonToMap() {
        try {
            if (inputJson == null) {
                inputJson = IOUtils.toString(getRequest().getInputStream(), "UTF-8");
                inputJson = HtmlFilter.getBasicHtmlandimage(inputJson);
            }
            logger.debug("POST JSON: {}", inputJson);
            return JsonKit.convertToMap(inputJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object>[] convertPostJsonToMapArray() {
        try {
            if (inputJson == null) {
                inputJson = IOUtils.toString(getRequest().getInputStream(), "UTF-8");
            }
            logger.debug("POST JSON: {}", inputJson);
            return JsonKit.convertToMapArray(inputJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T createInstance(Class<T> objClass) {
        try {
            return objClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final <T> T injectModel(Class<T> modelClass, Map<String, Object> dataMap, boolean putExtAttr) {
        Object temp = createInstance(modelClass);
        if (!(temp instanceof Model)) {
            throw new IllegalArgumentException("getModel only support class of Model, using getBean for other class.");
        }

        Model<?> model = (Model<?>)temp;
        Table table = TableMapping.me().getTable(model.getClass());
        if (table == null) {
            throw new ActiveRecordException("The Table mapping of model: " + modelClass.getName() +
                    " not exists or the ActiveRecordPlugin not start.");
        }

        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String paraName = entry.getKey();
            String attrName = paraName;

            Class<?> colType = table.getColumnType(attrName);

            try {
                Object paraValue = entry.getValue();
                if (colType == null) {
                    if (putExtAttr) {
                        model.put(paraName, paraValue);
                    }
                    continue;
                }
                Object value = paraValue != null ? TypeConverter.convert(colType, paraValue.toString()) : null;
                model.set(attrName, value);
            } catch (Exception e) {
                logger.error("Can not convert parameter: " + paraName, e);
            }
        }

        return (T)model;
    }
    
    /**
     * GET /rest
     */
    public void index() {
        throw new RuntimeException("Unsupported action.");
    }
    
    /**
     * GET /rest/id
     */
    public void show() {
        throw new RuntimeException("Unsupported action.");
    }
    
    /**
     * GET /rest/add
     */
    public void add() {
        throw new RuntimeException("Unsupported action.");
    }
    
    /**
     * POST /rest
     */
    public void save() {
        throw new RuntimeException("Unsupported action.");
    }
    
    /**
     * GET /rest/edit/id
     */
    public void edit() {
        throw new RuntimeException("Unsupported action.");
    }
    
    /**
     * PUT /rest/id
     */
    public void update() {
        throw new RuntimeException("Unsupported action.");
    }
    
    /**
     * DELETE /rest/id
     */
    public void delete() {
        throw new RuntimeException("Unsupported action.");
    }

    /**
     * OPTIONS /rest/id
     */
    public void options() {
        getResponse().setStatus(204);
        renderNull();
    }
}
