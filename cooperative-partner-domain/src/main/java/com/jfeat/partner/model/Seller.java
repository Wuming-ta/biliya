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

/*
 * This file is automatically generated by tools.
 * It defines the model for the table. All customize operation should 
 * be written here. Such as query/update/delete.
 * The controller calls this object.
 */
package com.jfeat.partner.model;

import com.jfeat.identity.model.User;
import com.jfeat.kit.SqlQuery;
import com.jfeat.partner.model.base.SellerBase;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

@TableBind(tableName = "t_seller")
public class Seller extends SellerBase<Seller> {
    /**
     * 字段说明（避免修改.sql文件）
     * partner_id 上级合伙人的id
     * crown_id 上级皇冠商的id
     * partner_ship 是否是合伙人（星级经销商）
     * partner_level_id 合伙人级别（即此seller是一个1/2/3/4/5/6级的星级经销商）
     */

    /**
     * Only use for query.
     */
    public static Seller dao = new Seller();

    public enum CrownShip {
        YES(1),
        NO(0);
        private int value;

        CrownShip(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum PartnerShip {
        YES(1),
        NO(0);
        private int value;

        PartnerShip(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum SellerShip {
        YES(1),
        NO(0);
        private int value;

        SellerShip(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum CrownShipTemp {
        YES(1),
        NO(0);
        private int value;

        CrownShipTemp(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public boolean isPartnerShip() {
        return getPartnerShip() == PartnerShip.YES.getValue();
    }

    public boolean isCrownShip() {
        return getCrownShip() == CrownShip.YES.getValue();
    }

    public boolean isCrownShipTemp() {
        return CrownShip.YES.getValue() == getCrownShip() && CrownShipTemp.YES.getValue() == getCrownShipTemp();
    }

    public boolean isSellerShip() {
        return getSellerShip() == SellerShip.YES.getValue();
    }

    public boolean isAgent() {
        return Agent.dao.findByUserId(getUserId()) != null;
    }

    public boolean isPhysicalSeller() {
        return PhysicalSeller.dao.findBySellerId(getId()) != null;
    }

    public User getUser() {
        return User.dao.findById(getUserId());
    }

    public Seller getPartner() {
        return findById(getPartnerId());
    }

    public Seller getCrown() {
        return findById(getCrownId());
    }

    public Seller getParent() {
        return findById(getParentId());
    }

    public PartnerLevel getPartnerLevel() {
        return PartnerLevel.dao.findById(getPartnerLevelId());
    }

    /**
     * 返回该分销商的下级分销商列表
     *
     * @return
     */
    public List<Seller> getChildren() {
        String sql = "select * from t_seller s join t_seller_ancestor sa on sa.seller_id=s.id where sa.level=1 and sa.ancestor_id=?";
        return find(sql, getId());
    }

    /**
     * 返回该分销商的下面一级分销商列表
     *
     * @return
     */
    public List<Seller> getVerboseChildren() {
        String sql = "select s.*,u.name as user_name, u.avatar, u.followed, u.follow_time, u.uid, u.grade, u.register_date,  " +
                "(select count(*) from t_agent a where a.user_id=s.user_id) as agent_ship, sa.level as sa_level " +
                "from t_seller_ancestor sa " +
                "join t_seller s on s.id=sa.seller_id " +
                "join t_user u on u.id=s.user_id " +
                "where sa.level=1 and sa.ancestor_id=? " +
                "order by s.id desc ";
        return find(sql, getId());
    }

    /**
     * 返回该分销商下面一级分销商人数
     *
     * @return
     */
    public long getChildrenCount() {
        String sql = "select count(*) from t_seller_ancestor sa where sa.level=1 and sa.ancestor_id=?";
        return Db.queryLong(sql, getId());
    }

    /**
     * 返回下面两级分销商人数, 用于计算分销商升级到经销商的条件。
     *
     * @return
     */
    public long getTwoLevelsChildrenCount() {
        String sql = "select count(*) from t_seller_ancestor sa where sa.ancestor_id=? and (sa.level=1 or sa.level=2)";
        return Db.queryLong(sql, getId());
    }

    /**
     * 该分销商是合伙人，返回他的合伙人池成员总数
     *
     * @return
     */
    public long getPartnerPoolCount() {
        return Db.queryLong("select count(*) from t_seller where partner_id=?", getId());
    }

    /**
     * 该分销商是合伙人，返回他的合伙人池成员
     *
     * @return
     */
    public List<Seller> getPartnerPool() {
        return findByPartnerId(getId());
    }

    /**
     * 该分销商是合伙人，返回他的合伙人池成员
     *
     * @return
     */
    public List<Seller> getVerbosePartnerPool() {
        return findVerboseByPartnerId(getId());
    }

    /**
     * 该分销商是皇冠，返回他的皇冠池成员
     *
     * @return
     */
    public List<Seller> getVerboseCrownPool() {
        return findVerboseByCrownId(getId());
    }

    /**
     * 返回分销商裂变层级总数
     *
     * @return
     */
    public int queryTotalLevel() {
        SqlQuery query = new SqlQuery().select("max(level)").from(getTableName());
        return Db.queryInt(query.sql());
    }

    /**
     * 查询合伙人总数
     *
     * @return
     */
    public long queryPartnerCountTotal() {
        SqlQuery query = new SqlQuery().select("Count(*)").from(getTableName()).where(Fields.PARTNER_SHIP.eq("?"));
        return Db.queryLong(query.sql(), PartnerShip.YES.getValue());
    }

    /**
     * 查询某级别的合伙人总数
     *
     * @param partnerLevelId
     * @return
     */
    public long queryPartnerCountTotalByPartnerLevelId(int partnerLevelId) {
        SqlQuery query = new SqlQuery().select("Count(*)").from(getTableName())
                .where(Fields.PARTNER_SHIP.eq("?")).and(Fields.PARTNER_LEVEL_ID.eq("?"));
        return Db.queryLong(query.sql(), PartnerShip.YES.getValue(), partnerLevelId);
    }

    /**
     * 查询消费者总数
     *
     * @return
     */
    public long queryCustomerCountTotal() {
        return queryCountTotal(SellerShip.NO);
    }

    /**
     * 查询分销商总数
     *
     * @return
     */
    public long querySellerCountTotal() {
        return queryCountTotal(SellerShip.YES);
    }

    private long queryCountTotal(SellerShip sellerShip) {
        SqlQuery query = new SqlQuery().select("Count(*)").from(getTableName()).where(Fields.SELLER_SHIP.eq("?"));
        return Db.queryLong(query.sql(), sellerShip.getValue());
    }

    /**
     * 查询关注了公众号的下级总数
     *
     * @return
     */
    public long queryChildrenCountFollowed(int sellerId) {
        return queryChildrenCount(sellerId, User.INFOLLOW_SUBSCRIBE);
    }

    /**
     * 查询未关注公众号的下级总数
     *
     * @return
     */
    public long queryChildrenCountUnFollowed(int sellerId) {
        return queryChildrenCount(sellerId, User.INFOLLOW_UNSUBSCRIBE);
    }

    /**
     * 只查下面一级的人数
     *
     * @param sellerId
     * @param followed
     * @return
     */
    private long queryChildrenCount(int sellerId, int followed) {
        String sql = "select count(*) from t_seller_ancestor sa " +
                "join t_seller s on s.id=sa.seller_id " +
                "join t_user u on u.id=s.user_id " +
                "where sa.level=1 and u.followed=? and sa.ancestor_id=?";
        return Db.queryLong(sql, followed, sellerId);
    }

    public Seller findPlatformSeller() {
        return findFirst("select s.* from t_seller s join t_platform_seller ps on s.id=ps.seller_id");
    }

    public List<Seller> findByPartnerId(Integer partnerId) {
        if (partnerId == null) {
            return find("select * from t_seller where partner_id is null");
        } else {
            return find("select * from t_seller where partner_id=?", partnerId);
        }
    }

    public List<Seller> findVerboseByPartnerId(Integer partnerId) {
        if (partnerId == null) {
            return find("select s.*,u.name as user_name, u.avatar, u.followed, u.follow_time, u.uid from t_seller s join t_user u on u.id=s.user_id where partner_id is null");
        } else {
            return find("select s.*,u.name as user_name, u.avatar, u.followed, u.follow_time, u.uid from t_seller s join t_user u on u.id=s.user_id where partner_id=?", partnerId);
        }
    }

    public List<Seller> findVerboseByCrownId(Integer crownId) {
        if (crownId == null) {
            return find("select s.*,u.name as user_name, u.avatar, u.followed, u.follow_time, u.uid from t_seller s join t_user u on u.id=s.user_id where crown_id is null");
        } else {
            return find("select s.*,u.name as user_name, u.avatar, u.followed, u.follow_time, u.uid from t_seller s join t_user u on u.id=s.user_id where crown_id=?", crownId);
        }
    }

    public Seller findByUserId(Integer userId) {
        return findFirst("select * from t_seller where user_id=?", userId);
    }

    public Seller findVerboseByUserId(Integer userId) {
        return findFirst("select s.*,u.name as user_name, u.avatar, u.followed, u.follow_time, u.uid from t_seller s join t_user u on u.id=s.user_id where user_id=?", userId);
    }


    public Seller findVerboseById(Integer id) {
        return findFirst("select s.*,u.name as user_name, u.avatar, u.followed, u.follow_time, u.uid from t_seller s join t_user u on u.id=s.user_id where s.id=?", id);
    }

    public List<Seller> findByParentId(Integer parentId) {
        if (parentId == null) {
            return find("select * from t_seller where parent_id is null");
        } else {
            return find("select * from t_seller where parent_id=?", parentId);
        }
    }

//    /**
//     * 分页返回某seller的所有下属分销商, 因为分销商自己拿了一份佣金,所以这里不取最后一级
//     * @param pageNumber
//     * @param pageSize
//     * @param sellerId
//     * @return
//     */
//    public Page<Record> paginateDescendants(int pageNumber, int pageSize, int sellerId) {
//        int maxLevel = MerchantOptions.dao.getDefault().getMaxLevel();
//        String select = "select u.name as user_name, u.avatar, u.followed, u.follow_time, u.uid, seller_id, sa.level as sa_level, s.*," +
//                "(select count(*) from t_agent a where a.user_id=s.user_id) as agent_ship";
//        StringBuilder query = new StringBuilder();
//        query.append("from t_user u join t_seller s on s.user_id=u.id " +
//                "join t_seller_ancestor sa on sa.seller_id=s.id " +
//                "where ancestor_id=? and sa.level<>? order by sa.level");
//        return Db.paginate(pageNumber, pageSize, select, query.toString(), sellerId, maxLevel);
//    }

    public Page<Seller> paginate(int pageNumber, int pageSize, Integer sellerId, String userName, String uid) {
        return paginate(pageNumber, pageSize, sellerId, userName, uid, null);
    }

    public Page<Seller> paginate(int pageNumber, int pageSize, Integer sellerId, String userName, String uid, PartnerShip partnerShip) {
        ArrayList<Object> params = new ArrayList<>();
        StringBuilder select = new StringBuilder("select s.*, u.name, u.followed, u.follow_time, u.uid, " +
                "(select name from t_user where id=parent_id) as parent_name, " +
                "(select name from t_user where id=partner_id) as partner_name, " +
                "(select name from t_user where id=crown_id) as crown_name");
        StringBuilder query = new StringBuilder();
        query.append("from t_seller s join t_user u on s.user_id=u.id");
        String cond = " where ";
        if (partnerShip != null) {
            query.append(cond);
            query.append(Fields.PARTNER_SHIP.eq("?"));
            params.add(partnerShip.getValue());
            cond = " and ";
        }

        if (sellerId != null || StrKit.notBlank(userName)) {
            query.append(cond);
            query.append(" ( ");
            cond = "";
        }
        if (sellerId != null) {
            query.append(cond);
            query.append(" s.id=? ");
            params.add(sellerId);
            cond = " or ";
        }
        if (StrKit.notBlank(userName)) {
            query.append(cond);
            query.append(" u.name like ? ");
            params.add("%" + userName + "%");
        }

        if (sellerId != null || StrKit.notBlank(userName)) {
            query.append(" )  ");
            cond = " and ";
        }

        if (StrKit.notBlank(uid)) {
            query.append(cond);
            query.append(" u.uid=? ");
            params.add(uid);
        }

        return this.paginate(pageNumber, pageSize, select.toString(), query.toString(), params.toArray());
    }

    public Page<Seller> paginatePartner(int pageNumber, int pageSize, Integer sellerId, String userName, String uid) {
        List<Object> params = new ArrayList<>();
        String select = "select s.*, u.name as user_name, u.followed, u.follow_time, u.uid, pl.name as partner_level_name ";
        StringBuilder query = new StringBuilder();
        query.append("from t_seller s join t_user u on u.id=s.user_id join t_partner_level pl on pl.id=s.partner_level_id ");
        query.append("where s.partner_ship=? ");
        params.add(PartnerShip.YES.getValue());
        String cond = " and ";
        if (sellerId != null || StrKit.notBlank(userName)) {
            query.append(cond);
            query.append(" ( ");
            cond = "";
        }
        if (sellerId != null) {
            query.append(cond);
            query.append(" s.id=? ");
            params.add(sellerId);
            cond = " or ";
        }
        if (StrKit.notBlank(userName)) {
            query.append(cond);
            query.append(" u.name like ? ");
            params.add("%" + userName + "%");
        }

        if (sellerId != null || StrKit.notBlank(userName)) {
            query.append(" )  ");
            cond = " and ";
        }

        if (StrKit.notBlank(uid)) {
            query.append(cond);
            query.append(" u.uid=? ");
            params.add(uid);
        }

        return paginate(pageNumber, pageSize, select, query.toString(), params.toArray());
    }

    public List<Seller> findAllCrown(Integer sellerId, String userName, String uid) {
        StringBuilder query = new StringBuilder("select s.*, u.name as user_name from t_seller s" +
                " join t_user u on u.id=s.user_id where s.crown_ship=?");
        List<Object> params = new ArrayList<>();
        params.add(CrownShip.YES.getValue());
        String cond = " and ";
        if (sellerId != null || StrKit.notBlank(userName)) {
            query.append(cond);
            query.append(" ( ");
            cond = "";
        }
        if (sellerId != null) {
            query.append(cond);
            query.append(" s.id=? ");
            params.add(sellerId);
            cond = " or ";
        }
        if (StrKit.notBlank(userName)) {
            query.append(cond);
            query.append(" u.name like ? ");
            params.add("%" + userName + "%");
        }

        if (sellerId != null || StrKit.notBlank(userName)) {
            query.append(" )  ");
            cond = " and ";
        }

        if (StrKit.notBlank(uid)) {
            query.append(cond);
            query.append(" u.uid=? ");
            params.add(uid);
        }
        return find(query.toString(), params.toArray());
    }


    /**
     * maintain the ancestor relationship when seller is created.
     *
     * @param ancestorId
     * @param level
     * @return
     */
    public boolean addAncestor(int ancestorId, int level) {
        Record record = new Record();
        record.set("seller_id", getId());
        record.set("ancestor_id", ancestorId);
        record.set("level", level);
        return Db.save("t_seller_ancestor", "seller_id,ancestor_id", record);
    }

    /**
     * 取消某人的皇冠资格, 他原来的皇冠池成员也要脱离关系。
     *
     * @param crownId
     * @return
     */
    public boolean resetCrown(int crownId) {
        String sql = "update t_seller set crown_id=null where crown_id=?";
        return Db.update(sql, crownId) > 0;
    }

    public int getCustomerCount(String startTime, String endTime) {
        String sql = "select count(*) from t_seller" +
                " where seller_ship=0 and seller_ship_time between ? and ?";
        return Db.queryNumber(sql, startTime, endTime).intValue();
    }

    public int getSellerCount(String startTime, String endTime) {
        String sql = "select count(*) from t_seller" +
                " where seller_ship=1 and seller_ship_time between ? and ?";
        return Db.queryNumber(sql, startTime, endTime).intValue();
    }

    public int getPartnerCount(String startTime, String endTime) {
        String sql = "select count(*) from t_seller" +
                " where partner_ship=1 and partner_ship_time between ? and ?";
        return Db.queryNumber(sql, startTime, endTime).intValue();
    }

    public int getCrownCount(String startTime, String endTime) {
        String sql = "select count(*) from t_seller" +
                " where crown_ship=1 and crown_ship_time between ? and ?";
        return Db.queryNumber(sql, startTime, endTime).intValue();
    }

    public int increaseCrownApplyFailureTimes() {
        String sql = "update t_seller set crown_apply_failure_times=crown_apply_failure_times+1 where id=?";
        return Db.update(sql, getId());
    }

    public int resetCrownApplyFailureTimes(){
        String sql = "update t_seller set crown_apply_failure_times=0 where id=?";
        return Db.update(sql, getId());
    }
}
