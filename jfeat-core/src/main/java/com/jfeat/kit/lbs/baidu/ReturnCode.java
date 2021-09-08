package com.jfeat.kit.lbs.baidu;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ehngjen on 1/11/2016.
 */
public class ReturnCode {
    private static final Map<Integer, String> statusToErrMsg = new HashMap<Integer, String>(){{
        put(0, "正常");
        put(1, "服务器内部错误");
        put(2, "请求参数非法");
        put(3, "权限校验失败");
        put(4, "配额校验失败");
        put(5, "ak不存在或者非法");
        put(101, "服务禁用");
        put(102, "不通过白名单或者安全码不对");

    }};

    public static String get(int status){
        String result = statusToErrMsg.get(status);
        return result !=null? result : "未知返回码：" + status;
    }

}
