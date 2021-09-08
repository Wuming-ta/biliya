package com.jfeat.kit;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Created by jacky on 2/11/15.
 */
public class MessageKitTest {

    @Test
    public void test() throws java.text.ParseException {
        String format = "this is {0} and {1}";
        Integer param_0 = 4;
        String param_1 = "abc";
        String result = MessageKit.format(format, param_0, param_1);
        System.out.println(result);
        assertEquals(result, "this is 4 and abc");

    }
}
