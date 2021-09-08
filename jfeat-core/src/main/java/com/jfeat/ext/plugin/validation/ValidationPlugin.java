package com.jfeat.ext.plugin.validation;

import com.jfinal.core.Const;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;

import java.util.Properties;

/**
 *
 define rules in validation.properties:

 格式:
 rulename=pattern;description

 email=^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$;邮箱格式不正确
 chinese=^[\u4e00-\u9fa5]+$;不是中文
 number=^[0-9]*$;只能输入数字
 phone=^1+[0-9]{10}$;号码格式不正确
 idcard=^(\\d{15}|\\d{18})$;身份证号码不正确,必须为15或18位
 postCode=^[0-9]{6}$;邮政编码格式不正确
 url=[a-zA-z]+://[^\s]*;url格式不对
 date=[0-9]{4}-[0-9]{2}-[0-9]{2};时间格式不对(yyyy-mm-dd)
 creditcard=^((?:4\d{3})|(?:5[1-5]\d{2})|(?:6011)|(?:3[68]\d{2})|(?:30[012345]\d);信用卡格式不正确
 acceptImg=[.](jpg|gif|bmp|png)$;图片后缀名格式不正确
 acceptFile=[.](xls|xlsx|csv|txt)$;文件后缀名格式不正确
 required=required;参数不能为空

 usage:
 in controller:

 @Validation(redirectUrl = "/user", rules = { "userName = required", "password = required" })
 public void login() {

 }

 * Created by jackyhuang on 17/2/10.
 */
public class ValidationPlugin implements IPlugin {

    private static final String validationPropertiesKey = "validation.properties.file";
    protected Prop prop = null;

    public ValidationPlugin() {
        this(System.getProperty(validationPropertiesKey, "validation.properties"));
    }

    public ValidationPlugin(String fileName) {
        this(fileName, Const.DEFAULT_ENCODING);
    }

    public ValidationPlugin(String fileName, String encoding) {
        prop = PropKit.use(fileName, encoding);
    }

    @Override
    public boolean start() {
        Properties properties = prop.getProperties();
        for (Object object : properties.keySet()) {
            String key = (String) object;
            String rule[] = properties.getProperty(key).split(";");
            ValidationRules.ruleMap.put(key, rule[0]);
            ValidationRules.ruleErrorMessageMap.put(key,rule[1]);
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
