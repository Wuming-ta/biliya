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

package com.jfeat.product.handler;

import com.jfeat.config.model.Config;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.product.util.CategoryPromotedCarousel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jackyhuang on 16/11/5.
 */
public class PromotedProductCarouselUpdatedHandler implements Observer {
    private static Logger logger = LoggerFactory.getLogger(PromotedProductCarouselUpdatedHandler.class);

    private String keyName = "mall.promoted_product_carousel";

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof Config && event == Config.EVENT_UPDATE) {
            Config config = (Config) subject;
            if (config.getKeyName().equals(keyName)) {
                int timeout = config.getValueToInt() > 30 ? config.getValueToInt() : 30;
                CategoryPromotedCarousel.me().setCarouselTimeout(timeout * 60 * 1000);
                logger.debug("timeout updated to {} minutes.", timeout);
            }
        }
    }
}
