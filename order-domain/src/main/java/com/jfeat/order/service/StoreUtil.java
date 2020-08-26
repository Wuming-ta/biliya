package com.jfeat.order.service;

import com.jfeat.ext.plugin.BasePlugin;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.StorePlugin;
import com.jfeat.ext.plugin.store.StoreApi;
import com.jfeat.ext.plugin.store.bean.Assistant;
import com.jfeat.ext.plugin.store.bean.QueryStoresApiResult;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.identity.model.User;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import java.util.List;
import java.util.Optional;

/**
 * @author jackyhuang
 * @date 2018/9/12
 */
public class StoreUtil {

    private StoreUtil() { }

    /**
     * 返回店铺对应的仓库ID
     *
     * @param storeId
     * @return
     */
    public static Long getWarehouseId(String storeId) {
        BasePlugin storePlugin = ExtPluginHolder.me().get(StorePlugin.class);
        if (!storePlugin.isEnabled()) {
            return null;
        }
        if (StrKit.isBlank(storeId)) {
            return null;
        }
        StoreApi storeApi = new StoreApi();
        Store store = storeApi.getStore(Long.parseLong(storeId));
        return store.getWarehouseId();
    }

    /**
     * 检查用户是否店员
     * @param controller
     * @param storeId
     * @return 错误时返回字符串
     */
    public static String verify(Controller controller, String storeId) {
        //店铺组件必须启用
        BasePlugin storePlugin = ExtPluginHolder.me().get(StorePlugin.class);
        if (!storePlugin.isEnabled()) {
            return ("店铺组件未启用");
        }

        //登录用户必须是线下门店的店员
        User currentUser = controller.getAttr("currentUser");
        StoreApi storeApi = new StoreApi();
        QueryStoresApiResult storesApiResult = storeApi.queryStores(currentUser.getId().longValue(), currentUser.getLoginName());
        List<Store> stores = storesApiResult.getRecords();
        if (stores == null || stores.isEmpty()) {
            return (" 您不是店员");
        }
        //检查登录用户是否是传上来的storeId对应的店铺的店员
        Optional<Store> optionalStore = stores.stream().filter(item -> item.getId().equals(Long.parseLong(storeId))).findFirst();
        if (!optionalStore.isPresent()) {
            return ("您不是此店铺的店员");
        }

        Store store = optionalStore.get();
        Assistant assistant = storesApiResult.getAssistant();
        controller.setAttr("store", store);
        controller.setAttr("assistant", assistant);

        return null;
    }
}
