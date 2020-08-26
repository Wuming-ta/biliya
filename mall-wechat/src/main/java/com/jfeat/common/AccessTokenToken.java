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

import com.jfeat.kit.Encodes;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.util.ByteSource;

/**
 * Created by huangjacky on 16/6/1.
 */
public class AccessTokenToken implements AuthenticationToken {
    private Integer id;
    private String username;
    private String token;
    private String accessToken;
    private String inviteCode;
    private String sellerStatus;
    private Integer inviterId;

    public AccessTokenToken(Integer id, String username, String token, String accessToken, String inviteCode, String sellerStatus, Integer inviterId) {
        this.id = id;
        this.username = username;
        this.token = token;
        this.accessToken = accessToken;
        this.inviteCode = inviteCode;
        this.sellerStatus = sellerStatus;
        this.inviterId = inviterId;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return ByteSource.Util.bytes(Encodes.decodeHex(token));
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUsername() {
        return username;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getSellerStatus() {
        return sellerStatus;
    }

    public void setSellerStatus(String sellerStatus) {
        this.sellerStatus = sellerStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInviterId() {
        return inviterId;
    }

    public void setInviterId(Integer inviterId) {
        this.inviterId = inviterId;
    }
}
