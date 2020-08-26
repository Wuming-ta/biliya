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

package com.jfeat.identity.authc;


import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Date;

/**
 * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
 */
public class ShiroUser implements Serializable {
    public Integer id;
    public String loginName;
    public String name;
    public String avatar;
    public Date tokenExpiredDate;

    public ShiroUser(Integer id, String loginName, String name) {
        this.id = id;
        this.loginName = loginName;
        this.name = name;
    }

    public ShiroUser(Integer id, String loginName, String name, Date tokenExpiredDate) {
        this(id, loginName, name);
        this.tokenExpiredDate = tokenExpiredDate;
    }

    public ShiroUser(Integer id, String loginName, String name, Date tokenExpiredDate, String avatar) {
        this(id, loginName, name, tokenExpiredDate);
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public Date getTokenExpiredDate() {
        return tokenExpiredDate;
    }

    public String LoginName() {
        return loginName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 本函数输出将作为默认的<shiro:principal/>输出.
     */
    @Override
    public String toString() {
        return loginName;
    }

    /**
     * 重载hashCode,只计算loginName;
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(loginName);
    }

    /**
     * 重载equals,只计算loginName;
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ShiroUser other = (ShiroUser) obj;
        if (loginName == null) {
            if (other.loginName != null) {
                return false;
            }
        } else if (!loginName.equals(other.loginName)) {
            return false;
        }
        return true;
    }
}
