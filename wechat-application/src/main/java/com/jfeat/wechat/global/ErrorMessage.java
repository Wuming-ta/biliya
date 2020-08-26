package com.jfeat.wechat.global;

import java.util.HashMap;

/**
 * Created by kang on 2017/4/10.
 */
public class ErrorMessage {
    private static ErrorMessage errorMessage = new ErrorMessage();
    private HashMap<Integer, String> map = new HashMap<>();

    private ErrorMessage() {
        map.put(0, "成功");

        //错误码：添加客服帐号
        map.put(65400, "API不可用，即没有开通/升级到新版客服");
        map.put(65403, "客服昵称不合法");
        map.put(65404, "客服帐号不合法");
        map.put(65405, "帐号数目已达到上限，不能继续添加");
        map.put(65406, "已经存在的客服帐号");

        //错误码：上传客服头像
        map.put(40005,"不支持的媒体类型");
        map.put(40009,"媒体文件长度不合法");

        //错误码：邀请绑定客服帐号
        map.put(65500, "API不可用，即没有开通/升级到新版客服");
        map.put(65401, "无效客服帐号");
        map.put(65407, "邀请对象已经是本公众号客服");
        map.put(65408, "本公众号已发送邀请给该微信号");
        map.put(65409, "无效的微信号");
        map.put(65410, "邀请对象绑定公众号客服数量达到上限（目前每个微信号最多可以绑定5个公众号客服帐号）");
        map.put(65411, "该帐号已经有一个等待确认的邀请，不能重复邀请");
        map.put(65412, "该帐号已经绑定微信号，不能进行邀请");

    }

    public static ErrorMessage getInstance() {
        return errorMessage;
    }

    public String getErrorMsg(int errCode) {
        return map.get(errCode);
    }

}
