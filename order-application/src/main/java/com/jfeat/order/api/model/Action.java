package com.jfeat.order.api.model;

/**
 * 店员对订单的操作
 */
public class Action {
    public static final String COMPLETE = "complete";  //完成
    public static final String CANCEL = "cancel";  //取消
    public static final String ACCEPT = "accept";  //受理
    public static final String REJECT= "reject";  //拒绝
    public static final String DELIVERING="delivering";  //开始配送
}
