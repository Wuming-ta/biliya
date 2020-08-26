package com.jfeat.partner.observer;

import com.jfeat.identity.model.User;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.partner.model.Apply;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfinal.aop.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kang on 2017/6/26.
 */
public class UserInfollowSubscribeHandler implements Observer {

    private static Logger logger = LoggerFactory.getLogger(UserInfollowSubscribeHandler.class);
    private PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);

    @Override
    public void invoke(Subject subject, int event, Object param) {
        try {
            if (subject instanceof User && event == User.EVENT_USER_INFOLLOW_SUBSCRIBE) {
                User user = (User) subject;
                logger.debug("user {} infollow subscribe.", user.getName());
                //从t_apply表中找申请记录，如果有INIT的申请线下记录，则根据自动批准线下皇冠商机制
                Apply physicalSellerApply = Apply.dao.findByUserIdType(user.getId(), Apply.Type.PHYSICAL.toString());
                if (physicalSellerApply != null && Apply.Status.valueOf(physicalSellerApply.getStatus()) == Apply.Status.INIT) {
                    physicalSellerService.autoAuditPhysicalSellerShip(physicalSellerApply.getId());
                }
                //从t_apply表中找申请记录，如果有INIT的申请皇冠商记录，则根据自动批准线下皇冠商机制
                Apply crownApply = Apply.dao.findByUserIdType(user.getId(), Apply.Type.CROWN.toString());
                if (crownApply != null && Apply.Status.valueOf(crownApply.getStatus()) == Apply.Status.INIT) {
                    physicalSellerService.autoAuditCrownShip(crownApply.getId());
                }

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
