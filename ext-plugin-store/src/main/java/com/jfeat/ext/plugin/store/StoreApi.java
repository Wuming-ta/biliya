package com.jfeat.ext.plugin.store;

import com.jfeat.ext.plugin.*;
import com.jfeat.ext.plugin.store.bean.Assistant;
import com.jfeat.ext.plugin.store.bean.QueryStoresApiResult;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.http.utils.HttpUtils;
import com.jfeat.http.utils.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author jackyhuang
 * @date 2018/6/14
 */
public class StoreApi extends BaseApi {

    private static final Logger logger = LoggerFactory.getLogger(StoreApi.class);

    private static final String QUERY_MY_STORES_URL = "/api/app/stores";

    private static final String QUERY_STORES_URL = "/api/store/stores";

    private static final String GET_STORE_URL = "/api/store/stores/%s";

    private static final String GET_ASSISTANT_URL = "/api/store/assistants/%s";

    private static final String QUERY_ASSISTANTS_URL = "/api/store/assistants";

    public StoreApi() {
        BasePlugin storePlugin = ExtPluginHolder.me().get(StorePlugin.class);
        init(storePlugin);
    }

    /**
     * 返回所有门店
     * @return
     */
    public List<Store> getStoreList(int pageNum, int pageSize) {
        String url = getBaseUrl() + QUERY_STORES_URL;
        Map<String, String> param = new HashMap<>();
        param.put("pageNum", String.valueOf(pageNum));
        param.put("pageSize", String.valueOf(pageSize));
        param.put("type", "Store");
        String result = HttpUtils.get(url, param, getAuthorizationHeader());
        logger.debug("get url = {}, param = {}, result = {}", url, JsonKit.toJson(param), result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("get store error. " + apiResult.getMessage());
        }
        Map<String, Object> data = apiResult.get("data");
        List<Store> stores = JsonKit.parseArray(JsonKit.toJson(data.get("records")), Store.class);
        return stores;
    }

    public Store getStore(Long id) {
        if (id == null) {
            return new Store();
        }
        String url = String.format(getBaseUrl() + GET_STORE_URL, id);
        String result = HttpUtils.get(url, null, getAuthorizationHeader());
        logger.debug("get url = {}, result = {}", url, result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("get store error. " + apiResult.getMessage());
        }
        Map<String, Object> data = apiResult.get("data");
        return JsonKit.parseObject(JsonKit.toJson(data), Store.class);
    }

    public Store getStore(String code) {
        if (StrKit.isBlank(code)) {
            return new Store();
        }
        String url = getBaseUrl() + QUERY_STORES_URL;
        Map<String, String> param = new HashMap<>();
        param.put("code", code);
        String result = HttpUtils.get(url, param, getAuthorizationHeader());
        logger.debug("get url = {}, param = {}, result = {}", url, JsonKit.toJson(param), result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("get store error. " + apiResult.getMessage());
        }
        Map<String, Object> data = apiResult.get("data");
        List<Store> stores = JsonKit.parseArray(JsonKit.toJson(data.get("records")), Store.class);
        if (stores == null || stores.isEmpty()) {
            return new Store();
        }
        Optional<Store> store = stores.stream().filter(item -> item.getCode().equals(code)).findFirst();
        return store.orElse(new Store());
    }

    public Assistant getAssistant(Long id) {
        if (id == null) {
            return new Assistant();
        }
        String url = String.format(getBaseUrl() + GET_ASSISTANT_URL, id);
        Map<String, String> param = new HashMap<>();
        param.put("_passgw", "true");
        String result = HttpUtils.get(url, param, getAuthorizationHeader());
        logger.debug("get url = {}, result = {}", url, result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("get store assistant error. " + apiResult.getMessage());
        }
        Map<String, Object> data = apiResult.get("data");
        Assistant assistant = JsonKit.parseObject(JsonKit.toJson(data), Assistant.class);
        if (assistant == null) {
            return new Assistant();
        }
        return assistant;
    }


    public Assistant queryAssistant(Long userId) {
        if (userId == null) {
            return new Assistant();
        }
        String url = getBaseUrl() + QUERY_ASSISTANTS_URL;
        Map<String, String> param = new HashMap<>();
        param.put("userId", userId.toString());
        String result = HttpUtils.get(url, param, getAuthorizationHeader());
        logger.debug("get url = {}, param = {}, result = {}", url, JsonKit.toJson(param), result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("get store assistant error. " + apiResult.getMessage());
        }
        Map<String, Object> data = apiResult.get("data");
        List<Assistant> assistants = JsonKit.parseArray(JsonKit.toJson(data.get("records")), Assistant.class);
        if (assistants == null || assistants.isEmpty()) {
            return new Assistant();
        }
        Optional<Assistant> assistant = assistants.stream().filter(item -> item.getUserId().equals(userId)).findFirst();
        return assistant.orElse(new Assistant());
    }

    public Assistant queryAssistant(String code) {
        if (StrKit.isBlank(code)) {
            return new Assistant();
        }
        String url = getBaseUrl() + QUERY_ASSISTANTS_URL;
        Map<String, String> param = new HashMap<>();
        param.put("code", code);
        String result = HttpUtils.get(url, param, getAuthorizationHeader());
        logger.debug("get url = {}, param = {}, result = {}", url, JsonKit.toJson(param), result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("get store assistant error. " + apiResult.getMessage());
        }
        Map<String, Object> data = apiResult.get("data");
        List<Assistant> assistants = JsonKit.parseArray(JsonKit.toJson(data.get("records")), Assistant.class);
        if (assistants == null || assistants.isEmpty()) {
            return new Assistant();
        }
        Optional<Assistant> assistant = assistants.stream().filter(item -> item.getCode().equals(code)).findFirst();
        return assistant.orElse(new Assistant());
    }

    /**
     * @return 登录员工的所有店铺
     */
    public QueryStoresApiResult queryStores(Long userId, String account) {
        /**
         * /api/app/stores的返回格式：
         * {
             "code": 200,
             "data": {
                 "avatar": "",
                 "code": "001",
                 "directorId": "",
                 "id": "0",
                 "isDirector": "",
                 "name": "Fox",
                 "position": "Shopkeeper",
                 "qq": "",
                 "status": "",
                 "storeId": "",
                 "storeShifting": "",
                 "stores": [
                     {
                     "address": "",
                     "avatar": "",
                     "city": "",
                     "code": "001",
                     "createTime": "",
                     "director": "",
                     "district": "",
                     "id": "0",
                     "introduce": "",
                     "latitude": "",
                     "longitude": "",
                     "name": "SHOP1",
                     "province": "",
                     "telephone": "",
                     "warehouseId": ""
                     }
                 ],
                 "telephone": "",
                 "userId": "2",
                 "wechat": ""
             },
             "message": "操作成功"
         }

         */

        String url = getBaseUrl() + QUERY_MY_STORES_URL;
        logger.debug("querying store list: {}", url);
        String result = HttpUtils.get(url, null, getAuthorizationHeader(null, userId, account));
        logger.debug("get url = {}, result = {}", url, result);
        QueryStoresApiResult apiResult = QueryStoresApiResult.create(result);
        return apiResult;
    }

}
