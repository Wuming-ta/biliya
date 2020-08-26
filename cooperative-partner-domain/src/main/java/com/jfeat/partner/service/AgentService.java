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

package com.jfeat.partner.service;

import com.google.common.collect.Lists;
import com.jfeat.core.BaseModel;
import com.jfeat.core.BaseService;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.*;
import com.jfeat.pcd.model.Pcd;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2016/3/12.
 */
public class AgentService extends BaseService {

    private static final DateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat ym1Format = new SimpleDateFormat("yyyy-MM-01");

    @Before(Tx.class)
    public Ret createAgent(Agent theAgent, Integer[] pcdQualifyIds, Integer[] agentPhysicalSettlementPercentages) throws ParseException {
        if (theAgent == null) {
            return failure("agent.is.null");
        }
        if (pcdQualifyIds != null) {
            theAgent.save();
            updateAgentArea(theAgent, pcdQualifyIds, agentPhysicalSettlementPercentages);
            createAgentSummaries(theAgent, pcdQualifyIds, agentPhysicalSettlementPercentages);
        }
        return this.success();
    }

    public void updateAgentArea(Agent agent, Integer[] pcdQualityIds, Integer[] agentPhysicalSettlementPercentages) {
        if (agent == null) {
            throw new RuntimeException("agent is null");
        }
        if (pcdQualityIds == null) {
            throw new RuntimeException("pcdQualityIds is null");
        }
        if (agentPhysicalSettlementPercentages != null && pcdQualityIds.length != agentPhysicalSettlementPercentages.length) {
            throw new RuntimeException("pcdQualityIds.length.is.not.equal.to.agentPhysicalSettlementPercentages.length");
        }

        List<AgentPcdQualify> agentPcdQualifies = AgentPcdQualify.dao.findByAgentId(agent.getId());
        List<Integer> keeps = Lists.newArrayList();
        for (int i = 0; i < pcdQualityIds.length; i++) {
            Integer pcdQualityId = pcdQualityIds[i];
            Integer percentage = agentPhysicalSettlementPercentages != null ? agentPhysicalSettlementPercentages[i] : null;
            boolean found = false;
            for (AgentPcdQualify agentPcdQualify : agentPcdQualifies) {
                if (pcdQualityId.equals(agentPcdQualify.getPcdQualifyId())) {
                    found = true;
                    agentPcdQualify.setPhysicalSettlementPercentage(percentage);
                    agentPcdQualify.update();
                    keeps.add(pcdQualityId);
                    break;
                }
            }
            if (!found) {
                //new one
                AgentPcdQualify agentPcdQualify = new AgentPcdQualify();
                agentPcdQualify.setAgentId(agent.getId());
                agentPcdQualify.setPcdQualifyId(pcdQualityId);
                agentPcdQualify.setPhysicalSettlementPercentage(percentage);
                agentPcdQualify.save();
                keeps.add(pcdQualityId);
            }
        }
        List<AgentPcdQualify> toRemove = agentPcdQualifies.stream().filter(item -> !keeps.contains(item.getPcdQualifyId())).collect(Collectors.toList());
        toRemove.forEach(BaseModel::delete);
    }


    private void createAgentSummaries(Agent agent, Integer[] pcdQualifyIds, Integer[] agentPhysicalSettlementPercentages) throws ParseException {
        String statisticMonth = ym1Format.format(new Date());
        for (Integer pcdQualifyId : pcdQualifyIds) {
            Seller seller = Seller.dao.findByUserId(agent.getUserId());
            PcdQualify pcdQualify = PcdQualify.dao.findById(pcdQualifyId);
            PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
            if (physicalSeller == null) {
                logger.info("agentId = {} is not yet a physicalSeller,can't create agentSummary", agent.getId());
                return;
            }
            //如果没有当月提成的记录，则生成1条
            if (AgentSummary.dao.findFirstBySellerIdPcdIdStatisticMonthWithoutEndMonth(seller.getId(), pcdQualify.getPcdId(), statisticMonth) == null) {
                AgentSummary agentSummary = new AgentSummary();
                agentSummary.setPcdId(pcdQualify.getPcdId());
                agentSummary.setSellerId(seller.getId());
                agentSummary.setStatisticMonth(ym1Format.parse(statisticMonth));
                agentSummary.save();
            }
            Date latestBonusDate = physicalSeller.getLatestBonusDate();
            if (latestBonusDate == null) {
                AgentPcdQualify agentPcdQualify = AgentPcdQualify.dao.findByAgentIdAndPcdQualifyId(agent.getId(), pcdQualifyId);
                latestBonusDate = agentPcdQualify.getCreateTime().getTime() > physicalSeller.getCreatedDate().getTime() ? agentPcdQualify.getCreateTime() : physicalSeller.getCreatedDate();
            }
            //如果没有奖金记录，则生成1条
            String endMonth = addYears(latestBonusDate, 1);
            if (AgentSummary.dao.findFirstBySellerIdPcdIdStatisticMonthEndMonth(seller.getId(), pcdQualify.getPcdId(), ymdFormat.format(latestBonusDate), endMonth) == null) {
                AgentSummary agentSummary = new AgentSummary();
                agentSummary.setPcdId(pcdQualify.getPcdId());
                agentSummary.setSellerId(seller.getId());
                agentSummary.setStatisticMonth(latestBonusDate);
                agentSummary.setEndMonth(DateKit.toDate(endMonth));
                agentSummary.save();
            }
        }
    }

    @Before(Tx.class)
    public Ret updateAgent(Agent agent, Integer[] pcdQualifyIds, Integer[] agentPhysicalSettlementPercentages) throws ParseException {
        if (pcdQualifyIds == null) {
            agent.delete();
        } else {
            agent.update();
            updateAgentArea(agent, pcdQualifyIds, agentPhysicalSettlementPercentages);
            createAgentSummaries(agent, pcdQualifyIds, agentPhysicalSettlementPercentages);
        }
        return this.success();
    }

    public Ret deleteAgent(int agentId) {
        Agent agent = Agent.dao.findById(agentId);
        return deleteAgent(agent);
    }

    public Ret deleteAgent(Agent agent) {
        if (agent == null) {
            return this.failure();
        }
        agent.delete();
        return this.success();
    }

    public Agent queryProvinceAgent(String province) {
        return queryPcdAgent(province, Pcd.PROVINCE);
    }

    public Agent queryCityAgent(String city) {
        return queryPcdAgent(city, Pcd.CITY);
    }

    public Agent queryDistrictAgent(String district) {
        return queryPcdAgent(district, Pcd.DISTRICT);
    }

    private Agent queryPcdAgent(String pcdName, String pcdType) {
        Pcd pcd = Pcd.dao.findByName(pcdName, pcdType);
        if (pcd != null) {
            return Agent.dao.findByPcdId(pcd.getId());
        }
        return null;
    }

    /**
     * 增加某代理商相应月份的提成汇总数额（如果没有汇总记录则创建），并返回这条汇总记录
     *
     * @param sellerId
     * @param pcdId
     * @param orderItemMapList 为null或者size为0代表只想获取某代理商相应月份的提成汇总记录，不用生成针对每个订单项的提成明细（AgentPurchaseJournal）
     * @param percentage       某seller代理某地区的提成比例
     * @param month            yyyy-MM-01
     * @return
     * @throws ParseException
     */
    @Before(Tx.class)
    public AgentSummary acquireAgentSummary(AgentPcdQualify agentPcdQualify, int sellerId, int pcdId, List<Map<String, Object>> orderItemMapList, BigDecimal percentage, String month) throws ParseException {
        AgentSummary summary = AgentSummary.dao.findFirstBySellerIdPcdIdStatisticMonthWithoutEndMonth(sellerId, pcdId, month);
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(sellerId);
        Date now = new Date();
        if (orderItemMapList == null || orderItemMapList.size() == 0) { //只获取某代理商相应月份的提成汇总记录，不用生成针对每个订单项的提成明细（AgentPurchaseJournal）
            if (summary == null) {
                summary = new AgentSummary();
                summary.setSellerId(sellerId);
                summary.setPcdId(pcdId);
                summary.setAmount(BigDecimal.ZERO);
                //settlementProportion不需要设置，因为与订单中每个订单项对应的批发活动相关，而不是与整个订单相关
                summary.setSettledAmount(BigDecimal.ZERO);
                summary.setStatisticMonth(DateKit.toDate(month));
                summary.setTransferred(PhysicalPurchaseSummary.UN_TRANSFERRED);
                summary.setTransferredAmount(BigDecimal.ZERO);
                summary.save();
            }
            return summary;
        }

        //生成针对每个订单项的提成明细
        BigDecimal newAmount = BigDecimal.ZERO; //订单总额
        BigDecimal newSettledAmount = BigDecimal.ZERO;
        Integer orderUserId = (Integer) orderItemMapList.get(0).get("orderUserId");
        String orderUserName = (String) orderItemMapList.get(0).get("orderUserName");
        List<AgentPurchaseJournal> agentPurchaseJournals = Lists.newArrayList();
        for (Map<String, Object> map : orderItemMapList) {
            logger.debug("orderItem = {}", map);
            BigDecimal agentProportion = (BigDecimal) map.get("agentProportion");
            Integer quantity = (Integer) map.get("quantity");
            BigDecimal finalPrice = (BigDecimal) map.get("finalPrice");
            BigDecimal settledAmount = BigDecimal.valueOf(finalPrice.doubleValue() * (agentProportion.doubleValue() / 100) * (percentage.doubleValue() / 100)).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
            newAmount = newAmount.add(finalPrice);
            newSettledAmount = newSettledAmount.add(settledAmount);

            AgentPurchaseJournal agentPurchaseJournal = new AgentPurchaseJournal();
            agentPurchaseJournal.setSettledAmount(settledAmount);
            agentPurchaseJournal.setSellerId(sellerId);
            agentPurchaseJournal.setPcdId(pcdId);
            agentPurchaseJournal.setPcdName(Pcd.dao.findById(pcdId).getName());
            agentPurchaseJournal.setOrderItemId((Integer) map.get("orderItemId"));
            agentPurchaseJournal.setProductId((Integer) map.get("productId"));
            agentPurchaseJournal.setProductName((String) map.get("productName"));
            agentPurchaseJournal.setProductSpecificationName((String) map.get("productSpecificationName"));
            agentPurchaseJournal.setProductCover((String) map.get("productCover"));
            agentPurchaseJournal.setPrice((BigDecimal) map.get("price"));
            agentPurchaseJournal.setQuantity(quantity);
            agentPurchaseJournal.setFinalPrice(finalPrice);
            agentPurchaseJournal.setMarketingId((Integer) map.get("marketingId"));
            agentPurchaseJournal.setMarketingName((String) map.get("marketingName"));
            agentPurchaseJournal.setAgentProportion(agentProportion);
            agentPurchaseJournal.setPercentage(percentage);
            agentPurchaseJournal.setOrderUserId(orderUserId);
            agentPurchaseJournal.setOrderUserName(orderUserName);
            agentPurchaseJournal.setCreateDate(now);

            agentPurchaseJournals.add(agentPurchaseJournal);
        }
        Db.batchSave(agentPurchaseJournals, 10);

        if (summary == null) {
            summary = new AgentSummary();
            summary.setSellerId(sellerId);
            summary.setPcdId(pcdId);
            summary.setAmount(newAmount);
            summary.setSettledAmount(newSettledAmount);
            //settlementProportion不需要设置，因为与订单中每个订单项对应的批发活动相关，而不是与整个订单相关
            summary.setStatisticMonth(DateKit.toDate(month));
            summary.setTransferred(PhysicalPurchaseSummary.UN_TRANSFERRED);
            summary.setTransferredAmount(BigDecimal.ZERO);
            summary.save();
        } else {
            summary.setAmount(summary.getAmount().add(newAmount));
            summary.setSettledAmount(summary.getSettledAmount().add(newSettledAmount));
            summary.update();
        }
        Date latestBonusDate = physicalSeller.getLatestBonusDate();
        if (latestBonusDate == null) {
            latestBonusDate = agentPcdQualify.getCreateTime().getTime() > physicalSeller.getCreatedDate().getTime() ? agentPcdQualify.getCreateTime() : physicalSeller.getCreatedDate();
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BigDecimal totalAmount = AgentPurchaseJournal.dao.getTotalAmount(summary.getSellerId(), summary.getPcdId(),
                dateFormat.format(latestBonusDate), DateKit.today("yyyy-MM-dd HH:mm:ss"));
        summary.setYearStatisticAmount(totalAmount);
        summary.update();
        return summary;
    }

    /**
     * 增加某代理商该年年终奖的提成汇总记录（如果没有汇总记录则创建），并返回这条汇总记录
     *
     * @param sellerId
     * @param pcdId
     * @param statisticMonth yyyy-MM-dd
     * @param endMonth       yyyy-MM-dd
     * @param newAmount      增加的数额
     * @throws ParseException
     */

    public AgentSummary acquireAgentBonusSummary(int sellerId, int pcdId, String statisticMonth, String endMonth, BigDecimal newAmount) throws ParseException {
        AgentSummary summary = AgentSummary.dao.findFirstBySellerIdPcdIdStatisticMonthEndMonth(sellerId, pcdId, statisticMonth, endMonth);
        BigDecimal settlementProportion;
        if (summary == null) {
            summary = new AgentSummary();
            summary.setSellerId(sellerId);
            summary.setPcdId(pcdId);
            summary.setAmount(newAmount);
            settlementProportion = AgentSummary.dao.getPercentage(pcdId, newAmount);
            summary.setSettlementProportion(settlementProportion);
            summary.setSettledAmount(BigDecimal.valueOf(newAmount.doubleValue() * settlementProportion.doubleValue() / 100));
            summary.setStatisticMonth(DateKit.toDate(statisticMonth));
            summary.setEndMonth(DateKit.toDate(endMonth));
            summary.setTransferred(PhysicalPurchaseSummary.UN_TRANSFERRED);
            summary.setTransferredAmount(BigDecimal.ZERO);
            summary.save();
        } else {
            BigDecimal amount = summary.getAmount().add(newAmount);
            summary.setAmount(amount);
            settlementProportion = AgentSummary.dao.getPercentage(pcdId, amount);
            summary.setSettlementProportion(settlementProportion);
            summary.setSettledAmount(BigDecimal.valueOf(amount.doubleValue() * settlementProportion.doubleValue() / 100));
            summary.update();
        }

        logger.debug("sellerId = {}, pcdId = {}, statisticMonth = {} , endMonth = {}, amount = {}, settlementProportion = {}",
                sellerId, pcdId, statisticMonth, endMonth, newAmount, settlementProportion);

        return summary;
    }

    /**
     * 为指定日期增加指定年
     *
     * @param date 原日期
     * @param year 增加多少年
     * @return yyyy-MM-dd
     */
    private String addYears(Date date, int year) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, year);
        return ymdFormat.format(c.getTime());
    }
}
