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

package com.jfeat.mall.api.response;

import java.util.List;

/**
 * Created by jackyhuang on 2017/5/27.
 */
public class PieceGroupPurchaseResponse extends ApiResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private Integer id;
        private String marketing_name;
        private Integer min_participator_count;
        private Integer sale;
        private String cover;
        private double price;

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getMarketing_name() {
            return marketing_name;
        }

        public void setMarketing_name(String marketing_name) {
            this.marketing_name = marketing_name;
        }

        public Integer getMin_participator_count() {
            return min_participator_count;
        }

        public void setMin_participator_count(Integer min_participator_count) {
            this.min_participator_count = min_participator_count;
        }

        public Integer getSale() {
            return sale;
        }

        public void setSale(Integer sale) {
            this.sale = sale;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}
