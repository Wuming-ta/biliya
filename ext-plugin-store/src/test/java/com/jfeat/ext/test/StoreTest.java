package com.jfeat.ext.test;

import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.JsonKit;
import com.jfeat.ext.plugin.StorePlugin;
import com.jfeat.ext.plugin.store.StoreApi;
import com.jfeat.ext.plugin.store.bean.Assistant;
import com.jfeat.ext.plugin.store.bean.QueryStoresApiResult;
import com.jfeat.ext.plugin.store.bean.Store;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jackyhuang
 * @date 2018/7/23
 */
public class StoreTest {

    @BeforeClass
    public static void beforeClass() {
        StorePlugin storePlugin = new StorePlugin(true,
                "http://120.79.77.207:8080",
                "L7A/6zARSkK1j7Vd5SDD9pSSqZlqF7mAhiOgRbgv9Smce6tf4cJnvKOjtKPxNNnWQj+2lQEScm3XIUjhW+YVZg==");
        ExtPluginHolder.me().start(StorePlugin.class, storePlugin);
    }


    @Test
    public void testApiResult() {
        String json = "{\n" +
                "             \"code\": 200,\n" +
                "             \"data\": {\n" +
                "                 \"avatar\": \"\",\n" +
                "                 \"code\": \"001\",\n" +
                "                 \"directorId\": \"\",\n" +
                "                 \"id\": \"0\",\n" +
                "                 \"isDirector\": \"\",\n" +
                "                 \"name\": \"Fox\",\n" +
                "                 \"position\": \"Shopkeeper\",\n" +
                "                 \"qq\": \"\",\n" +
                "                 \"status\": \"\",\n" +
                "                 \"storeId\": \"\",\n" +
                "                 \"storeShifting\": \"\",\n" +
                "                 \"stores\": [\n" +
                "                     {\n" +
                "                     \"address\": \"\",\n" +
                "                     \"avatar\": \"\",\n" +
                "                     \"city\": \"\",\n" +
                "                     \"code\": \"001\",\n" +
                "                     \"createTime\": \"\",\n" +
                "                     \"director\": \"\",\n" +
                "                     \"district\": \"\",\n" +
                "                     \"id\": \"0\",\n" +
                "                     \"introduce\": \"\",\n" +
                "                     \"latitude\": \"\",\n" +
                "                     \"longitude\": \"\",\n" +
                "                     \"name\": \"SHOP1\",\n" +
                "                     \"province\": \"\",\n" +
                "                     \"telephone\": \"\",\n" +
                "                     \"warehouseId\": \"\"\n" +
                "                     }\n" +
                "                 ],\n" +
                "                 \"telephone\": \"\",\n" +
                "                 \"userId\": \"2\",\n" +
                "                 \"wechat\": \"\"\n" +
                "             },\n" +
                "             \"message\": \"操作成功\"\n" +
                "         }";
        QueryStoresApiResult result = QueryStoresApiResult.create(json);
        System.out.println(result);
        System.out.println(result.getRecords());
        for (Store store : result.getRecords()) {
            System.out.println(store.getName());
        }
    }

    @Test
    public void testApiResultEmpty() {
        String json = "{\n" +
                "             \"code\": 200,\n" +
                "             \"data\": \"\"," +
                "             \"message\": \"操作成功\"\n" +
                "         }";
        QueryStoresApiResult result = QueryStoresApiResult.create(json);
        System.out.println(result);
        System.out.println(result.getRecords());
    }

    @Test
    @Ignore
    public void testQueryAssistant() {
        StoreApi storeApi = new StoreApi();
        Assistant assistant = storeApi.queryAssistant("1000");
        System.out.println(JsonKit.toJson(assistant));
    }

    @Test
    @Ignore
    public void testGetAssistant() {
        StoreApi storeApi = new StoreApi();
        Assistant assistant = storeApi.getAssistant(1L);
        System.out.println(JsonKit.toJson(assistant.getStores()));
    }

    @Test
    @Ignore
    public void testGetStoreById() {
        StoreApi storeApi = new StoreApi();
        Store store = storeApi.getStore(1L);
        System.out.println(JsonKit.toJson(store));
    }

    @Test
    @Ignore
    public void testGetStoreByCode() {
        StoreApi storeApi = new StoreApi();
        Store store = storeApi.getStore("001");
        System.out.println(JsonKit.toJson(store));
    }
}
