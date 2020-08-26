package com.jfeat.partner.service;

import com.jfeat.core.BaseService;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.*;
import com.jfeat.partner.model.param.CopartnerParam;
import com.jfinal.aop.Before;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.jfeat.partner.model.CopartnerSettlement.UN_TRANSFERRED;

/**
 * 合伙人
 * @author jackyhuang
 * @date 2018/8/22
 */
public class CopartnerService extends BaseService {

    public boolean isApplying(Integer userId) {
        return Apply.dao.findByUserIdTypeStatus(userId, Apply.Type.COPARTNER.toString(), Apply.Status.INIT.toString()) != null;
    }

    public Ret apply(Integer sellerId, String name, String phone, String address) {
        Map<String, String> props = new HashMap<>();
        props.put("name", name);
        props.put("phone", phone);
        props.put("address", address);
        Seller seller = Seller.dao.findById(sellerId);
        Apply apply = new Apply();
        apply.setStatus(Apply.Status.INIT.toString());
        apply.setUserId(seller.getUserId());
        apply.setType(Apply.Type.COPARTNER.toString());
        apply.setApplyDate(new Date());
        apply.setProperties(JsonKit.toJson(props));
        if (apply.save()) {
            return success();
        }
        return failure();
    }

    @Before(Tx.class)
    public Ret agree(Integer id) {
        Apply apply = Apply.dao.findById(id);
        String status = apply.getStatus();
        String type = apply.getType();
        if (status.equals(Apply.Status.INIT.toString()) && type.equals(Apply.Type.COPARTNER.toString())) {
            Seller seller = Seller.dao.findByUserId(apply.getUserId());
            Copartner copartner = Copartner.dao.findBySellerId(seller.getId());
            if (copartner == null) {
                copartner = new Copartner();
                copartner.setStatus(Copartner.Status.NORMAL.toString());
                copartner.setCreateTime(new Date());
                copartner.setSellerId(seller.getId());
                copartner.save();
            }
            apply.setStatus(Apply.Status.APPROVE.toString());
            apply.setApproveDate(new Date());
            apply.update();
            return success("partner.copartner.approve.success");
        }
        return failure("partner.copartner.approve.failure");
    }

    public Ret reject(Integer id) {
        Apply apply = Apply.dao.findById(id);
        String status = apply.getStatus();
        String type = apply.getType();
        if (status.equals(Apply.Status.INIT.toString()) && type.equals(Apply.Type.COPARTNER.toString())) {
            apply.setRejectDate(new Date());
            apply.setStatus(Apply.Status.REJECT.toString());
            apply.update();
            return success("partner.copartner.reject.success");
        }
        return failure("partner.copartner.reject.failure");
    }

    public boolean isCopartner(Integer sellerId) {
        Copartner copartner = Copartner.dao.findBySellerId(sellerId);
        return copartner != null;
    }

    /**
     * 为合伙人添加团队成员
     * @param parentSellerId
     * @param childSellerId
     * @return
     */
    public Ret addTeamMember(Integer parentSellerId, Integer childSellerId) {
        if (Copartner.dao.findBySellerId(childSellerId) != null) {
            return failure("child.is.a.copartner");
        }

        Copartner copartner = Copartner.dao.findBySellerId(parentSellerId);
        if (copartner == null) {
            copartner = Copartner.dao.findByChildId(parentSellerId);
            if (copartner == null) {
                return failure("copartner.not.found");
            }
        }

        if (copartner.isChild(childSellerId)) {
            return failure("already.child");
        }
        if (copartner.addChildren(childSellerId)) {
            return success("add.child.success");
        }
        return failure("add.child.failure");
    }

    /**
     * 返回合伙人分成比例
     * @return
     */
    public BigDecimal getSettlementProportion() {
        SettlementProportion settlementProportion = SettlementProportion.dao.findByCopartner();
        return BigDecimal.valueOf(settlementProportion.getProportionObject().getValue());
    }

    /**
     * 返回该合伙人的提出情况
     *
     * @param copartner
     * @param month
     * @param settlementProportionValue
     * @return
     *      * {
     *      *     "monthly_amount": 1000,
     *      *     "monthly_settlement_amount": 100,
     *      *     "total_amount": 1000,
     *      *     "total_settlement_amount": 100,
     *      *     "settlement_proportion": 10,
     *      *     "children": [
     *      *          {
     *      *              "use_name": "abc",
     *      *              "monthly_amount": 400,
     *      *              "monthly_settlement_amount": 40
     *      *          },
     *      *          {
     *      *              "use_name": "xyz",
     *      *              "monthly_amount": 600,
     *      *              "monthly_settlement_amount": 60
     *      *          }
     *      *     ]
     *      * }
     */
    public Record querySettlement(Copartner copartner, String month, BigDecimal settlementProportionValue) {
        List<PhysicalPurchaseSummary> summaries = new ArrayList<>();
        List<Seller> sellers = copartner.getChildren();
        sellers = sellers.stream().peek(seller -> {
            PhysicalPurchaseSummary summary = PhysicalPurchaseSummary.dao.findBySellerIdAndMonth(seller.getId(), month);
            seller.put("monthly_amount", summary == null ? 0 : summary.getMonthlyAmount());
            seller.put("monthly_settlement_amount", summary == null ? 0 : summary.getMonthlyAmount().multiply(settlementProportionValue).divide(new BigDecimal(100)));
            if (summary != null) {
                summaries.add(summary);
            }
        }).collect(Collectors.toList());

        BigDecimal totalAmount = BigDecimal.ZERO;
        Optional<BigDecimal> sum = sellers.stream().map(seller -> PhysicalPurchaseSummary.dao.queryTotalAmount(seller.getId())).reduce(BigDecimal::add);
        if (sum.isPresent()) {
            totalAmount = sum.get();
        }
        BigDecimal monthlyAmount = BigDecimal.valueOf(summaries.stream().mapToDouble(x -> x.getMonthlyAmount().doubleValue()).sum());
        copartner.put("total_amount", totalAmount);
        copartner.put("total_settlement_amount", totalAmount.multiply(settlementProportionValue).divide(new BigDecimal(100)));
        copartner.put("monthly_amount", monthlyAmount);
        copartner.put("monthly_settlement_amount", monthlyAmount.multiply(settlementProportionValue).divide(new BigDecimal(100)));
        copartner.put("settlement_proportion", settlementProportionValue);
        copartner.put("children", sellers);
        logger.debug("querySettlement result = ", copartner.toJson());
        return copartner.toRecord();
    }

    /**
     * 对该月份进行分成计算
     * @param month yyyy-mm-01
     * @return
     */
    public void handleSettlement(String month) {
        SettlementProportion proportion = SettlementProportion.dao.findByCopartner();
        BigDecimal proportionValue = BigDecimal.valueOf(proportion.getProportionObject().getValue());
        boolean isLastPage = true;
        int pageNumber = 1;
        int pageSize = 50;
        do {
            CopartnerParam param = new CopartnerParam(pageNumber, pageSize);
            param.setStatus(Copartner.Status.NORMAL.toString());
            Page<Copartner> copartnerPage = Copartner.dao.paginate(param);
            isLastPage = copartnerPage.isLastPage();
            pageNumber++;
            copartnerPage.getList().forEach(copartner -> {
                logger.debug("handling settlement for copartner {}", copartner.toJson());
                CopartnerSettlement copartnerSettlement = CopartnerSettlement.dao.findByCond(copartner.getId(), month);
                if (copartnerSettlement == null) {
                    Record record = querySettlement(copartner, month, proportionValue);
                    BigDecimal totalAmount = record.get("monthly_amount");
                    BigDecimal settledAmount = totalAmount.multiply(proportionValue).divide(new BigDecimal(100));
                    try {
                        copartnerSettlement = new CopartnerSettlement();
                        copartnerSettlement.setCopartnerId(copartner.getId());
                        copartnerSettlement.setSettlementProportion(proportionValue);
                        copartnerSettlement.setStatisticMonth(DateKit.toDate(month));
                        copartnerSettlement.setSettledAmount(settledAmount);
                        copartnerSettlement.setTransferredAmount(BigDecimal.ZERO);
                        copartnerSettlement.setTransferred(UN_TRANSFERRED);
                        copartnerSettlement.save();
                        logger.debug("CopartnerSettlement saved. {}", copartnerSettlement.toJson());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    logger.debug("copartnerSettlement already exists. {}", copartnerSettlement.toJson());
                }
            });
        } while (!isLastPage);
    }
}
