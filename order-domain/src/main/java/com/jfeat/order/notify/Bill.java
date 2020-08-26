package com.jfeat.order.notify;

import java.math.BigDecimal;

/**
 * @author jackyhuang
 * @date 2018/11/14
 */
public class Bill {
    private String bill_id;
    private String bill_type;
    private String bill_number;
    private String amount;
    private String status;
    private String billing_time;
    private String vendor_id;
    private String assistant_id;
    private String payer_id;
    private String location;

    public String getBill_id() {
        return bill_id;
    }

    public Bill setBill_id(String bill_id) {
        this.bill_id = bill_id;
        return this;
    }

    public String getBill_type() {
        return bill_type;
    }

    public Bill setBill_type(String bill_type) {
        this.bill_type = bill_type;
        return this;
    }

    public String getBill_number() {
        return bill_number;
    }

    public Bill setBill_number(String bill_number) {
        this.bill_number = bill_number;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    public Bill setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Bill setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getBilling_time() {
        return billing_time;
    }

    public Bill setBilling_time(String billing_time) {
        this.billing_time = billing_time;
        return this;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public Bill setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
        return this;
    }

    public String getAssistant_id() {
        return assistant_id;
    }

    public Bill setAssistant_id(String assistant_id) {
        this.assistant_id = assistant_id;
        return this;
    }

    public String getPayer_id() {
        return payer_id;
    }

    public Bill setPayer_id(String payer_id) {
        this.payer_id = payer_id;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Bill setLocation(String location) {
        this.location = location;
        return this;
    }
}
