package com.jfeat.partner.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.AgentPurchaseJournal;
import com.jfeat.partner.model.AgentSummary;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.model.Seller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Administrator on 2017/7/27.
 */
@ControllerBind(controllerKey = "/rest/agent_summary")
public class AgentSummaryController extends RestController {

    private DateFormat ymdDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat ymdhmsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void index() {
        User currentUser = getAttr("currentUser");
        Seller seller = Seller.dao.findByUserId(currentUser.getId());
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
        if (physicalSeller == null) {
            renderFailure("seller.is.not.a.physical");
            return;
        }
        if (!seller.isAgent()) {
            renderFailure("seller.is.not.a.agent");
            return;
        }
        //yyyy-MM
        String month = getPara("month");
        if (StrKit.isBlank(month)) {
            renderSuccess(AgentSummary.dao.findBySellerId(seller.getId()));
            return;
        }

        month = month + "-01";
        Calendar endOfMonth = Calendar.getInstance();
        try {
            endOfMonth.setTime(DateKit.toDate(month));
            endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
            endOfMonth.set(Calendar.HOUR_OF_DAY, endOfMonth.getActualMaximum(Calendar.HOUR_OF_DAY));
            endOfMonth.set(Calendar.MINUTE, endOfMonth.getActualMaximum(Calendar.MINUTE));
            endOfMonth.set(Calendar.SECOND, endOfMonth.getActualMaximum(Calendar.SECOND));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String[] arr = month.split("-");
        String year = arr[0];

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.parseInt(year));
        c.set(Calendar.MONTH, Integer.parseInt(arr[1]) - 1);

        //????????????????????????
        List<AgentSummary> agentSummaries = AgentSummary.dao.findBySellerIdAndMonth(seller.getId(), month);
        ListIterator<AgentSummary> iterator = agentSummaries.listIterator();
        while (iterator.hasNext()) {
            AgentSummary agentSummary = iterator.next();
            if (agentSummary.getEndMonth() != null) { //?????????????????????
                iterator.remove();
                continue;
            }
            // ?????? t_agent_summary??????????????????????????????????????????2015-08-03?????????????????????
            //  id  pcd_id      statistic_month      end_month
            //   1    1           2016-08-03     2017-08-03
            //   2    1           2015-08-03     2016-08-03
            //??????api????????????????????? "2016-08" ???????????????????????????????????? "2016-08-03"?????????????????????????????????????????????????????????????????????????????????
            agentSummary.put("bonus", AgentSummary.dao.findFirstBySellerIdPcdIdBetweenStatisticMonthEndMonth(
                    agentSummary.getSellerId(), agentSummary.getPcdId(), ymdDateFormat.format(c.getTime())));

            agentSummary.put("agentPurchaseJournals",
                    AgentPurchaseJournal.dao.findBySellerIdPcdIdBetweenCreateDate(agentSummary.getSellerId(), agentSummary.getPcdId(), month, ymdhmsDateFormat.format(endOfMonth.getTime())));
        }
        renderSuccess(agentSummaries);
    }
}
