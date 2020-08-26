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

package com.jfeat.product.service;

import com.google.common.collect.Lists;
import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.core.UploadedFile;
import com.jfeat.ext.plugin.BasePlugin;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.WmsPlugin;
import com.jfeat.ext.plugin.wms.WmsApi;
import com.jfeat.identity.model.User;
import com.jfeat.product.exception.StockBalanceException;
import com.jfeat.product.model.*;
import com.jfeat.product.model.base.ProductSettlementProportionBase;
import com.jfeat.product.model.param.ProductParam;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jacky on 3/9/16.
 */
public class ProductService extends BaseService {

    private static String MALL_AUTO_OFFSELL = "mall.auto_offsell";

    private static Logger logger = LoggerFactory.getLogger(ProductService.class);

    private String productUploadDir = "p";

    public String getProductUploadDir() {
        return productUploadDir;
    }

    public void setProductUploadDir(String productUploadDir) {
        this.productUploadDir = productUploadDir;
    }

    public List<ProductCategory> getProductCategories() {
        return ProductCategory.dao.findAllRecursively();
    }

    public Ret deleteProduct(Integer id) {
        Product product = Product.dao.findById(id);
        if (product == null) {
            return failure("invalid.product");
        }
        Product.Status status = Product.Status.valueOf(product.getStatus());
        if (status == Product.Status.ONSELL) {
            return failure("product.delete.fail");
        }
//        for (ProductImage productImage : product.getImages()) {
//            UploadedFile.remove(productImage.getUrl());
//        }
//        for (ProductImage productImage : product.getCovers()) {
//            UploadedFile.remove(productImage.getUrl());
//        }
        product.delete();
        return success("product.delete.success");
    }

    @Before(Tx.class)
    public Ret createProduct(Product product, List<String> covers, List<String> videos, String description, List<ProductSpecification> specifications) {
        product.setStatus(Product.Status.DRAFT.toString());
        product.save();

        for (int i = 0; i < covers.size(); i++) {
            String url = covers.get(i);
            product.addCover(url, i + 1);
            if (i == 0) {
                product.setCover(url);
                product.update();
            }
        }

        if (videos != null && !videos.isEmpty()) {
            for (int i = 0; i < covers.size(); i++) {
                String url = videos.get(i);
                product.addVideo(url, i + 1);
            }
        }

        product.updateDescription(description);

        if (specifications == null) {
            specifications = new ArrayList<>();
        }
        for (ProductSpecification specification : specifications) {
            specification.setProductId(product.getId());
            if (specification.getCostPrice() == null) {
                specification.setCostPrice(product.getCostPrice());
            }
            if (specification.getSuggestedPrice() == null) {
                specification.setSuggestedPrice(product.getSuggestedPrice());
            }
            if (specification.getPrice() == null) {
                specification.setPrice(product.getPrice());
            }
        }
        Db.batchSave(specifications, 20);

        return success("product.create.success");
    }

    @Before(Tx.class)
    public Ret updateProduct(Product product,
                             List<String> newCovers,
                             Map<Integer, String> updatedCovers,
                             Integer[] unchangedCoverIds,
                             List<String> newVideos,
                             Map<Integer, String> updatedVideos,
                             Integer[] unchangedVideoIds,
                             String description,
                             List<ProductSpecification> specifications) {

        matainProductImages(ProductImage.TYPE_VIDEO, product.getId(), updatedVideos, unchangedVideoIds);
        Integer sortOrder = ProductImage.dao.queryMaxSortOrder(product.getId(), ProductImage.TYPE_VIDEO);
        for (int i = 0; i < newVideos.size(); i++) {
            String url = newVideos.get(i);
            product.addVideo(url, ++sortOrder);
        }

        matainProductImages(ProductImage.TYPE_COVER, product.getId(), updatedCovers, unchangedCoverIds);
        sortOrder = ProductImage.dao.queryMaxSortOrder(product.getId(), ProductImage.TYPE_COVER);
        for (int i = 0; i < newCovers.size(); i++) {
            String url = newCovers.get(i);
            product.addCover(url, ++sortOrder);
        }

        boolean coverUpdated = false;
        Product originalProduct = Product.dao.findById(product.getId());
        ProductImage firstCover = ProductImage.dao.findFirst(product.getId(), ProductImage.TYPE_COVER);
        if (firstCover != null && StrKit.notBlank(firstCover.getUrl()) && !firstCover.getUrl().equals(originalProduct.getCover())) {
            product.setCover(firstCover.getUrl());
            coverUpdated = true;
        }

        if (description != null) {
            product.updateDescription(description);
        }

        product.update();

        // updating specification
        List<ProductSpecification> originalSpecifications = product.getProductSpecifications();
        List<ProductSpecification> toAddSpecifications = Lists.newArrayList();
        List<ProductSpecification> toUpdateSpecifications = Lists.newArrayList();
        List<ProductSpecification> toRemoveSpecifications = Lists.newArrayList();
        List<ProductSpecification> toNotifyPriceUpdatedSpecifications = Lists.newArrayList();

        if (specifications == null) {
            specifications = Lists.newArrayList();
        }
        for (ProductSpecification specification : specifications) {
            if (specification.getId() == null) {
                specification.setProductId(product.getId());
                toAddSpecifications.add(specification);
            } else {
                for (ProductSpecification originalSpecification : originalSpecifications) {
                    if (specification.getId().equals(originalSpecification.getId())) {
                        toUpdateSpecifications.add(specification);
                    }
                    if (specification.getId().equals(originalSpecification.getId())
                            && specification.getPrice().compareTo(originalSpecification.getPrice()) != 0) {
                        toNotifyPriceUpdatedSpecifications.add(specification);
                    }
                }
            }
        }

        for (ProductSpecification originalSpecification : originalSpecifications) {
            boolean found = false;
            for (ProductSpecification specification : specifications) {
                if (specification.getId() != null && specification.getId().equals(originalSpecification.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                toRemoveSpecifications.add(originalSpecification);
            }
        }

        Db.batchSave(toAddSpecifications, 20);
        Db.batchUpdate(toUpdateSpecifications, 20);
        for (ProductSpecification toRemoveSpecification : toRemoveSpecifications) {
            toRemoveSpecification.delete();
        }

        if (toUpdateSpecifications.isEmpty() && originalProduct.getPrice().compareTo(product.getPrice()) != 0) {
            product.priceUpdatedNotify(null);
        }
        if (!toNotifyPriceUpdatedSpecifications.isEmpty()) {
            product.priceUpdatedNotify(toNotifyPriceUpdatedSpecifications);
        }

        if (coverUpdated) {
            product.coverUpdatedNotify();
        }

        return success("product.update.success");
    }

    private void matainProductImages(int type, int productId,
                                     Map<Integer, String> updatedCovers,
                                     Integer[] unchangedCoverIds) {
        List<ProductImage> originalCovers = ProductImage.dao.findByProductIdAndType(productId, type);
        List<ProductImage> toRemovedCovers = new LinkedList<>();
        for (ProductImage productImage : originalCovers) {
            boolean found = false;
            if (unchangedCoverIds != null) {
                for (Integer coverId : unchangedCoverIds) {
                    if (productImage.getId().equals(coverId)) {
                        found = true;
                        break;
                    }
                }
            }
            if (updatedCovers != null) {
                for (Integer coverId : updatedCovers.keySet()) {
                    if (productImage.getId().equals(coverId)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                toRemovedCovers.add(productImage);
            }

            for (Integer coverId : updatedCovers.keySet()) {
                if (productImage.getId().equals(coverId)) {
                    UploadedFile.remove(productImage.getUrl());
                    String url = updatedCovers.get(coverId);
                    productImage.setUrl(url);
                    productImage.update();
                    logger.debug("cover updated: {}", productImage);
                    break;
                }
            }
        }
        logger.debug("toRemovedCovers: " + toRemovedCovers);
        for (ProductImage productImage : toRemovedCovers) {
            productImage.delete();
        }
    }

    public Ret deleteProductCategory(Integer id) {
        ProductCategory productCategory = ProductCategory.dao.findById(id);
        if (productCategory == null) {
            return failure("product_category.invalid.product_category");
        }
        if (productCategory.hasChildren() || productCategory.hasProduct()) {
            return failure("product_category.delete.has.children");
        }

        UploadedFile.remove(productCategory.getCover());
        productCategory.delete();
        return success("product_category.delete.success");
    }

    public void increaseProductViewCount(Integer productId) {
        Product.increaseViewCount(productId);
    }

    /**
     * 产品售出，增加销量，扣库存
     * @param productIds
     * @param specificationIds
     * @param quantities
     * @throws StockBalanceException
     */
    @Before(Tx.class)
    public void increaseProductSales(Long userId, String account,
                                     String orderNumber,
                                     List<Integer> productIds, List<Integer> specificationIds, List<Integer> quantities,
                                     String note, Long warehouseId
    ) throws StockBalanceException {
        BasePlugin wmsPlugin = ExtPluginHolder.me().get(WmsPlugin.class);
        if(!wmsPlugin.isEnabled()) {
            Product.increaseSales(productIds, specificationIds, quantities);
            offSellProduct(productIds);
        } else {
            logger.debug("wms plugin is enabled. reduce WMS stock balance.");
            List<Long> skuIds = new ArrayList<>();
            List<Integer> skuQuantities = new ArrayList<>();
            List<BigDecimal> skuPrices = new ArrayList<>();
            List<Integer> virtualProductIds = new ArrayList<>();
            List<Integer> virtualSpecificationIds = new ArrayList<>();
            List<Integer> virtualQuantities = new ArrayList<>();
            for (int i = 0; i < productIds.size(); i++) {
                Long skuId;
                String skuName;
                BigDecimal skuPrice;
                Integer quantity = quantities.get(i);
                Product product = Product.dao.findById(productIds.get(i));
                if (product.getIsVirtual() == Product.Virtual.YES.getValue()) {
                    logger.debug("product {} is virtual product. ", product.getName());
                    virtualProductIds.add(productIds.get(i));
                    virtualSpecificationIds.add(specificationIds.get(i));
                    virtualQuantities.add(quantities.get(i));
                }
                else {
                    if (specificationIds.get(i) != null) {
                        ProductSpecification productSpecification = ProductSpecification.dao.findById(specificationIds.get(i));
                        skuId = Long.parseLong(productSpecification.getSkuId());
                        skuName = productSpecification.getSkuName();
                        skuPrice = productSpecification.getPrice();
                    } else {
                        skuId = Long.parseLong(product.getSkuId());
                        skuName = product.getSkuName();
                        skuPrice = product.getPrice();
                    }
                    Product.increaseSale(productIds.get(i), quantities.get(i));
                    skuIds.add(skuId);
                    skuQuantities.add(quantities.get(i));
                    skuPrices.add(skuPrice);
                    logger.debug("reducing skuname = {}, skuid = {}, quantity = {}, price = {}", skuName, skuId, quantity, skuPrice);
                }
            }

            if (!virtualProductIds.isEmpty()) {
                logger.debug("increasing sales and reducing stock balance for virtual product. virtualProductIds = {}, virtualSpecIds = {}, virtualQuantities = {}",
                        virtualProductIds, virtualSpecificationIds, virtualQuantities);
                Product.increaseSales(virtualProductIds, virtualSpecificationIds, virtualQuantities);
            }

            if (!skuIds.isEmpty()) {
                logger.debug("decreasing wms stock balance. orderNumber = {}, skuIds = {}, quantities = {}, warehouseId = {}",
                        orderNumber, skuIds, skuQuantities, warehouseId);

                User user = User.dao.findById(userId);
                WmsApi wmsApi = new WmsApi();
                wmsApi.decreaseStockBalance(userId, account,
                        user.getName(),
                        orderNumber,
                        skuIds.toArray(new Long[0]),
                        skuQuantities.toArray(new Integer[0]),
                        skuPrices.toArray(new BigDecimal[0]),
                        note, warehouseId).getData();
            }
        }
    }

    /**
     * 产品退货，减销量，回退库存
     * @param productIds
     * @param specificationIds
     * @param quantities
     * @throws StockBalanceException
     */
    @Before(Tx.class)
    public void decreaseProductSales(Long userId, String account,
                                     String orderNumber,
                                     List<Integer> productIds, List<Integer> specificationIds, List<Integer> quantities,
                                     String note, Long warehouseId
    ) throws StockBalanceException {
        BasePlugin wmsPlugin = ExtPluginHolder.me().get(WmsPlugin.class);
        if (!wmsPlugin.isEnabled()) {
            Product.decreaseSales(productIds, specificationIds, quantities);
        } else {
            logger.debug("wms plugin is enabled. increase WMS stock balance.");
            List<Long> skuIds = new ArrayList<>();
            List<Integer> skuQuantities = new ArrayList<>();
            List<Integer> virtualProductIds = new ArrayList<>();
            List<Integer> virtualSpecificationIds = new ArrayList<>();
            List<Integer> virtualQuantities = new ArrayList<>();
            for (int i = 0; i < productIds.size(); i++) {
                Long skuId;
                String skuName;
                Integer quantity = quantities.get(i);
                Product product = Product.dao.findById(productIds.get(i));
                if (product.getIsVirtual() == Product.Virtual.YES.getValue()) {
                    logger.debug("product {} is virtual product. ", product.getName());
                    virtualProductIds.add(productIds.get(i));
                    virtualSpecificationIds.add(specificationIds.get(i));
                    virtualQuantities.add(quantities.get(i));
                }
                else {
                    if (specificationIds.get(i) != null) {
                        ProductSpecification productSpecification = ProductSpecification.dao.findById(specificationIds.get(i));
                        skuId = Long.parseLong(productSpecification.getSkuId());
                        skuName = productSpecification.getSkuName();
                    } else {
                        skuId = Long.parseLong(product.getSkuId());
                        skuName = product.getSkuName();
                    }

                    skuIds.add(skuId);
                    skuQuantities.add(quantities.get(i));
                    Product.decreaseSale(productIds.get(i), quantities.get(i));

                    logger.debug("increasing skuname = {}, skuid = {}, quantity = {}", skuName, skuId, quantity);
                }
            }

            if (!virtualProductIds.isEmpty()) {
                logger.debug("decreasing sales and reducing stock balance for virtual product. virtualProductIds = {}, virtualSpecIds = {}, virtualQuantities = {}",
                        virtualProductIds, virtualSpecificationIds, virtualQuantities);
                Product.decreaseSales(virtualProductIds, virtualSpecificationIds, virtualQuantities);
            }

            if (!skuIds.isEmpty()) {
                User user = User.dao.findById(userId);
                WmsApi wmsApi = new WmsApi();
                wmsApi.increaseStockBalance(userId, account, user.getName(), orderNumber,
                        skuIds.toArray(new Long[0]), skuQuantities.toArray(new Integer[0]), note, warehouseId).getData();
            }
        }
    }

    /**
     * 更新搜索关键字
     *
     * @param name
     */
    public void updateHitWord(String name) {
        try {
            ProductHitWord hitWord = ProductHitWord.dao.findByName(name);
            if (hitWord != null) {
                hitWord.setHit(hitWord.getHit() + 1);
                hitWord.update();
            } else {
                hitWord = new ProductHitWord();
                hitWord.setName(name);
                hitWord.save();
            }
        } catch (Exception ex) {
            logger.error("update hit word error. {}", ex.getMessage());
        }
    }

    public void offSellProduct(List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }
        // 若未开启产品库存检查功能
        Config config = Config.dao.findByKey(MALL_AUTO_OFFSELL);
        if (config == null || config.getValueToBoolean() == null || !config.getValueToBoolean()) {
            logger.debug("库存检查功能状态：未开启");
            return;
        }

        //找出库存为<=0的产品，得到这些产品的id集合
        List<Integer> zeroStockBalanceProductIdList = new ArrayList<>();
        for (Integer productId : productIds) {
            Product product = Product.dao.findById(productId);
            if (product != null && product.getStockBalance() <= 0) {
                zeroStockBalanceProductIdList.add(productId);
            }
        }

        //把库存<=0的产品的状态设置为OFFSELL
        Product.dao.offSellProduct(zeroStockBalanceProductIdList);
    }

    /**
     * 更新产品的分成配置。
     * @param settlementProportions
     * @return
     */
    @Before(Tx.class)
    public Ret updateSettlementProportions(List<ProductSettlementProportion> settlementProportions) {
        Integer productId = settlementProportions.stream().findFirst().get().getProductId();
        ProductSettlementProportion productSettlementProportion = new ProductSettlementProportion();
        productSettlementProportion.deleteByField(ProductSettlementProportion.Fields.PRODUCT_ID.toString(), productId);
        Db.batchSave(settlementProportions, 10);
        return success();
    }

    /**
     * 返回产品分成的配置。
     * @return
     */
    public List<ProductSettlementProportion> getProductSettlementProportions() {
        List<ProductSettlementProportion> productSettlementProportions = new ArrayList<>();
        String[][] defaultSettlementProportionData = ProductSettlementProportion.defaultSettlementProportionData;
        for (int i = 0; i < defaultSettlementProportionData.length; i++) {
            ProductSettlementProportion productSettlementProportion = new ProductSettlementProportion();
            productSettlementProportion.setType(defaultSettlementProportionData[i][0]);
            productSettlementProportion.setName(defaultSettlementProportionData[i][1]);
            productSettlementProportion.setLevel(Integer.parseInt(defaultSettlementProportionData[i][2]));
            ProductSettlementProportion.Proportion proportion = new ProductSettlementProportion.Proportion();
            proportion.setFixedvalue(ProductSettlementProportion.Proportion.FIXEDVALUE.equals(defaultSettlementProportionData[i][3]));
            proportion.setPercentage(ProductSettlementProportion.Proportion.PERCENTAGE.equals(defaultSettlementProportionData[i][3]));
            productSettlementProportion.setProportion(proportion.toString());
            productSettlementProportions.add(productSettlementProportion);
        }
        return productSettlementProportions;
    }
}
