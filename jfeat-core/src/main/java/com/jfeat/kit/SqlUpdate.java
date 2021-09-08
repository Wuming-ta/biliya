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

package com.jfeat.kit;

import com.jfinal.kit.StrKit;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ehngjen on 10/15/2015.
 */
public class SqlUpdate {
    public static final String update = "UPDATE";
    public static final String delete = "DELETE";
    public static final String set = "SET";
    public static final String from = "FROM";
    public static final String where = "WHERE";
    public static final String and = "AND";
    public static final String or = "OR";

    private static final String space = " ";

    private StringBuilder sql = new StringBuilder();
    private List<String> whereConds = new LinkedList<>();
    private Map<String, Object> updateMap = new LinkedHashMap<>();
    private boolean whereCalled = false;
    private boolean isUpdate = false;
    private boolean isDelete = false;
    private String result = null;
    private String table = null;

    public SqlUpdate delete(String table) {
        isDelete = true;
        this.table = table;
        result = null;
        return this;
    }

    public SqlUpdate update(String table) {
        isUpdate = true;
        this.table = table;
        return this;
    }

    public SqlUpdate set(String column, Object value) {
        if (isUpdate == false) {
            throw new IllegalStateException("SET must be called after UPDATE statement.");
        }
        updateMap.put(column, value);
        return this;
    }

    public SqlUpdate where(String cond) {
        result = null;
        whereConds.add(cond);
        whereCalled = true;
        return this;
    }

    public SqlUpdate and(String cond) {
        result = null;
        if (whereCalled == false) {
            throw new IllegalStateException("AND must be called after WHERE statement.");
        }
        whereConds.add(and);
        whereConds.add(cond);
        return this;
    }

    public SqlUpdate or(String cond) {
        result = null;
        if (whereCalled == false) {
            throw new IllegalStateException("OR must be called after WHERE statement.");
        }
        whereConds.add(or);
        whereConds.add(cond);
        return this;
    }

    public String sql() {
        if (isDelete && isUpdate) {
            throw new IllegalStateException("Cannot DELETE and UPDATE at the same time.");
        }
        if (!isDelete && !isUpdate) {
            throw new IllegalStateException("DELETE or UPDATE must be specified.");
        }

        if (result == null) {
            if (isDelete) {
                sql.append(delete).append(space).append(from).append(space).append(table);
            }
            if (isUpdate) {
                sql.append(update).append(space).append(table).append(space);
                int index = 0;
                for (Map.Entry<String, Object> entry : updateMap.entrySet()) {
                    sql.append(set).append(space).append(entry.getKey()).append("=").append(entry.getValue());
                    if (index != updateMap.size() - 1) {
                        sql.append(",").append(space);
                    }
                    ++index;
                }
            }

            // where
            if (whereConds.size() > 0) {
                sql.append(space).append(where).append(space);
                int index = 0;
                for (String cond : whereConds) {
                    sql.append(cond);
                    if (index != whereConds.size() - 1) {
                        sql.append(space);
                    }
                    ++index;
                }
            }

            result = sql.toString();
            if (!result.endsWith(";")) {
                sql.append(";");
                result = sql.toString();
            }
        }
        return result;
    }
}
