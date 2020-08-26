package com.jfeat.order;

import com.jfeat.ext.plugin.store.StoreApi;
import com.jfeat.ext.plugin.store.bean.Assistant;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.ext.plugin.vip.VipApi;
import com.jfeat.ext.plugin.vip.bean.VipAccount;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.Order;

/**
 * @author jackyhuang
 * @date 2018/11/2
 */
public class OrderStoreUtil {

    /**
     * 根据订单里的信息设置关注门店/导购/绑定小屋
     * @param user
     * @param order
     */
    public static void updateStore(User user, Order order) {
        // 取得该用户的导购员信息
        VipApi vipApi = new VipApi();
        VipAccount vipAccount = vipApi.getVipAccount(user.getLoginName());
        String assistantCode = vipAccount.getBindingAssistantCode();
        StoreApi storeApi = new StoreApi();
        Assistant bindingAssistant = storeApi.queryAssistant(assistantCode);
        order.setStoreGuideUserId(String.valueOf(bindingAssistant.getUserId()));
        order.setStoreGuideUserCode(bindingAssistant.getCode());
        order.setStoreGuideUserName(bindingAssistant.getName());

        //关注门店信息
        Store followedStore = storeApi.getStore(vipAccount.getFollowedStoreCode());
        order.setFollowedStoreCode(followedStore.getCode());
        order.setFollowedStoreId(String.valueOf(followedStore.getId()));
        order.setFollowedStoreName(followedStore.getName());
        order.setFollowedStoreCover(followedStore.getAvatar());

        // 绑定小屋
        Store bindingStore = storeApi.getStore(vipAccount.getBindingStoreCode());
        order.setBindingStoreId(String.valueOf(bindingStore.getId()));
        order.setBindingStoreCode(bindingStore.getCode());
        order.setBindingStoreCover(bindingStore.getAvatar());
        order.setBindingStoreName(bindingStore.getName());

        if (vipAccount.getGradeId() != null) {
            user.updateGrade(vipAccount.getGradeId().toString());
        }
    }
}
