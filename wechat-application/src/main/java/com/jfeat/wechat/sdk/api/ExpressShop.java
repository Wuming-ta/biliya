package com.jfeat.wechat.sdk.api;

/**
 * wxa_path 	string 		是 	商家小程序的路径，建议为订单页面
 * img_url 	string 		是 	商品缩略图 url
 * goods_name 	string 		是 	商品名称
 * goods_count 	number 		是 	商品数量
 *
 * @author jackyhuang
 * @date 2019/11/13
 */
public class ExpressShop {
    private String wxa_path;
    private String img_url;
    private String goods_name;
    private Integer goods_count;

    public String getWxa_path() {
        return wxa_path;
    }

    public ExpressShop setWxa_path(String wxa_path) {
        this.wxa_path = wxa_path;
        return this;
    }

    public String getImg_url() {
        return img_url;
    }

    public ExpressShop setImg_url(String img_url) {
        this.img_url = img_url;
        return this;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public ExpressShop setGoods_name(String goods_name) {
        this.goods_name = goods_name;
        return this;
    }

    public Integer getGoods_count() {
        return goods_count;
    }

    public ExpressShop setGoods_count(Integer goods_count) {
        this.goods_count = goods_count;
        return this;
    }
}
