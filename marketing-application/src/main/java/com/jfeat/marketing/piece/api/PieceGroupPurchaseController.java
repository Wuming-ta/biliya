package com.jfeat.marketing.piece.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.marketing.piece.api.validator.PieceGroupPurchaseConfigValidator;
import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMaster;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;
import com.jfeat.marketing.piece.service.PieceGroupPurchaseService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductDescription;
import com.jfeat.product.model.ProductImage;
import com.jfeat.product.model.ProductSpecification;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by kang on 2017/4/19.
 */
@ControllerBind(controllerKey = "/rest/piece_group_purchase")
public class PieceGroupPurchaseController extends RestController {
    private PieceGroupPurchaseService pieceGroupPurchaseService = Enhancer.enhance(PieceGroupPurchaseService.class);

    @Before(PieceGroupPurchaseConfigValidator.class)
    @Validation(rules = {
            "pageNumber = number", "pageSize = number", "masterFree = number"
    })
    public void index() {
        User currentUser = getAttr("currentUser");
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        Integer masterFree = getParaToInt("masterFree");
        Page<PieceGroupPurchase> page = PieceGroupPurchase.dao.paginate(pageNumber, pageSize, null, PieceGroupPurchase.Status.ONSELL.toString(), masterFree);
        PieceGroupPurchaseMaster master = pieceGroupPurchaseService.getRandomPromotedMaster(currentUser.getId());
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("pageNumber", page.getPageNumber());
        map.put("pageSize", page.getPageSize());
        map.put("totalPage", page.getTotalPage());
        map.put("totalRow", page.getTotalRow());
        map.put("list", page.getList());
        if (master != null) {
            master.put("piece_group_purchase_master_status", master.getStatus());
            master.remove(PieceGroupPurchaseMaster.Fields.STATUS.toString());
            //随机团长用户信息
            User masterUser = master.getUser();
            master.put("user_name", masterUser.getName());
            master.put("user_avatar", masterUser.getAvatar());
            //随机团长拼团活动信息
            PieceGroupPurchase pieceGroupPurchase = master.getPieceGroupPurchase();
            master.put(PieceGroupPurchase.Fields.MARKETING_NAME.toString(), pieceGroupPurchase.getMarketingName());
            master.put(PieceGroupPurchase.Fields.MARKETING_SHORT_NAME.toString(), pieceGroupPurchase.getMarketingShortName());
            master.put(PieceGroupPurchase.Fields.PRODUCT_ID.toString(), pieceGroupPurchase.getProductId());
            master.put("piece_group_purchase_status", pieceGroupPurchase.getStatus());
            master.put(PieceGroupPurchase.Fields.MIN_PARTICIPATOR_COUNT.toString(), pieceGroupPurchase.getMinParticipatorCount());
            master.put(PieceGroupPurchase.Fields.PRICE.toString(), pieceGroupPurchase.getPrice());
            master.put(PieceGroupPurchase.Fields.SUGGESTED_PRICE.toString(), pieceGroupPurchase.getSuggestedPrice());
            master.put(PieceGroupPurchase.Fields.SALE.toString(), pieceGroupPurchase.getSale());
            master.put(PieceGroupPurchase.Fields.COUPON_USAGE.toString(), pieceGroupPurchase.getCouponUsage());
            master.put(PieceGroupPurchase.Fields.COVER.toString(), pieceGroupPurchase.getCover());
            master.put(PieceGroupPurchase.Fields.DURATION.toString(), pieceGroupPurchase.getDuration());
            master.put(PieceGroupPurchase.Fields.FREE_SHIPPING.toString(), pieceGroupPurchase.getFreeShipping());
            master.put(PieceGroupPurchase.Fields.MASTER_FREE.toString(), pieceGroupPurchase.getMasterFree());
            master.put(PieceGroupPurchase.Fields.DESCRIPTION.toString(), pieceGroupPurchase.getDescription());
        }
        map.put("promoted_master", master);
        renderSuccess(map);
    }

    @Before({PieceGroupPurchaseConfigValidator.class, CurrentUserInterceptor.class})
    public void show() {
        User currentUser = getAttr("currentUser");
        Integer id = getParaToInt();
        if (id == null) {
            renderFailure("id.is.required");
            return;
        }
        PieceGroupPurchase pieceGroupPurchase = PieceGroupPurchase.dao.findById(id);
        if (pieceGroupPurchase == null) {
            renderFailure("pieceGroupPurchase.not.found");
            return;
        }
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put(PieceGroupPurchase.Fields.ID.toString(), pieceGroupPurchase.getId());
        map.put(PieceGroupPurchase.Fields.MARKETING_NAME.toString(), pieceGroupPurchase.getMarketingName());
        map.put(PieceGroupPurchase.Fields.MARKETING_SHORT_NAME.toString(), pieceGroupPurchase.getMarketingShortName());
        map.put(PieceGroupPurchase.Fields.PRODUCT_ID.toString(), pieceGroupPurchase.getProductId());
        map.put(PieceGroupPurchase.Fields.STATUS.toString(), pieceGroupPurchase.getStatus());
        map.put(PieceGroupPurchase.Fields.MIN_PARTICIPATOR_COUNT.toString(), pieceGroupPurchase.getMinParticipatorCount());
        map.put(PieceGroupPurchase.Fields.PRICE.toString(), pieceGroupPurchase.getPrice());
        map.put(PieceGroupPurchase.Fields.SUGGESTED_PRICE.toString(), pieceGroupPurchase.getSuggestedPrice());
        map.put(PieceGroupPurchase.Fields.SALE.toString(), pieceGroupPurchase.getSale());
        map.put(PieceGroupPurchase.Fields.COUPON_USAGE.toString(), pieceGroupPurchase.getCouponUsage());
        map.put(PieceGroupPurchase.Fields.COVER.toString(), pieceGroupPurchase.getCover());
        map.put(PieceGroupPurchase.Fields.DURATION.toString(), pieceGroupPurchase.getDuration());
        map.put(PieceGroupPurchase.Fields.FREE_SHIPPING.toString(), pieceGroupPurchase.getFreeShipping());
        map.put(PieceGroupPurchase.Fields.PAYMENT_TYPE.toString(), pieceGroupPurchase.getPaymentType());
        map.put(PieceGroupPurchase.Fields.MASTER_FREE.toString(), pieceGroupPurchase.getMasterFree());
        map.put(PieceGroupPurchase.Fields.DESCRIPTION.toString(), pieceGroupPurchase.getDescription());

        Product product = pieceGroupPurchase.getProduct();
        List<ProductImage> covers = product.getCovers();
        List<String> coverUrls = Lists.newLinkedList();
        for (ProductImage productImage : covers) {
            coverUrls.add(productImage.getUrl());
        }
        product.put("covers", coverUrls);
        List<ProductSpecification> specifications = product.getProductSpecifications();
        product.put("specifications", specifications);
        ProductDescription productDescription = product.getProductDescription();
        if (productDescription != null) {
            product.put("description", product.getProductDescription().getDescription());
        }
        map.put("product", product);

        List<PieceGroupPurchaseMaster> masters = PieceGroupPurchaseMaster.dao.findByPieceGroupPurchaseIdAndStatus(pieceGroupPurchase.getId(),
                PieceGroupPurchaseMaster.Status.OPENING.toString(),
                PieceGroupPurchaseService.PROMOTED,
                PieceGroupPurchaseMember.Status.PAID.toString());
        List<PieceGroupPurchaseMaster> promotedMasters = pieceGroupPurchaseService.userIdFilter(currentUser.getId(), masters);
        promotedMasters = pieceGroupPurchaseService.paidMembersReachedFilter(promotedMasters);
        for (PieceGroupPurchaseMaster master : promotedMasters) {
            master.put("members_count", master.getMembersCount());
            master.put("paid_members_count", PieceGroupPurchaseMember.dao.findByMasterIdAndStatus(master.getId(),
                    PieceGroupPurchaseMember.Status.PAID.toString())
                    .size());
            User user = master.getUser();
            master.put("user_name", user.getName());
            master.put("user_avatar", user.getAvatar());
        }

        map.put("promoted_masters", promotedMasters);
        renderSuccess(map);
    }


}
