package com.jfeat.identity.mq;

/**
 * @author jackyhuang
 * @date 2019/1/22
 */
public class VipAccount {
    private String realName;
    private String vipName;
    private String dob;
    private Integer sex;
    private String account;
    private String inviterAccount;
    private String inviterName;

    public String getRealName() {
        return realName;
    }

    public VipAccount setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getVipName() {
        return vipName;
    }

    public VipAccount setVipName(String vipName) {
        this.vipName = vipName;
        return this;
    }

    public String getDob() {
        return dob;
    }

    public VipAccount setDob(String dob) {
        this.dob = dob;
        return this;
    }

    public Integer getSex() {
        return sex;
    }

    public VipAccount setSex(Integer sex) {
        this.sex = sex;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public VipAccount setAccount(String account) {
        this.account = account;
        return this;
    }

    public String getInviterAccount() {
        return inviterAccount;
    }

    public VipAccount setInviterAccount(String inviterAccount) {
        this.inviterAccount = inviterAccount;
        return this;
    }

    public String getInviterName() {
        return inviterName;
    }

    public VipAccount setInviterName(String inviterName) {
        this.inviterName = inviterName;
        return this;
    }
}
