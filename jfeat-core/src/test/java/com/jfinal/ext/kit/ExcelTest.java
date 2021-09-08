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

package com.jfinal.ext.kit;

import com.jfinal.ext.kit.excel.PoiExporter;
import com.jfinal.ext.kit.excel.PoiImporter;
import com.jfinal.ext.kit.excel.Rule;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

/**
 * Created by ehngjen on 9/14/2015.
 */
public class ExcelTest {
    @Test
    public void testImportExcel() {
        Rule rule = new Rule();
        rule.addCell(0,"en_name");
        rule.addCell(1, "cn_name");
        List<List<String>> list = PoiImporter.readSheet(new File(getClass().getResource("/data.xlsx").getFile()), rule);
        System.out.println(list);
    }

    @Test
    public void testExportExcel() throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("a", "this is a");
        data.put("b", "this is b");
        List<Map<String, String>> list = new ArrayList<>();
        list.add(data);
        PoiExporter exporter = new PoiExporter(list);
        exporter.header("A", "B");
        exporter.column("a", "b");
        Workbook book = exporter.export();
        assertEquals(1, book.getNumberOfSheets());
        //OutputStream outputStream = new FileOutputStream(new File("test.xlsx"));
        //book.write(outputStream);
    }
}
