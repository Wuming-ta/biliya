package com.jfeat.identity.api;

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.Role;
import com.jfeat.identity.model.User;
import com.jfeat.identity.model.param.UserParam;
import com.jfeat.identity.service.UserService;
import com.jfeat.kit.DateKit;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 员工api
 * @author jackyhuang
 * @date 2018/8/30
 */
@ControllerBind(controllerKey = "/rest/staff")
public class StaffController extends RestController {


    @Override
    @Before(CurrentUserInterceptor.class)
    @RequiresPermissions(value = "identity.view")
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String phone = getPara("phone");
        String name = getPara("name");
        UserParam param = new UserParam(pageNumber, pageSize);
        param.setAppUser(User.ADMIN_USER).setStatus(User.Status.NORMAL.toString()).setPhone(phone).setName(name);
        Page<User> page = User.dao.paginate(param);
        renderSuccess(page);
    }

    /**
     * 添加员工
     * POST /rest/staff
     * {
     *     loginName: "xyz",
     *     password: "xxxyy",
     *     name: "abc",
     *     contactWxNumber: "xxx",
     *     contactPhone: "1238888",
     *     realName: "xxx",
     *     sex: 1,
     *     birthday: "1999-01-01"
     * }
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    @Validation(rules = { "loginName = required", "password = required" })
    @RequiresPermissions(value = "identity.view")
    public void save() {
        Map<String, Object> maps = convertPostJsonToMap();
        String loginName = (String) maps.get("loginName");
        String password = (String) maps.get("password");
        String name = (String) maps.get("name");
        String realName = (String) maps.get("realName");
        String contactPhone = (String) maps.get("contactPhone");
        String contactWxNumber = (String) maps.get("contactWxNumber");
        String email = (String) maps.get("email");
        Integer sex = (Integer) maps.get("sex");
        Date birthday = null;
        try {
            birthday = DateKit.toDate((String) maps.get("birthday"), "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        User user = User.dao.findByLoginName(loginName);
        if (user != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status_code", 1);
            result.put("message", "user.already.exist");
            result.put("data", user);
            renderJson(result);
            return;
        }

        User newUser = new User();
        newUser.setLoginName(loginName);
        newUser.setName(name);
        newUser.setRealName(realName);
        newUser.setPassword(password);
        newUser.setContactPhone(contactPhone);
        newUser.setContactWxNumber(contactWxNumber);
        newUser.setEmail(email);
        newUser.setBirthday(birthday);
        newUser.setSex(sex);
        newUser.setAppUser(User.ADMIN_USER);
        UserService userService = new UserService();
        User currentUser = getAttr("currentUser");
        Ret ret = userService.createUser(newUser, currentUser.getRoles().stream().map(Role::getId).toArray(Integer[]::new));
        if (BaseService.isSucceed(ret)) {
            renderSuccess(newUser);
            return;
        }

        renderFailure(ret.getData());
    }

    /**
     * 更新员工
     * PUT /rest/staff/:id
     * {
     *     name: "abc",
     *     contactWxNumber: "xxx",
     *     contactPhone: "1238888",
     *     realName: "xx",
     *     sex: 1,
     *     birthday: "2000-11-21"
     * }
     */
    @Override
    @RequiresPermissions(value = "identity.view")
    public void update() {
        Map<String, Object> maps = convertPostJsonToMap();
        Integer id = (Integer) maps.get("id");
        String name = (String) maps.get("name");
        String realName = (String) maps.get("realName");
        String contactPhone = (String) maps.get("contactPhone");
        String contactWxNumber = (String) maps.get("contactWxNumber");
        String email = (String) maps.get("email");
        Integer sex = (Integer) maps.get("sex");
        String status = (String) maps.get("status");
        Date birthday = null;
        try {
            birthday = DateKit.toDate((String) maps.get("birthday"), "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        User user = User.dao.findById(getParaToInt());
        if (user == null) {
            renderError(404);
            return;
        }

        user.setName(name);
        user.setRealName(realName);
        user.setPassword("");
        user.setContactPhone(contactPhone);
        user.setContactWxNumber(contactWxNumber);
        user.setBirthday(birthday);
        user.setSex(sex);
        user.setEmail(email);
        if (StrKit.notBlank(status)) {
            user.setStatus(status);
        }
        user.setAppUser(User.ADMIN_USER);
        UserService userService = new UserService();
        userService.updateUser(user, null);

        renderSuccessMessage("user.updated");
    }

    @Override
    @RequiresPermissions(value = "identity.view")
    public void show() {
        User user = User.dao.findById(getParaToInt());
        if (user == null) {
            renderError(404);
            return;
        }

        renderSuccess(user);
    }
}
