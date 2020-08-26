package com.jfeat.merchant.controller;

import com.jfeat.core.BaseController;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.UploadedFile;
import com.jfeat.identity.model.User;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.merchant.model.*;
import com.jfeat.merchant.service.MerchantApplyService;
import com.jfeat.ui.model.Widget;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.upload.UploadFile;

import java.util.List;

/**
 * Created by kang on 2017/3/22.
 */
public class MerchantApplyController extends BaseController {

    private MerchantApplyService merchantApplyService = Enhancer.enhance(MerchantApplyService.class);

    public void index() {
        User user = getAttr("currentUser");
        UserSettledMerchant userSettledMerchant = UserSettledMerchant.dao.findFirstByField(UserSettledMerchant.Fields.USER_ID.toString(), user.getId());
        if (userSettledMerchant != null) {
            redirect("/");
            return;
        }
        setAttr("settledTerm", SettledTerm.dao.findDefault());
    }

    public void apply() {
        setAttr("allowApply", merchantApplyService.isAllowApply());
        setAttr("settledMerchantTypes", SettledMerchantType.dao.findAll());
    }

    public void save() {
        User user = getAttr("currentUser");
        List<UploadFile> uploadFiles = getUploadFiles();
        SettledMerchant settledMerchant = getModel(SettledMerchant.class);
        for (UploadFile uploadFile : uploadFiles) {
            if (SettledMerchant.Fields.LOGO.toString().equals(uploadFile.getParameterName())) {
                settledMerchant.setLogo(getUploadFileUrl(uploadFile));
            }
            if (SettledMerchant.Fields.BUSINESS_LICENSE_IMAGE.toString().equals(uploadFile.getParameterName())) {
                settledMerchant.setBusinessLicenseImage(getUploadFileUrl(uploadFile));
            }
            if (SettledMerchant.Fields.ID_FRONT.toString().equals(uploadFile.getParameterName())) {
                settledMerchant.setIdFront(getUploadFileUrl(uploadFile));
            }
            if (SettledMerchant.Fields.ID_BACK.toString().equals(uploadFile.getParameterName())) {
                settledMerchant.setIdBack(getUploadFileUrl(uploadFile));
            }
        }
        SettledMerchantIntroduction settledMerchantIntroduction = getModel(SettledMerchantIntroduction.class);
        merchantApplyService.save(settledMerchant, settledMerchantIntroduction, user.getId());
        redirect("/");
    }

    public void widget() {
        User user = getAttr("currentUser");
        UserSettledMerchant userSettledMerchant = UserSettledMerchant.dao.findFirstByField(UserSettledMerchant.Fields.USER_ID.toString(), user.getId());
        SettledMerchant settledMerchant = userSettledMerchant == null ? null : SettledMerchant.dao.findById(userSettledMerchant.getMerchantId());
        setAttr("merchantApplyWidgetDisplayName", Widget.dao.findFirstByField(Widget.Fields.NAME.toString(), "merchant_apply.overview").getDisplayName());
        setAttr("allowApply", merchantApplyService.isAllowApply());

        if (settledMerchant == null) {
            setAttr("tips", "您从未提交过申请");
        } else if (!SettledMerchant.Status.APPROVED.toString().equals(settledMerchant.getStatus())) {
            setAttr("tips", "您已经提交申请，请耐心等待审核通过");
            setAttr("settledMerchant", settledMerchant);
        }
    }

    public void detail() {
        User user = getAttr("currentUser");
        UserSettledMerchant userSettledMerchant = UserSettledMerchant.dao.findFirstByField(UserSettledMerchant.Fields.USER_ID.toString(), user.getId());
        SettledMerchant settledMerchant = SettledMerchant.dao.findById(userSettledMerchant.getMerchantId());
        setAttr("settledMerchant", settledMerchant);
        setAttr("settledMerchantIntroduction", SettledMerchantIntroduction.dao.findFirstByField(SettledMerchantIntroduction.Fields.MERCHANT_ID.toString(), settledMerchant.getId()));
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
        String subDir = merchantApplyService.getUploadDir();
        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        return getFiles(PhotoGalleryConstants.me().getUploadPath(), policy);
    }

    private String getUploadFileUrl(UploadFile uploadFile) {
        if (QiniuKit.me().isInited()) {
            return saveToQiniu(uploadFile);
        }
        return UploadedFile.buildUrl(uploadFile, merchantApplyService.getUploadDir());
    }

}
