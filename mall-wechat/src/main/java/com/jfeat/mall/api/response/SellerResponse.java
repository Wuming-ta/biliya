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

/**
 * Created by huangjacky on 16/7/16.
 */
public class SellerResponse extends ApiResponse {
    private String message;
    private Data data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        public static final String SELLER = "YES";
        public static final String APPLYING = "APPLYING";
        public static final String NOT_SELLER = "NO";

        /**
         * seller_ship : 1
         */
        private int seller_ship;
        /**
         * is_applying : 1
         */

        private int is_applying;


        public boolean getSeller_ship() {
            return seller_ship == 1;
        }

        public int getSellership() {
            return seller_ship;
        }

        public void setSeller_ship(int seller_ship) {
            this.seller_ship = seller_ship;
        }

        public int getIs_applying() {
            return is_applying;
        }

        public void setIs_applying(int is_applying) {
            this.is_applying = is_applying;
        }

        public String getSellerStatus() {
            if (seller_ship == 1) {
                return SELLER;
            }
            if (is_applying == 1) {
                return APPLYING;
            }
            return NOT_SELLER;
        }
    }
}
