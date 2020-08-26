package com.jfeat.product.controller;

import com.jfeat.core.BaseController;
import com.jfeat.product.model.ProductTag;

/**
 * @author jackyhuang
 * @date 2018/11/13
 */
public class ProductTagController extends BaseController {

    @Override
    public void index() {
        setAttr("tags", ProductTag.dao.findAll());
    }

    @Override
    public void add() {
        setAttr("tag", new ProductTag());
    }

    @Override
    public void save() {
        ProductTag productTag = getModel(ProductTag.class);
        productTag.save();
        redirect("/product_tag");
    }

    @Override
    public void edit() {
        ProductTag productTag = ProductTag.dao.findById(getPara());
        setAttr("tag", productTag);
    }

    @Override
    public void update() {
        ProductTag productTag = getModel(ProductTag.class);
        productTag.update();
        redirect("/product_tag");
    }

    @Override
    public void delete() {
        ProductTag.dao.deleteById(getPara());
        redirect("/product_tag");
    }

    /**
     * ajax verify
     */
    public void identifierVerify() {
        checkProductTag(ProductTag.dao.findByIdentifier(getPara("identifier")), getParaToInt("id"));
    }
    private void checkProductTag(ProductTag productTag, Integer id) {
        ProductTag originProductTag = null;
        if (id != null) {
            originProductTag = ProductTag.dao.findById(id);
        }

        if (originProductTag == null) {
            if (productTag == null) {
                renderText("true");
            }
            else {
                renderText("false");
            }
            return;
        }

        // originUser is not null

        if (productTag == null) {
            renderText("true");
            return;
        }

        if (originProductTag.getId().equals(productTag.getId())) {
            renderText("true");
        }
        else {
            renderText("false");
        }

    }
}
