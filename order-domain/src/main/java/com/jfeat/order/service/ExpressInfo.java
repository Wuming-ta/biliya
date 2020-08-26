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

package com.jfeat.order.service;

import com.jfinal.kit.StrKit;

import java.util.List;

/**
 * Created by jackyhuang on 16/9/29.
 */
public class ExpressInfo {

    private String company;

    private String com;
    private String comcontact;
    private String message;
    private String nu;

    /**
     * 快递单当前的状态 ：
     * 0：在途，即货物处于运输过程中；
     * 1：揽件，货物已由快递公司揽收并且产生了第一条跟踪信息；
     * 2：疑难，货物寄送过程出了问题；
     * 3：签收，收件人已签收；
     * 4：退签，即货物由于用户拒签、超区等原因退回，而且发件人已经签收；
     * 5：派件，即快递正在进行同城派件；
     * 6：退回，货物正处于退回发件人的途中；
     */
    private String state;

    /**
     * 查询结果状态 :
     * 0：物流单暂无结果，
     * 1：查询成功，
     * 2：接口出现异常，
     */
    private String status;

    /**
     * ProService field
     * false - 查询失败
     */
    private boolean result;
    /**
     * ProService field
     * 400: 提交的数据不完整，或者贵公司没授权
     * 500: 表示查询失败，或没有POST提交
     * 501: 服务器错误，快递100服务器压力过大或需要升级，暂停服务
     * 502: 服务器繁忙，详细说明见2.2《查询接口并发协议》
     * 503: 验证签名失败。
     */
    private String returnCode;

    private List<Data> data;

    public boolean isSucceed() {
        return "1".equals(getStatus());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getComcontact() {
        return comcontact;
    }

    public void setComcontact(String comcontact) {
        this.comcontact = comcontact;
    }

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public static class Data {
        private String time;
        private String context;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }
    }
}