package com.jfeat.partner.service;

import com.jfeat.core.BaseService;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.handler.AllianceShipExpiredHandler;
import com.jfeat.partner.model.Alliance;
import com.jfinal.kit.Ret;

import java.util.Date;

/**
 * @author jackyhuang
 * @date 2019/10/9
 */
public class AllianceService extends BaseService {

    /**
     * 设置用户成为正式盟友
     * @param userId
     * @return
     */
    public Ret assignAllianceShip(Integer userId) {

        Alliance alliance = Alliance.dao.findByUserId(userId);
        if (alliance == null) {
            alliance = new Alliance();
            alliance.setUserId(userId);
            alliance.save();
        }
        if (alliance.getAllianceShip() == Alliance.AllianceShip.REGULAR.getValue()) {
            return failure("user.is.already.regular.alliance");
        }

        alliance.setAllianceShip(Alliance.AllianceShip.REGULAR.getValue());
        alliance.update();

        return success();
    }

    /**
     * 设置用户成为股东，如果不是盟友，同时把他设为盟友。
     * @param userId
     * @return
     */
    public Ret assignStockholderShip(Integer userId) {
        Alliance alliance = Alliance.dao.findByUserId(userId);
        if (alliance == null) {
            alliance = new Alliance();
            alliance.setUserId(userId);
            alliance.save();
        }
        if (alliance.getStockholderShip() == Alliance.StockholderShip.YES.getValue()) {
            return failure("user.is.already.stockholder");
        }

        alliance.setAllianceShip(Alliance.AllianceShip.REGULAR.getValue());
        alliance.setAllianceShipTime(new Date());
        alliance.setStockholderShip(Alliance.StockholderShip.YES.getValue());
        alliance.setStockholderShipTime(new Date());
        alliance.update();

        return success();
    }

    /**
     * 邀请用户成为盟友
     * @param userId 临时盟友的用户ID
     * @param invitorUserId 邀请人的用户ID
     * @return
     */
    public Ret assignTempAllianceShip(Integer userId, Integer invitorUserId) {
        Alliance invitorAlliance = Alliance.dao.findByUserId(invitorUserId);
        if (invitorAlliance == null) {
            return failure("invitor.is.null");
        }
        if (invitorAlliance.getAllianceShip() == Alliance.AllianceShip.NO.getValue()) {
            return failure("invitor.is.not.alliance");
        }
        if (invitorAlliance.getAllianceShip() == Alliance.AllianceShip.TEMP.getValue()) {
            return failure("invitor.is.temp.alliance");
        }

        Alliance alliance = Alliance.dao.findByUserId(userId);
        if (alliance == null) {
            alliance = new Alliance();
            alliance.setUserId(userId);
            alliance.save();
        }
        if (alliance.getAllianceShip() == Alliance.AllianceShip.REGULAR.getValue()) {
            return failure("user.is.already.regular.alliance");
        }
        if (alliance.getAllianceShip() == Alliance.AllianceShip.TEMP.getValue()) {
            return failure("user.is.already.temp.alliance");
        }

        alliance.setAllianceShip(Alliance.AllianceShip.TEMP.getValue());
        alliance.setInvitorAllianceId(invitorAlliance.getId());
        //TODO config the value
        alliance.setTempAllianceExpiryTime(DateKit.daysLater(1));
        alliance.update();

        // create redis expire event.
        try {
            AllianceShipExpiredHandler.add(alliance.getId());
        } catch (Exception e) {
            logger.error("alliance_id: {} ,fail to register ExpiredHandler", alliance.getId());
        }

        return success();
    }

    /**
     * 处理临时盟友超时事件。把盟友关系设为0。取消邀请关系。
     * @param allianceId
     * @return
     */
    public Ret handleAllianceShipExpired(Integer allianceId) {
        Alliance alliance = Alliance.dao.findById(allianceId);
        if (alliance == null) {
            return failure("alliance.not.found");
        }
        if (alliance.getAllianceShip() != Alliance.AllianceShip.TEMP.getValue()) {
            return failure("alliance.is.not.temp");
        }
        alliance.setAllianceShip(Alliance.AllianceShip.NO.getValue());
        alliance.setInvitorAllianceId(null);
        alliance.update();
        return success();
    }
}
