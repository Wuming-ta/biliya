/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.settlement.observer;

import com.jfeat.identity.model.User;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.settlement.model.WithdrawAccount;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * 添加用户时创建一个默认的微信帐户和钱包帐户
 * 如果用户的微信绑定更新，微信帐户也相应更新。
 * Created by huangjacky on 16/6/13.
 */
public class WithdrawAccountObserver implements Observer {
    private static Logger logger = LoggerFactory.getLogger(WithdrawAccountObserver.class);

    @Override
    public void invoke(Subject subject, int event, Object o) {
        if (subject instanceof User && event == User.EVENT_SAVE) {
            try {
                User user = (User) subject;
                WithdrawAccount account = WithdrawAccount.Type.WECHAT.createAccount(user.getId(), user.getName(), getOpenid(user));
                logger.debug("withdraw wx account saved. {}", account);

                account = WithdrawAccount.Type.WALLET.createAccount(user.getId(), user.getName(), null);
                logger.debug("withdraw wallet account saved. {}", account);

            } catch (Exception ex) {
                logger.error(ex.getMessage());
                for (StackTraceElement element : ex.getStackTrace()) {
                    ex.printStackTrace();
                    logger.error("    {}:{} - {}:{}", element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }
        }

        if (subject instanceof User && event == User.EVENT_UPDATE) {
            try {
                User user = (User) subject;
                logger.debug("user info updated. attempting update withdraw-account's weixin info if needed. userid = {}, weixin = {}", user.getId(), getOpenid(user));
                List<WithdrawAccount> withdrawAccountList = WithdrawAccount.dao.findByUserId(user.getId());
                Optional<WithdrawAccount> withdrawAccountOptional = withdrawAccountList.stream()
                        .filter(item -> item.getType().equalsIgnoreCase(WithdrawAccount.Type.WECHAT.toString()))
                        .findFirst();
                WithdrawAccount account = withdrawAccountOptional.orElseGet(() -> WithdrawAccount.Type.WECHAT
                        .createAccount(user.getId(), user.getName(), getOpenid(user)));
                if (StrKit.notBlank(account.getAccount()) && !account.getAccount().equals(getOpenid(user))) {
                    account.setAccount(getOpenid(user));
                    account.update();
                    logger.debug("withdraw wx account update. {}", account);
                }

            } catch (Exception ex) {
                logger.error(ex.getMessage());
                for (StackTraceElement element : ex.getStackTrace()) {
                    ex.printStackTrace();
                    logger.error("    {}:{} - {}:{}", element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }
        }
    }

    private String getOpenid(User user) {
        if (StrKit.notBlank(user.getWeixin())) {
            return user.getWeixin();
        }
        if (StrKit.notBlank(user.getWxaOpenid())) {
            return user.getWxaOpenid();
        }
        return "";
    }
}
