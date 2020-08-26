package com.jfeat.settlement.task;

import com.jfeat.config.model.Config;
import com.jfeat.job.CronExp;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.*;
import com.jfeat.settlement.service.OwnerBalanceService;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Record;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2017/7/24.
 */
@CronExp("0 0 1 * * ?")
public class PhysicalAgentBonusJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(PhysicalAgentBonusJob.class);
    private static OwnerBalanceService ownerBalanceService = Enhancer.enhance(OwnerBalanceService.class);
    private static final String POINT_EXCHANGE_RATE_KEY = "mall.point_exchange_rate";
    private static final DateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 把上一年的线下代理商的奖金进行结算
     * 比如程序执行于 2017-08-03 01:03:10 ，则应检查所有最后一次结算年终奖日期为 2016-08-03的线下代理，为其结算年终奖，
     * 该程序寻找 与2016-08-03~2017-08-03对应的一条t_agent_summary记录进行结算，结算完毕后，把physicalSeller的最后一次
     * 结算年终奖日期改为2017-08-03
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        execute();
    }

    public void execute() {
        // 将所有线下代理找出来
        List<Agent> allPhysicalAgents = Agent.dao.findAllPhysicalAgents();
        if (allPhysicalAgents.size() == 0) {
            logger.info("no physical agents.");
            return;
        }
        Config config = Config.dao.findByKey(POINT_EXCHANGE_RATE_KEY);
        int pointExchangeRate = 100;
        if (config != null && config.getValueToInt() != null) {
            pointExchangeRate = config.getValueToInt();
        }
        logger.info("pointExchangeRate = {}", pointExchangeRate);

        String today = DateKit.today();
        for (Agent agent : allPhysicalAgents) {
            Seller seller = Seller.dao.findByUserId(agent.getUserId());
            PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
            //最后一次生成奖金的时间通过physicalSeller的latest_bonus_date指定，若此值为null，则通过 created_date（即成为线下时间）指定
            Date latestBonusDate = physicalSeller.getLatestBonusDate();
            if (latestBonusDate == null) {
                latestBonusDate = physicalSeller.getCreatedDate();
            }
            String latestBonusDateAfterAddOneYear = addYears(latestBonusDate, 1);
            //如果今天是2017-08-03，则最后一次生成奖金的时间一定要是2016-08-03（差1天，多一天都不行）
            if (!today.equals(latestBonusDateAfterAddOneYear)) {
                continue;
            }
            List<Record> agentProvinces = agent.getAgentProvinces();
            List<Record> agentCities = agent.getAgentCities();
            List<Record> agentDistricts = agent.getAgentDistricts();
            try {
                String latestBonusDateStr = ymdFormat.format(latestBonusDate); //yyyy-mm-dd
                calc(physicalSeller, agentProvinces, latestBonusDateStr, today, pointExchangeRate);
                calc(physicalSeller, agentCities, latestBonusDateStr, today, pointExchangeRate);
                calc(physicalSeller, agentDistricts, latestBonusDateStr, today, pointExchangeRate);
                //最后一次生成奖金的日期（注意：由于使用了 physicalSeller的created_date和latested_bonus_date字段，为了好处理，这里设end而不是设start，即
                //设置的是“成功生成这个physicalSeller的奖金的奖金程序的执行日期”)
                physicalSeller.setLatestBonusDate(DateKit.toDate(today));
                physicalSeller.update();
            } catch (ParseException e) {
                e.printStackTrace();
                logger.error("parsing date to str error, agentId = {}, latestBonusDate={}", agent.getId(), latestBonusDate);
            }
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

    /**
     * @param physicalSeller
     * @param records           省集合/市集合/区集合 （只有一种）
     * @param startMonth        yyyy-MM-dd
     * @param endMonth          yyyy-MM-dd
     * @param pointExchangeRate
     * @throws ParseException
     */
    private void calc(PhysicalSeller physicalSeller, List<Record> records, String startMonth, String endMonth, int pointExchangeRate) throws ParseException {
        for (Record record : records) {
            Integer pcdId = record.getInt(PcdQualify.Fields.PCD_ID.toString());
            AgentSummary agentSummary = AgentSummary.dao.findFirstBySellerIdPcdIdStatisticMonthEndMonth(physicalSeller.getSellerId(), pcdId, startMonth, endMonth);
            if (agentSummary == null) {
                logger.debug("agentSummary is null. sellerId = {}, pcdId = {}, startMonth = {}, endMonth = {}", physicalSeller.getSellerId(), pcdId, startMonth, endMonth);
                continue;
            }
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
