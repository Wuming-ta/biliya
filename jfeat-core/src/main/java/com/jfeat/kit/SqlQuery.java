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

import java.util.*;

/**
 * Created by ehngjen on 10/14/2015.
 */
public class SqlQuery {
    public static final String select = "SELECT";
    public static final String from = "FROM";
    public static final String join = "JOIN";
    public static final String on = "ON";
    public static final String where = "WHERE";
    public static final String like = "LIKE";
    public static final String and = "AND";
    public static final String or = "OR";
    public static final String orderby = "ORDER BY";
    public static final String groupby = "GROUP BY";
    public static final String limit = "LIMIT";

    private enum ORDER{
        DESC,
        ASC
    };

    private static final String space = " ";

    private StringBuilder sql = new StringBuilder();
    private List<String> columns = new LinkedList<>();
    private List<String> tables = new LinkedList<>();
    private Map<String, String> joinMap = new LinkedHashMap<>();
    private String currentJoinTable = null;
    private Map<String, ORDER> orderMap = new LinkedHashMap<>();
    private List<String> whereConds = new LinkedList<>();
    private List<String> groups = new LinkedList<>();
    private boolean whereCalled = false;
    private String result = null;

    public SqlQuery select(String ...columns) {
        result = null;
        for (String column : columns) {
            this.columns.add(column);
        }
        return this;
    }

    public SqlQuery from(String ...table) {
        result = null;
        for (String t : table) {
            tables.add(t);
        }
        return this;
    }

    public SqlQuery join(String table) {
        result = null;
        joinMap.put(table, "");
        currentJoinTable = table;
        return this;
    }

    public SqlQuery on(String cond) {
        result = null;
        if (currentJoinTable == null) {
            throw new IllegalStateException("ON must be called after JOIN statement.");
        }
        joinMap.put(currentJoinTable, cond);
        return this;
    }

    public SqlQuery where(String cond) {
        result = null;
        whereConds.add(cond);
        whereCalled = true;
        return this;
    }

    public SqlQuery and(String cond) {
        result = null;
        if (whereCalled == false) {
            throw new IllegalStateException("AND must be called after WHERE statement.");
        }
        whereConds.add(and);
        whereConds.add(cond);
        return this;
    }

    public SqlQuery or(String cond) {
        result = null;
        if (whereCalled == false) {
            throw new IllegalStateException("OR must be called after WHERE statement.");
        }
        whereConds.add(or);
        whereConds.add(cond);
        return this;
    }

    public SqlQuery orderBy(String column) {
        result = null;
        orderMap.put(column, ORDER.ASC);
        return this;
    }

    public SqlQuery orderByDesc(String column) {
        result = null;
        orderMap.put(column, ORDER.DESC);
        return this;
    }

    public SqlQuery groupBy(String column) {
        result = null;
        groups.add(column);
        return this;
    }


    public String sql() {
        if (result == null) {
            int index = 0;
            // select * from table
            sql.append(select).append(space);

            if (columns.size() == 0) {
                sql.append("*").append(space);
            }
            else {
                for (String column : columns) {
                    sql.append(column);
                    if (index != columns.size() - 1) {
                        sql.append(",");
                    }
                    sql.append(space);
                    ++index;
                }
            }

            sql.append(from).append(space);
            if (tables.size() == 0) {
                throw new IllegalStateException("table must be specified.");
            }

            index = 0;
            for (String table : tables) {
                sql.append(table);
                if (index != tables.size() - 1) {
                    sql.append(",").append(space);
                }
                ++index;
            }

            // join
            for (Map.Entry<String, String> entry : joinMap.entrySet()) {
                sql.append(space).append(join).append(space).append(entry.getKey());
                if (StrKit.notBlank(entry.getValue())) {
                    sql.append(space).append(on).append(space).append(entry.getValue());
                }
            }

            // where
            if (whereConds.size() > 0) {
                sql.append(space).append(where).append(space);
                index = 0;
                for (String cond : whereConds) {
                    sql.append(cond);
                    if (index != whereConds.size() - 1) {
                        sql.append(space);
                    }
                    ++index;
                }
            }

            // group by
            if (groups.size() > 0) {
                sql.append(space).append(groupby).append(space);
                index = 0;
                for (String group : groups) {
                    sql.append(group).append(space);
                    if (index != groups.size() - 1) {
                        sql.append(",").append(space);
                    }
                    ++index;
                }
            }

            // order by
            if (orderMap.size() > 0) {
                sql.append(space).append(orderby).append(space);
                index = 0;
                for (Map.Entry<String, ORDER> entry : orderMap.entrySet()) {
                    sql.append(entry.getKey()).append(space).append(entry.getValue());
                    if (index != orderMap.size() - 1) {
                        sql.append(",").append(space);
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
