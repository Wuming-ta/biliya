package com.jfeat.wechat.controller;

import com.jfeat.core.BaseController;
import com.jfeat.identity.model.User;
import com.jfeat.identity.model.base.UserBase;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.UserApi;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 把旧系统里的openid和unionid关联。
 * @author jackyhuang
 * @date 2018/5/25
 */
public class WechatUnionidMigrationController extends BaseController {

    @RequiresPermissions("wechat.edit")
    @Override
    public void index() {
        StringBuilder result = new StringBuilder("migration result : ");
        boolean isLastPage = false;
        int pageNumber = 1;
        int pageSize = 50;
        Map<String, Object> params = new HashMap<>();
        while (!isLastPage) {
            Page<User> users = User.dao.paginate(pageNumber, pageSize, params);
            logger.debug("processing users: {}", users.getList());
            List<String> openidList = users.getList().stream()
                    .filter(u -> StrKit.isBlank(u.getWxUnionid()) && StrKit.notBlank(u.getWeixin()))
                    .map(UserBase::getWeixin)
                    .collect(Collectors.toList());
            logger.debug("processing openidList: {}", openidList);
            isLastPage = users.isLastPage();
            pageNumber++;

            ApiResult apiResult = UserApi.batchGetUserInfo(openidList);
            if (apiResult.isSucceed()) {
                List userInfoList = apiResult.getList("user_info_list");
                List<User> toUpdateList = new ArrayList<>();
                for (Object obj : userInfoList) {
                    Map<String, Object> map = (Map<String, Object>) obj;
                    String openid = (String) map.get("openid");
                    String unionid = (String) map.get("unionid");
                    Optional<User> res = users.getList().stream().filter(u -> openid.equals(u.getWeixin())).findFirst();
                    if (res.isPresent()) {
                        User user = new User();
                        user.setId(res.get().getId());
                        user.setWxUnionid(unionid);
                        toUpdateList.add(user);
                    }
                }
                if (!toUpdateList.isEmpty()) {
                    Db.batchUpdate(toUpdateList, 100);
                    result.append(toUpdateList);
                }
            }
            logger.debug("result: {}", apiResult.getJson());
        }
        renderText(result.toString());
    }
}
