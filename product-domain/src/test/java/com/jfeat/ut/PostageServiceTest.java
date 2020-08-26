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

package com.jfeat.ut;

import com.google.common.collect.Lists;
import com.jfeat.AbstractTestCase;
import com.jfeat.product.model.*;
import com.jfeat.product.service.CarriageCalcResult;
import com.jfeat.product.service.PostageService;
import com.jfeat.product.service.ProductPurchasing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jackyhuang on 16/8/30.
 */
public class PostageServiceTest extends AbstractTestCase {

    private PostageService service = new PostageService();
    private ProductCategory productCategory;
    private FareTemplate fareTemplatePiece;
    private FareTemplate fareTemplateWeight;

    private FareTemplate setupPiece() {
        FareTemplate fareTemplate = new FareTemplate();
        fareTemplate.setName("TEST PIECE");
        fareTemplate.setDispatchTime("24");
        fareTemplate.setValuationModel(FareTemplate.ValuationModel.PIECE.getValue());
        fareTemplate.setIsInclPostage(FareTemplate.InclPostage.YES.getValue());
        fareTemplate.save();

        CarryMode defaultCarryMode = new CarryMode();
        defaultCarryMode.setFareId(fareTemplate.getId());
        defaultCarryMode.setIsDefault(CarryMode.IS_DEFAULT);
        defaultCarryMode.setCarryWay(CarryMode.CarryWay.EXPRESS.getValue());
        defaultCarryMode.setFirstPiece(2);
        defaultCarryMode.setFirstAmount(new BigDecimal(10));
        defaultCarryMode.setSecondPiece(2);
        defaultCarryMode.setSecondAmount(new BigDecimal(5));
        defaultCarryMode.save();

        CarryMode carryMode = new CarryMode();
        carryMode.setFareId(fareTemplate.getId());
        carryMode.setRegion("GD-GZ|BJ|SH|ZJ");
        carryMode.setIsDefault(CarryMode.NOT_DEFAULT);
        carryMode.setCarryWay(CarryMode.CarryWay.EXPRESS.getValue());
        carryMode.setFirstPiece(2);
        carryMode.setFirstAmount(new BigDecimal(8));
        carryMode.setSecondPiece(8);
        carryMode.setSecondAmount(new BigDecimal(4));
        carryMode.save();

        InclPostageProviso inclPostageProviso = new InclPostageProviso();
        inclPostageProviso.setFareId(fareTemplate.getId());
        inclPostageProviso.setType(InclPostageProviso.InclPostageType.AMOUNT.getValue());
        inclPostageProviso.setCarryWay(CarryMode.CarryWay.EXPRESS.getValue());
        inclPostageProviso.setAmount(new BigDecimal(80));
        inclPostageProviso.save();

        Product product = new Product();
        product.setCategoryId(productCategory.getId());
        product.setName("TEST");
        product.setShortName("TEST");
        product.setStatus(Product.Status.ONSELL.toString());
        product.setPrice(new BigDecimal(200));
        product.setWeight(1200);
        product.setCostPrice(new BigDecimal(100));
        product.setStockBalance(1000);
        product.setPartnerLevelZone(Product.ZONE_ZERO);
        product.setFareId(fareTemplate.getId());
        product.save();

        return fareTemplate;
    }

    private FareTemplate setupWeight() {
        FareTemplate fareTemplate = new FareTemplate();
        fareTemplate.setName("TEST WEIGHT");
        fareTemplate.setDispatchTime("24");
        fareTemplate.setValuationModel(FareTemplate.ValuationModel.WEIGHT.getValue());
        fareTemplate.setIsInclPostage(FareTemplate.InclPostage.YES.getValue());
        fareTemplate.save();

        CarryMode defaultCarryMode = new CarryMode();
        defaultCarryMode.setFareId(fareTemplate.getId());
        defaultCarryMode.setIsDefault(CarryMode.IS_DEFAULT);
        defaultCarryMode.setCarryWay(CarryMode.CarryWay.EXPRESS.getValue());
        defaultCarryMode.setFirstWeight(1000);
        defaultCarryMode.setFirstAmount(new BigDecimal(10));
        defaultCarryMode.setSecondWeight(500);
        defaultCarryMode.setSecondAmount(new BigDecimal(5));
        defaultCarryMode.save();

        CarryMode carryMode = new CarryMode();
        carryMode.setFareId(fareTemplate.getId());
        carryMode.setRegion("GD-GZ|BJ|SH|ZJ");
        carryMode.setIsDefault(CarryMode.NOT_DEFAULT);
        carryMode.setCarryWay(CarryMode.CarryWay.EXPRESS.getValue());
        carryMode.setFirstWeight(1000);
        carryMode.setFirstAmount(new BigDecimal(8));
        carryMode.setSecondWeight(600);
        carryMode.setSecondAmount(new BigDecimal(4));
        carryMode.save();

        InclPostageProviso inclPostageProviso = new InclPostageProviso();
        inclPostageProviso.setFareId(fareTemplate.getId());
        inclPostageProviso.setType(InclPostageProviso.InclPostageType.WEIGHT_NO.getValue());
        inclPostageProviso.setCarryWay(CarryMode.CarryWay.EXPRESS.getValue());
        inclPostageProviso.setAmount(new BigDecimal(80));
        inclPostageProviso.setWeightNo(1600);//达到1600g包邮
        inclPostageProviso.save();

        Product product = new Product();
        product.setCategoryId(productCategory.getId());
        product.setName("TEST");
        product.setShortName("TEST");
        product.setStatus(Product.Status.ONSELL.toString());
        product.setPrice(new BigDecimal(200));
        product.setWeight(1200);
        product.setCostPrice(new BigDecimal(100));
        product.setStockBalance(1000);
        product.setPartnerLevelZone(Product.ZONE_ZERO);
        product.setFareId(fareTemplate.getId());
        product.save();

        return fareTemplate;
    }

    @Before
    public void setup() {
        productCategory = new ProductCategory();
        productCategory.setName("TEST");
        productCategory.save();

        fareTemplatePiece = setupPiece();
        fareTemplateWeight = setupWeight();
    }

    @After
    public void tearDown() {
        productCategory.delete();
        fareTemplatePiece.delete();
        fareTemplateWeight.delete();
    }

    ////////////////////////////////////////////
    @Test
    public void testCalculateInclPostage_Piece() {
        fareTemplatePiece.setIsInclPostage(FareTemplate.InclPostage.YES.getValue());
        fareTemplatePiece.setIsInclPostageByIf(FareTemplate.InclPostageByIf.NO.getValue());
        fareTemplatePiece.update();
        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        ProductPurchasing productPurchasing = new ProductPurchasing(fareTemplatePiece.getId(), 1, new BigDecimal(100), 500, 0);
        productPurchasings.add(productPurchasing);
        CarriageCalcResult result = service.calculate(productPurchasings, "GD-GZ", CarryMode.CarryWay.EXPRESS);
        assertEquals(BigDecimal.ZERO, result.getResult());
    }

    @Test
    public void testCalculateNotInclPostageDefault_Piece() {
        fareTemplatePiece.setIsInclPostage(FareTemplate.InclPostage.NO.getValue());
        fareTemplatePiece.setIsInclPostageByIf(FareTemplate.InclPostageByIf.NO.getValue());
        fareTemplatePiece.setMessageFormat("付同样运费,你还可以拼单%1$s件哦.");
        fareTemplatePiece.update();
        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        ProductPurchasing productPurchasing = new ProductPurchasing(fareTemplatePiece.getId(), 3, new BigDecimal(100), 500, 0);
        productPurchasings.add(productPurchasing);
        CarriageCalcResult result = service.calculate(productPurchasings, "DD", CarryMode.CarryWay.EXPRESS);
        assertEquals(0, result.getResult().compareTo(new BigDecimal(15)));
    }

    @Test
    public void testCalculateNotInclPostage_Piece() {
        fareTemplatePiece.setIsInclPostage(FareTemplate.InclPostage.NO.getValue());
        fareTemplatePiece.setIsInclPostageByIf(FareTemplate.InclPostageByIf.NO.getValue());
        fareTemplatePiece.setMessageFormat("付同样运费,你还可以拼单%1$s件哦.");
        fareTemplatePiece.update();
        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        ProductPurchasing productPurchasing = new ProductPurchasing(fareTemplatePiece.getId(), 5, new BigDecimal(100), 500, 0);
        productPurchasings.add(productPurchasing);
        CarriageCalcResult result = service.calculate(productPurchasings, "BJ", CarryMode.CarryWay.EXPRESS);
        assertEquals(0, result.getResult().compareTo(new BigDecimal(12)));
        result = service.calculate(productPurchasings, "XX-YY", CarryMode.CarryWay.EXPRESS);
        assertEquals(0, result.getResult().compareTo(new BigDecimal(20)));
    }

    @Test
    public void testCalculateInclPostageByIfResultYes_Piece() {
        fareTemplatePiece.setIsInclPostage(FareTemplate.InclPostage.NO.getValue());
        fareTemplatePiece.setIsInclPostageByIf(FareTemplate.InclPostageByIf.YES.getValue());
        fareTemplatePiece.setMessageFormat("还差%1$s件就可以包邮了.");
        fareTemplatePiece.update();
        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        ProductPurchasing productPurchasing = new ProductPurchasing(fareTemplatePiece.getId(), 3, new BigDecimal(50), 500, 0);
        productPurchasings.add(productPurchasing);
        CarriageCalcResult result = service.calculate(productPurchasings, "BJ", CarryMode.CarryWay.EXPRESS);
        assertEquals(0, result.getResult().compareTo(new BigDecimal(0)));
    }

    @Test
    public void testCalculateInclPostageByIfResultNot_Piece() {
        fareTemplatePiece.setIsInclPostage(FareTemplate.InclPostage.NO.getValue());
        fareTemplatePiece.setIsInclPostageByIf(FareTemplate.InclPostageByIf.YES.getValue());
        fareTemplatePiece.setMessageFormat("还差%1$s元就可以包邮了.");
        fareTemplatePiece.update();
        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        ProductPurchasing productPurchasing = new ProductPurchasing(fareTemplatePiece.getId(), 1, new BigDecimal(50), 500, 0);
        productPurchasings.add(productPurchasing);
        CarriageCalcResult result = service.calculate(productPurchasings, "BJ", CarryMode.CarryWay.EXPRESS);
        assertEquals(0, result.getResult().compareTo(new BigDecimal(8)));
        assertEquals(0, new BigDecimal(-30).compareTo(result.getDelta())); //还差30满足包邮
    }


    ///////////////////////////////////////////////////
    @Test
    public void testCalculateInclPostage_Weight() {
        fareTemplateWeight.setIsInclPostage(FareTemplate.InclPostage.YES.getValue());
        fareTemplateWeight.setIsInclPostageByIf(FareTemplate.InclPostageByIf.NO.getValue());
        fareTemplateWeight.update();
        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        ProductPurchasing productPurchasing = new ProductPurchasing(fareTemplatePiece.getId(), 1, new BigDecimal(100), 500, 0);
        productPurchasings.add(productPurchasing);
        CarriageCalcResult result = service.calculate(productPurchasings, "GD-GZ", CarryMode.CarryWay.EXPRESS);
        assertEquals(BigDecimal.ZERO, result.getResult());
    }

    @Test
    public void testCalculateNotInclPostageDefault_Weight() {
        fareTemplateWeight.setIsInclPostage(FareTemplate.InclPostage.NO.getValue());
        fareTemplateWeight.setIsInclPostageByIf(FareTemplate.InclPostageByIf.NO.getValue());
        fareTemplateWeight.setMessageFormat("付同样的运费,还可以拼单%1$sKG哦.");
        fareTemplateWeight.update();
        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        ProductPurchasing productPurchasing = new ProductPurchasing(fareTemplateWeight.getId(), 3, new BigDecimal(100), 400, 0);
        productPurchasings.add(productPurchasing);
        CarriageCalcResult result = service.calculate(productPurchasings, "DD", CarryMode.CarryWay.EXPRESS);
        assertEquals(0, result.getResult().compareTo(new BigDecimal(15)));
    }


    @Test
    public void testCalculateNotInclPostage_Weight() {
        fareTemplateWeight.setIsInclPostage(FareTemplate.InclPostage.NO.getValue());
        fareTemplateWeight.setIsInclPostageByIf(FareTemplate.InclPostageByIf.NO.getValue());
        fareTemplateWeight.setMessageFormat("付同样的运费,还可以拼单%1$sKG哦.");
        fareTemplateWeight.update();
        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        ProductPurchasing productPurchasing = new ProductPurchasing(fareTemplateWeight.getId(), 6, new BigDecimal(100), 400, 0);
        productPurchasings.add(productPurchasing);
        CarriageCalcResult result = service.calculate(productPurchasings, "BJ", CarryMode.CarryWay.EXPRESS);
        assertEquals(0, result.getResult().compareTo(new BigDecimal(20)));
        result = service.calculate(productPurchasings, "XX-YY", CarryMode.CarryWay.EXPRESS);
        assertEquals(0, result.getResult().compareTo(new BigDecimal(25)));
    }

    @Test
    public void testCalculateInclPostageByIfResultYes_Weight() {
        fareTemplateWeight.setIsInclPostage(FareTemplate.InclPostage.NO.getValue());
        fareTemplateWeight.setIsInclPostageByIf(FareTemplate.InclPostageByIf.YES.getValue());
        fareTemplateWeight.setMessageFormat("还差%1$sKG就可以包邮了");
        fareTemplateWeight.update();
        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        ProductPurchasing productPurchasing = new ProductPurchasing(fareTemplateWeight.getId(), 4, new BigDecimal(50), 500, 0);
        productPurchasings.add(productPurchasing);
        CarriageCalcResult result = service.calculate(productPurchasings, "BJ", CarryMode.CarryWay.EXPRESS);
        assertEquals(0, result.getResult().compareTo(new BigDecimal(0)));
    }

    @Test
    public void testCalculateInclPostageByIfResultNot_Weight() {
        fareTemplateWeight.setIsInclPostage(FareTemplate.InclPostage.NO.getValue());
        fareTemplateWeight.setIsInclPostageByIf(FareTemplate.InclPostageByIf.YES.getValue());
        fareTemplateWeight.setMessageFormat("还差%1$sKG就可以包邮了");
        fareTemplateWeight.update();
        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        ProductPurchasing productPurchasing = new ProductPurchasing(fareTemplateWeight.getId(), 1, new BigDecimal(50), 500, 0);
        productPurchasings.add(productPurchasing);
        CarriageCalcResult result = service.calculate(productPurchasings, "BJ", CarryMode.CarryWay.EXPRESS);
        assertEquals(0, result.getResult().compareTo(new BigDecimal(8)));
        assertEquals(0, new BigDecimal(-1100).compareTo(result.getDelta())); //还差1100g满足包邮
    }
}
