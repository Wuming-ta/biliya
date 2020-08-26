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

package com.jfeat.member.controller;

import com.jfeat.common.CouponConfigHolder;
import com.jfeat.core.BaseController;
import com.jfeat.member.model.CouponTemplate;
import com.jfeat.member.model.CouponType;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.param.ProductParam;
import com.jfeat.product.service.ProductService;
import com.jfinal.kit.StrKit;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jacky on 4/1/16.
 */
public class CouponTypeController extends BaseController {
    ProductService service = new ProductService();

    private enum Type {
        // 满减
        SUBTRACTION,
        // 折扣
        DISCOUNT,
        // 代金
        MONEY
    }

    @Override
    @RequiresPermissions(value = { "coupon.edit", "coupon_type.menu" }, logical = Logical.OR)
    public void index() {
        String name = getPara("name");
        String type = getPara("type");

        setAttr("types", Type.values());
        setAttr("couponTypes", CouponType.dao.findByCond(name,
                Type.SUBTRACTION.toString().equals(type),
                Type.DISCOUNT.toString().equals(type),
                Type.MONEY.toString().equals(type)));
        List<CouponTemplate> couponTemplates = CouponTemplate.dao.findAll();
        setAttr("couponTemplates", couponTemplates);
        setAttr("productCouponTemplate", couponTemplates.stream()
                .filter(item -> !CouponConfigHolder.me().getExcludedTemplateTypes().contains(item.getType())
                        && CouponTemplate.TYPE_PRODUCT.equalsIgnoreCase(item.getType()))
                .collect(Collectors.toList()));
        setAttr("orderCouponTemplate", couponTemplates.stream()
                .filter(item -> !CouponConfigHolder.me().getExcludedTemplateTypes().contains(item.getType())
                        && CouponTemplate.TYPE_ORDER.equalsIgnoreCase(item.getType()))
                .collect(Collectors.toList()));
        setAttr("marketingCouponTemplate", couponTemplates.stream()
                .filter(item -> !CouponConfigHolder.me().getExcludedTemplateTypes().contains(item.getType())
                        && item.getType().startsWith(CouponTemplate.TYPE_MARKETING_PREFIX))
                .collect(Collectors.toList()));
        keepPara();
    }

    @RequiresPermissions("coupon.edit")
    public void add() {
        CouponTemplate template = CouponTemplate.dao.findById(getParaToInt("templateId"));
        if (template == null) {
            renderError(404);
            return;
        }
        setAttr("template", template);
    }

    @RequiresPermissions("coupon.edit")
    public void save() {
        CouponType couponType = getModel(CouponType.class);
        String condition = replaceTemplate(couponType.getTemplate(),
                couponType.getProductId() != null ? String.valueOf(couponType.getProductId()) : "",
                couponType.getDiscount(),
                couponType.getMoney(),
                couponType.getUpTo());
        couponType.setCond(condition);
        couponType.save();
        redirect("/coupon_type");
    }

    @RequiresPermissions("coupon.edit")
    public void edit() {
        CouponType couponType = CouponType.dao.findById(getParaToInt());
        if (couponType.getProductId() != null) {
            couponType.put("product_name", Product.dao.findById(couponType.getProductId()).getName());
        }
        setAttr("couponType", couponType);
    }

    @RequiresPermissions("coupon.edit")
    public void update() {
        CouponType couponType = getModel(CouponType.class);

        String condition = replaceTemplate(couponType.getTemplate(),
                couponType.getProductId() != null ? String.valueOf(couponType.getProductId()) : "",
                couponType.getDiscount(),
                couponType.getMoney(),
                couponType.getUpTo());
        if (StrKit.notBlank(condition)) {
            couponType.setCond(condition);
        }
        couponType.update();
        redirect("/coupon_type");
    }

    @RequiresPermissions("coupon.edit")
    public void delete() {
        CouponType couponType = CouponType.dao.findById(getParaToInt());
        if (couponType == null) {
            renderError(404);
            return;
        }

        couponType.delete();
        redirect("/coupon_type");
    }

    private String replaceTemplate(String condition, String productId, Integer discount, Integer money, Integer limit) {
        if (StrKit.notBlank(condition)) {
            condition = condition.replace("#id#", productId);
            if (discount != null) {
                condition = condition.replace("#discount#", discount.toString());
            }
            if (money != null) {
                condition = condition.replace("#money#", money.toString());
            }
            if (limit != null) {
                condition = condition.replace("#totalPrice#", limit.toString());
            }
        }
        return condition;
    }

    /**
     * ajax
     */
    public void listProduct() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 20);
        String productName = getPara("productName", "");
        ProductParam param = new ProductParam(pageNumber, pageSize);
        param.setName(productName).setStatus(Product.Status.ONSELL.toString());

        setAttr("products", Product.dao.paginate(param));
        keepPara();
    }
}
