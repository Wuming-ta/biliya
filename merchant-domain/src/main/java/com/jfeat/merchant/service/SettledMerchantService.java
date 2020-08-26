package com.jfeat.merchant.service;

import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.identity.model.base.RoleBase;
import com.jfeat.identity.service.UserService;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfeat.merchant.model.SettledMerchantApproveLog;
import com.jfeat.merchant.model.UserSettledMerchant;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.SellerService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kang on 2017/3/21.
 */
public class SettledMerchantService extends BaseService {

    private UserService userService = Enhancer.enhance(UserService.class);
    private SellerService sellerService = Enhancer.enhance(SellerService.class);

    public SettledMerchantService() {
    }

    @Before(Tx.class)
    public void updateStatus(SettledMerchant settledMerchant, Integer roleId, String result, String administrator) {
        settledMerchant.update();

        SettledMerchantApproveLog settledMerchantApproveLog = new SettledMerchantApproveLog();
        settledMerchantApproveLog.setAdministrator(administrator);
        settledMerchantApproveLog.setHandledDate(new Date());
        settledMerchantApproveLog.setMerchantId(settledMerchant.getId());
        settledMerchantApproveLog.setResult(result);
        settledMerchantApproveLog.save();

        List<UserSettledMerchant> userSettledMerchants = UserSettledMerchant.dao.findByMerchantId(settledMerchant.getId());
        User user = userSettledMerchants.get(0).getUser();
        List<Integer> roles = user.getRoles().stream().map(RoleBase::getId).collect(Collectors.toList());
        if (roleId != null && !roles.contains(roleId)) {
            roles.add(roleId);
        }
        user.setPassword("");
        userService.updateUser(user, roles.toArray(new Integer[0]));

        if (SettledMerchant.Status.APPROVED.toString().equals(settledMerchant.getStatus())) {
            // 设置为经销商,因为经销商才可以提现
            //Seller seller = Seller.dao.findByUserId(user.getId());
            //Ret ret = sellerService.assignPartnerRight(seller.getId());
            //logger.debug("assign partner result: ret = {}", ret.getData());

            //todo 通知用户资料审核通过 由于用户是自注册的，不一定关联微信，所以不能用微信消息模板
        }
        if (SettledMerchant.Status.REJECTED.toString().equals(settledMerchant.getStatus())) {
            //todo 通知用户资料没审核通过
        }
    }
}
