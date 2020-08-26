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

package com.jfeat.partner.controller;

import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.eventlog.service.EventLogService;
import com.jfeat.flash.Flash;
import com.jfeat.identity.model.User;
import com.jfeat.partner.constants.EventLogName;
import com.jfeat.partner.model.Agent;
import com.jfeat.partner.model.PcdQualify;
import com.jfeat.partner.model.PhysicalAgentBonus;
import com.jfeat.partner.model.SettlementProportion;
import com.jfeat.partner.service.AgentService;
import com.jfeat.pcd.model.Pcd;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingfei on 2016/3/16.
 */
public class AgentController extends BaseController {

    private AgentService agentService = Enhancer.enhance(AgentService.class);
    private EventLogService eventLogService = Enhancer.enhance(EventLogService.class);

    @RequiresPermissions("CooperativePartnerApplication.view")
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String userName = getPara("agentId");
        Integer agentId = null;
        if (StrKit.notBlank(userName) && StringUtils.isNumeric(userName)) {
            agentId = Integer.parseInt(userName);
        }
        setAttr("agents", Agent.dao.paginate(pageNumber, pageSize, agentId, userName));
        keepPara();

        setAttr("agentCount", Agent.dao.queryAgentCountTotal());
        setAttr("provinceAgentCount", Agent.dao.queryAgentCount(Pcd.PROVINCE));
        setAttr("cityAgentCount", Agent.dao.queryAgentCount(Pcd.CITY));
        setAttr("districtAgentCount", Agent.dao.queryAgentCount(Pcd.DISTRICT));
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void edit() {
        int agentId = getParaToInt();
        Agent agent = Agent.dao.findById(agentId);
        setAttr("currentAgent", agent);
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void update() {
        Agent agent = getModel(Agent.class);
        Integer[] pcdQualityIds = getParaValuesToInt("pcdQualityId");
        String[] agentPhysicalSettlementPercentages = getParaValues("agentPhysicalSettlementPercentage");

        Integer[] apsp = null;
        if (pcdQualityIds != null && agentPhysicalSettlementPercentages != null) {
            apsp = new Integer[pcdQualityIds.length];
            for (int i = 0; i < pcdQualityIds.length; i++) {
                //有指定分成比例，则agentPhysicalSettlementPercentage格式为 "pcdQualityId-分成比例"；否则格式为 "pcdQualityId-"
                for (String agentPhysicalSettlementPercentage : agentPhysicalSettlementPercentages) {
                    String[] arr = agentPhysicalSettlementPercentage.split("-");
                    if (arr.length == 2) {
                        Integer pqid = Integer.parseInt(arr[0]);
                        Integer psp = Integer.parseInt(arr[1]);
                        if (pqid.equals(pcdQualityIds[i])) {
                            apsp[i] = psp;
                            break;
                        }
                    }
                }
            }
        }
        Ret ret = null;
        try {
            ret = agentService.updateAgent(agent, pcdQualityIds, apsp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.agent.update.success"));
            Map<String, Object> data = new HashMap<>();
            data.put("agent", agent);
            data.put("pcdQualityIds", pcdQualityIds);
            recordEvent(EventLogName.COOPERATIVE_PARTNER_AGENT_UPDATE_KEY, data);
        }
        redirect("/agent");
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void add() {
        setAttr("agent", new Agent());
    }


    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void save() {
        Agent agent = getModel(Agent.class);
        Integer[] pcdQualityIds = getParaValuesToInt("pcdQualityId");
        String[] agentPhysicalSettlementPercentages = getParaValues("agentPhysicalSettlementPercentage");
        Integer[] apsp = null;
        if (pcdQualityIds != null && agentPhysicalSettlementPercentages != null) {
            apsp = new Integer[pcdQualityIds.length];
            for (int i = 0; i < pcdQualityIds.length; i++) {
                //有指定分成比例，则agentPhysicalSettlementPercentage格式为 "pcdQualityId-分成比例"；否则格式为 "pcdQualityId-"
                for (String agentPhysicalSettlementPercentage : agentPhysicalSettlementPercentages) {
                    String[] arr = agentPhysicalSettlementPercentage.split("-");
                    if (arr.length == 2) {
                        Integer pqid = Integer.parseInt(arr[0]);
                        Integer psp = Integer.parseInt(arr[1]);
                        if (pqid.equals(pcdQualityIds[i])) {
                            apsp[i] = psp;
                            break;
                        }
                    }
                }
            }
        }
        Ret ret = null;
        try {
            ret = agentService.createAgent(agent, pcdQualityIds, apsp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.agent.create.success"));
            Map<String, Object> data = new HashMap<>();
            data.put("agent", agent);
            data.put("pcdQualityIds", pcdQualityIds);
            recordEvent(EventLogName.COOPERATIVE_PARTNER_AGENT_SAVE_KEY, data);
        }
        redirect("/agent");
    }


    @RequiresPermissions("CooperativePartnerApplication.delete")
    public void delete() {
        Integer agentId = getParaToInt();
        Ret ret = agentService.deleteAgent(agentId);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.agent.delete.success"));
            Map<String, Object> data = new HashMap<>();
            data.put("agentId", agentId);
            recordEvent(EventLogName.COOPERATIVE_PARTNER_AGENT_DELETE_KEY, data);
        }
        redirect("/agent");
    }

    public void nonAgentUser() {
        String keyWord = getPara("keyWord");
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 30);
        setAttr("users", Agent.dao.paginateNonAgentUser(pageNumber, pageSize, keyWord));
    }

    public void ajaxGetProvinces() {
        renderJson(PcdQualify.dao.queryPcd(Pcd.PROVINCE, null));
    }

    public void ajaxGetCities() {
        renderJson(getPcd(Pcd.CITY));
    }

    public void ajaxGetDistricts() {
        renderJson(getPcd(Pcd.DISTRICT));
    }

    private Object getPcd(String pcd) {
        String pcdQualifyId = getPara("id");
        PcdQualify pcdQualify = PcdQualify.dao.findById(pcdQualifyId);
        return PcdQualify.dao.queryPcd(pcd, pcdQualify.getPcdId());
    }

    private void recordEvent(String eventNameKey, Map<String, Object> data) {
        String eventType = getRes().get(EventLogName.COOPERATIVE_PARTNER_EVENT_TYPE_KEY);
        String eventName = getRes().get(eventNameKey);
        User currentUser = getAttr("currentUser");
        String ip = getRequest().getRemoteAddr();
        String userAgent = getRequest().getHeader("User-Agent");
        eventLogService.record(eventType, eventName, currentUser.getName(), ip, userAgent, data);
    }

    @Before(Flash.class)
    public void editPcdQualify() {
        setAttr("provinceSettlementProportion",
                SettlementProportion.dao.findByTypeLevel(SettlementProportion.Type.PHYSICAL_AGENT, SettlementProportion.Level.PROVINCE_PHYSICAL_AGENT.getValue()));
        setAttr("citySettlementProportion",
                SettlementProportion.dao.findByTypeLevel(SettlementProportion.Type.PHYSICAL_AGENT, SettlementProportion.Level.CITY_PHYSICAL_AGENT.getValue()));
        setAttr("districtSettlementProportion",
                SettlementProportion.dao.findByTypeLevel(SettlementProportion.Type.PHYSICAL_AGENT, SettlementProportion.Level.DISTRICT_PHYSICAL_AGENT.getValue()));
    }

    public void updatePcdQualify() {
        //更新省、市、区的特定提成比例
        List<PcdQualify> pcdQualifies = getModels(PcdQualify.class);
        Db.batchUpdate(pcdQualifies, 200);
        //更新省、市、区的默认提成比例
        List<SettlementProportion> settlementProportions = getModels(SettlementProportion.class);
        List<SettlementProportion.Proportion> ps = getBeans(SettlementProportion.Proportion.class);
        for (SettlementProportion settlementProportion : settlementProportions) {
            for (SettlementProportion.Proportion p : ps) {
                if (p.getSettlementProportionId() == settlementProportion.getId().intValue()) {
                    settlementProportion.setProportion(p.toString());
                    settlementProportion.update();
                }
            }
        }
        setFlash("message", getRes().get("pcd_qualify.update.success"));
        redirect("/agent/editPcdQualify");
    }

    public void ajaxGetPhysicalAgentBonus() {
        Integer pcdId = getParaToInt();
        renderJson(PhysicalAgentBonus.dao.findByPcdId(pcdId));
    }

    public void editPhysicalAgentBonus() {

    }

    public void updatePhysicalAgentBonus() {
        Integer pcdId = getParaToInt();
        PhysicalAgentBonus.dao.deleteByField(PhysicalAgentBonus.Fields.PCD_ID.toString(), pcdId);
        List<PhysicalAgentBonus> bonuses = getModels(PhysicalAgentBonus.class);
        for (PhysicalAgentBonus physicalAgentBonus : bonuses) {
            physicalAgentBonus.setPcdId(pcdId);
        }
        Db.batchSave(bonuses, 200);
        redirect("/agent/editPhysicalAgentBonus");
    }
}
