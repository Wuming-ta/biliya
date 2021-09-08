package com.jfeat.plugintest.validation;

import com.jfeat.ext.plugin.validation.ValidationPlugin;
import com.jfeat.ext.plugin.validation.ValidationRules;
import com.jfeat.ext.plugin.validation.ValidatorInterceptor;
import com.jfinal.aop.Duang;
import com.jfinal.aop.Invocation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by jackyhuang on 17/2/10.
 */
public class ValidationTest {

    @Mock
    private Invocation invocation;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ValidationPlugin plugin = new ValidationPlugin();
        plugin.start();
    }

    @Test
    public void testValidationPass() throws NoSuchMethodException {
        TestController controller = new TestController();
        ValidatorInterceptor interceptor = new ValidatorInterceptor();
        Mockito.when(invocation.getController()).thenReturn(controller);
        Mockito.when(invocation.getMethod()).thenReturn(TestController.class.getMethod("pass"));
        interceptor.intercept(invocation);
        Mockito.verify(invocation).invoke();
    }


    @Test
    public void testValidationFailed() throws NoSuchMethodException {
        TestController controller = new TestController();
        ValidatorInterceptor interceptor = new ValidatorInterceptor();
        Mockito.when(invocation.getController()).thenReturn(controller);
        Mockito.when(invocation.getMethod()).thenReturn(TestController.class.getMethod("fail"));
        interceptor.intercept(invocation);
        assertEquals(true, controller.failed);
    }

    @Test
    public void testMoneyFormat() {
        String rule = "(^[1-9]\\d*(\\.\\d{1,2})?$)|(^[0]{1}(\\.\\d{1,2})?$)";
        String[] trueValues = {"1", "123", "0.1", "12.34", "9.9", "9999.99", "0"};
        for (String value : trueValues) {
            boolean result = Pattern.compile(rule).matcher(value).matches();
            System.out.println(value + ", " + result);
            assertEquals(true, result);
        }

        String[] falseValues = {"xf", ".3", "011", "23.x", "22.222", "3.", "3.4a"};
        for (String value : falseValues) {
            boolean result = Pattern.compile(rule).matcher(value).matches();
            System.out.println(value + ", " + result);
            assertEquals(false, result);
        }
    }

    @Test
    public void testBoolean() {
        String rule = "(true|false)";
        String[] trueValues = {"true", "false"};
        for (String value : trueValues) {
            boolean result = Pattern.compile(rule).matcher(value).matches();
            System.out.println(value + ", " + result);
            assertEquals(true, result);
        }

        String[] falseValues = {"xf", ".3", "011", "23.x", "22.222", "3.", "3.4a"};
        for (String value : falseValues) {
            boolean result = Pattern.compile(rule).matcher(value).matches();
            System.out.println(value + ", " + result);
            assertEquals(false, result);
        }
    }

    @Test
    public void testDatetime() {
        String rule = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-2][0-9]:[0-5][0-9]:[0-5][0-9]";
        String[] trueValues = {"2017-01-02 12:00:00"};
        for (String value : trueValues) {
            boolean result = Pattern.compile(rule).matcher(value).matches();
            System.out.println(value + ", " + result);
            assertEquals(true, result);
        }

        String[] falseValues = {"xf", "20170101 11:00:00", "2017-01-02", "2017-1-12 12:00:00", "2017-01-02 44:00:00", "2017-02-02 21:65:00", "2017-01-02 21:22:90"};
        for (String value : falseValues) {
            boolean result = Pattern.compile(rule).matcher(value).matches();
            System.out.println(value + ", " + result);
            assertEquals(false, result);
        }
    }
}
