package com.jfeat.ext.plugin.vip.bean;

import java.util.Date;

/**
 * @author jackyhuang
 * @date 2018/8/29
 */
public class VipAccount {
    private String vipNo;
    private String account;
    private Integer sex;
    private String registerMobile;
    private Integer isFollowedWechat;
    private String wechatName;
    private String inviterName;
    private String inviterAccount;
    private String vipName;
    private String realName;
    private String avatar;
    private String dob;
    private Integer invalid;

    private String registerDate;
    private Integer credit;
    private Integer totalCredit;
    private Long gradeId;

    private String followedStoreCode;
    private String followedStoreName;
    private String bindingAssistantCode;
    private String bindingAssistantName;
    private String bindingStoreCode;
    private String bindingStoreName;

    public Integer getTotalCredit() {
        return totalCredit;
    }

    public VipAccount setTotalCredit(Integer totalCredit) {
        this.totalCredit = totalCredit;
        return this;
    }

    public Integer getCredit() {
        return credit;
    }

    public VipAccount setCredit(Integer credit) {
        this.credit = credit;
        return this;
    }

    public String getBindingStoreCode() {
        return bindingStoreCode;
    }

    public VipAccount setBindingStoreCode(String bindingStoreCode) {
        this.bindingStoreCode = bindingStoreCode;
        return this;
    }

    public String getBindingStoreName() {
        return bindingStoreName;
    }

    public VipAccount setBindingStoreName(String bindingStoreName) {
        this.bindingStoreName = bindingStoreName;
        return this;
    }

    public String getVipNo() {
        return vipNo;
    }

    public VipAccount setVipNo(String vipNo) {
        this.vipNo = vipNo;
        return this;
    }

    public String getFollowedStoreCode() {
        return followedStoreCode;
    }

    public VipAccount setFollowedStoreCode(String followedStoreCode) {
        this.followedStoreCode = followedStoreCode;
        return this;
    }

    public String getFollowedStoreName() {
        return followedStoreName;
    }

    public VipAccount setFollowedStoreName(String followedStoreName) {
        this.followedStoreName = followedStoreName;
        return this;
    }

    public String getBindingAssistantCode() {
        return bindingAssistantCode;
    }

    public VipAccount setBindingAssistantCode(String bindingAssistantCode) {
        this.bindingAssistantCode = bindingAssistantCode;
        return this;
    }

    public String getBindingAssistantName() {
        return bindingAssistantName;
    }

    public VipAccount setBindingAssistantName(String bindingAssistantName) {
        this.bindingAssistantName = bindingAssistantName;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public VipAccount setAccount(String account) {
        this.account = account;
        return this;
    }

    public Integer getSex() {
        return sex;
    }

    public VipAccount setSex(Integer sex) {
        this.sex = sex;
        return this;
    }

    public String getRegisterMobile() {
        return registerMobile;
    }

    public VipAccount setRegisterMobile(String registerMobile) {
        this.registerMobile = registerMobile;
        return this;
    }

    public Integer getIsFollowedWechat() {
        return isFollowedWechat;
    }

    public VipAccount setIsFollowedWechat(Integer isFollowedWechat) {
        this.isFollowedWechat = isFollowedWechat;
        return this;
    }

    public String getWechatName() {
        return wechatName;
    }

    public VipAccount setWechatName(String wechatName) {
        this.wechatName = wechatName;
        return this;
    }

    public String getInviterName() {
        return inviterName;
    }

    public VipAccount setInviterName(String inviterName) {
        this.inviterName = inviterName;
        return this;
    }

    public String getInviterAccount() {
        return inviterAccount;
    }

    public VipAccount setInviterAccount(String inviterAccount) {
        this.inviterAccount = inviterAccount;
        return this;
    }

    public String getVipName() {
        return vipName;
    }

    public VipAccount setVipName(String vipName) {
        this.vipName = vipName;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public VipAccount setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public VipAccount setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public Integer getInvalid() {
        return invalid;
    }

    public VipAccount setInvalid(Integer invalid) {
        this.invalid = invalid;
        return this;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public VipAccount setGradeId(Long gradeId) {
        this.gradeId = gradeId;
        return this;
    }

    public String getDob() {
        return dob;
    }

    public VipAccount setDob(String dob) {
        this.dob = dob;
        return this;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public VipAccount setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
        return this;
    }

    @Override
    public String toString() {
        return "VipAccount{" +
                "vipNo='" + vipNo + '\'' +
                ", account='" + account + '\'' +
                ", sex=" + sex +
                ", registerMobile='" + registerMobile + '\'' +
                ", isFollowedWechat=" + isFollowedWechat +
                ", wechatName='" + wechatName + '\'' +
                ", inviterName='" + inviterName + '\'' +
                ", inviterAccount='" + inviterAccount + '\'' +
                ", vipName='" + vipName + '\'' +
                ", realName='" + realName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", dob=" + dob +
                ", invalid=" + invalid +
                ", gradeId=" + gradeId +
                ", followedStoreCode='" + followedStoreCode + '\'' +
                ", followedStoreName='" + followedStoreName + '\'' +
                ", bindingAssistantCode='" + bindingAssistantCode + '\'' +
                ", bindingAssistantName='" + bindingAssistantName + '\'' +
                ", bindingStoreCode='" + bindingStoreCode + '\'' +
                ", bindingStoreName='" + bindingStoreName + '\'' +
                '}';
    }
}
