package com.jfeat.ui;

import com.jfinal.ext.plugin.shiro.ShiroMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ehngjen on 1/18/2016.
 */
public class PrivilegeStrategyImpl implements IPrivilegeStrategy, Serializable {
    private static Logger logger = LoggerFactory.getLogger(PrivilegeStrategyImpl.class);

    /**
     * 对于只有根菜单的，使用permissionList 保存权限。
     */
    private List<String> permissionList = new LinkedList<>();

    /**
     * 对于有子菜单的，使用用permissionMap针对每个子菜单分别保存权限。
     */
    private Map<String, List<String>> permissionMap = new HashMap<>();

    /**
     * 原始的数据
     */
    private List<String> permissions;

    /**
     *
     * 两种使用方式：
     *   1. 根菜单下有子菜单的
     *   2. 只有根菜单的
     *
     * @param permissions menu.key1:permission1|permission2|permission3, menu.key2:permission2|permission3
     *                    or permission1, permission2
     */
    public PrivilegeStrategyImpl(String... permissions) {
        this.permissions = Arrays.asList(permissions);
        for (String permission : permissions) {
            String[] values = permission.split(":");
            if (values.length == 2) {
                List<String> list = new LinkedList<>();
                list.addAll(Arrays.asList(values[1].split("\\|")));
                permissionMap.put(values[0], list);
            }
            else {
                permissionList.add(permission);
            }
        }
    }

    @Override
    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public void updatePermission(String... permissions) {
        for (String permission : permissions) {
            String[] values = permission.split(":");
            if (values.length == 2) {
                String key = values[0];
                String value = values[1];
                List<String> list = new LinkedList<>();
                if (permissionMap.get(key) != null && !permissionMap.get(key).isEmpty()) {
                    list.addAll(permissionMap.get(key));
                }
                list.addAll(Arrays.asList(value.split("\\|")));
                permissionMap.put(key, list);
            }
            else {
                permissionList.add(permission);
            }
        }
    }

    @Override
    public boolean isAllowed(String key) {
        logger.debug("checking privilege for key = {}", key);

        if (permissionMap.containsKey(key)) {
            if (permissionMap.get(key).isEmpty()) {
                logger.debug("permissionMap is empty, allow to access {}", key);
                return true;
            }
            for (String permission : permissionMap.get(key)) {
                if (ShiroMethod.hasPermission(permission)) {
                    logger.debug("Allow to access {}", key);
                    return true;
                }
            }
        }
        else {
            if (permissionList.isEmpty()) {
                logger.debug("permissionList is empty, allow to access {}", key);
                return true;
            }
            for (String permission : permissionList) {
                if (ShiroMethod.hasPermission(permission)) {
                    logger.debug("Allow to access {}", key);
                    return true;
                }
            }
        }

        logger.debug("NOT ALLOWED. access {}  but lack of permissions.", key);
        return false;
    }
}
