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
import com.google.common.collect.Maps;
import com.jfeat.core.BaseService;
import com.jfeat.product.model.CarryMode;
import com.jfeat.product.model.FareTemplate;
import com.jfeat.product.model.InclPostageProviso;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 16/8/26.
 */
public class PostageService extends BaseService {

    @Before(Tx.class)
    public boolean createFareTemplate(FareTemplate fareTemplate, List<CarryMode> carryModes, List<InclPostageProviso> inclPostageProvisoes) {
        fareTemplate.save();
        for (CarryMode carryMode : carryModes) {
            carryMode.setFareId(fareTemplate.getId());
            //default carry mode has empty region
            if (StrKit.notBlank(carryMode.getRegion()) || CarryMode.IS_DEFAULT == carryMode.getIsDefault()) {
                carryMode.save();
                logger.debug("Add CarryMode={}", carryMode.toJson());
            }
        }

        for (InclPostageProviso inclPostageProviso : inclPostageProvisoes) {
            inclPostageProviso.setFareId(fareTemplate.getId());
            inclPostageProviso.save();
            logger.debug("Add InclPostageProviso={}", inclPostageProviso.toJson());
        }

        return true;
    }

    @Before(Tx.class)
    public boolean updateFareTemplate(FareTemplate fareTemplate, List<CarryMode> carryModes, List<InclPostageProviso> inclPostageProvisoes) {
        fareTemplate.update();

        List<CarryMode> originalCarryModes = fareTemplate.getCarryModes();
        List<CarryMode> toDeleteCarryModes = Lists.newArrayList();
        for (CarryMode carryMode : carryModes) {
            if (carryMode.getId() == null) {
                carryMode.setFareId(fareTemplate.getId());
                carryMode.save();
                logger.debug("Add CarryMode={}", carryMode.toJson());
            }
        }
        for (CarryMode originalCarryMode : originalCarryModes) {
            boolean found = false;
            for (CarryMode carryMode : carryModes) {
                if (originalCarryMode.getId().equals(carryMode.getId())) {
                    carryMode.update();
                    found = true;
                    logger.debug("Update CarryMode={}", carryMode.toJson());
                }
            }
            if (!found) {
                toDeleteCarryModes.add(originalCarryMode);
            }
        }

        for (CarryMode carryMode : toDeleteCarryModes) {
            carryMode.delete();
        }

        //Incl Postage Proviso
        List<InclPostageProviso> originalInclPostageProvisoes = fareTemplate.getInclPostageProvisoes();
        List<InclPostageProviso> toDeleteInclPostageProvisoes = Lists.newArrayList();
        for (InclPostageProviso inclPostageProviso : inclPostageProvisoes) {
            if (inclPostageProviso.getId() == null) {
                inclPostageProviso.setFareId(fareTemplate.getId());
                inclPostageProviso.save();
            }
        }
        for (InclPostageProviso originalInclPostageProviso : originalInclPostageProvisoes) {
            boolean found = false;
            for (InclPostageProviso inclPostageProviso : inclPostageProvisoes) {
                if (originalInclPostageProviso.getId().equals(inclPostageProviso.getId())) {
                    inclPostageProviso.update();
                    found = true;
                }
            }
            if (!found) {
                toDeleteInclPostageProvisoes.add(originalInclPostageProviso);
            }
        }
        for (InclPostageProviso inclPostageProviso : toDeleteInclPostageProvisoes) {
            inclPostageProviso.delete();
        }

        return true;
    }

    @Before(Tx.class)
    public FareTemplate duplicateFareTemplate(int fareTemplateId) {
        FareTemplate template = FareTemplate.dao.findById(fareTemplateId);
        if (template == null) {
            return new FareTemplate();
        }
        FareTemplate newTemplate = new FareTemplate();
        newTemplate.setName("Copy_" + template.getName());
        newTemplate.setIsInclPostageByIf(template.getIsInclPostageByIf());
        newTemplate.setIsInclPostage(template.getIsInclPostage());
        newTemplate.setValuationModel(template.getValuationModel());
        newTemplate.setDispatchTime(template.getDispatchTime());
        newTemplate.setShopAddr(template.getShopAddr());
        newTemplate.save();

        List<CarryMode> carryModes = Lists.newArrayList();
        List<InclPostageProviso> inclPostageProvisoes = Lists.newArrayList();

        for (CarryMode carryMode : template.getCarryModes()) {
            CarryMode newCarryMode = new CarryMode();
            newCarryMode.setFareId(newTemplate.getId());
            newCarryMode.setCarryWay(carryMode.getCarryWay());
            newCarryMode.setFirstAmount(carryMode.getFirstAmount());
            newCarryMode.setFirstBulk(carryMode.getFirstBulk());
            newCarryMode.setFirstPiece(carryMode.getFirstPiece());
            newCarryMode.setFirstWeight(carryMode.getFirstWeight());
            newCarryMode.setIsDefault(carryMode.getIsDefault());
            newCarryMode.setRegion(carryMode.getRegion());
            newCarryMode.setSecondAmount(carryMode.getSecondAmount());
            newCarryMode.setSecondBulk(carryMode.getSecondBulk());
            newCarryMode.setSecondPiece(carryMode.getSecondPiece());
            newCarryMode.setSecondWeight(carryMode.getSecondWeight());
            carryModes.add(newCarryMode);
        }

        for (InclPostageProviso inclPostageProviso : template.getInclPostageProvisoes()) {
            InclPostageProviso newInclPostageProviso = new InclPostageProviso();
            newInclPostageProviso.setFareId(newTemplate.getId());
            newInclPostageProviso.setRegion(inclPostageProviso.getRegion());
            newInclPostageProviso.setCarryWay(inclPostageProviso.getCarryWay());
            newInclPostageProviso.setAmount(inclPostageProviso.getAmount());
            newInclPostageProviso.setBulkNo(inclPostageProviso.getBulkNo());
            newInclPostageProviso.setPieceNo(inclPostageProviso.getPieceNo());
            newInclPostageProviso.setWeightNo(inclPostageProviso.getWeightNo());
            newInclPostageProviso.setType(inclPostageProviso.getType());
            inclPostageProvisoes.add(newInclPostageProviso);
        }

        Db.batchSave(carryModes, 10);
        Db.batchSave(inclPostageProvisoes, 10);

        return newTemplate;
    }

    /**
     * 计算产品运费
     * @param productPurchasings
     * @param theRegion String 广东-广州-荔湾
     * @return CarriageCalcResult
     */
    public CarriageCalcResult calculate(List<ProductPurchasing> productPurchasings, String theRegion, CarryMode.CarryWay carryWay) {
        // 根据运费模版把产品分组, 后面用来计算条件包邮时, 同一个运费模版的一起合计条件包邮。
        Map<Integer, List<ProductPurchasing>> map = Maps.newHashMap();
        for (ProductPurchasing productPurchasing : productPurchasings) {
            List<ProductPurchasing> list = map.get(productPurchasing.getFareId());
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(productPurchasing);
            map.put(productPurchasing.getFareId(), list);
        }

        CarriageCalcResult result = new CarriageCalcResult();
        BigDecimal totalCarriage = BigDecimal.ZERO;
        String message = null;
        for (Integer fareId : map.keySet()) {
            FareTemplate fareTemplate = FareTemplate.dao.findById(fareId);
            logger.debug("calculating fare template {}", fareTemplate);

            // 包邮
            if (fareTemplate.getIsInclPostage() == FareTemplate.InclPostage.YES.getValue()) {
                continue;
            }

            // 条件包邮
            CarriageCalcResult carriageCalcResult = inclPostageEvaluate(map.get(fareId), theRegion, fareTemplate, carryWay);
            if (carriageCalcResult.isInclPostage()) {
                continue;
            }
            else {
                result.setMessage(carriageCalcResult.getMessage());
                result.setDelta(carriageCalcResult.getDelta());
            }

            // 计算地区运费
            List<CarryMode> carryModes = fareTemplate.findCarryModes(carryWay);
            boolean regionCarryModeFound = false;
            for (CarryMode carryMode : carryModes) {
                if (carryMode.getIsDefault() != CarryMode.IS_DEFAULT) {
                    if (containRegion(carryMode.getRegion(), theRegion)) {
                        regionCarryModeFound = true;
                        CarriageCalcResult calcResult = calculateCarriage(fareTemplate, map.get(fareId), totalCarriage, carryMode);
                        totalCarriage = calcResult.getResult();
                        message = calcResult.getMessage();
                    }
                }
            }
            if (!regionCarryModeFound) {
                // 使用默认运费
                CarryMode defaultCarryMode = fareTemplate.findDefaultCarryMode(carryWay);
                CarriageCalcResult calcResult = calculateCarriage(fareTemplate, map.get(fareId), totalCarriage, defaultCarryMode);
                totalCarriage = calcResult.getResult();
                message = calcResult.getMessage();
            }
        }

        result.setResult(totalCarriage);
        if (StrKit.isBlank(result.getMessage()) && StrKit.notBlank(message)) {
            result.setMessage(message);
        }

        logger.debug("carriage calculation result is {}", result);

        return result;
    }

    /**
     * 应用某运送方式计算产品的运费
     * @param totalCarriage
     * @param carryMode
     * @return CarriageCalcResult 只用到result message 两个属性
     */
    private CarriageCalcResult calculateCarriage(FareTemplate fareTemplate, List<ProductPurchasing> productPurchasings,
                                                BigDecimal totalCarriage, CarryMode carryMode) {
        CarriageCalcResult carriageCalcResult = new CarriageCalcResult();
        String message = null;
            // 按件
            if (fareTemplate.getValuationModel() == FareTemplate.ValuationModel.PIECE.getValue()) {
                Integer quantity = 0;
                for (ProductPurchasing productPurchasing : productPurchasings) {
                    quantity += productPurchasing.getQuantity();
                }
                //remainpiece / secondpiece + (remainpiece % secondpiece > 0 ? 1 : 0);
                Integer remainPiece = (quantity - carryMode.getFirstPiece()) > 0 ? (quantity - carryMode.getFirstPiece()) : 0;
                Integer count = (remainPiece / carryMode.getSecondPiece()) + (remainPiece % carryMode.getSecondPiece() > 0 ? 1 : 0);
                BigDecimal result = carryMode.getFirstAmount().add(carryMode.getSecondAmount().multiply(new BigDecimal(count)));
                if (result.compareTo(totalCarriage) > 0) {
                    totalCarriage = result;
                }
                if (result.compareTo(carryMode.getFirstAmount()) == 0) {
                    message = carriageMessageFormat(fareTemplate.getMessageFormat(), new BigDecimal(carryMode.getFirstPiece() - quantity));
                } else {
                    Integer value = remainPiece % carryMode.getSecondPiece() == 0 ? carryMode.getSecondPiece() : remainPiece % carryMode.getSecondPiece();
                    message = carriageMessageFormat(fareTemplate.getMessageFormat(), new BigDecimal(carryMode.getSecondPiece() - value));
                }
            }

            // 按重量
            if (fareTemplate.getValuationModel() == FareTemplate.ValuationModel.WEIGHT.getValue()) {
                Integer weight = 0;
                for (ProductPurchasing productPurchasing : productPurchasings) {
                    weight += productPurchasing.getWeight() * productPurchasing.getQuantity();
                }
                Integer remainWeight = (weight - carryMode.getFirstWeight()) > 0 ? (weight - carryMode.getFirstWeight()) : 0;
                Integer count = (remainWeight / carryMode.getSecondWeight()) + (remainWeight % carryMode.getSecondWeight() > 0 ? 1 : 0);
                BigDecimal result = carryMode.getFirstAmount().add(carryMode.getSecondAmount().multiply(new BigDecimal(count)));
                if (result.compareTo(totalCarriage) > 0) {
                    totalCarriage = result;
                }
                if (result.compareTo(carryMode.getFirstAmount()) == 0) {
                    double value = (carryMode.getFirstWeight() - weight) / 1000d;
                    message = carriageMessageFormat(fareTemplate.getMessageFormat(), new BigDecimal(value).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP));
                } else {
                    double value = remainWeight % carryMode.getSecondWeight() == 0 ? carryMode.getSecondWeight() : remainWeight % carryMode.getSecondWeight();
                    value = (carryMode.getSecondWeight() - value) / 1000d;
                    message = carriageMessageFormat(fareTemplate.getMessageFormat(), new BigDecimal(value).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP));
                }
            }

            // 按体积
            if (fareTemplate.getValuationModel() == FareTemplate.ValuationModel.BULK.getValue()) {
                //TODO
            }

            if (StrKit.notBlank(message)) {
                carriageCalcResult.setMessage(message);
            }


        carriageCalcResult.setResult(totalCarriage);
        return carriageCalcResult;
    }

    /**
     * 评估这组产品是否满足条件包邮. delta < 0 表示还差|delta|满足包邮
     * @param productPurchasings
     * @param theRegion
     * @param fareTemplate
     * @param carryWay
     * @return
     */
    private CarriageCalcResult inclPostageEvaluate(List<ProductPurchasing> productPurchasings, String theRegion, FareTemplate fareTemplate, CarryMode.CarryWay carryWay) {
        if (fareTemplate.getIsInclPostageByIf() != FareTemplate.InclPostageByIf.YES.getValue()) {
            return new CarriageCalcResult();
        }

        CarriageCalcResult carriageCalcResult = new CarriageCalcResult();
        List<InclPostageProviso> inclPostageProvisos = fareTemplate.findInclPostageProvisoes(carryWay);
        for (InclPostageProviso inclPostageProviso : inclPostageProvisos) {
            // region 为空, 表示所有地区都满足
            if (StrKit.isBlank(inclPostageProviso.getRegion()) || containRegion(inclPostageProviso.getRegion(), theRegion)) {
                //按件
                if (inclPostageProviso.getType() == InclPostageProviso.InclPostageType.PIECE_NO.getValue()) {
                    Integer piece = 0;
                    for (ProductPurchasing productPurchasing : productPurchasings) {
                        piece += productPurchasing.getQuantity();
                    }
                    Integer delta = piece - inclPostageProviso.getPieceNo();
                    if (delta < 0) {
                        carriageCalcResult.setDelta(BigDecimal.valueOf(delta));
                        carriageCalcResult.setMessage(carriageMessageFormat(fareTemplate.getMessageFormat(), new BigDecimal(Math.abs(carriageCalcResult.getDelta().intValue()))));
                    }
                }
                //按金额
                if (inclPostageProviso.getType() == InclPostageProviso.InclPostageType.AMOUNT.getValue()) {
                    BigDecimal finalPrice = BigDecimal.ZERO;
                    for (ProductPurchasing productPurchasing : productPurchasings) {
                        Integer quantity = productPurchasing.getQuantity();
                        finalPrice = finalPrice.add(productPurchasing.getPrice().multiply(BigDecimal.valueOf(quantity)));
                    }
                    BigDecimal delta = finalPrice.subtract(inclPostageProviso.getAmount());
                    if (delta.compareTo(BigDecimal.ZERO) < 0) {
                        carriageCalcResult.setDelta(delta);
                        carriageCalcResult.setMessage(carriageMessageFormat(fareTemplate.getMessageFormat(),
                                new BigDecimal(Math.abs(carriageCalcResult.getDelta().doubleValue())).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP)));
                    }
                }
                //按重量
                if (inclPostageProviso.getType() == InclPostageProviso.InclPostageType.WEIGHT_NO.getValue()) {
                    Integer weight = 0;
                    for (ProductPurchasing productPurchasing : productPurchasings) {
                        weight += productPurchasing.getWeight() * productPurchasing.getQuantity();
                    }
                    Integer delta = weight - inclPostageProviso.getWeightNo();
                    if (delta < 0) {
                        carriageCalcResult.setDelta(BigDecimal.valueOf(delta));
                        BigDecimal value = carriageCalcResult.getDelta().multiply(new BigDecimal(-0.001)).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                        carriageCalcResult.setMessage(carriageMessageFormat(fareTemplate.getMessageFormat(), value));
                    }
                }
                //按体积
                if (inclPostageProviso.getType() == InclPostageProviso.InclPostageType.BULK_NO.getValue()) {
                    //TODO
                }
            }
        }

        if (carriageCalcResult.getDelta().compareTo(BigDecimal.ZERO) >= 0) {
            carriageCalcResult.setInclPostage(true);
        }

        return carriageCalcResult;
    }

    /**
     * 返回格式化的运费消息
     * @param messageFormat
     * @param value
     * @return
     */
    private String carriageMessageFormat(String messageFormat, BigDecimal value) {
        if (StrKit.notBlank(messageFormat) && value.compareTo(BigDecimal.ZERO) > 0) {
            return String.format(messageFormat, value);
        }
        return null;
    }

    /**
     * source contains target.
     * @param sourceRegion 广东-广州|广西-桂林|广东-深圳
     * @param targetRegion 广东-广州
     * @return
     */
    private boolean containRegion(String sourceRegion, String targetRegion) {
        if (StrKit.isBlank(sourceRegion) || StrKit.isBlank(targetRegion)) {
            return false;
        }
        if (sourceRegion.contains(targetRegion)) {
            return true;
        }
        String province = targetRegion.split("-")[0].trim();
        for (String region : sourceRegion.split("\\|")) {
            if (region.trim().split("-")[0].trim().equals(province)) {
                return true;
            }
        }

        return false;
    }
}
