package com.jfeat.wechat.sdk.api;

import com.jfeat.kit.StrKit;

/**
 * @author jackyhuang
 * @date 2019/11/9
 */
public class ExpressUser {
    private String name;
    private String mobile;
    private String company;
    private String province;
    private String city;
    private String area;
    private String address;

    public String getName() {
        return name;
    }

    public ExpressUser setName(String name) {
        this.name = StrKit.negoString(name, 64);
        return this;
    }

    public String getCompany() {
        return company;
    }

    public ExpressUser setCompany(String company) {
        this.company = StrKit.negoString(company, 64);
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public ExpressUser setMobile(String mobile) {
        this.mobile = StrKit.negoString(mobile, 32);
        return this;
    }

    public String getProvince() {
        return province;
    }

    public ExpressUser setProvince(String province) {
        this.province = StrKit.negoString(province, 64);
        return this;
    }

    public String getCity() {
        return city;
    }

    public ExpressUser setCity(String city) {
        this.city = StrKit.negoString(city, 64);
        return this;
    }

    public String getArea() {
        return area;
    }

    public ExpressUser setArea(String area) {
        this.area = StrKit.negoString(area, 64);
        return this;
    }

    public String getAddress() {
        return address;
    }

    public ExpressUser setAddress(String address) {
        this.address = StrKit.negoString(address, 512);
        return this;
    }
}
