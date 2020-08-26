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
package com.jfeat.product.model;

import com.jfeat.product.model.base.FareTemplateBase;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;

import java.util.Date;
import java.util.List;

@TableBind(tableName = "t_fare_template")
public class FareTemplate extends FareTemplateBase<FareTemplate> {

    public enum ValuationModel {
        PIECE(0),
        WEIGHT(1),
        BULK(2);

        private int value;
        ValuationModel(int model) {
            this.value = model;
        }
        public int getValue() {
            return this.value;
        }
    }

    public enum InclPostage {
        YES(1),
        NO(0);

        private int value;
        InclPostage(int value) {
            this.value = value;
        }
        public int getValue() {
            return this.value;
        }
    }

    public enum InclPostageByIf {
        YES(1),
        NO(0);

        private int value;
        InclPostageByIf(int value) {
            this.value = value;
        }
        public int getValue() {
            return this.value;
        }
    }

    /**
     * Only use for query.
     */
    public static FareTemplate dao = new FareTemplate();

    public boolean isInUsed() {
        return Db.queryLong("select count(*) from t_product where fare_id=?", getId()) > 0;
    }

    /**
     * 返回某运送方式的默认方式
     * @param carryWay
     * @return
     */
    public CarryMode findDefaultCarryMode(CarryMode.CarryWay carryWay) {
        return CarryMode.dao.findByFareIdCarryWayDefault(getId(), carryWay);
    }

    /**
     * 返回该模版的某运送方式的所有方式
     * @param carryWay
     * @return
     */
    public List<CarryMode> findCarryModes(CarryMode.CarryWay carryWay) {
        return CarryMode.dao.findByFareIdCarryWay(getId(), carryWay);
    }

    /**
     * 返回该模版的所有运送方式
     * @return
     */
    public List<CarryMode> getCarryModes() {
        return CarryMode.dao.findByFareId(getId());
    }

    public List<InclPostageProviso> findInclPostageProvisoes(CarryMode.CarryWay carryWay) {
        return InclPostageProviso.dao.findByFareIdCarryWay(getId(), carryWay);
    }

    /**
     * 返回该模版的所有条件包邮
     * @return
     */
    public List<InclPostageProviso> getInclPostageProvisoes() {
        return InclPostageProviso.dao.findByFareId(getId());
    }

    public boolean save() {
        setLastModifiedDate(new Date());
        return super.save();
    }

    public boolean update() {
        setLastModifiedDate(new Date());
        return super.update();
    }

    @Override
    public String toString() {
        return getName();
    }
}
