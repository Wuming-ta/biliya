package com.jfeat.identity.api;

import com.jfeat.captcha.CaptchaKit;
import com.jfeat.common.AuthConfigHolder;
import com.jfeat.core.BaseController;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.api.model.PhoneCaptcha;
import com.jfeat.identity.api.model.PhoneCaptchaWhitelist;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.UserService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.Map;
import com.jfeat.core.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author jackyhuang
 * @date 2018/6/26
 */
@ControllerBind(controllerKey = "/rest/phone")
public class PhoneController extends RestController {
    public static Logger logger = LoggerFactory.getLogger(PhoneController.class);

    /*
        可通过第三方模块注册/注销白名单
     */
    //private String WHITELIST__PHONE  = "15800254850";
    //private static String WHITELIST_CAPTCHA = "000000";
    //private PhoneCaptchaWhitelist whitelist = PhoneCaptchaWhitelist.getInstance().register(WHITELIST__PHONE, WHITELIST_CAPTCHA);
    /**
     * End whitelist
     */

    ///
    private UserService userService = Enhancer.enhance(UserService.class);

    /**
     * 绑定手机号.
     * 如果先手机注册，再用微信登录，会出现两条记录的情况，这时微信端进行手机绑定时候就需要进行账户合并。
     */
    @Override
    @Validation(rules = {"phone = required", "captcha = required"})
    @Before({CurrentUserInterceptor.class, Tx.class})
    public void save() {
        User currentUser = getAttr("currentUser");
        Map<String, Object> map = convertPostJsonToMap();
        String phone = (String) map.get("phone");
        String name = (String) map.get("name");
        String captcha = (String) map.get("captcha");

        boolean whitelistPassed = PhoneCaptchaWhitelist.getInstance().check(phone,captcha);

        if(!whitelistPassed) {
            if (!CaptchaKit.verifyCode(phone, captcha)) {
                renderFailure("captcha.invalid");
                return;
            }
        }else{
            logger.info("phone {} pass captcha whitelist check !", phone);
        }

        User user = User.dao.findByPhone(phone);
        if(! whitelistPassed) {
            /// 正常检查
            if (user == null || user.getId().equals(currentUser.getId())) {
                userService.updatePhone(currentUser.getId(), phone);
                renderSuccess("phone.updated");
                return;
            }
            //else{
            //    logger.info("phoneUser={}", JsonKit.toJson(user));
            //    logger.info("currentUser={}}", JsonKit.toJson(currentUser));
            //}
        }else{
            // 白名单独立处理，逻辑尽可能不影响原功能
            //

            // 直接更新手机
            if ( (user != null) && (! user.getId().equals(currentUser.getId())) ) {
                /// 手机在之前注册过，解除白名单前注册用户手机信息
                userService.updatePhone(user.getId(), null);
            }
            userService.updatePhone(currentUser.getId(), phone);
            logger.info("user phone {} updated for captcha whitelist check !", phone);
            renderSuccess("phone.updated");
            return;
        }

        logger.warn("phone.already.exist, phone: {}", phone);
        logger.debug("merge conflict user enabled={}, currentUser={}, user={}", AuthConfigHolder.me().isMergeUserEnabled(), currentUser.getId(), user.getId());
        //if (needMerge(user)) {
        if(AuthConfigHolder.me().isMergeUserEnabled()){
            logger.debug("merging user id:{} => id:{}", currentUser.getId(), user.getId());

            user.setWeixin(currentUser.getWeixin());
            user.setWxUnionid(currentUser.getWxUnionid());
            user.setWxappOpenid(currentUser.getWxappOpenid());
            user.setWxaOpenid(currentUser.getWxaOpenid());
            user.setPassword("");
            user.setInviterId(currentUser.getInviterId());
            userService.deleteUser(currentUser.getId());
            userService.updateUser(user, null);
            renderSuccess("phone.updated.relogin.required");
            return;
        }

        renderFailure("phone.already.exist");
    }

    private boolean needMerge(User phoneUser) {
        if (AuthConfigHolder.me().isMergeUserEnabled()) {
            logger.info("weixin= {}, wxaOpenid={}, wxappOpenid={}", phoneUser.getWeixin(), phoneUser.getWxaOpenid(), phoneUser.getWxappOpenid());
            if (StrKit.isBlank(phoneUser.getWeixin())
                    && StrKit.isBlank(phoneUser.getWxaOpenid())
                    && StrKit.isBlank(phoneUser.getWxappOpenid())) {
                return true;
            }
            return true;
        }
        return false;
    }
}
