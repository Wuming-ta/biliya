package com.jfeat.product.controller;

import com.jfeat.core.BaseController;
import com.jfeat.product.model.ProductHitWord;
import com.jfeat.product.service.ProductService;
import com.jfinal.aop.Enhancer;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by kang on 2016/11/2.
 */
public class ProductHitWordController extends BaseController {
    private ProductService productService = Enhancer.enhance(ProductService.class);

    @RequiresPermissions("product_hit_word.edit")
    public void index() {
        setAttr("productHitWords", ProductHitWord.dao.findAllOrderByHit());
    }

    @RequiresPermissions("product_hit_word.edit")
    public void save() {
        productService.updateHitWord(getPara("name"));
        redirect("/product_hit_word");
    }

    @RequiresPermissions("product_hit_word.edit")
    public void delete() {
        ProductHitWord.dao.deleteById(getParaToInt());
        redirect("/product_hit_word");
    }
}
