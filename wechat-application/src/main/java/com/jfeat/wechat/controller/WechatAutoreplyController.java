package com.jfeat.wechat.controller;

import com.jfeat.core.BaseController;
import com.jfeat.ext.plugin.JsonKit;
import com.jfeat.wechat.model.WechatKeywordAutoreply;
import com.jfeat.wechat.model.WechatMessageAutoreply;
import com.jfeat.wechat.model.WechatSubscribeAutoreply;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MediaApi;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;
import java.util.Map;

/**
 * Created by kang on 2017/4/27.
 */
public class WechatAutoreplyController extends BaseController {

    @Override
    @RequiresPermissions(value = { "wechat.view" }, logical = Logical.OR)
    public void index() {
        setAttr("text", WechatSubscribeAutoreply.dao.findFirstByField(WechatSubscribeAutoreply.Fields.TYPE.toString(), WechatSubscribeAutoreply.Type.TEXT.toString()));
        setAttr("newses", WechatSubscribeAutoreply.dao.findByField(WechatSubscribeAutoreply.Fields.TYPE.toString(), WechatSubscribeAutoreply.Type.NEWS.toString()));
        setAttr("messageText", WechatMessageAutoreply.dao.findFirstByField(WechatMessageAutoreply.Fields.TYPE.toString(), WechatMessageAutoreply.Type.TEXT.toString()));
        setAttr("messageNewses", WechatMessageAutoreply.dao.findByField(WechatMessageAutoreply.Fields.TYPE.toString(), WechatMessageAutoreply.Type.NEWS.toString()));
        setAttr("wechatKeywordAutoreplies", WechatKeywordAutoreply.dao.findAll());
    }

    @RequiresPermissions(value = { "wechat.view" }, logical = Logical.OR)
    public void listItem() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        ApiResult apiResult = MediaApi.batchGetMaterialNews((pageNumber - 1) * pageSize, pageSize);
        logger.debug("batchGetMaterial result = {}", JsonKit.toJson(apiResult));
        List<Map<String, Object>> item = apiResult.getList("item");
        int totalCount = apiResult.getInt("total_count");
        setAttr("item", new Page<>(item, pageNumber, pageSize, ((totalCount - 1) / pageSize) + 1, totalCount));
        keepPara();
    }

}
