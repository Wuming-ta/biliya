package com.jfeat.mall.api.response;

/**
 * Created by jackyhuang on 16/8/15.
 */
public class UserResponse extends ApiResponse {
    private String message;
    private UserResponse.Data data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserResponse.Data getData() {
        return data;
    }

    public void setData(UserResponse.Data data) {
        this.data = data;
    }

    public static class Data {

        /**
         * id : 1
         * login_name : abc
         * weixin : abc
         * name : name
         */

        private int id;
        private String login_name;
        private String weixin;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLogin_name() {
            return login_name;
        }

        public void setLogin_name(String login_name) {
            this.login_name = login_name;
        }

        public String getWeixin() {
            return weixin;
        }

        public void setWeixin(String weixin) {
            this.weixin = weixin;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
