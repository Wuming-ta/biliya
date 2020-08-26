package com.jfeat.merchant.controller;

import com.jfeat.config.model.Config;
import com.jfeat.core.BaseController;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.UploadedFile;
import com.jfeat.identity.model.Role;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.UserService;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfeat.merchant.model.SettledMerchantIntroduction;
import com.jfeat.merchant.model.SettledMerchantType;
import com.jfeat.merchant.model.UserSettledMerchant;
import com.jfeat.merchant.service.MerchantApplyService;
import com.jfeat.merchant.service.SettledMerchantService;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.upload.UploadFile;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * Created by kang on 2017/3/20.
 */
public class SettledMerchantController extends BaseController {

    private SettledMerchantService settledMerchantService = Enhancer.enhance(SettledMerchantService.class);
    private MerchantApplyService merchantApplyService = Enhancer.enhance(MerchantApplyService.class);
    private UserService userService = Enhancer.enhance(UserService.class);

    @RequiresPermissions("merchant.manage")
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        setAttr("settledMerchants", SettledMerchant.dao.paginate(pageNumber, pageSize));
        Config config = Config.dao.findByKey("wx.host");
        if (config != null) {
            setAttr("wxHost", config.getValue());
        }
        keepPara();
    }

    @RequiresPermissions("merchant.manage")
    public void add() {
        setAttr("settledMerchantTypes", SettledMerchantType.dao.findAll());
    }

    @RequiresPermissions("merchant.manage")
    public void save() {
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
        Integer userId = getParaToInt("user_id");
        merchantApplyService.save(settledMerchant, settledMerchantIntroduction, userId);
        redirect("/settled_merchant");
    }

    @RequiresPermissions("merchant.manage")
    public void detail() {
        Integer settledMerchantId = getParaToInt();
        List<UserSettledMerchant> userSettledMerchants = UserSettledMerchant.dao.findByMerchantId(settledMerchantId);
        if (userSettledMerchants == null || userSettledMerchants.size() == 0) {
            renderError(404);
            return;
        }
        SettledMerchant settledMerchant = userSettledMerchants.get(0).getSettledMerchant();
        User user = userSettledMerchants.get(0).getUser();
        settledMerchant.put("invite_code", user.getInvitationCode());
        settledMerchant.put("username", user.getName());
        settledMerchant.put("userId", user.getId());
        settledMerchant.put("uid", user.getUid());
        settledMerchant.put("roles", user.getRoleList());
        setAttr("settledMerchant", settledMerchant);
        setAttr("settledMerchantIntroduction", SettledMerchantIntroduction.dao.findFirstByField(SettledMerchantIntroduction.Fields.MERCHANT_ID.toString(), settledMerchantId));
        setAttr("roles", Role.dao.findAll());
        Config config = Config.dao.findByKey("wx.host");
        if (config != null) {
            setAttr("wxHost", config.getValue());
        }
    }

    @RequiresPermissions("merchant.manage")
    public void delete() {
        SettledMerchant settledMerchant = getModel(SettledMerchant.class);
        if (settledMerchant == null) {
            renderError(404);
            return;
        }
        settledMerchant.delete();
        redirect("/settled_merchant");
    }

    @RequiresPermissions("merchant.manage")
    public void updateStatus() {
        User currentUser = getAttr("currentUser");
        SettledMerchant settledMerchant = getModel(SettledMerchant.class);
        List<UserSettledMerchant> userSettledMerchants = UserSettledMerchant.dao.findByMerchantId(settledMerchant.getId());
        if (userSettledMerchants == null || userSettledMerchants.size() == 0) {
            renderError(500);
            return;
        }
        settledMerchantService.updateStatus(settledMerchant, getParaToInt("role_id"), getPara("result"), currentUser.getName());

        redirect("/settled_merchant");
    }

    @RequiresPermissions("merchant.manage")
    public void userList() {
        Integer pageNum = getParaToInt("pageNum", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String name = getPara("name");
        setAttr("users", UserSettledMerchant.dao.findNonMerchantUsers(pageNum, pageSize, name));
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
