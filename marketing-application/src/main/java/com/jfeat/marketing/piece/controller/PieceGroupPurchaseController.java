package com.jfeat.marketing.piece.controller;

import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.UploadedFile;
import com.jfeat.flash.Flash;
import com.jfeat.kit.DateKit;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.marketing.common.model.MarketingConfig;
import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMaster;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;
import com.jfeat.marketing.piece.model.PieceGroupPurchasePricing;
import com.jfeat.marketing.piece.service.CouponGiveStrategyHolder;
import com.jfeat.marketing.piece.service.PieceGroupPurchaseService;
import com.jfinal.aop.Before;
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
 * Created by kang on 2017/4/19.
 */
public class PieceGroupPurchaseController extends BaseController {

    private PieceGroupPurchaseService pieceGroupPurchaseService = new PieceGroupPurchaseService();

    @RequiresPermissions("marketing.piece.view")
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String marketingName = getPara("marketingName");
        String status = getPara("status");
        setAttr("pieceGroupPurchases", PieceGroupPurchase.dao.paginate(pageNumber, pageSize, marketingName, status, null));
        setAttr("statuses", PieceGroupPurchase.Status.values());
        setAttr("marketingConfig", MarketingConfig.dao.findFirstByField(MarketingConfig.Fields.TYPE.toString(), MarketingConfig.Type.PIECEGROUP.toString()));
        keepPara();
    }

    @RequiresPermissions("marketing.piece.edit")
    public void add() {
        setAttr("paymentTypes", PieceGroupPurchase.PaymentType.values());
        setAttr("strategies", CouponGiveStrategyHolder.me().getServiceMap().keySet());
    }

    private List<UploadFile> getUploadFiles() {
        if (QiniuKit.me().isInited()) {
            return getFiles(QiniuKit.me().getTmpdir());
        }
        String subDir = pieceGroupPurchaseService.getUploadDir();
        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        return getFiles(PhotoGalleryConstants.me().getUploadPath(), policy);
    }

    private String getUploadFileUrl(UploadFile uploadFile) {
        if (QiniuKit.me().isInited()) {
            return saveToQiniu(uploadFile);
        }
        return UploadedFile.buildUrl(uploadFile, pieceGroupPurchaseService.getUploadDir());
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

    //将字符串数组各元素之间用 separator 拼接
    private String join(String[] arr, String separator) {
        if (arr == null || arr.length == 0 || separator == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            stringBuilder.append(separator).append(arr[i]);
        }
        return stringBuilder.toString();
    }

    @RequiresPermissions("marketing.piece.edit")
    public void save() {
        List<UploadFile> uploadFiles = getUploadFiles();
        PieceGroupPurchase pieceGroupPurchase = getModel(PieceGroupPurchase.class);
        pieceGroupPurchase.setDuration(pieceGroupPurchase.getDuration() * 3600);
        List<PieceGroupPurchasePricing> pieceGroupPurchasePricings = getModels(PieceGroupPurchasePricing.class);
        if (uploadFiles != null && uploadFiles.size() > 0) {
            String url = getUploadFileUrl(uploadFiles.get(0));
            if (StrKit.notBlank(url)) {
                pieceGroupPurchase.setCover(url);
            }
        }
        String[] paymentTypes = getParaValues("payment_type");
        if (paymentTypes == null || paymentTypes.length == 0) {
            setFlash("message", "添加失败，必须至少选择一种支付方式！");
            redirect("/piece_group_purchase");
            return;
        }
        pieceGroupPurchase.setPaymentType(join(paymentTypes, "|"));
        Ret ret = pieceGroupPurchaseService.createPieceGroupPurchase(pieceGroupPurchase, pieceGroupPurchasePricings);
        setFlash("message", getRes().get(ret.get(PieceGroupPurchaseService.MESSAGE).toString()));
        redirect("/piece_group_purchase");
    }

    @RequiresPermissions("marketing.piece.edit")
    public void edit() {
        setAttr("pieceGroupPurchase", PieceGroupPurchase.dao.findById(getParaToInt()));
        setAttr("paymentTypes", PieceGroupPurchase.PaymentType.values());
        setAttr("strategies", CouponGiveStrategyHolder.me().getServiceMap().keySet());
        keepPara();
    }

    @RequiresPermissions("marketing.piece.edit")
    public void update() {
        List<UploadFile> uploadFiles = getUploadFiles();
        PieceGroupPurchase pieceGroupPurchase = getModel(PieceGroupPurchase.class);
        pieceGroupPurchase.setDuration(pieceGroupPurchase.getDuration() * 3600);
        List<PieceGroupPurchasePricing> pieceGroupPurchasePricings = getModels(PieceGroupPurchasePricing.class);
        if (uploadFiles != null && uploadFiles.size() > 0) {
            String url = getUploadFileUrl(uploadFiles.get(0));
            if (StrKit.notBlank(url)) {
                pieceGroupPurchase.setCover(url);
            }
        }
        String[] paymentTypes = getParaValues("payment_type");
        if (paymentTypes == null || paymentTypes.length == 0) {
            setFlash("message", "添加失败，必须至少选择一种支付方式！");
            redirect("/piece_group_purchase");
            return;
        }
        pieceGroupPurchase.setPaymentType(join(paymentTypes, "|"));
        Ret ret = pieceGroupPurchaseService.updatePieceGroupPurchase(pieceGroupPurchase, pieceGroupPurchasePricings);
        setFlash("message", getRes().get(ret.get(BaseService.MESSAGE).toString()));
        String returnUrl = getPara("returnUrl", "/piece_group_purchase");
        redirect(urlDecode(returnUrl));
    }

    @RequiresPermissions("marketing.piece.delete")
    public void delete() {
        new PieceGroupPurchase().deleteById(getParaToInt());
        String returnUrl = getPara("returnUrl", "/piece_group_purchase");
        redirect(urlDecode(returnUrl));
    }

    private String urlDecode(String url) {
        return StringEscapeUtils.unescapeHtml4(url);
    }

    public void pieceGroupPurchaseList() {
        PieceGroupPurchase pieceGroupPurchase = PieceGroupPurchase.dao.findById(getParaToInt());
        setAttr("pieceGroupPurchase", pieceGroupPurchase);
        String endTime = getPara("endTime");
        Page<PieceGroupPurchaseMaster> pieceGroupPurchaseMasters = PieceGroupPurchaseMaster.dao.paginate(
                getParaToInt("pageNumber", 1),
                getParaToInt("pageSize", 50),
                pieceGroupPurchase.getId(),
                getPara("status"),
                getPara("startTime", DateKit.lastMonth("yyyy-MM-dd")) + " 00:00:00",
                StrKit.notBlank(endTime) ? endTime + " 23:59:59" : null);
        for (PieceGroupPurchaseMaster pieceGroupPurchaseMaster : pieceGroupPurchaseMasters.getList()) {
            pieceGroupPurchaseMaster.put("paidMembersCount", pieceGroupPurchaseMaster.getMembersCount(PieceGroupPurchaseMember.Status.PAID.toString()));
        }
        setAttr("pieceGroupPurchaseMasters", pieceGroupPurchaseMasters);
        setAttr("statuses", PieceGroupPurchaseMaster.Status.values());
        List<PieceGroupPurchasePricing> pricings = pieceGroupPurchase.getPricings();
        if (pricings.size() > 0) {
            setAttr("maxParticipatorCount", pricings.get(pricings.size() - 1).getParticipatorCount());
        }
        keepPara();
    }

    public void memberList() {
        PieceGroupPurchaseMaster pieceGroupPurchaseMaster = PieceGroupPurchaseMaster.dao.findById(getParaToInt());
        List<PieceGroupPurchasePricing> pricings = pieceGroupPurchaseMaster.getPieceGroupPurchase().getPricings();
        if (pricings.size() > 0) {
            setAttr("maxParticipatorCount", pricings.get(pricings.size() - 1).getParticipatorCount());
        }
        setAttr("pieceGroupPurchaseMaster", pieceGroupPurchaseMaster);
        setAttr("pieceGroupPurchaseMembers", PieceGroupPurchaseMember.dao.paginateByMasterId(getParaToInt("pageNumber", 1), getParaToInt("pageSize", 50), pieceGroupPurchaseMaster.getId()));
    }

    public void onsell() {
        //INIT,OFFSELL,LOCK都可切换为ONSELL状态
        PieceGroupPurchase pieceGroupPurchase = PieceGroupPurchase.dao.findById(getParaToInt());
        PieceGroupPurchase.Status status = PieceGroupPurchase.Status.valueOf(pieceGroupPurchase.getStatus());
        if (!status.transfer(PieceGroupPurchase.Status.ONSELL)) {
            setFlash("message", "状态错误！只有处于【草稿】、【已停止】、【已锁定】状态的活动才可以发布");
        } else {
            pieceGroupPurchase.setStatus(PieceGroupPurchase.Status.ONSELL.toString());
            pieceGroupPurchase.update();
        }
        String returnUrl = getPara("returnUrl", "/piece_group_purchase");
        redirect(urlDecode(returnUrl));
    }

    public void offsell() {
        //ONSELL,LOCK都可切换为OFFSELL状态
        PieceGroupPurchase pieceGroupPurchase = PieceGroupPurchase.dao.findById(getParaToInt());
        PieceGroupPurchase.Status status = PieceGroupPurchase.Status.valueOf(pieceGroupPurchase.getStatus());
        if (!status.transfer(PieceGroupPurchase.Status.OFFSELL)) {
            setFlash("message", "状态错误，只有【已发布】、【锁定】状态的活动才可以停止");
        } else {
            pieceGroupPurchase.setStatus(PieceGroupPurchase.Status.OFFSELL.toString());
            pieceGroupPurchase.update();
        }
        String returnUrl = getPara("returnUrl", "/piece_group_purchase");
        redirect(urlDecode(returnUrl));
    }

    public void lock() {
        //ONSELL可切换为lock状态
        PieceGroupPurchase pieceGroupPurchase = PieceGroupPurchase.dao.findById(getParaToInt());
        PieceGroupPurchase.Status status = PieceGroupPurchase.Status.valueOf(pieceGroupPurchase.getStatus());
        if (!status.transfer(PieceGroupPurchase.Status.LOCK)) {
            setFlash("message", "状态错误，只有【已发布】状态的活动才可以锁定");
        } else {
            pieceGroupPurchase.setStatus(PieceGroupPurchase.Status.LOCK.toString());
            pieceGroupPurchase.update();
        }
        String returnUrl = getPara("returnUrl", "/piece_group_purchase");
        redirect(urlDecode(returnUrl));
    }

    public void switchEnabled() {
        //目前的状态
        Integer enabled = getParaToInt();
        Ret ret;
        if (enabled.equals(PieceGroupPurchaseService.ENABLED)) {
            ret = pieceGroupPurchaseService.disable();
        } else {
            ret = pieceGroupPurchaseService.enable();
        }
        if (!BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get(BaseService.getMessage(ret)));
        }
        redirect("/piece_group_purchase");
    }
}
