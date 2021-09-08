package com.jfeat.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.Page;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 16/12/16.
 */
public class Pagination<M extends BaseModel<?>> {

    public static final String EQ = " = ";
    public static final String GT = " > ";
    public static final String LT = " < ";
    public static final String GE = " >= ";
    public static final String LE = " <= ";
    public static final String LIKE = " LIKE ";
    public static final String IS_NULL = " IS NULL ";
    public static final String NOT_NULL = " IS NOT NULL ";

    private BaseModel<M> baseModel;
    private List<Param> params = Lists.newArrayList();
    private List<OrderBy> orderBys = Lists.newArrayList();
    private List<String> selects = Lists.newArrayList();
    private Map<String, String> joins = Maps.newHashMap();
    private Map<String, String> tableAlias = Maps.newHashMap();
    private int pageNumber = 1;
    private int pageSize = 50;

    public Pagination(BaseModel<M> baseModel) {
        this.baseModel = baseModel;
        tableAlias.put(baseModel.getTableName(), "t1");
    }

    public Pagination<M> select(String table, Field... fields) {
        addTableAlias(table);
        if (fields == null) {
            selects.add(tableAlias.get(table) + ".*");
        }
        else {
            StringBuilder builder = new StringBuilder();
            String prefix = tableAlias.get(table) + ".";
            String comma = "";
            for (Field field : fields) {
                builder.append(comma);
                builder.append(prefix);
                builder.append(field.getField());
                if (field.getAlias() != null) {
                    builder.append(" as ");
                    builder.append(field.getAlias());
                }
                comma = ", ";
            }
            selects.add(builder.toString());
        }
        return this;
    }

    public Pagination<M> select(String table, String... fields) {
        List<Field> list = Lists.newArrayList();
        if (fields != null) {
            for (String field : fields) {
                list.add(new Field(field));
            }
        }
        return select(table, list.toArray(new Pagination.Field[list.size()]));
    }

    public Pagination<M> addParam(String field, Object value) {
        return addParam(field, value, EQ);
    }

    public Pagination<M> addParam(String field, Object value, String operation) {
        return addParam(baseModel.getTableName(), field, value, operation);
    }

    public Pagination<M> addParam(String table, String field, Object value, String operation) {
        addTableAlias(table);
        Param param = new Param(table, field, value, operation);
        this.params.add(param);
        return this;
    }

    public Pagination<M> join(String table, String onMainTableField, String onJoinTableField) {
        addTableAlias(table);
        StringBuilder condition = new StringBuilder();
        condition.append(tableAlias.get(baseModel.getTableName()));
        condition.append(".");
        condition.append(onMainTableField);
        condition.append(EQ);
        condition.append(tableAlias.get(table));
        condition.append(".");
        condition.append(onJoinTableField);
        joins.put(table, condition.toString());
        return this;
    }

    public Pagination<M> orderBy(String field) {
        return orderBy(field, false);
    }

    public Pagination<M> orderBy(String field, boolean desc) {
        return orderBy(baseModel.getTableName(), field, desc);
    }

    public Pagination<M> orderBy(String table, String field, boolean desc) {
        addTableAlias(table);
        OrderBy orderBy = new OrderBy(table, field, desc);
        this.orderBys.add(orderBy);
        return this;
    }

    public Pagination<M> setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public Pagination<M> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Page<M> doPaginate() {
        List<Object> paramList = Lists.newArrayList();
        StringBuilder select = new StringBuilder("select ");
        if (selects.size() > 0) {
            String comma = "";
            for (String str : selects) {
                select.append(comma);
                select.append(str);
                comma = ", ";
            }
        }
        else {
            select.append(tableAlias.get(baseModel.getTableName())).append(".*");
        }
        StringBuilder sql = new StringBuilder("from ");
        sql.append(baseModel.getTableName());
        sql.append(" ");
        sql.append("as ");
        sql.append(tableAlias.get(baseModel.getTableName()));
        if (joins.size() > 0) {
            for (String table : joins.keySet()) {
                sql.append(" join ");
                sql.append(table);
                sql.append(" as ");
                sql.append(tableAlias.get(table));
                sql.append(" on ");
                sql.append(joins.get(table));
            }
        }
        if (params.size() > 0) {
            String cond = " where ";
            for (Param param : params) {
                sql.append(cond);
                sql.append(tableAlias.get(param.getTable()));
                sql.append(".");
                sql.append(param.getField());
                sql.append(param.getOperation());
                if (!param.getOperation().equals(IS_NULL) || !param.getOperation().equals(NOT_NULL)) {
                    paramList.add(param.getValue());
                    sql.append("?");
                }
                cond = " and ";
            }
        }
        if (orderBys.size() > 0) {
            sql.append(" order by ");
            String comma = "";
            for (OrderBy orderBy : orderBys) {
                sql.append(comma);
                sql.append(tableAlias.get(orderBy.getTable()));
                sql.append(".");
                sql.append(orderBy.getField());
                if (orderBy.isDesc()) {
                    sql.append(" desc ");
                }
                else {
                    sql.append(" asc ");
                }
                comma = ",";
            }
        }
        return baseModel.paginate(pageNumber, pageSize, select.toString(), sql.toString(), paramList.toArray());
    }

    private void addTableAlias(String table) {
        if (tableAlias.get(table) == null) {
            String alias = "t" + (tableAlias.size() + 1);
            tableAlias.put(table, alias);
        }
    }

    public static class Field {
        private String field;
        private String alias;
        public Field(String field) {
            this(field, null);
        }
        public Field(String field, String alias) {
            this.field = field;
            this.alias = alias;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }

    private class OrderBy {
        private String table;
        private String field;
        private boolean desc;

        public OrderBy(String table, String field) {
            this.table = table;
            this.field = field;
            this.desc = false;
        }

        public OrderBy(String table, String field, boolean desc) {
            this.table = table;
            this.field = field;
            this.desc = desc;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public boolean isDesc() {
            return desc;
        }

        public void setDesc(boolean desc) {
            this.desc = desc;
        }
    }

    private class Param {
        private String table;
        private String field;
        private Object value;
        private String operation;

        public Param(String table, String field, Object value) {
            this(table, field, value, "=");
        }

        public Param(String table, String field, Object value, String operation) {
            this.table = table;
            this.field = field;
            this.value = value;
            this.operation = operation;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }
    }
}
