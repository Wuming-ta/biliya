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

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;


public class OSKit {

    public static double getMemoryUsage() {
        double usage = 0.0;
        try {
            if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean) {
                OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                long freeMemorySize = osBean.getFreePhysicalMemorySize();
                long totalMemorySize = osBean.getTotalPhysicalMemorySize();
                usage = (double)(totalMemorySize - freeMemorySize)/totalMemorySize * 100.0;
            }
        }
        catch (ClassCastException e) {
        }
        return usage;
    }

    public static double getCpuUsage(){
        double usage = 0.0;
        try {
            if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean) {
                OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                usage = osBean.getSystemCpuLoad() * 100.0;
            }
        }
        catch (ClassCastException e) {
        }
        return usage;
    }

}
