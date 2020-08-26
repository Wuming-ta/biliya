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

package com.jfeat.partner.observer;

import com.jfeat.identity.model.User;
import com.jfeat.identity.service.UserService;
import com.jfeat.identity.subject.AttemptingUpdateInviterSubject;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.partner.model.Apply;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.SellerService;
import com.jfinal.kit.Ret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 当用户注册时没有邀请者, 如果用户还不是分销商,或正在申请成为分销商, 那么就可以改变他的邀请者
 * 如果用户已经是分销商了, 只要他的inviterId是null, 那么还是可以设置邀请者的。
 * <p/>
 * Created by huangjacky on 16/7/12.
 */
public class InvitorUpdatedObserver implements Observer {

    private static Logger logger = LoggerFactory.getLogger(InvitorUpdatedObserver.class);

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof AttemptingUpdateInviterSubject && event == AttemptingUpdateInviterSubject.EVENT_ATTEMPTING_UPDATE) {
            try {
                AttemptingUpdateInviterSubject attemptingUpdateInvotorSubject = (AttemptingUpdateInviterSubject) subject;
                logger.debug("attempting update user[{}]'s invitor[{}].", attemptingUpdateInvotorSubject.getUserId(), attemptingUpdateInvotorSubject.getInvitorId());
                User user = User.dao.findById(attemptingUpdateInvotorSubject.getUserId());
                Seller seller = Seller.dao.findByUserId(attemptingUpdateInvotorSubject.getUserId());
                if (user == null || seller == null) {
                    logger.debug("seller is null. ignore");
                    return;
                }

                if (user.getId().equals(attemptingUpdateInvotorSubject.getInvitorId())) {
                    logger.debug("user_id is the same as attempting inviter_id. ignore");
                    return;
                }

                if (!seller.isSellerShip()) {
                    Apply apply = Apply.dao.findByUserId(seller.getUserId());
                    if (apply != null
                            && apply.getType().equals(Apply.Type.SELLER.toString())
                            && apply.getStatus().equals(Apply.Status.INIT.toString())) {
                        logger.debug("user is applying sellership. ignore");
                        return;
                    }

                    UserService userService = new UserService();
                    user.setInviterId(attemptingUpdateInvotorSubject.getInvitorId());
                    user.remove(User.Fields.PASSWORD.toString());
                    userService.updateUser(user, null);
                    logger.debug("ok, update user {} 's inviter_id to {}", user.getName(), user.getInviterId());
                    return;
                }

                //用户已经是分销商了, 检查邀请者信息
                if (user.getInviterId() != null) {
                    logger.debug("user already has inviter, ignore it.");
                    return;
                }

                SellerService sellerService = new SellerService();
                Seller parentSeller = Seller.dao.findByUserId(attemptingUpdateInvotorSubject.getInvitorId());
                if (sellerService.isAbsoluteChild(seller.getId(), parentSeller.getId())) {
                    logger.debug("seller {} is seller {} 's parent. ignore it.", seller.getId(), parentSeller.getId());
                    return;
                }

                UserService userService = new UserService();
                user.setInviterId(attemptingUpdateInvotorSubject.getInvitorId());
                user.remove(User.Fields.PASSWORD.toString());
                Ret ret = userService.updateUser(user, null);
                logger.debug("ret = {}, ok, update user {} 's inviter_id to {}", ret.getData(), user.getName(), user.getInviterId());

                ret = sellerService.maintainSellerAncestor(seller, parentSeller);
                logger.debug("maintain ancestor ret = {}", ret.getData());
                ret = sellerService.promotePartnerLevel(seller.getPartner());
                logger.debug("partner level promote result: {}", ret.getData());
                ret = sellerService.promoteParentSellerToPartner(seller);
                logger.debug("promote partner result: {}", ret.getData());
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} - {}:{}",
                            element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }
        }
    }
}
