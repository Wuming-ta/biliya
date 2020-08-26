package com.jfeat.identity.service;

import com.jfeat.identity.model.Role;
import com.jfeat.identity.model.User;
import com.jfinal.plugin.redis.Redis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/10/26
 */
public class PermissionCache {

    private String cacheName = "permission";
    private boolean enabled = false;

    private PermissionCache() {

    }

    private static PermissionCache me = new PermissionCache();
    public static PermissionCache me() {
        return me;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public PermissionCache setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * 对某用户的权限进行cache到redis
     * @param account
     */
    public void cache(String account) {
        User user = User.dao.findByLoginName(account);
        if (user != null) {
            List<String> permissions = new ArrayList<>();
            for (Role role : user.getRoles()) {
                permissions.addAll(role.getPermissionList());
            }
            cache(account, permissions);
        }
    }

    public void cache(String account, List<String> permissions) {
        if (enabled) {
            Redis.use(cacheName).set(account, String.join("|", permissions));
        }
    }
}
