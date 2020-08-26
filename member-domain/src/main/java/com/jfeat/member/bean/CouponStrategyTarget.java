package com.jfeat.member.bean;

import com.jfeat.kit.DateKit;
import com.jfinal.kit.StrKit;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author jackyhuang
 * @date 2018/8/15
 */
public class CouponStrategyTarget {

    public static final String TYPE_ALL = "all";
    public static final String TYPE_SOME = "some";

    private String type = TYPE_ALL; // all, some


    public static final Integer USER_TYPE_ALL = 0;
    public static final Integer USER_TYPE_MEMBER = 1;
    public static final Integer USER_TYPE_CUSTOMER = 2;

    public static final Integer SEX_ALL = 0;
    public static final Integer SEX_MALE = 1;
    public static final Integer SEX_FEMALE = 2;

    public static final Integer LT = -1;
    public static final Integer EQ = 0;
    public static final Integer GT = 1;

    // 0 全部， 1 会员 2 顾客
    private Integer userType;
    // 0 全部 1 男 2 女
    private Integer sex;
    private String birthdayStartTime;
    private String birthdayEndTime;
    private Integer ageStart;
    private Integer ageEnd;
    private String beCustomerStartTime;
    private String beCustomerEndTime;
    private String beMemberStartTime;
    private String beMemberEndTime;

    // -1 小于 0 等于 1 大于
    private Integer walletAmountType;
    //累计储值
    private Integer walletAmount;


    private Integer creditAmountType;
    //累计积分
    private Integer creditAmount;


    private Integer walletBalanceType;
    //储值余额
    private Integer walletBalance;


    private Integer creditBalanceType;
    // 积分余额
    private Integer creditBalance;


    private Integer consumeAmountType;
    // 累计消费
    private Integer consumeAmount;


    private Integer consumeCountType;
    //累计消费次数
    private Integer consumeCount;


    private Integer consumeAverageAmountType;
    // 次均消费
    private Integer consumeAverageAmount;


    private Integer walletConsumeAmountType;
    // 累计储值消费
    private Integer walletConsumeAmount;


    private Integer creditConsumeAmountType;
    // 累计非储值消费
    private Integer creditConsumeAmount;


    private String lastConsumeStartTime;
    private String lastConsumeEndTime;

    /**
     *
     * @param data
     * @return
     */
    public boolean canDispatchCoupon(CouponStrategyData data) {
        boolean res = true;
        try {
            if (type.equals(TYPE_ALL)) {
                return true;
            }

            res = checkUserType(data);
            res &= checkSex(data);
            res &= checkBirthday(data);
            res &= checkAge(data);
            res &= checkMemberTime(data);
            res &= checkCustomerTime(data);
            res &= checkAccount(data);
            res &= checkConsume(data);

        } catch (Exception ex) {
            return false;
        }

        return res;
    }

    private boolean checkUserType(CouponStrategyData data) {
        if (USER_TYPE_MEMBER.equals(userType) && !data.getUserType().equals(USER_TYPE_MEMBER)) {
            return false;
        }
        if (USER_TYPE_CUSTOMER.equals(userType) && !data.getUserType().equals(USER_TYPE_CUSTOMER)) {
            return false;
        }
        return true;
    }

    private boolean checkSex(CouponStrategyData data) {
        if (SEX_MALE.equals(sex) && !data.getSex().equals(SEX_MALE)) {
            return false;
        }
        if (SEX_FEMALE.equals(sex) && !data.getSex().equals(SEX_FEMALE)) {
            return false;
        }
        return true;
    }

    private boolean checkBirthday(CouponStrategyData data) throws ParseException {
        if (StrKit.notBlank(birthdayStartTime) && data.getBirthday() == null) {
            return false;
        }
        if (StrKit.notBlank(birthdayEndTime) && data.getBirthday() == null) {
            return false;
        }
        if (StrKit.notBlank(birthdayStartTime) && data.getBirthday() != null) {
            Date birthday = data.getBirthday();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birthday);
            Date definedBirthday = DateKit.toDate(calendar.get(Calendar.YEAR) + "-" + birthdayStartTime);
            if (birthday.getTime() < definedBirthday.getTime()) {
                return false;
            }
        }
        if (StrKit.notBlank(birthdayEndTime) && data.getBirthday() != null) {
            Date birthday = data.getBirthday();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birthday);
            Date definedBirthday = DateKit.toDate(calendar.get(Calendar.YEAR) + "-" + birthdayEndTime);
            if (birthday.getTime() > definedBirthday.getTime()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAge(CouponStrategyData data) throws ParseException {
        if (ageStart != null && data.getBirthday() == null) {
            return false;
        }
        if (ageEnd != null && data.getBirthday() == null) {
            return false;
        }
        if (ageStart != null && data.getBirthday() != null) {
            Date birthday = data.getBirthday();
            int age = DateKit.ageOfNow(birthday);
            if (ageStart > age) {
                return false;
            }
        }
        if (ageEnd != null && data.getBirthday() != null) {
            Date birthday = data.getBirthday();
            int age = DateKit.ageOfNow(birthday);
            if (ageEnd < age) {
                return false;
            }
        }
        return true;
    }

    private boolean checkCustomerTime(CouponStrategyData data) throws ParseException {
        if (StrKit.notBlank(beCustomerStartTime) && data.getBeCustomerTime() == null) {
            return false;
        }
        if (StrKit.notBlank(beCustomerEndTime) && data.getBeCustomerTime() == null) {
            return false;
        }
        if (StrKit.notBlank(beCustomerStartTime) && data.getBeCustomerTime() != null) {
            Date beCustomerTime = data.getBeCustomerTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(beCustomerTime);
            Date definedBeCustomerTime = DateKit.toDate(calendar.get(Calendar.YEAR) + "-" + beCustomerStartTime);
            if (beCustomerTime.getTime() < definedBeCustomerTime.getTime()) {
                return false;
            }
        }
        if (StrKit.notBlank(beCustomerEndTime) && data.getBeCustomerTime() != null) {
            Date beCustomerTime = data.getBeCustomerTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(beCustomerTime);
            Date definedBeCustomerTime = DateKit.toDate(calendar.get(Calendar.YEAR) + "-" + beCustomerEndTime);
            if (beCustomerTime.getTime() > definedBeCustomerTime.getTime()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkMemberTime(CouponStrategyData data) throws ParseException {
        if (StrKit.notBlank(beMemberStartTime) && data.getBeMemberTime() == null) {
            return false;
        }
        if (StrKit.notBlank(beMemberEndTime) && data.getBeMemberTime() == null) {
            return false;
        }
        if (StrKit.notBlank(beMemberStartTime) && data.getBeMemberTime() != null) {
            Date beMemberTime = data.getBeMemberTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(beMemberTime);
            Date definedBeMemberTime = DateKit.toDate(calendar.get(Calendar.YEAR) + "-" + beMemberStartTime);
            if (beMemberTime.getTime() < definedBeMemberTime.getTime()) {
                return false;
            }
        }
        if (StrKit.notBlank(beMemberEndTime) && data.getBeMemberTime() != null) {
            Date beMemberTime = data.getBeMemberTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(beMemberTime);
            Date definedBeMemberTime = DateKit.toDate(calendar.get(Calendar.YEAR) + "-" + beMemberEndTime);
            if (beMemberTime.getTime() > definedBeMemberTime.getTime()) {
                return false;
            }
        }
        return true;
    }

    public boolean checkAccount(CouponStrategyData data) {
        if (walletAmount != null && data.getWalletAmount() == null) {
            return false;
        }
        if (walletAmount != null && data.getWalletAmount() != null) {
            if (walletAmountType.equals(LT) && data.getWalletAmount().compareTo(walletAmount) >= 0) {
                return false;
            }
            if (walletAmountType.equals(EQ) && data.getWalletAmount().compareTo(walletAmount) != 0) {
                return false;
            }
            if (walletAmountType.equals(GT) && data.getWalletAmount().compareTo(walletAmount) <= 0) {
                return false;
            }
        }

        if (creditAmount != null && data.getCreditAmount() == null) {
            return false;
        }
        if (creditAmount != null && data.getCreditAmount() != null) {
            if (creditAmountType.equals(LT) && data.getCreditAmount().compareTo(creditAmount) >= 0) {
                return false;
            }
            if (creditAmountType.equals(EQ) && data.getCreditAmount().compareTo(creditAmount) != 0) {
                return false;
            }
            if (creditAmountType.equals(GT) && data.getCreditAmount().compareTo(creditAmount) <= 0) {
                return false;
            }
        }

        if (walletBalance != null && data.getWalletBalance() == null) {
            return false;
        }
        if (walletBalance != null && data.getWalletBalance() != null) {
            if (walletBalanceType.equals(LT) && data.getWalletBalance().compareTo(walletBalance) >= 0) {
                return false;
            }
            if (walletBalanceType.equals(EQ) && data.getWalletBalance().compareTo(walletBalance) != 0) {
                return false;
            }
            if (walletBalanceType.equals(GT) && data.getWalletBalance().compareTo(walletBalance) <= 0) {
                return false;
            }
        }

        if (creditBalance != null && data.getCreditBalance() == null) {
            return false;
        }
        if (creditBalance != null && data.getCreditBalance() != null) {
            if (creditBalanceType.equals(LT) && data.getCreditBalance().compareTo(creditBalance) >= 0) {
                return false;
            }
            if (creditBalanceType.equals(EQ) && data.getCreditBalance().compareTo(creditBalance) != 0) {
                return false;
            }
            if (creditBalanceType.equals(GT) && data.getCreditBalance().compareTo(creditBalance) <= 0) {
                return false;
            }
        }

        return true;
    }

    public boolean checkConsume(CouponStrategyData data) throws ParseException {
        if (consumeAmount != null && data.getConsumeAmount() == null) {
            return false;
        }
        if (consumeAmount != null && data.getConsumeAmount() != null) {
            if (consumeAmountType.equals(LT) && data.getConsumeAmount().compareTo(consumeAmount) >= 0) {
                return false;
            }
            if (consumeAmountType.equals(EQ) && data.getConsumeAmount().compareTo(consumeAmount) != 0) {
                return false;
            }
            if (consumeAmountType.equals(GT) && data.getConsumeAmount().compareTo(consumeAmount) <= 0) {
                return false;
            }
        }

        if (consumeCount != null && data.getConsumeCount() == null) {
            return false;
        }
        if (consumeCount != null && data.getConsumeCount() != null) {
            if (consumeCountType.equals(LT) && data.getConsumeCount().compareTo(consumeCount) >= 0) {
                return false;
            }
            if (consumeCountType.equals(EQ) && data.getConsumeCount().compareTo(consumeCount) != 0) {
                return false;
            }
            if (consumeCountType.equals(GT) && data.getConsumeCount().compareTo(consumeCount) <= 0) {
                return false;
            }
        }

        if (consumeAverageAmount != null && data.getConsumeAverageAmount() == null) {
            return false;
        }
        if (consumeAverageAmount != null && data.getConsumeAverageAmount() != null) {
            if (consumeAverageAmountType.equals(LT) && data.getConsumeAverageAmount().compareTo(consumeAverageAmount) >= 0) {
                return false;
            }
            if (consumeAverageAmountType.equals(EQ) && data.getConsumeAverageAmount().compareTo(consumeAverageAmount) != 0) {
                return false;
            }
            if (consumeAverageAmountType.equals(GT) && data.getConsumeAverageAmount().compareTo(consumeAverageAmount) <= 0) {
                return false;
            }
        }

        if (walletConsumeAmount != null && data.getWalletAmount() == null) {
            return false;
        }
        if (walletConsumeAmount != null && data.getWalletAmount() != null) {
            if (walletConsumeAmountType.equals(LT) && data.getWalletAmount().compareTo(walletConsumeAmount) >= 0) {
                return false;
            }
            if (walletConsumeAmountType.equals(EQ) && data.getWalletAmount().compareTo(walletConsumeAmount) != 0) {
                return false;
            }
            if (walletConsumeAmountType.equals(GT) && data.getWalletAmount().compareTo(walletConsumeAmount) <= 0) {
                return false;
            }
        }

        if (creditConsumeAmount != null && data.getCreditAmount() == null) {
            return false;
        }
        if (creditConsumeAmount != null && data.getCreditAmount() != null) {
            if (creditConsumeAmountType.equals(LT) && data.getCreditAmount().compareTo(creditConsumeAmount) >= 0) {
                return false;
            }
            if (creditConsumeAmountType.equals(EQ) && data.getCreditAmount().compareTo(creditConsumeAmount) != 0) {
                return false;
            }
            if (creditConsumeAmountType.equals(GT) && data.getCreditAmount().compareTo(creditConsumeAmount) <= 0) {
                return false;
            }
        }

        if (StrKit.notBlank(lastConsumeStartTime) && data.getLastConsumeTime() == null) {
            return false;
        }
        if (StrKit.notBlank(lastConsumeEndTime) && data.getLastConsumeTime() == null) {
            return false;
        }
        if (StrKit.notBlank(lastConsumeStartTime) && data.getLastConsumeTime() != null) {
            Date lastConsumeTime = data.getLastConsumeTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastConsumeTime);
            Date definedLastConsumeTime = DateKit.toDate(calendar.get(Calendar.YEAR) + "-" + lastConsumeStartTime);
            if (lastConsumeTime.getTime() < definedLastConsumeTime.getTime()) {
                return false;
            }
        }
        if (StrKit.notBlank(lastConsumeEndTime) && data.getLastConsumeTime() != null) {
            Date lastConsumeTime = data.getLastConsumeTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastConsumeTime);
            Date definedLastConsumeTime = DateKit.toDate(calendar.get(Calendar.YEAR) + "-" + lastConsumeEndTime);
            if (lastConsumeTime.getTime() > definedLastConsumeTime.getTime()) {
                return false;
            }
        }

        return true;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getBirthdayStartTime() {
        return birthdayStartTime;
    }

    public void setBirthdayStartTime(String birthdayStartTime) {
        this.birthdayStartTime = birthdayStartTime;
    }

    public String getBirthdayEndTime() {
        return birthdayEndTime;
    }

    public void setBirthdayEndTime(String birthdayEndTime) {
        this.birthdayEndTime = birthdayEndTime;
    }

    public Integer getAgeStart() {
        return ageStart;
    }

    public void setAgeStart(Integer ageStart) {
        this.ageStart = ageStart;
    }

    public Integer getAgeEnd() {
        return ageEnd;
    }

    public void setAgeEnd(Integer ageEnd) {
        this.ageEnd = ageEnd;
    }

    public String getBeCustomerStartTime() {
        return beCustomerStartTime;
    }

    public void setBeCustomerStartTime(String beCustomerStartTime) {
        this.beCustomerStartTime = beCustomerStartTime;
    }

    public String getBeCustomerEndTime() {
        return beCustomerEndTime;
    }

    public void setBeCustomerEndTime(String beCustomerEndTime) {
        this.beCustomerEndTime = beCustomerEndTime;
    }

    public String getBeMemberStartTime() {
        return beMemberStartTime;
    }

    public void setBeMemberStartTime(String beMemberStartTime) {
        this.beMemberStartTime = beMemberStartTime;
    }

    public String getBeMemberEndTime() {
        return beMemberEndTime;
    }

    public void setBeMemberEndTime(String beMemberEndTime) {
        this.beMemberEndTime = beMemberEndTime;
    }

    public Integer getWalletAmountType() {
        return walletAmountType;
    }

    public void setWalletAmountType(Integer walletAmountType) {
        this.walletAmountType = walletAmountType;
    }

    public Integer getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(Integer walletAmount) {
        this.walletAmount = walletAmount;
    }

    public Integer getCreditAmountType() {
        return creditAmountType;
    }

    public void setCreditAmountType(Integer creditAmountType) {
        this.creditAmountType = creditAmountType;
    }

    public Integer getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Integer creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Integer getWalletBalanceType() {
        return walletBalanceType;
    }

    public void setWalletBalanceType(Integer walletBalanceType) {
        this.walletBalanceType = walletBalanceType;
    }

    public Integer getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Integer walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Integer getCreditBalanceType() {
        return creditBalanceType;
    }

    public void setCreditBalanceType(Integer creditBalanceType) {
        this.creditBalanceType = creditBalanceType;
    }

    public Integer getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(Integer creditBalance) {
        this.creditBalance = creditBalance;
    }

    public Integer getConsumeAmountType() {
        return consumeAmountType;
    }

    public void setConsumeAmountType(Integer consumeAmountType) {
        this.consumeAmountType = consumeAmountType;
    }

    public Integer getConsumeAmount() {
        return consumeAmount;
    }

    public void setConsumeAmount(Integer consumeAmount) {
        this.consumeAmount = consumeAmount;
    }

    public Integer getConsumeCountType() {
        return consumeCountType;
    }

    public void setConsumeCountType(Integer consumeCountType) {
        this.consumeCountType = consumeCountType;
    }

    public Integer getConsumeCount() {
        return consumeCount;
    }

    public void setConsumeCount(Integer consumeCount) {
        this.consumeCount = consumeCount;
    }

    public Integer getConsumeAverageAmountType() {
        return consumeAverageAmountType;
    }

    public void setConsumeAverageAmountType(Integer consumeAverageAmountType) {
        this.consumeAverageAmountType = consumeAverageAmountType;
    }

    public Integer getConsumeAverageAmount() {
        return consumeAverageAmount;
    }

    public void setConsumeAverageAmount(Integer consumeAverageAmount) {
        this.consumeAverageAmount = consumeAverageAmount;
    }

    public Integer getWalletConsumeAmountType() {
        return walletConsumeAmountType;
    }

    public void setWalletConsumeAmountType(Integer walletConsumeAmountType) {
        this.walletConsumeAmountType = walletConsumeAmountType;
    }

    public Integer getWalletConsumeAmount() {
        return walletConsumeAmount;
    }

    public void setWalletConsumeAmount(Integer walletConsumeAmount) {
        this.walletConsumeAmount = walletConsumeAmount;
    }

    public Integer getCreditConsumeAmountType() {
        return creditConsumeAmountType;
    }

    public void setCreditConsumeAmountType(Integer creditConsumeAmountType) {
        this.creditConsumeAmountType = creditConsumeAmountType;
    }

    public Integer getCreditConsumeAmount() {
        return creditConsumeAmount;
    }

    public void setCreditConsumeAmount(Integer creditConsumeAmount) {
        this.creditConsumeAmount = creditConsumeAmount;
    }

    public String getLastConsumeStartTime() {
        return lastConsumeStartTime;
    }

    public void setLastConsumeStartTime(String lastConsumeStartTime) {
        this.lastConsumeStartTime = lastConsumeStartTime;
    }

    public String getLastConsumeEndTime() {
        return lastConsumeEndTime;
    }

    public void setLastConsumeEndTime(String lastConsumeEndTime) {
        this.lastConsumeEndTime = lastConsumeEndTime;
    }
}
