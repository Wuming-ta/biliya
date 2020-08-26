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

package com.jfeat.member.api.model;

/**
 * Created by jacky on 4/8/16.
 */
public class MemberExtEntity {

    /**
     * birthday : 1999-10-10
     * sex : 1
     * address : 广州
     * description : sss
     * name : abc
     * mobile : 13800000
     */

    private String birthday;
    private int sex;
    private String address;
    private String description;
    private String name;
    private String mobile;

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getSex() {
        return sex;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }
}
