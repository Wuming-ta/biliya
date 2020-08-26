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

package com.jfeat.product.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.product.model.ProductPurchaseStrategy;
import com.jfeat.product.model.ProductPurchaseStrategyItem;
import com.jfeat.product.purchase.ProductPurchaseHolder;
import com.jfeat.product.purchase.PurchaseStrategy;
import com.jfeat.product.purchase.StrategyParameterDefinition;
import com.jfinal.aop.Before;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.*;

/**
 * Created by jackyhuang on 16/10/12.
 */
@RequiresPermissions("purchase_strategy.edit")
public class PurchaseStrategyController extends BaseController {

    @Before(Flash.class)
    public void index() {
        List<ProductPurchaseStrategy> strategies = ProductPurchaseStrategy.dao.findAll();
        setAttr("strategies", strategies);
    }

    public void add() {
        setAttr("purchaseStrategies", ProductPurchaseHolder.me().getStrategies());
    }

    @Before(Tx.class)
    public void save() {
        ProductPurchaseStrategy productPurchaseStrategy = getModel(ProductPurchaseStrategy.class);
        productPurchaseStrategy.save();
        List<ProductPurchaseStrategyItem> list = convertProductPurchaseStrategyItems();
        productPurchaseStrategy.updateItems(list);
        redirect("/purchase_strategy");
    }

    private List<ProductPurchaseStrategyItem> convertProductPurchaseStrategyItems() {
        Map<Integer, ProductPurchaseStrategyItem> itemMap = Maps.newHashMap();
        String itemModelName = StrKit.firstCharToLowerCase(ProductPurchaseStrategyItem.class.getSimpleName());
        List<String> indexes = getIndexes(itemModelName);
        Collections.sort(indexes, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1 == null) return -1;
                if (o2 == null) return 1;
                try {
                    Integer i1 = Integer.parseInt(o1);
                    Integer i2 = Integer.parseInt(o2);
                    return i1.compareTo(i2);
                } catch (NumberFormatException ex) {
                    return o1.compareTo(o2);
                }
            }
        });

        int sortOrder = indexes.size() + 1;
        for (String index : indexes) {
            ProductPurchaseStrategyItem item = getModel(ProductPurchaseStrategyItem.class, itemModelName + "[" + index + "]", true);
            item.setSortOrder(sortOrder--);
            itemMap.put(Integer.parseInt(index), item);
        }
        List<ParameterDefinition> parameterDefinitions = getBeans(ParameterDefinition.class);
        Map<Integer, Map<String, Object>> parameterMap = Maps.newHashMap();
        for (ParameterDefinition definition : parameterDefinitions) {
            Map<String, Object> map = parameterMap.get(definition.getItem_id());
            if (map == null) {
                map = Maps.newHashMap();
            }
            if (definition.getType().equals("INTEGER")) {
                map.put(definition.getName(), Integer.parseInt(definition.getValue()));
            }
            else {
                map.put(definition.getName(), definition.getValue());
            }
            parameterMap.put(definition.getItem_id(), map);
        }

        for (Map.Entry<Integer, ProductPurchaseStrategyItem> entry : itemMap.entrySet()) {
            Map<String, Object> parameter = parameterMap.get(entry.getKey());
            if (parameter == null) {
                parameter = Maps.newHashMap();
            }
            ProductPurchaseStrategyItem item = entry.getValue();
            item.setParam(JsonKit.toJson(parameter));
        }

        return Lists.newArrayList(itemMap.values());

    }

    public void edit() {
        setAttr("purchaseStrategies", ProductPurchaseHolder.me().getStrategies());
        ProductPurchaseStrategy strategy = ProductPurchaseStrategy.dao.findById(getParaToInt());
        List<ProductPurchaseStrategyItem> items = strategy.getItems();
        for (ProductPurchaseStrategyItem item : items) {
            try {
                PurchaseStrategy purchaseStrategy = ProductPurchaseHolder.me().getStrategy(item.getName());
                List<StrategyParameterDefinition> parameterDefinitions = purchaseStrategy.getParameterDefinition();
                Map<String, Object> parameter = com.jfeat.kit.JsonKit.convertToMap(item.getParam());
                List<Map<String, Object>> definitions = Lists.newArrayList();
                for (StrategyParameterDefinition d : parameterDefinitions) {
                    Map<String, Object> definition = Maps.newHashMap();
                    definition.put("name", d.getName());
                    definition.put("displayName", d.getDisplayName());
                    definition.put("value", parameter.get(d.getName()));
                    definition.put("type", d.getType());
                    definitions.add(definition);
                }
                item.put("displayName", purchaseStrategy.getDisplayName());
                item.put("definitions", definitions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        strategy.put("newItems", items);
        setAttr("strategy", strategy);
    }

    @Before(Tx.class)
    public void update() {
        ProductPurchaseStrategy productPurchaseStrategy = getModel(ProductPurchaseStrategy.class);
        productPurchaseStrategy.update();
        List<ProductPurchaseStrategyItem> list = convertProductPurchaseStrategyItems();
        productPurchaseStrategy.updateItems(list);
        redirect("/purchase_strategy");
    }

    public void delete() {
        ProductPurchaseStrategy.dao.deleteById(getParaToInt());
        redirect("/purchase_strategy");
    }


    public void ajaxGetPurchaseStrategyTemplate() {
        String name = getPara("name");
        setAttr("purchaseStrategy", ProductPurchaseHolder.me().getStrategy(name));
        render("purchase_strategy_template.html");
    }
}
