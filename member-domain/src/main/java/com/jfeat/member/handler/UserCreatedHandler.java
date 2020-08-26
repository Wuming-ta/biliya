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
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponStrategy;
import com.jfeat.member.model.MemberExt;
import com.jfeat.member.model.MemberLevel;
import com.jfeat.member.service.CouponStrategyService;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 新用户注册, 添加会员信息, 赠送优惠券。
 * Created by kang on 2016/11/23.
 */
public class UserCreatedHandler implements Observer {
    private static Logger logger = LoggerFactory.getLogger(UserCreatedHandler.class);
    private CouponStrategyService couponStrategyService = new CouponStrategyService();

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof User && event == User.EVENT_SAVE) {
            logger.debug("user created. going to create memberext and coupon.");
            User user = User.dao.findById(((User) subject).getId());
            try {
                MemberLevel level = MemberLevel.dao.findFirstLevel();
                if (level != null) {
                    MemberExt memberExt = new MemberExt();
                    memberExt.setUserId(user.getId());
                    memberExt.setName(user.getName());
                    memberExt.setLevelId(level.getId());
                    memberExt.setPoint(0);
                    boolean result = memberExt.save();
                    logger.debug("save member ext result = {}, memberExt = {}", result, memberExt);
                } else {
                    logger.warn("member level is not defined.");
                }

                List<CouponStrategy> couponStrategies = CouponStrategy.dao.findByType(CouponStrategy.Type.REGISTER);
                for (CouponStrategy strategy : couponStrategies) {
                    Ret ret = couponStrategyService.dispatchCoupon(strategy, user.getId(), Coupon.Source.REGISTER);
                    logger.debug("Coupon dispatch result. Strategy = {}, Ret = {}", strategy.getName(), ret.getData());
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


}