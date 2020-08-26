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

package com.jfeat.service;

import com.jfeat.core.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * N小时内完成指定额的批发。 用于线下经销商申请成为皇冠商后进行资质检查。
 * 如果没完成则会撤销皇冠资格。
 *
 * Created by jackyhuang on 2017/6/3.
 */
public interface WholesaleValidationService extends Service {
    boolean completed(int userId, Date startTime, Date endTime, BigDecimal target);
}
