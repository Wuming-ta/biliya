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

package com.jfeat.identity.api.model;

/**
 * Created by jacky on 5/17/16.
 */
public class ProfileEntity {

    /**
     * name : abc
     * avatar : afdafafdaf
     * email : a@a.com
     * phone : 138000000
     */

    private String name;
    private String avatar;
    private String email;
    private String phone;
    /**
     * birthday : 2015-12-11
     * sex : 0
     * details : fssfa fs
     */
    private String birthday;
    private int sex;
    private String details;
    /**
     * real_name : ABC
     */

    private String real_name;


    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getSex() {
        return sex;
    }

    public String getDetails() {
        return details;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }
}
