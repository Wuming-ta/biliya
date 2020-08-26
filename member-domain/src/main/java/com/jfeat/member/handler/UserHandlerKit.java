package com.jfeat.member.handler;

import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.StorePlugin;
import com.jfeat.ext.plugin.VipPlugin;
import com.jfeat.ext.plugin.store.StoreApi;
import com.jfeat.ext.plugin.store.bean.Assistant;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.ext.plugin.vip.VipApi;
import com.jfeat.ext.plugin.vip.bean.VipAccount;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.MemberExt;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jackyhuang
 * @date 2018/9/1
 */
public class UserHandlerKit {
    private static final Logger logger = LoggerFactory.getLogger(UserHandlerKit.class);

    /**
     * 新建vip
     *
     * @param user
     */
    public static void createVipAccount(User user) {
        if (User.APP_USER != user.getAppUser()) {
            logger.debug("not app user, ignore.");
            return;
        }

        if (!ExtPluginHolder.me().get(VipPlugin.class).isEnabled()) {
            logger.debug("vip plugin is disabled.");
            return;
        }

        VipApi vipApi = new VipApi();
        VipAccount vipAccount = new VipAccount();
        vipAccount.setAccount(user.getLoginName())
                .setAvatar(user.getAvatar())
                .setIsFollowedWechat(user.getFollowed() == 0 ? 1 : 0)
                .setRealName(user.getRealName())
                .setRegisterMobile(user.getPhone())
                .setSex(user.getSex())
                .setVipName(user.getName())
                .setWechatName(user.getWechatName())
                .setVipNo(user.getPhone())
                .setInvalid(user.getStatus().equals(User.Status.NORMAL.toString()) ? 1 : 0);
        if (user.getBirthday() != null) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String birthdayString = sf.format(user.getBirthday());
            vipAccount.setDob(birthdayString);
        }

        if (StrKit.notBlank(user.getAssistantCode()) && ExtPluginHolder.me().get(StorePlugin.class).isEnabled()) {
            StoreApi storeApi = new StoreApi();
            Assistant assistant = storeApi.queryAssistant(user.getAssistantCode());
            vipAccount.setBindingAssistantCode(assistant.getCode());
            vipAccount.setBindingAssistantName(assistant.getName());
            assistant = storeApi.getAssistant(assistant.getId());
            if (assistant.getStores() != null && !assistant.getStores().isEmpty()) {
                Store followedStore = storeApi.getStore(assistant.getStores().get(0).getId());
                vipAccount.setFollowedStoreCode(followedStore.getCode());
                vipAccount.setFollowedStoreName(followedStore.getName());
            }
        }

        if (StrKit.isBlank(user.getAssistantCode())
                && StrKit.notBlank(user.getStoreCode())
                && ExtPluginHolder.me().get(StorePlugin.class).isEnabled()) {
            StoreApi storeApi = new StoreApi();
            Store followedStore = storeApi.getStore(user.getStoreCode());
            vipAccount.setFollowedStoreCode(followedStore.getCode());
            vipAccount.setFollowedStoreName(followedStore.getName());
        }

        if (StrKit.notBlank(user.getCabinCode()) && ExtPluginHolder.me().get(StorePlugin.class).isEnabled()) {
            StoreApi storeApi = new StoreApi();
            Store store = storeApi.getStore(user.getCabinCode());
            vipAccount.setBindingStoreCode(store.getCode());
            vipAccount.setBindingStoreName(store.getName());
        }

        User inviter = user.getInviter();
        if (inviter != null) {
            vipAccount.setInviterName(inviter.getName());
            vipAccount.setInviterAccount(inviter.getLoginName());
        }

        ApiResult apiResult = vipApi.createVipAccount(vipAccount);

        logger.debug("create result = {}", apiResult.getJson());
    }

    /**
     * 更新vip
     *
     * @param user
     * @param originalVipAccount
     */
    public static void notifyVipAccount(User user, VipAccount originalVipAccount) {
        VipAccount vipAccount = new VipAccount();
        vipAccount.setAccount(user.getLoginName())
                .setAvatar(user.getAvatar())
                .setIsFollowedWechat(user.getFollowed() == 0 ? 1 : 0)
                .setRealName(user.getRealName())
                .setRegisterMobile(user.getPhone())
                .setSex(user.getSex())
                .setVipName(user.getName())
                .setWechatName(user.getWechatName())
                .setVipNo(user.getPhone())
                .setBindingStoreName(originalVipAccount.getBindingStoreName())
                .setBindingStoreCode(originalVipAccount.getBindingStoreCode())
                .setFollowedStoreName(originalVipAccount.getFollowedStoreName())
                .setFollowedStoreCode(originalVipAccount.getFollowedStoreCode())
                .setBindingAssistantName(originalVipAccount.getBindingAssistantName())
                .setBindingAssistantCode(originalVipAccount.getBindingAssistantCode());
        if (user.getBirthday() != null) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String birthdayString = sf.format(user.getBirthday());
            vipAccount.setDob(birthdayString);
        }

        if (StrKit.isBlank(originalVipAccount.getBindingAssistantCode())
                && StrKit.notBlank(user.getAssistantCode())
                && ExtPluginHolder.me().get(StorePlugin.class).isEnabled()) {
            StoreApi storeApi = new StoreApi();
            Assistant assistant = storeApi.queryAssistant(user.getAssistantCode());
            vipAccount.setBindingAssistantCode(assistant.getCode());
            vipAccount.setBindingAssistantName(assistant.getName());
            assistant = storeApi.getAssistant(assistant.getId());
            if (assistant.getStores() != null && !assistant.getStores().isEmpty()) {
                Store followedStore = storeApi.getStore(assistant.getStores().get(0).getId());
                vipAccount.setFollowedStoreCode(followedStore.getCode());
                vipAccount.setFollowedStoreName(followedStore.getName());
            }
        }

        if (StrKit.isBlank(originalVipAccount.getFollowedStoreCode())
                && StrKit.isBlank(user.getAssistantCode())
                && StrKit.notBlank(user.getStoreCode())
                && ExtPluginHolder.me().get(StorePlugin.class).isEnabled()) {
            StoreApi storeApi = new StoreApi();
            Store store = storeApi.getStore(user.getStoreCode());
            vipAccount.setFollowedStoreCode(store.getCode());
            vipAccount.setFollowedStoreName(store.getName());
        }

        if (StrKit.isBlank(originalVipAccount.getBindingStoreCode())
                && StrKit.notBlank(user.getCabinCode())
                && ExtPluginHolder.me().get(StorePlugin.class).isEnabled()) {
            StoreApi storeApi = new StoreApi();
            Store store = storeApi.getStore(user.getCabinCode());
            vipAccount.setBindingStoreCode(store.getCode());
            vipAccount.setBindingStoreName(store.getName());
        }

        User inviter = user.getInviter();
        if (inviter != null) {
            vipAccount.setInviterName(inviter.getName());
            vipAccount.setInviterAccount(inviter.getLoginName());
        }

        VipApi vipApi = new VipApi();
        ApiResult apiResult = vipApi.updateVipAccount(vipAccount);

        logger.debug("notify result = {}", apiResult.getJson());
    }

    /**
     * 更新本地user
     *
     * @param user
     * @param originalVipAccount
     */
    public static void updateLocalUser(User user, VipAccount originalVipAccount) {
        try {
            boolean shouldUpdateUser = false;
            if (StrKit.notBlank(originalVipAccount.getBindingAssistantCode()) && StrKit.isBlank(user.getAssistantCode())) {
                user.setAssistantCode(originalVipAccount.getBindingAssistantCode());
                shouldUpdateUser = true;
            }
            if (StrKit.notBlank(originalVipAccount.getFollowedStoreCode()) && StrKit.isBlank(user.getStoreCode())) {
                user.setStoreCode(originalVipAccount.getFollowedStoreCode());
                shouldUpdateUser = true;
            }
            if (StrKit.notBlank(originalVipAccount.getBindingStoreCode()) && StrKit.isBlank(user.getCabinCode())) {
                user.setCabinCode(originalVipAccount.getBindingStoreCode());
                shouldUpdateUser = true;
            }
            if (shouldUpdateUser) {
                logger.debug("bound user from PAD, so update the local user {}", user);
                user.updateWithoutNotify();
            }

            MemberExt memberExt = MemberExt.dao.findByUserId(user.getId());
            if (memberExt != null) {
                memberExt.setCredit(originalVipAccount.getCredit());
                memberExt.setTotalCredit(originalVipAccount.getTotalCredit());
                if (originalVipAccount.getRegisterDate() != null) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sf.parse(originalVipAccount.getRegisterDate());
                    memberExt.setBeMemberTime(date);
                }
                memberExt.update();
            }
        } catch (Exception ex) {
            logger.error("updateLocalUser error. {}", ex.getMessage());
            logger.error(ex.getMessage());
            logger.error(ex.toString());
            for (StackTraceElement element : ex.getStackTrace()) {
                logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
            }
        }
    }
}
