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

package com.jfeat.product.util;

import com.jfinal.upload.UploadFile;

import java.util.Comparator;

/**
 * 根据上传文件的name去比较。一般情况下,name=cover-1 的格式
 * Created by huangjacky on 16/7/8.
 */
public class UploadFileComparator implements Comparator<UploadFile> {

    private String namePrefix;

    public UploadFileComparator(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public int compare(UploadFile file1, UploadFile file2) {
        String name1 = file1.getParameterName();
        String name2 = file2.getParameterName();
        if (name1 == null || name1.equals("")) {
            return -1;
        }
        if (name2 == null || name2.equals("")) {
            return 1;
        }
        if (name1.equals(name2)) {
            return 0;
        }
        int index = 0;
        if (namePrefix != null && !namePrefix.equals("")) {
            index = namePrefix.length();
        }
        String value1 = name1.substring(index, name1.length());
        String value2 = name2.substring(index, name2.length());
        if (value1.equals("")) {
            return -1;
        }
        if (value2.equals("")) {
            return 1;
        }
        if (value1.equals(value2)) {
            return 0;
        }

        try {
            Integer v1 = Integer.parseInt(value1);
            Integer v2 = Integer.parseInt(value2);
            return v1.compareTo(v2);
        }catch (NumberFormatException ex) {
            return value1.compareTo(value2);
        }
    }
}
