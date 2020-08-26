package com.jfeat.marketing.piece.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.core.RestController;
import com.jfeat.marketing.piece.api.validator.PieceGroupPurchaseMasterValidator;
import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMaster;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductDescription;
import com.jfeat.product.model.ProductImage;
import com.jfeat.product.model.ProductSpecification;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;

import java.util.List;
import java.util.Map;

/**
 * Created by kang on 2017/5/27.
 */
@ControllerBind(controllerKey = "/rest/piece_group_purchase_master")
public class PieceGroupPurchaseMasterController extends RestController {

    @Before(PieceGroupPurchaseMasterValidator.class)
    public void show() {
        PieceGroupPurchaseMaster master = getAttr("pieceGroupPurchaseMaster");
        PieceGroupPurchase pieceGroupPurchase = master.getPieceGroupPurchase();
        Map<String, Object> map = Maps.newLinkedHashMap();
        //put the pieceGroupPurchaseMaster's data
        map.put(PieceGroupPurchaseMaster.Fields.ID.toString(), master.getId());
        map.put(PieceGroupPurchaseMaster.Fields.USER_ID.toString(), master.getUserId());
        map.put(PieceGroupPurchaseMaster.Fields.PIECE_GROUP_PURCHASE_ID.toString(), master.getPieceGroupPurchaseId());
        map.put(PieceGroupPurchaseMaster.Fields.START_TIME.toString(), master.getStartTime());
        map.put(PieceGroupPurchaseMaster.Fields.END_TIME.toString(), master.getEndTime());
        map.put(PieceGroupPurchaseMaster.Fields.PROMOTED.toString(), master.getPromoted());
        map.put("piece_group_purchase_master_status", master.getStatus());
        //put the pieceGroupPurchase's data
        map.put(PieceGroupPurchase.Fields.MARKETING_NAME.toString(), pieceGroupPurchase.getMarketingName());
        map.put(PieceGroupPurchase.Fields.MARKETING_SHORT_NAME.toString(), pieceGroupPurchase.getMarketingShortName());
        map.put("piece_group_purchase_status", pieceGroupPurchase.getStatus());
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
        //put the product's data
        Product product = pieceGroupPurchase.getProduct();
        List<ProductImage> covers = product.getCovers();
        List<String> coverUrls = Lists.newLinkedList();
        for (ProductImage productImage : covers) {
            coverUrls.add(productImage.getUrl());
        }
        product.put("covers", coverUrls);
        product.put("specifications", product.getProductSpecifications());
        ProductDescription productDescription = product.getProductDescription();
        if (productDescription != null) {
            product.put("description", productDescription.getDescription());
        }
        map.put("product", product);
        renderSuccess(map);
    }
}

