/*
 * Copyright (C) 2014-2015 by ehngjen @ www.jfeat.com
 *
 *  The program may be used and/or copied only with the written permission
 *  from JFeat.com, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */

package com.jfeat.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.ext.plugin.cache.AutoCacheInterceptor;
import com.jfeat.observer.*;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@Before(AutoCacheInterceptor.class)
public abstract class BaseModel<M extends BaseModel<?>> extends Model<M> implements Subject {

    public static Logger logger = LoggerFactory.getLogger(BaseModel.class);
    
    private String tableName;
    private String pkName;

    public static final int EVENT_SAVE = 1;
    public static final int EVENT_UPDATE = 2;
    public static final int EVENT_DELETE = 3;

    public BaseModel() {
        pkName = "id";
        TableBind tb = (TableBind) getClass().getAnnotation(TableBind.class);
        if (tb != null) {
            tableName = tb.tableName();
            if (StrKit.notBlank(tb.pkName())) {
                pkName = tb.pkName();
            }
        }
        else {
            Class<M> clazz = getClazz();
            tableName = clazz.getSimpleName();
        }
        
    }

    public M duang() {
        return Enhancer.enhance(this);
    }


    /**
     * 获取M的class
     * 
     * @return M
     */
    @SuppressWarnings("unchecked")
    public Class<M> getClazz() {
        Type t = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) t).getActualTypeArguments();
        return (Class<M>) params[0];
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPkName() {
        return pkName;
    }

    public void setPkName(String pkName) {
        this.pkName = pkName;
    }


    public List<M> findAll() {
        return find("select * from " + getTableName());
    }

    public M findFirstByField(String field, Object value) {
        List<M> result = this.findByField(field, value);
        return result.size() > 0 ? result.get(0) : null;
    }

    public List<M> findByField(String field, Object value) {
        return findByFields(new String[]{field}, new Object[]{value});
    }

    public List<M> findByField(String field, Object value, String[] orderByList, String[] orderByDescList) {
        return findByFields(new String[]{field}, new Object[]{value}, orderByList, orderByDescList);
    }

    public List<M> findByFields(String[] fields, Object[] values) {
        return findByFields(fields, values, null, null);
    }

    public List<M> findByFields(String[] fields, Object[] values, String[] orderByList, String[] orderByDescList) {
        if (fields != null && values != null && fields.length != values.length) {
            throw new IllegalArgumentException("fields or values is null. or length not equal");
        }
        List<Object> params = Lists.newArrayList();
        StringBuilder sql = buildSelect();
        sql.append(" from ");
        sql.append(getTableName());
        if (fields != null && values != null) {
            sql.append(" where ");
            for (int i = 0; i < fields.length; ++i) {
                String field = fields[i];
                Object value = values[i];
                if (i > 0) {
                    sql.append(" and ");
                }
                sql.append(field);
                sql.append(" = ? ");
                params.add(value);
            }
        }
        boolean hasOrderBy = false;
        if (orderByList != null && orderByList.length > 0) {
            sql.append(" order by ");
            hasOrderBy = true;
            for (int i = 0 ; i < orderByList.length; ++i) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(orderByList[i]);
            }
        }
        if (orderByDescList != null && orderByDescList.length > 0) {
            if (!hasOrderBy) {
                sql.append(" order by ");
            }
            else {
                sql.append(", ");
            }
            for (int i = 0 ; i < orderByDescList.length; ++i) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(orderByDescList[i]);
                sql.append(" desc");
            }
        }

        return find(sql.toString(), params.toArray());
    }

    public boolean deleteByField(String key, Object val) {
        return Db.update("delete from " + getTableName() + " where " + key + " = ?", val) >= 1;
    }
    
    public Page<M> paginate(int pageNumber, int pageSize) {
        return paginate(pageNumber, pageSize, null);
    }

    private StringBuilder buildSelect() {
        StringBuilder select = new StringBuilder("select * ");
        return select;
    }

    public Pagination<M> createPagination() {
        return new Pagination<>(this);
    }

    public Page<M> paginate(int pageNumber, int pageSize, Map<String, Object> paramsMap) {
        Map<String, Boolean> orderByMap = Maps.newHashMap();
        orderByMap.put(getPkName(), false);
        return paginate(pageNumber, pageSize, paramsMap, orderByMap);
    }

    public Page<M> paginate(int pageNumber, int pageSize, Map<String, Object> paramsMap, Map<String, Boolean> orderByMap) {
        List<Object> params = Lists.newArrayList();
        StringBuilder select = buildSelect();
        StringBuilder sql = new StringBuilder("from ");
        sql.append(getTableName());
        sql.append(" ");
        if (paramsMap != null && paramsMap.size() > 0) {
            String cond = " where ";
            for (Map.Entry entry : paramsMap.entrySet()) {
                sql.append(cond);
                sql.append(entry.getKey());
                sql.append("=? ");
                cond = " and ";
            }
            params.addAll(paramsMap.values());
        }
        if (orderByMap != null && orderByMap.size() > 0) {
            sql.append(" order by ");
            String comma = "";
            for (Map.Entry entry : orderByMap.entrySet()) {
                sql.append(comma);
                sql.append(entry.getKey());
                if ((Boolean) entry.getValue()) {
                    sql.append(" desc ");
                }
                else {
                    sql.append(" asc ");
                }
                comma = ",";
            }
        }
        return super.paginate(pageNumber, pageSize, select.toString(), sql.toString(), params.toArray());
    }

    @Override
    public void notifyAllObserver(Subject subject, int event, Object param) {
        ObserverKit.me().notifyObserverSync(subject, event, param);
        ObserverKit.me().notifyObserver(subject, event, param);
    }

    public boolean save(Object param) {
        boolean result = super.save();
        if (result) {
            notifyAllObserver(this, EVENT_SAVE, param);
        }
        return result;
    }

    public boolean update(Object param) {
        boolean result = super.update();
        if (result) {
            notifyAllObserver(this, EVENT_UPDATE, param);
        }
        return result;
    }

    public boolean delete(Object param) {
        boolean result = super.delete();
        if (result) {
            notifyAllObserver(this, EVENT_DELETE, param);
        }
        return result;
    }

    public boolean saveWithoutNotify() {
        return super.save();
    }

    public boolean updateWithoutNotify() {
        return super.update();
    }

    public boolean deleteWithoutNotify() {
        return super.delete();
    }

    @Override
    public boolean save() {
        return save(null);
    }

    @Override
    public boolean update() {
        return update(null);
    }

    @Override
    public boolean delete() {
        return delete(null);
    }

}
