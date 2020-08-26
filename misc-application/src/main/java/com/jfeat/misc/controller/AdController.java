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

package com.jfeat.misc.controller;

import com.jfeat.core.BaseController;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.UploadedFile;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.misc.model.Ad;
import com.jfeat.misc.model.AdGroup;
import com.jfeat.misc.model.AdLinkDefinition;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.param.ProductParam;
import com.jfeat.product.service.ProductService;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * Created by jacky on 5/16/16.
 */
public class AdController extends BaseController {

    private ProductService service = new ProductService();

    @Override
    @RequiresPermissions(value = { "MiscApplication.view", "sys.ad.menu" }, logical = Logical.OR)
    public void index() {
        setAttr("groups", AdGroup.dao.findAll());
    }

    @RequiresPermissions("MiscApplication.edit")
    public void saveGroup() {
        AdGroup group = getModel(AdGroup.class);
        group.save();
        redirect("/ad");
    }

    @RequiresPermissions("MiscApplication.edit")
    public void updateGroup() {
        AdGroup group = getModel(AdGroup.class);
        group.update();
        redirect("/ad");
    }

    @RequiresPermissions("MiscApplication.delete")
    public void deleteGroup() {
        AdGroup group = AdGroup.dao.findById(getParaToInt());
        if (group == null) {
            renderError(404);
            return;
        }
        for (Ad ad : group.getAds()) {
            UploadedFile.remove(ad.getImage());
        }
        group.delete();
        redirect("/ad");
    }

    @RequiresPermissions(value = { "MiscApplication.view", "sys.ad.menu" }, logical = Logical.OR)
    public void list() {
        AdGroup group = AdGroup.dao.findById(getParaToInt());
        List<Ad> ads;
        if (StrKit.notBlank(getPara("available"))) {
            ads = group.getAvailableAds();
        }
        else {
            ads = group.getAds();
        }
        ads.add(new Ad());
        setAttr("group", group);
        setAttr("ads", ads);
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void add() {
        setAttr("groups", AdGroup.dao.findAll());
        setAttr("functionalLinkDefinitions", AdLinkDefinition.dao.findFunctionalLink());
        setAttr("categories", service.getProductCategories());
        setAttr("categoryLinkDefinition", AdLinkDefinition.dao.findCategoryLink());
        setAttr("carouselStrategies", Ad.CarouselStrategy.values());
        keepPara();
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void save() {
        String url = getAdImage();
        Ad ad = getModel(Ad.class);
        ad.setStrategy(getCarouselStrategy());
        if (url != null) {
            ad.setImage(url);
            ad.save();
        }
        redirect("/ad/list/" + ad.getGroupId());
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void edit() {
        setAttr("groups", AdGroup.dao.findAll());
        setAttr("functionalLinkDefinitions", AdLinkDefinition.dao.findFunctionalLink());
        setAttr("categories", service.getProductCategories());
        setAttr("categoryLinkDefinition", AdLinkDefinition.dao.findCategoryLink());
        setAttr("ad", Ad.dao.findById(getParaToInt()));
        setAttr("carouselStrategies", Ad.CarouselStrategy.values());
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void update() {
        String url = getAdImage();
        Ad ad = getModel(Ad.class);
        ad.setStrategy(getCarouselStrategy());
        if (url != null) {
            ad.setImage(url);
        }
        ad.update();
        redirect("/ad/list/" + ad.getGroupId());
    }

    @Override
    @RequiresPermissions("MiscApplication.delete")
    public void delete() {
        Ad ad = Ad.dao.findById(getParaToInt());
        if (ad == null) {
            renderError(404);
            return;
        }
        Integer groupId = ad.getGroupId();
        UploadedFile.remove(ad.getImage());
        ad.delete();
        redirect("/ad/list/" + groupId);
    }

    @RequiresPermissions("MiscApplication.edit")
    public void enable() {
        Ad ad = Ad.dao.findById(getParaToInt());
        if (ad == null) {
            renderError(404);
            return;
        }
        ad.setEnabled(Ad.ENABLED);
        ad.update();
        redirect("/ad/list/" + ad.getGroupId());
    }

    @RequiresPermissions("MiscApplication.edit")
    public void disable() {
        Ad ad = Ad.dao.findById(getParaToInt());
        if (ad == null) {
            renderError(404);
            return;
        }
        ad.setEnabled(Ad.DISABLED);
        ad.update();
        redirect("/ad/list/" + ad.getGroupId());
    }

    /**
     * ajax
     */
    public void listProduct() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 20);
        String productName = getPara("productName");
        setAttr("productLinkDefinition", AdLinkDefinition.dao.findProductLink());
        ProductParam param = new ProductParam(pageNumber, pageSize);
        param.setName(productName).setStatus(Product.Status.ONSELL.toString());

        setAttr("products", Product.dao.paginate(param));
        keepPara();
    }

    private String getAdImage() {
        if (QiniuKit.me().isInited()) {
            UploadFile cover = getFile("ad", QiniuKit.me().getTmpdir());
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

        String subDir = "ad";
        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        UploadFile uploadFile = getFile("ad", PhotoGalleryConstants.me().getUploadPath(), policy);
        return UploadedFile.buildUrl(uploadFile, subDir);
    }

    private String getCarouselStrategy() {
        String[] strategyStr = getParaValues("strategyStr");
        if (strategyStr != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < strategyStr.length; i++) {
                stringBuilder.append(strategyStr[i]);
                if (i < strategyStr.length - 1) {
                    stringBuilder.append("&");
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }
}
