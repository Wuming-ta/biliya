package com.jfeat.misc.controller;

import com.jfeat.config.model.Config;
import com.jfeat.config.utils.ConfigUtils;
import com.jfeat.core.BaseController;
import com.jfeat.product.model.Product;
import com.jfinal.kit.StrKit;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jackyhuang
 * @date 2018/9/29
 */
public class FallbackUrlController extends BaseController {

    private static final String HOME_KEY = "mall.url.home";
    private static final String PRODUCT_DETAIL_KEY = "mall.url.product_detail";
    private static final String WX_HOST_KEY = "wx.host";
    private static final String WXA_HOME_KEY = "mall.wxa.url.home";

    @Override
    public void index() {
        Config configHost = Config.dao.findByKey(WX_HOST_KEY);
        String wxHost = configHost != null ? configHost.getValue() : "";

        Config config = Config.dao.findByKey(HOME_KEY);
        List<Config> configList = new ArrayList<>();
        if (config != null) {
            configList = Config.dao.findByGroupId(config.getGroupId());
            configList = configList.stream()
                    .filter(c -> StrKit.notBlank(c.getValue()) && !c.getValue().contains("{"))
                    .peek(c -> c.put("wx_url", wxHost + "?fallback=" + Base64.getEncoder().encodeToString(c.getValue().getBytes())))
                    .collect(Collectors.toList());
        }
        setAttr("configList", configList);

        Config wxaConfig = Config.dao.findByKey(WXA_HOME_KEY);
        List<Config> wxaConfigList = new ArrayList<>();
        if (wxaConfig != null) {
            wxaConfigList = Config.dao.findByGroupId(wxaConfig.getGroupId());
        }
        setAttr("wxaConfigList", wxaConfigList);

        Config productDetailConfig = Config.dao.findByKey(PRODUCT_DETAIL_KEY);
        String detailMessageValue = productDetailConfig == null ? "" : productDetailConfig.getValue();
        List<Product> products = Product.dao.findAllOnSellRetail();
        products = products.stream()
                .peek(p -> p.put("wx_url", wxHost + "?fallback=" + Base64.getEncoder().encodeToString(MessageFormat.format(detailMessageValue, p.getId()).getBytes())))
                .collect(Collectors.toList());
        setAttr("products", products);
    }
}
