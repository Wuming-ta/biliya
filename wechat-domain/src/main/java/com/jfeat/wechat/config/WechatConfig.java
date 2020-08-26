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

package com.jfeat.wechat.config;

import com.jfinal.kit.StrKit;

/**
 * Created by jackyhuang on 16/9/1.
 */
public class WechatConfig {

    private String certUploadPath;
    private String invitationUrlPrefix;

    private static WechatConfig me = new WechatConfig();

    private WechatConfig() {

    }

    public static WechatConfig me() {
        return me;
    }

    public String getCertUploadPath() {
        return certUploadPath;
    }

    public void setCertUploadPath(String certUploadPath) {
        if (StrKit.isBlank(certUploadPath)) {
            throw new RuntimeException("Wechat Cert Upload Path is not set.");
        }
        this.certUploadPath = certUploadPath;
    }

    public String getInvitationUrlPrefix() {
        return invitationUrlPrefix;
    }

    public WechatConfig setInvitationUrlPrefix(String invitationUrlPrefix) {
        this.invitationUrlPrefix = invitationUrlPrefix;
        return this;
    }
}
