package com.jfeat.marketing.trial.controller;

import com.jfeat.core.*;
import com.jfeat.flash.Flash;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.marketing.common.model.MarketingConfig;
import com.jfeat.marketing.trial.model.Trial;
import com.jfeat.marketing.trial.model.TrialImage;
import com.jfeat.marketing.trial.model.base.TrialImageBase;
import com.jfeat.marketing.trial.service.TrialService;
import com.jfeat.marketing.wholesale.service.WholesaleService;
import com.jfeat.product.model.ProductCategory;
import com.jfeat.product.util.UploadFileComparator;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrialController extends BaseController {

    private TrialService trialService = new TrialService();

    @Override
    @RequiresPermissions(value = { "marketing.trial.view", "trial.menu" }, logical = Logical.OR)
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        Integer enabledInt = getParaToInt("enabled");
        Integer productCategoryId = getParaToInt("categoryId");
        String barCode = getPara("barCode");
        Short enabled = enabledInt == null ? null : enabledInt.shortValue();
        String name = getPara("name");
        Page<Trial> trialPage = Trial.dao.paginate(pageNumber, pageSize, enabled, name, barCode, productCategoryId);
        Page<Trial> trials = new Page<>(trialService.updateExpired(trialPage.getList()),
                trialPage.getPageNumber(), trialPage.getPageSize(), trialPage.getTotalPage(), trialPage.getTotalRow());
        setAttr("trials", trials);
        setAttr("productCategories", ProductCategory.dao.findAllRecursively());
        setAttr("marketingConfig", MarketingConfig.dao.findFirstByField(MarketingConfig.Fields.TYPE.toString(), MarketingConfig.Type.TRIAL.toString()));

        keepPara();
    }

    @RequiresPermissions(value = { "marketing.trial.view", "trial.menu" }, logical = Logical.OR)
    public void view() {
        Integer id = getParaToInt();
        Trial trial = Trial.dao.findById(id);
        setAttr("trial", trial);
    }

    @Override
    @RequiresPermissions("marketing.trial.edit")
    public void add() {
        setAttr("paymentTypes", Trial.PaymentType.values());
        Integer categoryId = null;
        List<ProductCategory> categories = ProductCategory.dao.findAllTrialRoot();
        if (!categories.isEmpty()) {
            categoryId = categories.get(0).getId();
        }
        setAttr("categoryId", categoryId);
        setAttr("trial", new Trial());
    }

    @Override
    @RequiresPermissions("marketing.trial.edit")
    public void save() {
        List<UploadFile> uploadFiles = getUploadFiles();
        Trial trial = getModel(Trial.class);
        trial.save();
        List<TrialImage> trialImages = uploadFiles.stream()
                .sorted(new UploadFileComparator("cover-"))
                .map(file -> {
                    TrialImage trialImage = new TrialImage();
                    trialImage.setTrialId(trial.getId());
                    Integer id = Integer.parseInt(file.getParameterName().split("cover-")[1]);
                    trialImage.setSortOrder(id);
                    trialImage.setUrl(getUploadFileUrl(file));
                    trialImage.save();
                    return trialImage;
                }).collect(Collectors.toList());
        TrialImage trialImage = trialImages.get(0);
        trial.setCover(trialImage.getUrl());
        trial.update();

        setFlash("message", getRes().get("action.success"));
        redirect("/trial");
    }

    @Override
    @RequiresPermissions("marketing.trial.edit")
    public void edit() {
        setAttr("trial", Trial.dao.findById(getParaToInt()));
        setAttr("paymentTypes", Trial.PaymentType.values());
        Integer categoryId = null;
        List<ProductCategory> categories = ProductCategory.dao.findAllTrialRoot();
        if (!categories.isEmpty()) {
            categoryId = categories.get(0).getId();
        }
        setAttr("categoryId", categoryId);
        keepPara();
    }

    @Override
    @RequiresPermissions("marketing.trial.edit")
    public void update() {
        List<UploadFile> uploadFiles = getUploadFiles();
        Trial trial = getModel(Trial.class);
        List<TrialImage> originalImages = trial.getCovers();
        List<Integer> updatedImages = new ArrayList<>();
        if (uploadFiles != null && !uploadFiles.isEmpty()) {
            Stream<UploadFile> uploadFileStream = uploadFiles.stream().sorted(new UploadFileComparator("cover-"));
            uploadFileStream.forEach(file -> {
                        Integer id = Integer.parseInt(file.getParameterName().split("cover-")[1]);
                        Optional<TrialImage> imageOptional = originalImages.stream().filter(item -> item.getSortOrder().equals(id)).findFirst();
                        if (imageOptional.isPresent()) {
                            TrialImage trialImage = imageOptional.get();
                            trialImage.setUrl(getUploadFileUrl(file));
                            trialImage.update();
                            updatedImages.add(trialImage.getId());
                        }
                        else {
                            TrialImage trialImage = new TrialImage();
                            trialImage.setTrialId(trial.getId());
                            trialImage.setSortOrder(id);
                            trialImage.setUrl(getUploadFileUrl(file));
                            trialImage.save();
                        }
                    });
        }

        Integer[] unchangedCoverIds = getParaValuesToInt("cover-id");
        List<Integer> unchangedImages = unchangedCoverIds == null ? new ArrayList<>() : Arrays.asList(unchangedCoverIds);

        originalImages.stream()
                .filter(trialImage -> !updatedImages.contains(trialImage.getId()) && !unchangedImages.contains(trialImage.getId()))
                .forEach(BaseModel::delete);

        List<TrialImage> trialImages = trial.getCovers();
        trial.setCover(trialImages.get(0).getUrl());
        trial.update();
        setFlash("message", getRes().get("action.success"));
        String returnUrl = getPara("returnUrl", "/trial");
        redirect(urlDecode(returnUrl));
    }

    /**
     * 重新发布。与update相同，不同的是version会在原来基础上加1
     */
    @RequiresPermissions("marketing.trial.edit")
    public void republish() {
        Trial trial = Trial.dao.findById(getParaToInt());
        boolean result = trialService.republish(trial);
        setFlash("message", getRes().get("action.success"));
        String returnUrl = getPara("returnUrl", "/trial");
        redirect(urlDecode(returnUrl));
    }

    /**
     * 启动/停止 试用活动
     */
    @RequiresPermissions("marketing.trial.edit")
    public void switchTrialEnabled() {
        Ret ret = trialService.switchEnabled(getParaToInt());
        setFlash("message", getRes().get(BaseService.getMessage(ret)));
        redirect("/trial");
    }

    public void switchEnabled() {
        //目前的状态
        Integer enabled = getParaToInt();
        Ret ret;
        if (enabled.equals(WholesaleService.ENABLED)) {
            ret = trialService.disable();
        } else {
            ret = trialService.enable();
        }
        if (!BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get(BaseService.getMessage(ret)));
        }
        redirect("/trial");
    }


    @Override
    @RequiresPermissions("marketing.trial.delete")
    public void delete() {
        new Trial().deleteById(getParaToInt());
        String returnUrl = getPara("returnUrl", "/trial");
        redirect(urlDecode(returnUrl));
    }

    private String urlDecode(String url) {
        return StringEscapeUtils.unescapeHtml4(url);
    }

    private List<UploadFile> getUploadFiles() {
        if (QiniuKit.me().isInited()) {
            return getFiles(QiniuKit.me().getTmpdir());
        }
        String subDir = trialService.getUploadDir();
        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        return getFiles(PhotoGalleryConstants.me().getUploadPath(), policy);
    }

    private String getUploadFileUrl(UploadFile uploadFile) {
        if (QiniuKit.me().isInited()) {
            return saveToQiniu(uploadFile);
        }
        return UploadedFile.buildUrl(uploadFile, trialService.getUploadDir());
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


}
