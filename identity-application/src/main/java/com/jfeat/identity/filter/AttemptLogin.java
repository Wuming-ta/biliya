package com.jfeat.identity.filter;

import com.jfeat.identity.authc.AccessToken;
import com.jfeat.identity.authc.AccessTokenToken;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/8/20
 */
public class AttemptLogin {

    private static Logger logger = LoggerFactory.getLogger(AttemptLogin.class);

    private String error;

    public String getError() {
        return error;
    }

    public boolean login(String theToken) {
        Subject currentUser = SecurityUtils.getSubject();
        if ( !currentUser.isAuthenticated() ) {
            Map<String, Object> map = AccessToken.extractAccessToken(theToken);
            String token = (String) map.get(AccessToken.TOKEN);
            String loginName = (String) map.get(User.Fields.LOGIN_NAME.toString());
            AccessTokenToken tokenToken = new AccessTokenToken(loginName, token);
            try {
                currentUser.login(tokenToken);
                Date expiredDate = ((ShiroUser) currentUser.getPrincipal()).getTokenExpiredDate();
                //check expired date
                try {
                    if (expiredDate == null || expiredDate.before(DateKit.toDate(DateKit.today()))) {
                        error = "token.expired";
                        return false;
                    }
                } catch (ParseException e) {
                    throw new AuthenticationException("token.expired.date.parse.error");
                }
                return true;
                //if no exception, that's it, we're done!
            } catch ( UnknownAccountException uae ) {
                //username wasn't in the system, show them an error message?
                logger.warn(uae.getMessage());
                error = "unknown.account";
            } catch ( IncorrectCredentialsException ice ) {
                //password didn't match, try again?
                logger.warn(ice.getMessage());
                error = "password.mismatch";
            } catch ( LockedAccountException lae ) {
                //account for that username is locked - can't login.  Show them a message?
                logger.warn(lae.getMessage());
                error = "account.locked";
            } catch ( AuthenticationException ae ) {
                //unexpected condition - error?
                logger.warn(ae.getMessage());
                error = "auth.error";
            }
            return false;
        }
        return true;
    }
}
