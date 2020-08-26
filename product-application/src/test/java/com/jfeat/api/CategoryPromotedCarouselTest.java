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

package com.jfeat.api;

import com.jfeat.product.util.CategoryPromotedCarousel;
import com.jfinal.ext.kit.RandomKit;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by jackyhuang on 16/11/3.
 */
public class CategoryPromotedCarouselTest {
    @Test
    public void test() throws InterruptedException {
        CategoryPromotedCarousel.me().setCarouselTimeout(2000);
        int categoryId = 1;
        int promotedCount = 6;
        int totalRow = 18;
        int totalPage = 3;
        for (int i = 0; i < 10; i++) {
            int t = RandomKit.random(1, 3);
            System.out.println("sleep = " + t);
            TimeUnit.SECONDS.sleep(t);
            int pageNumber = CategoryPromotedCarousel.me().nextCarouselPageNumber(categoryId, promotedCount);
            System.out.println("pageNumber = " + pageNumber);
            CategoryPromotedCarousel.me().updateCarousel(categoryId, totalRow, totalPage);
        }
    }
}
