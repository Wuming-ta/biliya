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

import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.partner.model.Apply;
import com.jfeat.partner.model.MerchantOptions;
import com.jfeat.partner.model.PartnerLevel;
import com.jfeat.partner.model.Seller;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/3/12.
 */
public class SellerService extends BaseService {

    private static final String SELLER_AUTO_SELLERSHIP = "mall.seller_auto_sellership";
    private static final String SELLER_APPLY_AUTO_APPROVE_KEY = "mall.seller_apply_auto_approve";
    private static final String SELLER_AUTO_PROMOTE_TO_PARTNER = "mall.seller_auto_promote_to_partner";

    public int queryMaxLevel() {
        return MerchantOptions.dao.getDefault().getMaxLevel();
    }

    /**
     * 返回某分销商往上的第N级分销商
     *
     * @param seller
     * @param level
     * @return the seller if found. NULL if not found.
     */
    public Seller queryParentSeller(Seller seller, int level) {
        int maxLevel = queryMaxLevel();
        if (level <= 0 || level > maxLevel) {
            throw new RuntimeException("level must be 1 to maxLevel");
        }
        int i = 1;
        Seller parent = seller.getParent();
        while (i < level && parent != null) {
            i++;
            parent = parent.getParent();
        }
        return parent;
    }

    /**
     * 判断两个分销商是否绝对的上下级关系, 不受N级分销影响。
     *
     * @param parentId
     * @param childId
     * @return
     */
    public boolean isAbsoluteChild(int parentId, int childId) {
        Seller child = Seller.dao.findById(childId);
        Seller parent = child.getParent();
        while (parent != null && parent.getId() != parentId) {
            parent = parent.getParent();
        }
        return parent != null;
    }

    /**
     * 判断两个分销商是否有级联关系(满足N级分销基础)
     *
     * @param parentId
     * @param childId
     * @return
     */
    public boolean isChild(int parentId, int childId) {
        int maxLevel = queryMaxLevel();
        int i = 1;
        Seller child = Seller.dao.findById(childId);
        Seller parent = child.getParent();
        while (i <= maxLevel && parent != null) {
            if (parent.getId() == parentId) {
                return true;
            }
            parent = parent.getParent();
            i++;
        }
        return false;
    }

    /**
     * 查询某分销商下面N级分销商人数
     * Integer maxLevel = ret.get("max_level");
     * List<Integer> levels = ret.get("levels");
     *
     * @param sellerId
     * @return Ret对象，里面包含两个keys,
     * "max_level" - Integer,最大级数
     * "levels" - List<Integer>, 各级人数的list
     */
    public Ret queryLevelCount(int sellerId) {
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null) {
            logger.debug("seller is null.");
            return failure();
        }
        int maxLevel = queryMaxLevel();
        List<Integer> levels = new ArrayList<>(maxLevel);
        for (int i = 0; i < maxLevel; i++) {
            levels.add(0);
        }
        calcLevelCount(seller, 1, levels);
        Ret ret = success();
        ret.put("max_level", maxLevel);
        ret.put("levels", levels);
        return ret;
    }

    private void calcLevelCount(Seller seller, int level, List<Integer> levels) {
        if (level > levels.size()) {
            return;
        }
        List<Seller> children = seller.getChildren();
        int count = children.size();
        int index = level - 1;
        Integer levelCount = levels.get(index);
        levels.set(index, levelCount + count);
        for (Seller child : children) {
            calcLevelCount(child, level + 1, levels);
        }
    }

    /**
     * 创建分销商。
     * 如果父结点是合伙人，则把合伙人设为父结点，否则合伙人设为和父结点的一样。
     *
     * @param seller
     * @param parent
     * @return
     */
    @Before({Tx.class})
    public Ret createSeller(Seller seller, Seller parent) {
        if (seller.getUserId() == null) {
            return this.failure();
        }

        seller.save();

        Config config = Config.dao.findByKey(SELLER_AUTO_SELLERSHIP);
        if (config != null && config.getValueToBoolean()) {
            seller.setSellerShip(Seller.SellerShip.YES.getValue());
            seller.setSellerShipTime(new Date());
            seller.update();
            return maintainSellerAncestor(seller, parent);
        }

        return this.success();
    }

    /**
     * 如果新建seller时没有建立上下级关系,那么还有机会添加上下级关系。一旦添加后就不能修改了。
     *
     * @param seller
     * @param parent
     * @return
     */
    public Ret maintainSellerAncestor(Seller seller, Seller parent) {
        if (seller.getParent() != null) {
            return failure("already.has.ancestor");
        }
        if (parent == null) {
            return success("don't.need.update");
        }

        seller.setParentId(parent.getId());
        seller.setLevel(parent.getLevel() + 1);
        if (parent.isPartnerShip()) {
            seller.setPartnerId(parent.getId());
            logger.debug("update seller's partner to {}", parent);
        } else {
            seller.setPartnerId(parent.getPartnerId());
            logger.debug("update seller's partner to {}", parent.getPartnerId());
        }

        if (parent.isCrownShip()) {
            seller.setCrownId(parent.getId());
            logger.info("update seller's crown to {}", parent);
        } else {
            Seller theParent = parent;
            Integer crownId = null;
            if (theParent.getCrownId() != null) {
                crownId = theParent.getCrownId();
            } else {
                while (theParent != null && !theParent.isCrownShip()) {
                    theParent = theParent.getParent();
                }
                if (theParent != null && theParent.isCrownShip()) {
                    crownId = theParent.getId();
                }
            }
            seller.setCrownId(crownId);
            logger.info("update seller's crown to {}", crownId);
        }

        seller.update();

        //maintain ancestor relation ship
        int maxLevel = queryMaxLevel();
        int level = 1;
        Seller theParent = seller.getParent();
        while (theParent != null && level <= maxLevel) {
            seller.addAncestor(theParent.getId(), level);
            level++;
            theParent = theParent.getParent();
        }

        return success();
    }

    /**
     * 申请成为分销商,如果设置了自动审核,则马上设置分销商角色
     *
     * @param userId
     * @return
     */
    public Ret applySellerShip(int userId) {
        Seller seller = Seller.dao.findByUserId(userId);
        if (seller == null) {
            return failure("invalid.seller");
        }
        if (seller.isSellerShip()) {
            return failure("already.seller");
        }

        Apply apply = Apply.dao.findByUserIdType(userId, Apply.Type.SELLER.toString());
        if (apply != null
                && apply.getStatus().equals(Apply.Status.INIT.toString())) {
            return failure("apply.already.exist");
        }
        Apply newApply = new Apply();
        newApply.setUserId(userId);
        newApply.setType(Apply.Type.SELLER.toString());
        newApply.setStatus(Apply.Status.INIT.toString());
        newApply.setApplyDate(new Date());
        newApply.save();
        Config config = Config.dao.findByKey(SELLER_APPLY_AUTO_APPROVE_KEY);
        if (config != null) {
            if (config.getValueToBoolean()) {
                approveSellerApply(newApply.getId());
            }
        }
        return success();
    }

    public Ret rejectSellerApply(int applyId) {
        Apply apply = Apply.dao.findById(applyId);
        String status = apply.getStatus();
        String type = apply.getType();
        if (status.equals(Apply.Status.INIT.toString()) && type.equals(Apply.Type.SELLER.toString())) {
            apply.setRejectDate(new Date());
            apply.setStatus(Apply.Status.REJECT.toString());
            apply.update();
            return success("partner.seller.reject.success");
        }

        return failure("partner.seller.reject.failure");
    }

    public Ret approveSellerApply(int applyId) {
        Apply apply = Apply.dao.findById(applyId);
        String status = apply.getStatus();
        String type = apply.getType();
        if (status.equals(Apply.Status.INIT.toString()) && type.equals(Apply.Type.SELLER.toString())) {

            // 1. approve
            apply.setApproveDate(new Date());

            // 2. sellership
            Seller seller = Seller.dao.findByUserId(apply.getUserId());
            seller.setSellerShip(Seller.SellerShip.YES.getValue());
            seller.setSellerShipTime(new Date());
            seller.update();

            // 3. maintain ancestor
            logger.debug("going to maintain the ancestor relation ship for seller {}", seller);
            SellerService service = new SellerService();
            Seller parentSeller = Seller.dao.findByUserId(seller.getUser().getInviterId());
            Ret ret = service.maintainSellerAncestor(seller, parentSeller);

            logger.debug("seller maintain result: {}", ret.getData());
            logger.debug("seller is : {}", seller);

            // 4. promote
            if (BaseService.isSucceed(ret)) {
                ret = service.promotePartnerLevel(seller.getPartner());
                logger.debug("partner promote result: {}", ret.getData());
                service.promoteParentSellerToPartner(seller);
            }

            apply.setStatus(Apply.Status.APPROVE.toString());
            apply.update();
            return success("partner.seller.approve.success");
        }

        return failure("partner.seller.approve.failure");
    }

    public boolean resetSellerRight(int sellerId) {
        Seller seller = Seller.dao.findById(sellerId);
        seller.setSellerShip(Seller.SellerShip.NO.getValue());
        return seller.update();
    }

    public boolean assignSellerRight(int sellerId) {
        Seller seller = Seller.dao.findById(sellerId);
        seller.setSellerShip(Seller.SellerShip.YES.getValue());
        seller.setSellerShipTime(new Date());
        return seller.update();
    }

    /**
     * exclude the 6th level
     *
     * @param currentLevel
     * @return
     */
    public PartnerLevel getNextPartnerLevel(int currentLevel) {
        List<PartnerLevel> levels = PartnerLevel.dao.findAllOrderByDesc(PartnerLevel.Fields.LEVEL.toString());
        PartnerLevel level = PartnerLevel.dao.findByLevel(currentLevel + 1);
        if (level != null && levels.size() > 0 && levels.get(0).getLevel().equals(level.getLevel())) {
            return null;
        }
        return level;
    }

    /**
     * 回收某分销商的合伙人权限
     *
     * @param sellerId
     * @return
     */
    @Before(Tx.class)
    public Ret resetPartnerRight(int sellerId) {
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null) {
            return failure("invalid.seller_id");
        }
        if (!seller.isPartnerShip()) {
            return failure("seller.is.not.a.partner");
        }
        seller.setPartnerShip(Seller.PartnerShip.NO.getValue());
        seller.update();
        List<Seller> list = Seller.dao.findByPartnerId(sellerId);
        for (Seller s : list) {
            s.setPartnerId(null);
        }
        Db.batchUpdate(list, 100);
        return success();
    }

    /**
     * 提高合伙人的等级
     *
     * @param sellerId
     * @param level
     * @return
     */
    public boolean updatePartnerLevel(int sellerId, int level) {
        PartnerLevel partnerLevel = PartnerLevel.dao.findByLevel(level);
        if (partnerLevel == null) {
            return false;
        }
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null || !seller.isPartnerShip()) {
            return false;
        }
        seller.setPartnerLevelId(partnerLevel.getId());
        return seller.update();
    }

    /**
     * 设置某分销商为合伙人
     *
     * @param sellerId
     * @return
     */
    @Before(Tx.class)
    public Ret assignPartnerRight(int sellerId) {
        Seller seller = Seller.dao.findById(sellerId);
        if (seller.isPartnerShip()) {
            return failure("seller.is.already.a.partner");
        }

        PartnerLevel partnerLevel = PartnerLevel.dao.getDefault();

        if (!seller.isSellerShip()) {
            seller.setSellerShip(Seller.SellerShip.YES.getValue());
            seller.setSellerShipTime(new Date());
        }
        seller.setPartnerShip(Seller.PartnerShip.YES.getValue());
        seller.setPartnerShipTime(new Date());
        seller.setPartnerLevelId(partnerLevel.getId());
        seller.update();
        return success();
    }

    /**
     * 设置某销售商为皇冠资格
     *
     * @param sellerId
     * @return
     */
    public Ret assignCrownRight(int sellerId) {
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null) {
            return failure("seller.not.found");
        }
        if (seller.isCrownShip()) {
            return failure("seller.is.already.crown");
        }
        seller.setCrownShip(Seller.CrownShip.YES.getValue());
        seller.setCrownShipTime(new Date());
        seller.update();

        return success();
    }

    /**
     * 取消皇冠资格
     *
     * @param sellerId
     * @return
     */
    public Ret resetCrownRight(int sellerId) {
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null) {
            return failure("seller.not.found");
        }
        if (!seller.isCrownShip()) {
            return failure("seller.is.not.crown");
        }
        seller.setCrownShip(Seller.CrownShip.NO.getValue());
        seller.update();
        seller.resetCrown(sellerId);
        return success();
    }

    /**
     * 当人数达到限制后,自动提升合伙人级别。第6级为特殊级别,不能自动跳到, 只能跳到第5级。
     *
     * @param partner
     * @return
     */
    public Ret promotePartnerLevel(Seller partner) {
        if (partner == null) {
            return failure("partner.is.null");
        }

        long count = partner.getPartnerPoolCount();
        PartnerLevel currentPartnerLevel = PartnerLevel.dao.findById(partner.getPartnerLevelId());
        List<PartnerLevel> partnerLevels = PartnerLevel.dao.findAllOrderByDesc(PartnerLevel.Fields.LEVEL.toString());
        //六星不在升级体系内, 直接忽略。直接处理5->2星
        for (int i = 1; i < partnerLevels.size() - 1; i++) {
            PartnerLevel partnerLevel = partnerLevels.get(i);
            if (count >= partnerLevel.getHeadcountQuota() && currentPartnerLevel.getLevel() < partnerLevel.getLevel()) {
                partner.setPartnerLevelId(partnerLevel.getId());
                partner.update();
                logger.info("partner pool count is {}, promote to next level {}", count, partnerLevel);
                break;
            }
        }
        return success();
    }

    /**
     * 当seller加入时, 检查他的父节点是否满足升级到合伙人要求,满足就提升
     *
     * @param seller
     * @return
     */
    public Ret promoteParentSellerToPartner(Seller seller) {
        int maxLevel = queryMaxLevel();
        int level = 1;
        Seller theParent = seller.getParent();
        while (theParent != null && level <= maxLevel) {
            Ret ret = promoteSellerToPartner(theParent);
            logger.debug("{} promote result {}", theParent, ret.getData());
            level++;
            theParent = theParent.getParent();
        }
        return success();
    }

    /**
     * 提升Seller to Partner, 根据下面两级分销商人数来决定是否达到升级条件(无论maxLevel配的是多少)
     *
     * @param seller
     * @return
     */
    private Ret promoteSellerToPartner(Seller seller) {
        if (seller == null) {
            return failure("seller.is.null");
        }
        if (!seller.isSellerShip()) {
            return failure("not.a.seller");
        }
        if (seller.isPartnerShip()) {
            return failure("seller.is.already.partner");
        }
        PartnerLevel firstLevel = PartnerLevel.dao.getDefault();
        long count = seller.getTwoLevelsChildrenCount();
        Config config = Config.dao.findByKey(SELLER_AUTO_PROMOTE_TO_PARTNER);
        boolean autoPromoted = false;
        if (config != null) {
            autoPromoted = config.getValueToBoolean();
        }
        if (count >= firstLevel.getHeadcountQuota() && autoPromoted) {
            return assignPartnerRight(seller.getId());
        }
        return success();
    }

}