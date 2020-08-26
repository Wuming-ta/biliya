package com.jfeat.ext.plugin.store.bean;

import java.math.BigDecimal;

/**
 * @author jackyhuang
 * @date 2018/8/28
 */
public class Appointment {
    private String itemName;
    private String code;
    private Long id;
    private BigDecimal fee;

//    PAY_PENDING,    //待支付
//    PAY_TIMEOUT,    //支付超时
//    WAIT_TO_STORE,  //待到店
//    PAYMENT_CANCEL, //未支付
//    ALREADY_TO_STORE,   //已到店
//    NO_TO_STORE,    //未到店
//    CANCEL,          //已取消
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
