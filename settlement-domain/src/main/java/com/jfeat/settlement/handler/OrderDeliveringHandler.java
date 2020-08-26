package com.jfeat.settlement.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.marketing.wholesale.model.Wholesale;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderItem;
import com.jfeat.partner.model.Agent;
import com.jfeat.partner.model.AgentPcdQualify;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.AgentService;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfeat.pcd.model.Pcd;
import com.jfeat.product.model.Product;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/6/28.
 */
public class OrderDeliveringHandler implements Observer {

    private static Logger logger = LoggerFactory.getLogger(OrderDeliveringHandler.class);
    private static PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);
    private static AgentService agentService = Enhancer.enhance(AgentService.class);
    private static DateFormat ym1DateFormat = new SimpleDateFormat("yyyy-MM-01");
    private static final DateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void invoke(Subject subject, int event, Object o) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_DELIVERING) {
            Order order = Order.dao.findById(((Order) subject).getId());
            try {
                logger.debug("handling delivering order {}.", order);
                //假如是批发的订单
                // 并且之前未计算过分成（需要此条件是因为订单有可能多次进入DELIVERING状态的
                if (StrKit.notBlank(order.getMarketing())
                        && order.getMarketing().equals(Constants.MARKETING_WHOLESALE)
                        && !physicalSellerService.settlementCounted(order.getId())) {
                    //处理线下皇冠对于批发订单的分成
                    handleWholesale(order);
                    //处理线下代理对于批发订单的分成
                    handleAgentWholesale(order);
                    //处理线下代理对于批发订单的年终奖
                    handleAgentWholesaleBonus(order);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                for (StackTraceElement element : e.getStackTrace()) {
                    logger.error("    {}:{} - {}:{}",
                            element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }
        }
    }

    private void handleWholesale(Order order) throws ParseException, RuntimeException {
        Seller orderSeller = Seller.dao.findByUserId(order.getUserId());
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(orderSeller.getId());
        StringBuilder note = new StringBuilder("Order Created. OrderNumber: ");
        note.append(order.getOrderNumber());
        note.append(". Description: ");
        note.append(order.getDescription());

        List<OrderItem> orderItems = order.getOrderItems();
        List<Integer> orderItemIds = Lists.newLinkedList();
        List<BigDecimal> amounts = Lists.newLinkedList();
        List<Integer> settlementProportions = Lists.newLinkedList();
        List<BigDecimal> expectedRewards = Lists.newLinkedList();
        List<String> productNames = Lists.newLinkedList();

        for (OrderItem orderItem : orderItems) {
            orderItemIds.add(orderItem.getId());
            amounts.add(orderItem.getFinalPrice());
            Wholesale wholesale = Wholesale.dao.findById(orderItem.getMarketingId());
            Integer settlementProportion = wholesale.getSettlementProportion();
            if (settlementProportion == null) {
                logger.error("批发活动{}未指定产品分成比例，分销结算失败!", wholesale.getMarketingName());
                throw new RuntimeException(String.format("批发活动【%s】未指定产品分成比例，分销结算失败！", wholesale.getMarketingName()));
            }
            settlementProportions.add(settlementProportion);
            BigDecimal expectedReward = orderItem.getFinalPrice()
                    .multiply(BigDecimal.valueOf(settlementProportion * 1.0 / 100))
                    .divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
            expectedRewards.add(expectedReward);
            productNames.add(orderItem.getProductName());
        }

        boolean result = physicalSellerService.updatePurchase(physicalSeller,
                order.getId(),
                order.getOrderNumber(),
                orderItemIds,
                productNames,
                amounts,
                settlementProportions,
                expectedRewards,
                note.toString());
        logger.debug("Physical seller {} update physical purchase result: {}", physicalSeller.getSellerId(), result);
    }

    private void handleAgentWholesale(Order order) throws ParseException {
        logger.debug("handlingAgentWholesale start-----------------------------");
        String month = ym1DateFormat.format(new Date());
        String province = order.getProvince();
        String city = order.getCity();
        String district = order.getDistrict();
        //计算此省的线下代理商的分成
        if (StrKit.notBlank(province)) {
            Pcd pcd = Pcd.dao.findByName(province, Pcd.PROVINCE);
            if (pcd != null) {
                List<AgentPcdQualify> agents = AgentPcdQualify.dao.findAllByPcdId(pcd.getId()); //这只是代理商，未必是线下
                addToSummary(agents, pcd.getId(), order, month);
            }
        }
        //计算此市的线下代理商的分成
        if (StrKit.notBlank(city)) {
            Pcd pcd = Pcd.dao.findByName(city, Pcd.CITY);
            if (pcd != null) {
                List<AgentPcdQualify> agents = AgentPcdQualify.dao.findAllByPcdId(pcd.getId());//这只是代理商，未必是线下
                addToSummary(agents, pcd.getId(), order, month);
            }
        }
        //计算此区的线下代理商的分成
        if (StrKit.notBlank(district)) {
            Pcd pcd = Pcd.dao.findByName(district, Pcd.DISTRICT);
            if (pcd != null) {
                List<AgentPcdQualify> agents = AgentPcdQualify.dao.findAllByPcdId(pcd.getId());//这只是代理商，未必是线下
                addToSummary(agents, pcd.getId(), order, month);
            }
        }
    }

    public void addToSummary(List<AgentPcdQualify> agents, int pcdId, Order order, String month) throws ParseException {
        for (AgentPcdQualify agentPcdQualify : agents) {
            Agent agent = agentPcdQualify.getAgent();
            Seller seller = Seller.dao.findByUserId(agent.getUserId());
            PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
            if (physicalSeller == null) {//只有当代理商是一个线下时，才有提成
                logger.debug("agent sellerId = {} is not a physical.", seller.getId());
                continue;
            }
            Double percentage = agent.getPercentage(pcdId); //某个代理商代理某地区的提成比例
            logger.debug("agentId: {}, pcdId: {}, percentage={}%", agent.getId(), pcdId, percentage);
            if (percentage == null || percentage < 0) {
                logger.debug("agentId: {}, pcdId: {}, percentage={}%，continue", agent.getId(), pcdId, percentage);
                continue;
            }

            List<Map<String, Object>> orderItemMapList = Lists.newArrayList();
            for (OrderItem orderItem : order.getOrderItems()) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("orderItemId", orderItem.getId());
                map.put("productId", orderItem.getProductId());
                map.put("productName", orderItem.getProductName());
                map.put("productSpecificationName", orderItem.getProductSpecificationName());
                map.put("productCover", Product.dao.findById(orderItem.getProductId()).getCover());
                map.put("quantity", orderItem.getQuantity());
                map.put("price", orderItem.getPrice());
                map.put("finalPrice", orderItem.getFinalPrice());
                map.put("marketingId", orderItem.getMarketingId());
                Wholesale wholesale = Wholesale.dao.findById(orderItem.getMarketingId());
                map.put("marketingName", wholesale.getMarketingName());
                map.put("agentProportion", wholesale.getAgentProportion());
                map.put("orderUserId", order.getUserId());
                map.put("orderUserName", order.getUser().getName());
                orderItemMapList.add(map);
            }
            agentService.acquireAgentSummary(agentPcdQualify, seller.getId(), pcdId, orderItemMapList, BigDecimal.valueOf(percentage), month);
        }
    }

    private void handleAgentWholesaleBonus(Order order) throws ParseException {
        String province = order.getProvince();
        String city = order.getCity();
        String district = order.getDistrict();
        //计算此省的线下代理商的分成
        if (StrKit.notBlank(province)) {
            Pcd pcd = Pcd.dao.findByName(province, Pcd.PROVINCE);
            List<AgentPcdQualify> agents = AgentPcdQualify.dao.findAllByPcdId(pcd.getId()); //这只是代理商，未必是线下
            addToBonusSummary(agents, pcd.getId(), order.getTotalPrice());
        }
        //计算此市的线下代理商的分成
        if (StrKit.notBlank(city)) {
            Pcd pcd = Pcd.dao.findByName(city, Pcd.CITY);
            List<AgentPcdQualify> agents = AgentPcdQualify.dao.findAllByPcdId(pcd.getId());//这只是代理商，未必是线下
            addToBonusSummary(agents, pcd.getId(), order.getTotalPrice());
        }
        //计算此区的线下代理商的分成
        if (StrKit.notBlank(district)) {
            Pcd pcd = Pcd.dao.findByName(district, Pcd.DISTRICT);
            List<AgentPcdQualify> agents = AgentPcdQualify.dao.findAllByPcdId(pcd.getId());//这只是代理商，未必是线下
            addToBonusSummary(agents, pcd.getId(), order.getTotalPrice());
        }
    }

    public void addToBonusSummary(List<AgentPcdQualify> agents, int pcdId, BigDecimal totalPrice) throws ParseException {
        for (AgentPcdQualify agentPcdQualify : agents) {
            Agent agent = agentPcdQualify.getAgent();
            Seller seller = Seller.dao.findByUserId(agent.getUserId());
            PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
            if (physicalSeller == null) {//只有当代理商是一个线下时，才有提成
                continue;
            }
            Date statisticMonth = physicalSeller.getLatestBonusDate();
            if (statisticMonth == null) {
                statisticMonth = agentPcdQualify.getCreateTime().getTime() > physicalSeller.getCreatedDate().getTime() ? agentPcdQualify.getCreateTime() : physicalSeller.getCreatedDate();
            }
            String endMonth = addYears(statisticMonth, 1);
            agentService.acquireAgentBonusSummary(seller.getId(), pcdId, ymdFormat.format(statisticMonth), endMonth, totalPrice);
        }
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
