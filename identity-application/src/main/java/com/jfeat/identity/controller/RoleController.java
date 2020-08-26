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

package com.jfeat.identity.controller;

import com.jfeat.core.BaseController;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.PermDataPlugin;
import com.jfeat.ext.plugin.perm.PermDataApi;
import com.jfeat.ext.plugin.perm.bean.PermDataResult;
import com.jfeat.flash.Flash;
import com.jfeat.identity.authc.LoginUserStore;
import com.jfeat.identity.model.PermissionDefinition;
import com.jfeat.identity.model.PermissionGroupDefinition;
import com.jfeat.identity.model.Role;
import com.jfeat.identity.service.RoleService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoleController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(Role.class);
    private RoleService roleService = Enhancer.enhance(RoleService.class);

    @Override
    @Before(Flash.class)
    @RequiresPermissions(value = { "identity.view", "sys.role.menu" }, logical = Logical.OR)
    public void index() {
        setAttr("roles", Role.dao.findAll());
    }

    @Override
    @RequiresPermissions(value = { "identity.view", "sys.role.menu" }, logical = Logical.OR)
    public void add() {
        setAttr("role", new Role());
        setAttr("permissionGroups", PermissionGroupDefinition.dao.findVisible());
        List<PermissionDefinition> permissionDefinitions = PermissionDefinition.dao.findAll();
        Map<String, PermissionDefinition> permissionDefinitionMap = permissionDefinitions.stream().collect(Collectors.toMap(PermissionDefinition::getIdentifier, p -> p));
        setAttr("permissionDefinitionMap", permissionDefinitionMap);
        if (ExtPluginHolder.me().get(PermDataPlugin.class).isEnabled()) {
            PermDataApi permDataApi = new PermDataApi();
            PermDataResult permDataResult = permDataApi.queryPermData();
            if (permDataResult.isSucceed()) {
                setAttr("extPermDataList", permDataResult.getData());
            }
        }
    }

    @Override
    @RequiresPermissions("identity.edit")
    public void save() {
        Role role = getModel(Role.class);
        roleService.createRole(role, getParaValues("permissions"));
        redirect("/role");
    }

    @Override
    @RequiresPermissions("identity.view")
    public void edit() {
        Integer id = getParaToInt();
        setAttr("role", Role.dao.findById(id));
        setAttr("permissionGroups", PermissionGroupDefinition.dao.findVisible());
        List<PermissionDefinition> permissionDefinitions = PermissionDefinition.dao.findAll();
        Map<String, PermissionDefinition> permissionDefinitionMap = permissionDefinitions.stream().collect(Collectors.toMap(PermissionDefinition::getIdentifier, p -> p));
        setAttr("permissionDefinitionMap", permissionDefinitionMap);
        if (ExtPluginHolder.me().get(PermDataPlugin.class).isEnabled()) {
            PermDataApi permDataApi = new PermDataApi();
            PermDataResult permDataResult = permDataApi.queryPermData();
            if (permDataResult.isSucceed()) {
                setAttr("extPermDataList", permDataResult.getData());
            }
        }
    }

    @Override
    @RequiresPermissions("identity.edit")
    public void update() {
        Role role = getModel(Role.class);
        roleService.updateRole(role, getParaValues("permissions"));
        role.getUsers().forEach(user -> LoginUserStore.me().forceLogout(user.getId()));
        redirect("/role");
    }

    @Override
    @RequiresPermissions("identity.delete")
    public void delete() {
        Role role = Role.dao.findById(getParaToInt());
        if (role == null) {
            renderError(404);
            return;
        }
        role.getUsers().forEach(user -> LoginUserStore.me().forceLogout(user.getId()));
        Ret ret = roleService.deleteRole(getParaToInt());
        if (ret.isFalse(RoleService.RESULT)) {
            setFlash("message", getRes().get("identity." + ret.get(RoleService.MESSAGE)));
        }
        else {
            setFlash("message", getRes().get("identity.role.delete.success"));
        }
        redirect("/role");
    }

    /**
     * ajax check
     */
    public void nameVerify() {
        Role originRole = Role.dao.findById(getParaToInt("id"));
        Role existingRole = Role.dao.findByName(getPara("name"));

        if (originRole == null) {
            if (existingRole == null) {
                renderText("true");
            }
            else {
                renderText("false");
            }
            return;
        }

        if (existingRole == null) {
            renderText("true");
            return;
        }

        if (existingRole.getId().equals(originRole.getId())) {
            renderText("true");
        }
        else {
            renderText("false");
        }
    }

    /**
     * 添加了 group_identifier 后，需要进行数据迁移
     */
    @RequiresPermissions("identity.edit")
    public void permMigration() {
        PermissionGroupDefinition.dao.findAll().forEach(group -> {
            group.getPermissionList().forEach(permission -> {
                PermissionDefinition permissionDefinition = PermissionDefinition.dao.findFirstByField(PermissionDefinition.Fields.IDENTIFIER.toString(), permission);
                if (permissionDefinition != null) {
                    permissionDefinition.updateGroupIdentifier(group.getIdentifier());
                }
            });
        });
        renderText("ok");
    }

}
