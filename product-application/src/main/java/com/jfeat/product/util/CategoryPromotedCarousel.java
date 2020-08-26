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

package com.jfeat.product.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对首页推荐商品轮换展出
 * Created by jackyhuang on 16/11/3.
 */
public class CategoryPromotedCarousel {
    private static CategoryPromotedCarousel me = new CategoryPromotedCarousel();

    private Map<Integer, CarouselInfo> map = new ConcurrentHashMap<>();
    private int carouselTimeout = 1000 * 60 * 60; //unit ms. default 60 minutes

    public static CategoryPromotedCarousel me() {
        return me;
    }

    private CategoryPromotedCarousel() {
    }

    private boolean needCarousel(long lastTime) {
        long time = System.currentTimeMillis();
        return  (lastTime + carouselTimeout) < time;
    }

    public void setCarouselTimeout(int ms) {
        this.carouselTimeout = ms;
    }

    public Integer nextCarouselPageNumber(Integer categoryId, Integer promotedProductCount) {
        CarouselInfo carouselInfo = map.get(categoryId);
        if (carouselInfo == null) {
            carouselInfo = new CarouselInfo(1, promotedProductCount);
        }
        if (needCarousel(carouselInfo.getTimestamp())) {
            carouselInfo.rotate();
        }
        map.put(categoryId, carouselInfo);
        return carouselInfo.getPageNumber();
    }

    public synchronized void updateCarousel(Integer categoryId, Integer totalRow, Integer totalPage) {
        CarouselInfo carouselInfo = map.get(categoryId);
        if (carouselInfo == null) {
            return;
        }
        //如果后台更新了产品数量
        if (carouselInfo.getPageNumber() > totalPage) {
            carouselInfo.setPageNumber(1);
        }
        carouselInfo.setTotalRow(totalRow);
        carouselInfo.setTotalPage(totalPage);
        map.put(categoryId, carouselInfo);
    }

    private class CarouselInfo {
        private Integer pageNumber;
        private Integer pageSize;
        private Integer totalPage;
        private Integer totalRow;
        private Long timestamp;

        CarouselInfo() {
            timestamp = System.currentTimeMillis();
            totalPage = 0;
            totalRow = 0;
        }

        CarouselInfo(Integer pageNumber, Integer pageSize) {
            this();
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
        }

        public void rotate() {
            setTimestamp(System.currentTimeMillis());

            if (totalPage <= 0 || pageNumber <= 0 || pageSize <= 0) {
                return;
            }

            if (isLastPage()) {
                pageNumber = 1;
                return;
            }

            if ((totalPage - pageNumber) == 1 && (totalRow - (totalPage - 1) * pageSize) % pageSize != 0) {
                pageNumber = 1;
                return;
            }

            ++pageNumber;
        }

        private boolean isLastPage() {
            return pageNumber.equals(totalPage);
        }

        public Integer getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(Integer pageNumber) {
            this.pageNumber = pageNumber;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public Integer getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(Integer totalPage) {
            this.totalPage = totalPage;
        }

        public Integer getTotalRow() {
            return totalRow;
        }

        public void setTotalRow(Integer totalRow) {
            this.totalRow = totalRow;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
