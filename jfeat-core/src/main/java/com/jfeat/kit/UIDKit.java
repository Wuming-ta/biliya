package com.jfeat.kit;

import java.util.Date;

/**
 * Created by jackyhuang on 16/9/30.
 */
public class UIDKit {

    private static int cluster = 1;
    private static Date date = new Date();
    private static StringBuilder buf = new StringBuilder();
    private static int seq = 1;
    private static final int ROTATION = 9999;

    public static synchronized void setCluster(int arg) {
        cluster = arg;
    }

    public static synchronized String next() {
        if (seq > ROTATION) seq = 1;
        buf.delete(0, buf.length());
        date.setTime(System.currentTimeMillis());
        return String.format("%3$02d%1$ty%1$tm%1$td%1$tH%1$tM%1$tS%2$04d", date, seq++, cluster);
    }
}