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

/*
 * This file is automatically generated by tools.
 * It defines the model for the table. All customize operation should 
 * be written here. Such as query/update/delete.
 * The controller calls this object.
 */
package com.jfeat.identity.model;

import com.jfeat.identity.constant.TableName;
import com.jfeat.identity.model.base.PermissionBase;
import com.jfeat.identity.model.base.RoleBase;
import com.jfeat.kit.SqlQuery;
import com.jfinal.ext.plugin.tablebind.TableBind;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@TableBind(tableName = "t_role")
public class Role extends RoleBase<Role> {

    public static int SYSTEM_ROLE = 1;

    /**
     * Only use for query.
     */
    public static Role dao = new Role();

    public boolean isSystemRole() {
        return getSystem() == 1;
    }

    public List<Role> findByUserId(int userId) {
        SqlQuery query = new SqlQuery();
        query.from(getTableName() + " as u");
        query.join(TableName.T_USER_ROLE);
        query.on(Fields.ID.eq("role_id"));
        query.where("user_id=?");
        return find(query.sql(), userId);
    }

    public Role findByName(String name) {
        SqlQuery query = new SqlQuery();
        query.from(getTableName());
        query.where(Fields.NAME.eq("?"));
        return findFirst(query.sql(), name);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public List<Permission> getPermissions() {
        List<Permission> permissions = Permission.dao.findByRoleId(getId());
        return permissions.stream().filter(distinctByKey(PermissionBase::getIdentifier)).collect(Collectors.toList());
    }

    public void updatePermission(String... permissions) {
        new Permission().deleteByRoleId(getId());
        if (permissions != null) {
            for (String p : permissions) {
                Permission permission = new Permission();
                permission.setIdentifier(p);
                permission.setRoleId(getId());
                permission.save();
            }
        }
    }

    public List<String> getPermissionList() {
        List<String> list = new ArrayList<String>();
        for (Permission permission : getPermissions()) {
            list.add(permission.getIdentifier());
        }
        return list;
    }

    public List<User> getUsers() {
        return User.dao.findByRoleId(getId());
    }
}