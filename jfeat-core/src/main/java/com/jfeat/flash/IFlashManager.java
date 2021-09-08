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

package com.jfeat.flash;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by ehngjen on 5/4/2015.
 */
public interface IFlashManager {
    /**
     * 添加flash信息到缓存中。
     *
     * @param sessionKey
     *            session路径
     * @param curAction
     *            当前ActionPath
     * @param key
     *            键
     * @param value
     *            值
     */
    public void setFlash(HttpSession session, String curAction, String key,
                         Object value);

    /***
     * 在调用redirect forwardAction
     * 时回调此接口，将以当前actionPath为key更替为下一个请求actionPath作为key。
     *
     * @param sessionKey
     *            session的Id值
     * @param curAction
     *            当前ActionPath
     * @param nextAction
     *            下一个ActionPath
     */
    public void updateFlashKey(HttpSession session, String curAction,
                               String nextAction);

    /**
     * 从cache中取得Flash的Map
     *
     * @param sessionKey
     *            session路径
     * @param curAction
     *            当前ActionPath
     * @return Flash的Map
     */
    public Map<String, Object> getFlash(HttpSession session, String curAction);
}
