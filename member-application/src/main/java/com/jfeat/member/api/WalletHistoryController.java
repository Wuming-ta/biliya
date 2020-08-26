package com.jfeat.member.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.member.model.Wallet;
import com.jfeat.member.model.WalletHistory;
import com.jfeat.member.model.base.WalletHistoryBase;
import com.jfeat.member.model.param.WalletHistoryParam;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/8/7
 */
@ControllerBind(controllerKey = "/rest/wallet_history")
public class WalletHistoryController extends RestController {

    @Before(CurrentUserInterceptor.class)
    @Override
    public void index() {
        String phone = getPara("phone");
        User currentUser = getAttr("currentUser");
        Integer userId = currentUser.getId();
        if (StrKit.notBlank(phone)) {
            User targetUser = User.dao.findByPhone(phone);
            if (targetUser == null) {
                renderFailure("user.not.found");
                return;
            }
            if (ShiroMethod.lacksPermission("member.edit")) {
                renderFailure("lack.of.permission");
                return;
            }
            userId = targetUser.getId();
        }
        Wallet wallet = Wallet.dao.findByUserId(userId);
        if (wallet == null) {
            renderFailure("wallet.not.found");
            return;
        }
        String type = getPara("type");
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        // yyyy-MM
        String month = getPara("month");
        WalletHistoryParam param = new WalletHistoryParam(pageNumber, pageSize);
        param.setType(type);
        param.setWalletId(wallet.getId());
        if (StrKit.notBlank(month)) {
            String[] d = month.split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(d[0]));
            // month 从0开始的，所以要减1
            calendar.set(Calendar.MONTH, Integer.parseInt(d[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startTime = calendar.getTime();
            param.setStartTime(startTime);

            // 当月最后一天
            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, lastDay);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date endTime = calendar.getTime();
            param.setEndTime(endTime);
        }
        logger.debug("query wallet history with param: {}", param.toString());
        renderSuccess(WalletHistory.dao.paginate(param));
    }
}
