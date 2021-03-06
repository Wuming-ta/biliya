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
package com.jfeat.member.model;

import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.member.model.base.CouponShareBase;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@TableBind(tableName = "t_coupon_share")
public class CouponShare extends CouponShareBase<CouponShare> {

    /**
     * Only use for query.
     */
    public static CouponShare dao = new CouponShare();

    public enum Type {
        ORDER {
            @Override
            public Coupon.Source mapCouponSource() {
                return Coupon.Source.LINK;
            }

            @Override
            public CouponStrategy.Type mapCouponStrategyType() {
                return CouponStrategy.Type.SHARE_LINK;
            }
        },
        SYSTEM {
            @Override
            public Coupon.Source mapCouponSource() {
                return Coupon.Source.SYSTEM;
            }

            @Override
            public CouponStrategy.Type mapCouponStrategyType() {
                return CouponStrategy.Type.SYSTEM;
            }
        };

        public abstract Coupon.Source mapCouponSource();
        public abstract CouponStrategy.Type mapCouponStrategyType();
    }

    public List<CouponTakenRecord> getCouponTakenRecord() {
        return CouponTakenRecord.dao.findByShareId(getId());
    }

    public User getUser() {
        return User.dao.findById(getUserId());
    }

    public boolean save() {
        setCode(UUID.randomUUID().toString());
        setShareDate(new Date());
        return super.save();
    }

    /**
     * ?????????????????????????????????????????????
     * @param beginDate
     * @param userId
     * @return
     */
    public int countSharedOrder(int userId, String beginDate) {
        return Db.queryNumber("select count(*) from t_coupon_share where user_id=? and share_date>?", userId, beginDate).intValue();
    }

    public List<CouponShare> findValidByUserId(int userId) {
        return find("select * from t_coupon_share where user_id=? and valid_date>?", userId, DateKit.today("yyyy-MM-dd HH:mm:ss"));
    }

    public List<CouponShare> findByType(Type type) {
        return find("select * from t_coupon_share where type=? order by id desc", type.toString());
    }

    public CouponShare findFirstByCode(String code) {
        return findFirstByFields(Fields.CODE, code);
    }

    public CouponShare findFirstByOrderNumber(String orderNumber) {
        return findFirstByFields(Fields.ORDER_NUMBER, orderNumber);
    }

    private CouponShare findFirstByFields(Fields fields, Object fieldValue) {
        String sql = "select * from t_coupon_share where " + fields.eq("?");
        return findFirst(sql, fieldValue);
    }
}
