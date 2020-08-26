package com.jfeat.marketing.common.controller;

import com.jfeat.core.BaseController;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfeat.product.model.param.ProductParam;
import com.jfeat.product.service.ProductService;
import com.jfinal.aop.Enhancer;

import java.util.List;

/**
 * Created by kang on 2017/5/18.
 */
public class ProductSelectController extends BaseController {

    private ProductService productService = Enhancer.enhance(ProductService.class);

    public void listProducts() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 20);
        String productName = getPara("productName");
        Integer categoryId = getParaToInt("categoryId");
        ProductParam param = new ProductParam(pageNumber, pageSize);
        param.setName(productName).setStatus(Product.Status.ONSELL.toString()).setCategoryId(categoryId);

        setAttr("products", Product.dao.paginate(param));
        setAttr("categories", ProductCategory.dao.findAllRecursively());
        keepPara();
    }

}
