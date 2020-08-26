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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.config.model.Config;
import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.eventlog.service.EventLogService;
import com.jfeat.flash.Flash;
import com.jfeat.identity.model.User;
import com.jfeat.partner.constants.EventLogName;
import com.jfeat.partner.model.*;
import com.jfeat.partner.service.MerchantService;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by jingfei on 2016/3/25.
 */
public class SettingsController extends BaseController {

    private MerchantService merchantService = Enhancer.enhance(MerchantService.class);
    private PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);
    private EventLogService eventLogService = Enhancer.enhance(EventLogService.class);

    private static final String PARTNER_LEVEL_ZONE_PREFIX = "product.partner_level_zone_";
    private static final Integer[] PARTNER_LEVEL_ZONE_LIST = { 1, 2, 3 };

    private List<Map<String, String>> getPartnerLevelZones() {
        List<Map<String, String>> zones = Lists.newArrayList();
        for (Integer i : PARTNER_LEVEL_ZONE_LIST) {
            Config config = Config.dao.findByKey(PARTNER_LEVEL_ZONE_PREFIX + i);
            String name = "Zone" + i;
            if (config != null) {
                name = config.getValueToStr();
            }
            Map<String, String> zone = Maps.newHashMap();
            zone.put("key", String.valueOf(i));
            zone.put("name", name);
            zones.add(zone);
        }
        return zones;
    }


    @Override
    @RequiresPermissions(value = { "CooperativePartnerApplication.view", "settlement.settings.menu" }, logical = Logical.OR)
    @Before(Flash.class)
    public void index() {
        MerchantOptions merchantOptions = MerchantOptions.dao.getDefault();
        setAttr("merchantOptions", merchantOptions);

        List<SettlementProportion> sellerSettlementProportions = merchantService.findBySellerType();
        setAttr("sellerSettlementProportionsVisible", sellerSettlementProportions.stream().anyMatch(item -> item.getVisible() == 1));
        setAttr("sellerSettlementProportions", sellerSettlementProportions);
        setAttr("settlementTypes", SettlementProportion.SettlementType.values());
        setAttr("sellerSettlementType", sellerSettlementProportions.get(0).getProportionObject().getSettlementtype());

        List<SettlementProportion> agentSettlementProportions = merchantService.findByAgentType();
        setAttr("agentSettlementProportionsVisible", agentSettlementProportions.stream().anyMatch(item -> item.getVisible() == 1));
        setAttr("agentSettlementProportions", agentSettlementProportions);

        List<SettlementProportion> physicalAgentSettlementProportions = merchantService.findByPhysicalAgentType();
        setAttr("physicalAgentSettlementProportionsVisible", physicalAgentSettlementProportions.stream().anyMatch(item -> item.getVisible() == 1));
        setAttr("physicalAgentSettlementProportions", physicalAgentSettlementProportions);

        List<SettlementProportion> partnerSettlementProportions = merchantService.findByPartnerType();
        setAttr("partnerSettlementProportionsVisible", partnerSettlementProportions.stream().anyMatch(item -> item.getVisible() == 1));
        setAttr("partnerSettlementProportions", partnerSettlementProportions);

        SettlementProportion platformSettlementProportions = SettlementProportion.dao.findByPlatform();
        setAttr("platformSettlementProportionsVisible", platformSettlementProportions.getVisible() == 1);
        setAttr("platformSettlementProportions", platformSettlementProportions);

        SettlementProportion selfSettlementProportions = SettlementProportion.dao.findBySelf();
        setAttr("selfSettlementProportionsVisible", selfSettlementProportions.getVisible() == 1);
        setAttr("selfSettlementProportions", selfSettlementProportions);

        SettlementProportion crownSettlementProportions = SettlementProportion.dao.findByCrown();
        setAttr("crownSettlementProportionsVisible", crownSettlementProportions.getVisible() == 1);
        setAttr("crownSettlementProportions", crownSettlementProportions);

        setAttr("partnerLevels", PartnerLevel.dao.findAll());

        PhysicalSettlementDefinition physicalSettlementDefinition = PhysicalSettlementDefinition.dao.getDefault();
        setAttr("physicalSettlementDefinition", physicalSettlementDefinition);

        List<PhysicalSettlementProportion> physicalSettlementProportions = PhysicalSettlementProportion.dao.findAll();
        setAttr("physicalSettlementProportions", physicalSettlementProportions);
        List<SettlementProportion> physicalCrownSettlementProportions = SettlementProportion.dao.findByType(SettlementProportion.Type.PHYSICAL_CROWN);
        setAttr("physicalCrownSettlementProportionsVisible", physicalCrownSettlementProportions.stream().anyMatch(item -> item.getVisible() == 1));
        setAttr("physicalCrownSettlementProportions", physicalCrownSettlementProportions);

        setAttr("partnerLevelZones", getPartnerLevelZones());

        SettlementProportion copartnerSettlementProportions = SettlementProportion.dao.findByCopartner();
        setAttr("copartnerSettlementProportionsVisible", copartnerSettlementProportions.getVisible() == 1);
        setAttr("copartnerSettlementProportions", copartnerSettlementProportions);

        keepPara();
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void sellerSave() {
        String sellerSettlementType = getPara("sellerSettlementType", SettlementProportion.SettlementType.PROFIT.toString());
        MerchantOptions merchantOptions = getModel(MerchantOptions.class);
        List<SettlementProportion> settlementProportions = getModels(SettlementProportion.class);
        List<SettlementProportion.Proportion> proportionObjectList = getBeans(SettlementProportion.Proportion.class);
        for (SettlementProportion proportion : settlementProportions) {
            for (SettlementProportion.Proportion p : proportionObjectList) {
                p.setSettlementtype(sellerSettlementType);
                if (p.getSettlementProportionId() == proportion.getId()) {
                    proportion.setProportion(p.toString());
                    break;
                }
            }
        }

        Ret ret = merchantService.updateMerchantOptions(merchantOptions, settlementProportions);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.setting.success"));

            Map<String, Object> data = new HashMap<>();
            data.put("merchantOptions", merchantOptions);
            data.put("settlementProportions", settlementProportions);
            recordEvent(data);
        }
        redirect("/settings#seller");
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void agentSave() {
        List<SettlementProportion> settlementProportions = getModels(SettlementProportion.class);
        List<SettlementProportion.Proportion> proportionObjectList = getBeans(SettlementProportion.Proportion.class);
        for (SettlementProportion proportion : settlementProportions) {
            for (SettlementProportion.Proportion p : proportionObjectList) {
                if (p.getSettlementProportionId() == proportion.getId()) {
                    proportion.setProportion(p.toString());
                    break;
                }
            }
        }
        Ret ret = merchantService.updateSettlementProportions(settlementProportions);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.setting.success"));

            Map<String, Object> data = new HashMap<>();
            data.put("settlementProportions", settlementProportions);
            recordEvent(data);
        }

        redirect("/settings#agent");
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void physicalAgentSave() {
        List<SettlementProportion> settlementProportions = getModels(SettlementProportion.class);
        List<SettlementProportion.Proportion> proportionObjectList = getBeans(SettlementProportion.Proportion.class);
        for (SettlementProportion proportion : settlementProportions) {
            for (SettlementProportion.Proportion p : proportionObjectList) {
                if (p.getSettlementProportionId() == proportion.getId()) {
                    proportion.setProportion(p.toString());
                    break;
                }
            }
        }
        Ret ret = merchantService.updateSettlementProportions(settlementProportions);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("physical_partner.setting.success"));

            Map<String, Object> data = new HashMap<>();
            data.put("settlementProportions", settlementProportions);
            recordEvent(data);
        }

        redirect("/settings#physical_agent");
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void partnerLevelSave() {
        List<PartnerLevel> partnerLevels = getModels(PartnerLevel.class);
        Db.batchUpdate(partnerLevels, 10);
        redirect("/settings#partner");
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void partnerSave() {
        List<SettlementProportion> settlementProportions = getModels(SettlementProportion.class);
        for (SettlementProportion proportion : settlementProportions) {
            String paraNamePrefix = "proportion[" + proportion.getId() + "].";
            SettlementProportion.Proportion p = new SettlementProportion.Proportion();
            p.setFixedvalue(getParaToBoolean(paraNamePrefix + "fixedvalue"));
            p.setValue(1, Double.parseDouble(getPara(paraNamePrefix + "1")));
            p.setValue(2, Double.parseDouble(getPara(paraNamePrefix + "2")));
            p.setValue(3, Double.parseDouble(getPara(paraNamePrefix + "3")));
            proportion.setProportion(p.toString());
        }
        Ret ret = merchantService.updateSettlementProportions(settlementProportions);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.setting.success"));

            Map<String, Object> data = new HashMap<>();
            data.put("settlementProportions", settlementProportions);
            recordEvent(data);
        }

        redirect("/settings#partner");
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void platformSave() {
        SettlementProportion settlementProportion = getModel(SettlementProportion.class);
        SettlementProportion.Proportion proportion = getBean(SettlementProportion.Proportion.class);
        settlementProportion.setProportion(proportion.toString());
        Ret ret = merchantService.updateSettlementProportions(settlementProportion);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.setting.success"));

            Map<String, Object> data = new HashMap<>();
            data.put("settlementProportions", settlementProportion);
            recordEvent(data);
        }

        redirect("/settings#platform");
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void copartnerSave() {
        SettlementProportion settlementProportion = getModel(SettlementProportion.class);
        SettlementProportion.Proportion proportion = getBean(SettlementProportion.Proportion.class);
        settlementProportion.setProportion(proportion.toString());
        Ret ret = merchantService.updateSettlementProportions(settlementProportion);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.setting.success"));

            Map<String, Object> data = new HashMap<>();
            data.put("settlementProportions", settlementProportion);
            recordEvent(data);
        }

        redirect("/settings#copartner");
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void crownSave() {
        SettlementProportion settlementProportion = getModel(SettlementProportion.class);
        String paraNamePrefix = "proportion.";
        SettlementProportion.Proportion p = new SettlementProportion.Proportion();
        p.setFixedvalue(getParaToBoolean(paraNamePrefix + "fixedvalue"));
        p.setValue(1, Double.parseDouble(getPara(paraNamePrefix + "1")));
        p.setValue(2, Double.parseDouble(getPara(paraNamePrefix + "2")));
        p.setValue(3, Double.parseDouble(getPara(paraNamePrefix + "3")));
        settlementProportion.setProportion(p.toString());
        Ret ret = merchantService.updateSettlementProportions(settlementProportion);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("crown.setting.success"));

            Map<String, Object> data = new HashMap<>();
            data.put("settlementProportions", settlementProportion);
            recordEvent(data);
        }

        redirect("/settings#crown");
    }

    @RequiresPermissions("CooperativePartnerApplication.edit")
    @Before(Tx.class)
    public void physicalSave() {
        PhysicalSettlementDefinition physicalSettlementDefinition = getModel(PhysicalSettlementDefinition.class);
        physicalSettlementDefinition.update();
        List<PhysicalSettlementProportion> physicalSettlementProportions = getModels(PhysicalSettlementProportion.class);
        Ret ret = physicalSellerService.updatePhysicalSettlementProportions(physicalSettlementProportions);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("physical.setting.success"));

            Map<String, Object> data = new HashMap<>();
            data.put("settlementProportions", physicalSettlementProportions);
            recordEvent(data);
        }

        List<SettlementProportion> settlementProportions = getModels(SettlementProportion.class);
        for (SettlementProportion proportion : settlementProportions) {
            String paraNamePrefix = "proportion[" + proportion.getId() + "].";
            SettlementProportion.Proportion p = new SettlementProportion.Proportion();
            p.setFixedvalue(getParaToBoolean(paraNamePrefix + "fixedvalue"));
            if(StrKit.notBlank(getPara(paraNamePrefix + "value"))){
                p.setValue(Double.parseDouble(getPara(paraNamePrefix + "value")));
            }
            proportion.setProportion(p.toString());
        }
        merchantService.updateSettlementProportions(settlementProportions);
        redirect("/settings#physical");
    }

    private void recordEvent(Map<String, Object> data) {
        String eventType = getRes().get(EventLogName.COOPERATIVE_PARTNER_EVENT_TYPE_KEY);
        String eventName = getRes().get(EventLogName.COOPERATIVE_PARTNER_SETTING_UPDATE_KEY);
        User currentUser = getAttr("currentUser");
        String ip = getRequest().getRemoteAddr();
        String userAgent = getRequest().getHeader("User-Agent");
        eventLogService.record(eventType, eventName, currentUser.getName(), ip, userAgent, data);
    }
}
