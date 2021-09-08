package com.jfeat;

import com.jfeat.ui.MenuHolder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author jackyhuang
 * @date 2018/9/18
 */
public class MenuTest {

    @Test
    public void testMenuSelect() {
        MenuHolder holder = new MenuHolder();
        Map<String, String[]> params = new HashMap<>();
        params.put("abc", new String[]{ "123" });
        assertFalse(holder.isMatch("ext", "ext?route=member", params));
        assertTrue(holder.isMatch("ext", "ext?abc=123", params));
    }
}
