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

import java.nio.file.FileStore;
import java.text.NumberFormat;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.FileSystemException;
import java.io.IOException;
import java.util.*;


public class DiskKit {
  
    public static String UNIT_G = "GB";
    public static String UNIT_M = "MB";
    public static String UNIT_K = "KB";
    public static String UNIT_B = "B";
    private static long G = 1000000000L;
    private static long M = 1000000L;
    private static long K = 1000L;
    private static long B = 1L;
    static final Map<String, Long> UNIT_MAP = new LinkedHashMap<String, Long>() {
        {
            put(UNIT_G, G);
            put(UNIT_M, M);
            put(UNIT_K, K);
            put(UNIT_B, B);
        }
    };

    public static long getUsableSpace() throws IOException {
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            return getUsableSpace(root);
        }
        return 0L;
    }
    
    public static long getTotalSpace() throws IOException {
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            return getTotalSpace(root);
        }
        return 0L;
    }

    public static long getUsableSpace(Path path) throws IOException {
        try {
            FileStore store = Files.getFileStore(path);
            return store.getUsableSpace();
        }
        catch (FileSystemException e) {
            System.out.println("error querying space: " + e.toString());
        }
        return 0L;
    }
    
    public static long getTotalSpace(Path path) throws IOException {
        try {
            FileStore store = Files.getFileStore(path);
            return store.getTotalSpace();
        }
        catch (FileSystemException e) {
            System.out.println("error querying space: " + e.toString());
        }
        return 0L;
    }

    public static double convert(long value) {
        for (Map.Entry<String, Long> entry : UNIT_MAP.entrySet()) {
            Long unitValue = entry.getValue();
            if (value >= unitValue) {
                return (double) value / unitValue;
            }
        }
        return 0.0;
    }

    public static double convert(long value, String unit) {
        Long unitValue = UNIT_MAP.get(unit);
        if (unitValue != null) {
            return (double) value / unitValue;
        }
        return convert(value);
    }

    public static String convertFormat(long value) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        for (Map.Entry<String, Long> entry : UNIT_MAP.entrySet()) {
            String unitName = entry.getKey();
            Long unitValue = entry.getValue();
            if (value >= unitValue) {
                return nf.format((double) value / unitValue) + unitName;
            }
        }
        return null;
    }
    
    public static String convertFormat(long value, String unit) {
        Long unitValue = UNIT_MAP.get(unit);
        if (unitValue != null) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            return nf.format((double) value / unitValue) + unit;
        }
        return convertFormat(value);
    }
}
