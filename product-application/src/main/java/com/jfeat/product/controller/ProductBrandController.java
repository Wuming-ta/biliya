package com.jfeat.product.controller;

import com.jfeat.core.BaseController;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.UploadedFile;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.product.model.ProductBrand;
import com.jfeat.product.service.ProductBrandService;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.upload.UploadFile;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * Created by kang on 2017/4/5.
 */
public class ProductBrandController extends BaseController {

    private ProductBrandService productBrandService = Enhancer.enhance(ProductBrandService.class);

    @RequiresPermissions("product_brand.edit")
    public void index() {
        setAttr("productBrands", ProductBrand.dao.findAll());
    }

    @RequiresPermissions("product_brand.edit")
    public void add() {

    }

    private String saveToQiniu(UploadFile uploadFile) {
        String url = QiniuKit.me().upload(uploadFile.getFile().getAbsolutePath());
        if (url != null) {
            logger.debug("deleted after saved to qiniu.");
            uploadFile.getFile().delete();
            return QiniuKit.me().getFullUrl(url);
        }
        return null;
    }

    private List<UploadFile> getUploadFiles() {
        if (QiniuKit.me().isInited()) {
            return getFiles(QiniuKit.me().getTmpdir());
        }
        String subDir = productBrandService.getUploadDir();
        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        return getFiles(PhotoGalleryConstants.me().getUploadPath(), policy);
    }

    private String getUploadFileUrl(UploadFile uploadFile) {
        if (QiniuKit.me().isInited()) {
            return saveToQiniu(uploadFile);
        }
        return UploadedFile.buildUrl(uploadFile, productBrandService.getUploadDir());
    }

    @RequiresPermissions("product_brand.edit")
    public void save() {
        List<UploadFile> uploadFiles = getUploadFiles();
        ProductBrand productBrand = getModel(ProductBrand.class);
        for (UploadFile uploadFile : uploadFiles) {
            if (ProductBrand.Fields.LOGO.toString().equals(uploadFile.getParameterName())) {
                productBrand.setLogo(getUploadFileUrl(uploadFile));
            }
        }
        productBrand.save();
        redirect("/product_brand");
    }

    @RequiresPermissions("product_brand.edit")
    public void edit() {
        setAttr("productBrand", ProductBrand.dao.findById(getParaToInt()));
    }

    @RequiresPermissions("product_brand.edit")
    public void update() {
        List<UploadFile> uploadFiles=getUploadFiles();
        ProductBrand productBrand = getModel(ProductBrand.class);
        for (UploadFile uploadFile : uploadFiles) {
            if (ProductBrand.Fields.LOGO.toString().equals(uploadFile.getParameterName())) {
                productBrand.setLogo(getUploadFileUrl(uploadFile));
            }
        }
        productBrand.update();
        redirect("/product_brand");
    }

    @RequiresPermissions("product_brand.edit")
    public void delete() {
        ProductBrand.dao.deleteById(getParaToInt());
        redirect("/product_brand");
    }
}
