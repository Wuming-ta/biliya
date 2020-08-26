package com.jfeat.ext.plugin.store.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/8/30
 */
public class Assistant {
    private Long id;
    private Long userId;
    private String code;
    private String name;
    private List<Store> stores;

    public Long getId() {
        return id;
    }

    public Assistant setId(Long id) {
        this.id = id;
        return this;
    }

    public List<Store> getStores() {
        return stores;
    }

    public Assistant setStores(List<Store> stores) {
        this.stores = stores;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Assistant setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Assistant setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public Assistant setName(String name) {
        this.name = name;
        return this;
    }
}
