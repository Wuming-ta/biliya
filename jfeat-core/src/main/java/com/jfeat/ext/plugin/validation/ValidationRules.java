package com.jfeat.ext.plugin.validation;

import com.jfinal.kit.StrKit;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by jackyhuang on 17/2/10.
 */
public class ValidationRules {

    public static Map<String, String> ruleMap = new HashMap<>();
    public static Map<String, String> ruleErrorMessageMap = new HashMap<>();

    public static boolean isMatch(String rule, String value) {
        boolean matchResult;
        if(rule.equals("required")) {
            matchResult = StrKit.notBlank(value);
        } else {
            matchResult = !StrKit.notBlank(value) || Pattern.compile(ruleMap.get(rule)).matcher(value).matches();
        }
        return matchResult;
    }

    public static String getErrorMessage(String rule){
        return ruleErrorMessageMap.get(rule);
    }

}
