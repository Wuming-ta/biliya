package com.jfeat.marketing.wholesale.controller;

import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.UploadedFile;
import com.jfeat.flash.Flash;
import com.jfeat.kit.DateKit;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.marketing.common.model.MarketingConfig;
import com.jfeat.marketing.wholesale.model.Wholesale;
import com.jfeat.marketing.wholesale.model.WholesaleCategory;
import com.jfeat.marketing.wholesale.model.WholesaleMember;
import com.jfeat.marketing.wholesale.model.WholesalePricing;
import com.jfeat.marketing.wholesale.service.WholesaleService;
import com.jfeat.pcd.model.Pcd;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfeat.product.model.param.ProductParam;
import com.jfeat.product.service.ProductService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * Created by kang on 2017/5/17.
 */
public class WholesaleController extends BaseController {

    private ProductService productService = Enhancer.enhance(ProductService.class);
    private WholesaleService wholesaleService = Enhancer.enhance(WholesaleService.class);

    @Before(Flash.class)
    @RequiresPermissions("marketing.wholesale.view")
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        Integer categoryId = getParaToInt("categoryId");
        String marketingName = getPara("marketingName");
        String status = getPara("status");
        setAttr("wholesales", Wholesale.dao.paginate(pageNumber, pageSize, categoryId, marketingName, status));
        setAttr("statuses", Wholesale.Status.values());
        setAttr("wholesaleCategories", WholesaleCategory.dao.findAll());
        setAttr("marketingConfig", MarketingConfig.dao.findFirstByField(MarketingConfig.Fields.TYPE.toString(), MarketingConfig.Type.WHOLESALE.toString()));

        keepPara();
    }

    @RequiresPermissions("marketing.wholesale.edit")
    public void add() {
        setAttr("wholesale", new Wholesale());
        setAttr("wholesaleCategories", WholesaleCategory.dao.findAll());
        Integer categoryId = null;
        List<ProductCategory> categories = ProductCategory.dao.findAllWholesaleRoot();
        if (!categories.isEmpty()) {
            categoryId = categories.get(0).getId();
        }
        setAttr("categoryId", categoryId);
    }

    @RequiresPermissions("marketing.wholesale.edit")
    public void save() {
        List<UploadFile> uploadFiles = getUploadFiles();
        Wholesale wholesale = getModel(Wholesale.class);
        List<WholesalePricing> wholesalePricings = getModels(WholesalePricing.class);
        if (uploadFiles != null && uploadFiles.size() > 0) {
            String url = getUploadFileUrl(uploadFiles.get(0));
            if (StrKit.notBlank(url)) {
                wholesale.setCover(url);
            }
        }

        Ret ret = wholesaleService.createWholeSale(wholesale, wholesalePricings);
        setFlash("message", getRes().get(ret.get(BaseService.MESSAGE).toString()));
        redirect("/wholesale");
    }

    @RequiresPermissions("marketing.wholesale.edit")
    public void edit() {
        setAttr("wholesale", Wholesale.dao.findById(getParaToInt()));
        setAttr("wholesaleCategories", WholesaleCategory.dao.findAll());
        Integer categoryId = null;
        List<ProductCategory> categories = ProductCategory.dao.findAllWholesaleRoot();
        if (!categories.isEmpty()) {
            categoryId = categories.get(0).getId();
        }
        setAttr("categoryId", categoryId);
        keepPara();
    }

    @RequiresPermissions("marketing.wholesale.edit")
    public void update() {
        List<UploadFile> uploadFiles = getUploadFiles();
        Wholesale wholesale = getModel(Wholesale.class);
        List<WholesalePricing> wholesalePricings = getModels(WholesalePricing.class);
        if (uploadFiles != null && uploadFiles.size() > 0) {
            String url = getUploadFileUrl(uploadFiles.get(0));
            if (StrKit.notBlank(url)) {
                wholesale.setCover(url);
            }
        }
        Ret ret = wholesaleService.updateWholesale(wholesale, wholesalePricings);
        setFlash("message", getRes().get(ret.get(BaseService.MESSAGE).toString()));
        String returnUrl = getPara("returnUrl", "/wholesale");
        redirect(urlDecode(returnUrl));
    }

    @RequiresPermissions("marketing.wholesale.delete")
    public void delete() {
        new Wholesale().deleteById(getParaToInt());
        String returnUrl = getPara("returnUrl", "/wholesale");
        redirect(urlDecode(returnUrl));
    }

    private String urlDecode(String url) {
        return StringEscapeUtils.unescapeHtml4(url);
    }

    /**
     * ajax
     */
    public void listProduct() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 20);
        String productName = getPara("productName");
        ProductParam param = new ProductParam(pageNumber, pageSize);
        param.setName(productName).setStatus(Product.Status.ONSELL.toString());

        setAttr("products", Product.dao.paginate(param));
        keepPara();
    }

    public void onsell() {
        Wholesale wholesale = Wholesale.dao.findById(getParaToInt());
        Wholesale.Status status = Wholesale.Status.valueOf(wholesale.getStatus());
        if (!status.transfer(Wholesale.Status.ONSELL)) {
            setFlash("message", "状态错误！只有处于【草稿】、【已停止】状态的活动才可以发布");
        } else {
            wholesale.setStatus(Wholesale.Status.ONSELL.toString());
            wholesale.update();
        }
        String returnUrl = getPara("returnUrl", "/wholesale");
        redirect(urlDecode(returnUrl));
    }

    public void offsell() {
        Wholesale wholesale = Wholesale.dao.findById(getParaToInt());
        Wholesale.Status status = Wholesale.Status.valueOf(wholesale.getStatus());
        if (!status.transfer(Wholesale.Status.OFFSELL)) {
            setFlash("message", "状态错误！只有处于【已发布】状态的活动才可以停止");
        } else {
            wholesale.setStatus(Wholesale.Status.OFFSELL.toString());
            wholesale.update();
        }
        String returnUrl = getPara("returnUrl", "/wholesale");
        redirect(urlDecode(returnUrl));
    }

    private List<UploadFile> getUploadFiles() {
        if (QiniuKit.me().isInited()) {
            return getFiles(QiniuKit.me().getTmpdir());
        }
        String subDir = wholesaleService.getUploadDir();
        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        return getFiles(PhotoGalleryConstants.me().getUploadPath(), policy);
    }

    private String getUploadFileUrl(UploadFile uploadFile) {
        if (QiniuKit.me().isInited()) {
            return saveToQiniu(uploadFile);
        }
        return UploadedFile.buildUrl(uploadFile, productService.getProductUploadDir());
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

    public void ajaxPcd() {
        renderJson(Pcd.dao.findAllByCache());
    }

    public void wholesaleList() {
        Wholesale wholesale = Wholesale.dao.findById(getParaToInt());
        setAttr("wholesale", wholesale);
        String endTime = getPara("endTime");
        Page<WholesaleMember> wholesaleMembers = WholesaleMember.dao.paginate(
                getParaToInt("pageNumber", 1),
                getParaToInt("pageSize", 50),
                wholesale.getId(),
                getPara("status"),
                getPara("startTime", DateKit.lastMonth("yyyy-MM-dd")) + " 00:00:00",
                StrKit.notBlank(endTime) ? endTime + " 23:59:59" : null);
        setAttr("wholesaleMembers", wholesaleMembers);
        setAttr("statuses", WholesaleMember.Status.values());
        keepPara();
    }

    public void switchEnabled() {
        //目前的状态
        Integer enabled = getParaToInt();
        Ret ret;
        if (enabled.equals(WholesaleService.ENABLED)) {
            ret = wholesaleService.disable();
        } else {
            ret = wholesaleService.enable();
        }
        if (!BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get(BaseService.getMessage(ret)));
        }
        redirect("/wholesale");
    }

}
