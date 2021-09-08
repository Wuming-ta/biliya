package com.jfeat.kit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.jfeat.kit.DiskKit;

import static org.junit.Assert.*;

public class DiskKitTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {

    }
    
    @After
    public void tearDown() throws Exception {
        
    }

    @Test
    public void testConvert() {
        assertEquals("10KB", DiskKit.convertFormat(10000));
        assertEquals("100B", DiskKit.convertFormat(100));
        assertEquals("1GB", DiskKit.convertFormat(1000000000L));
        
        assertEquals("1,000MB", DiskKit.convertFormat(1000000000L, DiskKit.UNIT_M));
        assertEquals("1GB", DiskKit.convertFormat(1000000000L, "INVALID"));
        assertEquals("0.1KB", DiskKit.convertFormat(100L, DiskKit.UNIT_K));
    }

}
