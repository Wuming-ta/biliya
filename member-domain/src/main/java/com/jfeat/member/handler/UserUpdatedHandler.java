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
import com.jfeat.member.service.DispatchCouponStrategyTrigger;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户信息更新，通知vip系统
 * @author jackyhuang
 * @date 2018/8/29
 */
public class UserUpdatedHandler implements Observer {

    private static final Logger logger = LoggerFactory.getLogger(UserUpdatedHandler.class);

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof User && event == User.EVENT_UPDATE) {
            logger.debug("user updated. going to notify vip service.");
            User user = (User) subject;
            if (!isAppUser(user)) {
                logger.debug("not app user {}, ignore.", user.getName());
                return;
            }
            if (StrKit.isBlank(user.getPhone())) {
                logger.debug("no phone found for user {}, ignore.", user.getName());
                return;
            }

            try {
                if (ExtPluginHolder.me().get(VipPlugin.class).isEnabled()) {
                    logger.debug("vip plugin is enabled.");

                    VipApi vipApi = new VipApi();
                    VipAccount originalVipAccount = vipApi.getVipAccount(user.getLoginName());

                    UserHandlerKit.updateLocalUser(user, originalVipAccount);
                    if (StrKit.notBlank(originalVipAccount.getAccount())) {
                        UserHandlerKit.notifyVipAccount(user, originalVipAccount);
                        DispatchCouponStrategyTrigger.me().trigger(user.getId());
                    }
                    else {
                        UserHandlerKit.createVipAccount(user);
                    }
                }
                else {
                    logger.debug("vip plugin is disabled.");
                }

            } catch (Exception ex) {
                logger.error(ex.getMessage());
                ex.printStackTrace();
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} - {}:{}", element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }
        }
    }

    /**
     * retrieve again since the param user may not contain APP__USER field.
     * @param user
     * @return
     */
    private boolean isAppUser(User user) {
        return User.dao.findById(user.getId()).getAppUser() == User.APP_USER;
    }



}
