package com.jfeat.product.controller;

import com.jfeat.core.BaseController;
import com.jfeat.ext.plugin.wms.WmsApi;
import com.jfeat.ext.plugin.wms.services.domain.model.QuerySkusApiResult;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfinal.aop.Before;

public class SkuSelectController extends BaseController {

    @Before({CurrentUserInterceptor.class})
    public void listSkus() {
        User currentUser = getAttr("currentUser");

        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 20);
        String skuCode = getPara("skuCode");
        String skuName = getPara("skuName");
        String barCode = getPara("barCode");

        WmsApi wmsApi = new WmsApi();
        QuerySkusApiResult apiResult = wmsApi.querySkus(currentUser.getId().longValue(), currentUser.getLoginName(), pageNumber, pageSize, skuCode, skuName, barCode);
        setAttr("skus", apiResult.getRecords());
        setAttr("skusPageNumber", apiResult.getCurrent());
        setAttr("skusTotalPage", apiResult.getPages());

        keepPara();
    }
}