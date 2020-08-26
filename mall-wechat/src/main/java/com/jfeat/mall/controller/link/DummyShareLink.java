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

package com.jfeat.mall.controller.link;

/**
 * Created by jackyhuang on 2017/5/27.
 */
public class DummyShareLink implements MarketingShareLink {

    @Override
    public boolean init(Integer marketingId) {
        return true;
    }

    @Override
    public String buildLink() {
        return null;
    }

    @Override
    public String buildTitle() {
        return null;
    }

    @Override
    public String buildDescription() {
        return null;
    }

    @Override
    public String buildCover() {
        return null;
    }
}
