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

import com.jfeat.core.BaseController;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.UploadedFile;
import com.jfeat.flash.Flash;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfeat.product.model.ProductCategoryProperty;
import com.jfeat.product.service.ProductService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ProductCategoryController.class);
    private ProductService productService = Enhancer.enhance(ProductService.class);

    @Before({Flash.class})
    public void index() {
        List<ProductCategory> productCategories = productService.getProductCategories();
        setAttr("productCategories", productCategories);
    }

    @RequiresPermissions("product.category.edit")
    public void edit() {
        ProductCategory productCategory = ProductCategory.dao.findById(getParaToInt());
        if (productCategory == null) {
            renderError(404);
            return;
        }
        setAttr("productCategory", productCategory);
        List<ProductCategory> productCategories = ProductCategory.dao.findAllRecursively();
        setAttr("productCategories", productCategories);
        setAttr("valueTypes", ProductCategoryProperty.ValueType.values());
        setAttr("inputTypes", ProductCategoryProperty.InputType.values());
        List<ProductCategoryProperty> properties = productCategory.getCategoryProperties();
        if (properties.size() > 0) {
            setAttr("nextId", properties.get(properties.size() - 1).getId() + 1);
        }
        keepPara();
    }

    @RequiresPermissions("product.category.edit")
    @Before({Tx.class, CurrentUserInterceptor.class})
    public void update() {
        String url = getCover();
        ProductCategory productCategory = getModel(ProductCategory.class);
        if (StrKit.notBlank(url)) {
            productCategory.setCover(url);
        }
        productCategory.update();

        List<ProductCategoryProperty> properties = getModels(ProductCategoryProperty.class);
        List<ProductCategoryProperty> originalProperties = productCategory.getCategoryProperties();
        List<ProductCategoryProperty> toUpdatedProperties = new ArrayList<>();
        List<ProductCategoryProperty> toAddedProperties = new ArrayList<>();
        List<ProductCategoryProperty> toRemovedProperties = new ArrayList<>();
        for (ProductCategoryProperty property : originalProperties) {
            boolean found = false;
            for (ProductCategoryProperty p : properties) {
                if (p.getId() != null && p.getId().equals(property.getId())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                toUpdatedProperties.add(property);
            }
        }

        for (ProductCategoryProperty property : originalProperties) {
            boolean found = false;
            for (ProductCategoryProperty toUpdatedProperty : toUpdatedProperties) {
                if (property.getId().equals(toUpdatedProperty.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                toRemovedProperties.add(property);
            }
        }

        for (ProductCategoryProperty p : properties) {
            if (p.getId() == null) {
                toAddedProperties.add(p);
            }
        }
        productCategory.addCategoryProperties(toAddedProperties);
        productCategory.updateCategoryProperties(toUpdatedProperties);
        productCategory.deleteCategoryProperties(toRemovedProperties);

        setFlash("message", getRes().get("product_category.update.success"));
        redirect("/product_category");
    }

    @RequiresPermissions("product.category.edit")
    public void add() {
        List<ProductCategory> productCategories = productService.getProductCategories();
        setAttr("productCategories", productCategories);
        setAttr("valueTypes", ProductCategoryProperty.ValueType.values());
        setAttr("inputTypes", ProductCategoryProperty.InputType.values());
        keepPara();
    }

    private String getCover() {
        if (QiniuKit.me().isInited()) {
            UploadFile cover = getFile("cover", QiniuKit.me().getTmpdir());
            if (cover != null) {
                String path = QiniuKit.me().upload(cover.getFile().getAbsolutePath());
                if (path != null) {
                    logger.debug("deleted after saved to qiniu.");
                    cover.getFile().delete();
                    return QiniuKit.me().getFullUrl(path);
                }
            }
            return null;
        }

        String subDir = productService.getProductUploadDir();
        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        UploadFile uploadFile = getFile("cover", PhotoGalleryConstants.me().getUploadPath(), policy);
        return UploadedFile.buildUrl(uploadFile, subDir);
    }

    @RequiresPermissions("product.category.edit")
    @Before({CurrentUserInterceptor.class})
    public void save() {
        String url = getCover();
        ProductCategory productCategory = getModel(ProductCategory.class);
        productCategory.setCover(url);
        productCategory.save();
        List<ProductCategoryProperty> properties = getModels(ProductCategoryProperty.class);
        productCategory.addCategoryProperties(properties);

        setFlash("message", getRes().get("product_category.create.success"));
        redirect("/product_category");
    }

    @RequiresPermissions("product.category.edit")
    public void delete() {
        Ret ret = productService.deleteProductCategory(getParaToInt());
        setFlash("message", getRes().get(ret.get(ProductService.MESSAGE).toString()));
        redirect("/product_category");
    }

    /**
     * ajax check name
     */
    public void nameVerify() {
        String name = getPara("name");
        Integer categoryId = getParaToInt("id");
        ProductCategory originCategory = null;
        ProductCategory category = ProductCategory.dao.findByName(name);
        if (categoryId != null) {
            originCategory = ProductCategory.dao.findById(categoryId);
        }

        if (originCategory == null) {
            if (category == null) {
                renderText("true");
            }
            else {
                renderText("false");
            }
            return;
        }

        if (category == null) {
            renderText("true");
            return;
        }

        if (originCategory.getId().equals(category.getId())) {
            renderText("true");
        }
        else {
            renderText("false");
        }
    }
}
