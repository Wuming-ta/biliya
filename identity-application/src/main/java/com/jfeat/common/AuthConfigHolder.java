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

package com.jfeat.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackyhuang on 2017/8/5.
 */
public class AuthConfigHolder {
    private static AuthConfigHolder me = new AuthConfigHolder();
    public static AuthConfigHolder me() {
        return me;
    }

    public static final String CAPTCHA_ENABLED_CONFIG_NAME = "auth.captcha.enabled";

    public static final String ALLOW_REGISTER_CONFIG_NAME = "auth.allow.register.enabled";

    public static final String ALLOW_INVITER_REASSIGN = "auth.allow.inviter.reassign";

    public static final String BG_IMAGE_CONFIG_NAME = "auth.bg.image";

    public static final String MERGE_USER_CONFIG_NAME = "auth.merge.user.enabled";

    public static final String IMMUTABLE_FIELDS_CONFIG_NAME = "auth.immutable.fields";

    private boolean captchaEnabled = true;
    private boolean allowRegisterEnabled = true;
    private boolean allowInviterReassign = true;
    private String bgImage;
    private boolean mergeUserEnabled = true;
    private String immutableFields = "";

    public String getImmutableFields() {
        return immutableFields;
    }

    public AuthConfigHolder setImmutableFields(String immutableFields) {
        this.immutableFields = immutableFields;
        return this;
    }

    public boolean isMergeUserEnabled() {
        return mergeUserEnabled;
    }

    public AuthConfigHolder setMergeUserEnabled(boolean mergeUserEnabled) {
        this.mergeUserEnabled = mergeUserEnabled;
        return this;
    }

    public boolean isCaptchaEnabled() {
        return captchaEnabled;
    }

    public void setCaptchaEnabled(boolean captchaEnabled) {
        this.captchaEnabled = captchaEnabled;
    }

    public boolean isAllowRegisterEnabled() {
        return allowRegisterEnabled;
    }

    public void setAllowRegisterEnabled(boolean allowRegisterEnabled) {
        this.allowRegisterEnabled = allowRegisterEnabled;
    }

    public boolean isAllowInviterReassign() {
        return allowInviterReassign;
    }

    public AuthConfigHolder setAllowInviterReassign(boolean allowInviterReassign) {
        this.allowInviterReassign = allowInviterReassign;
        return this;
    }

    public String getBgImage() {
        return bgImage;
    }

    public AuthConfigHolder setBgImage(String bgImage) {
        this.bgImage = bgImage;
        return this;
    }
}
