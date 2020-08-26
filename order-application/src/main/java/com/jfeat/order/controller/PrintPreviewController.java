package com.jfeat.order.controller;

import com.jfeat.config.model.Config;
import com.jfeat.core.BaseController;
import com.jfeat.order.model.Order;

/**
 * @author jackyhuang
 * @date 2018/10/24
 */
public class PrintPreviewController extends BaseController {

    private static final String PRINT_ORDER_LOGO_KEY = "print.order.logo";
    private static final String PRINT_ORDER_TITLE_KEY = "print.order.title";

    private String getPrintOrderValue(String key) {
        Config config = Config.dao.findByKey(key);
        if (config != null) {
            return config.getValue();
        }
        return null;
    }

    @Override
    public void index() {
        Integer id = getParaToInt("id");
        Order order = Order.dao.findById(id);
        if (order == null) {
            renderError(404);
            return;
        }
        setAttr("logo", getPrintOrderValue(PRINT_ORDER_LOGO_KEY));
        setAttr("title", getPrintOrderValue(PRINT_ORDER_TITLE_KEY));
        setAttr("order", order);
    }
}
