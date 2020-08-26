package com.jfeat.settlement.task;

import com.jfeat.config.model.Config;
import com.jfeat.job.CronExp;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.AgentSummary;
import com.jfeat.settlement.service.OwnerBalanceService;
import com.jfinal.aop.Enhancer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */
@CronExp(value = "0 0 1 1 * ?")
public class PhysicalAgentSettlementJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(PhysicalAgentSettlementJob.class);
    private static OwnerBalanceService ownerBalanceService = Enhancer.enhance(OwnerBalanceService.class);
    private static final String POINT_EXCHANGE_RATE_KEY = "mall.point_exchange_rate";

    /**
     * 线下代理商提成结算程序
     * 把上一个月的线下代理商的提成进行结算
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("========= Physical Agent Settlement Started =========");
        String month = DateKit.lastMonth("yyyy-MM-01");
        execute(month);
    }

    /**
     * @param month yyyy-MM-01
     */
    public void execute(String month) {
        Config config = Config.dao.findByKey(POINT_EXCHANGE_RATE_KEY);
        int pointExchangeRate = 100;
        if (config != null && config.getValueToInt() != null) {
            pointExchangeRate = config.getValueToInt();
        }
        logger.info("pointExchangeRate = {}", pointExchangeRate);
        List<AgentSummary> agentSummaries = AgentSummary.dao.findByMonthWithoutEndMonth(month);
        for (AgentSummary agentSummary : agentSummaries) {
            if (agentSummary.getSettledAmount().doubleValue() == 0 || AgentSummary.TRANSFERRED.equals(agentSummary.getTransferred())) {
                logger.debug("sellerId = {}, settledAmount = {}, transfered = {}, continue", agentSummary.getSellerId(), agentSummary.getSettledAmount(), agentSummary.getTransferred());
                continue;
            }
            logger.debug("sellerId={}, settledAmount={}", agentSummary.getSellerId(), agentSummary.getSettledAmount());
            // 结算到余额表
            if (ownerBalanceService.addReward(agentSummary.getSeller().getUserId(), agentSummary.getSettledAmount())) {
                agentSummary.setTransferred(AgentSummary.TRANSFERRED);
                agentSummary.setTransferredAmount(new BigDecimal(agentSummary.getSettledAmount().doubleValue() * pointExchangeRate));
                agentSummary.update();
            } else {
                logger.info("ownerBalanceService.addReward.failure. sellerId = {}, pcdId = {}", agentSummary.getSellerId(), agentSummary.getPcdId());
            }
        }
    }

}
