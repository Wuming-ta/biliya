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

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacky on 5/4/16.
 */
public class CreateUserResponse extends ApiResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("access_token")
        private String accessToken;

        @SerializedName("invite_code")
        private String inviteCode;
        /**
         * user_id : 134
         */

        private int user_id;

        private Integer inviter_id;

        public Integer getInviter_id() {
            return inviter_id;
        }

        public void setInviter_id(Integer inviter_id) {
            this.inviter_id = inviter_id;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getInviteCode() {
            return inviteCode;
        }

        public void setInviteCode(String inviteCode) {
            this.inviteCode = inviteCode;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
    }
}



