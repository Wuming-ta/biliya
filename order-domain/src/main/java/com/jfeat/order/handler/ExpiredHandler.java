package com.jfeat.order.handler;

import com.jfinal.kit.Ret;

/**
 * Created by kang on 2017/6/10.
 */
public interface ExpiredHandler {
    public Ret handle(int orderId);
}
