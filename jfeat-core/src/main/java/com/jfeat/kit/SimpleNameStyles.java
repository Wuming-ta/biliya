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

public class SimpleNameStyles {
    public static final INameStyle DEFAULT = new INameStyle() {
        public String name(String className) {
            return className;
        }
    };
    
    public static final INameStyle FIRST_UP = new INameStyle() {
        public String name(String className) {
            StringBuilder builder = new StringBuilder();
            for (String str : className.split("_")) {
                builder.append(StrKit.firstCharToUpperCase(str));
            }
            return builder.toString();
        }
    };

    public static final INameStyle FIRST_LOWER = new INameStyle() {
        public String name(String className) {
            return StrKit.firstCharToLowerCase(className);
        }
    };
    public static final INameStyle UP = new INameStyle() {
        public String name(String className) {
            return className.toUpperCase();
        }
    };
    public static final INameStyle LOWER = new INameStyle() {
        public String name(String className) {
            return className.toLowerCase();
        }
    };

    public static final INameStyle UP_UNDERLINE = new INameStyle() {
        public String name(String className) {
            String tableName = "";
            for (int i = 0; i < className.length(); i++) {
                char ch = className.charAt(i);
                if (i != 0 && Character.isUpperCase(ch)) {
                    tableName += "_" + ch;
                } else {
                    tableName += Character.toUpperCase(ch);
                }
            }
            return tableName;
        }
    };
    public static final INameStyle LOWER_UNDERLINE = new INameStyle() {
        public String name(String className) {
            String tableName = "";
            for (int i = 0; i < className.length(); i++) {
                char ch = className.charAt(i);
                if (i == 0) {
                    tableName += Character.toLowerCase(ch);
                } else if (Character.isUpperCase(ch)) {
                    tableName += "_" + Character.toLowerCase(ch);
                } else {
                    tableName += ch;
                }
            }
            return tableName;
        }
    };
}
