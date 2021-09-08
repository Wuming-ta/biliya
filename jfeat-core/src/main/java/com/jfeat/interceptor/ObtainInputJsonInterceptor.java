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

package com.jfeat.interceptor;

import java.util.Map;

import com.jfeat.core.RestController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Rest API can use this validator to get user posted json data.
 * The Rest controller must extend RestController class.
 * 
 * The input json data is read from inputstream, so it is not able to retrieve again if it is obtained.
 * So this validator must place in the first of other validator who will use the input json.
 * The json is stored in a controller attribute named "jsonMap".
 * 
 * Usage example:
 * 
 * 
 * public MyController extends RestController {
 *     @Before({ObtainInputJsonInterceptor.class, MyValidator.class})
 *     public void save() throws Exception {
 *         Map<String, Object> maps = getAttr("jsonMap");
 *     }
 * }    
 *     
 *     
 * public class OrderValidator extends Validator {
 *     @Override
 *     protected void validate(Controller c) {
 *         RestController controller = (RestController) c;
 *         Map<String, Object> jsonMap = controller.getAttr("jsonMap");
 *     }    
 * }            
 *             
 * 
 * @author ehngjen
 *
 */
public class ObtainInputJsonInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(ObtainInputJsonInterceptor.class);

    @Override
    public void intercept(Invocation ai) {
        RestController controller = (RestController) ai.getController();
        try {
            Map<String, Object> jsonMap = controller.convertPostJsonToMap();
            logger.debug("INPUT JSON: " + jsonMap);
            controller.setAttr("jsonMap", jsonMap);
        } catch (Exception e) {
            controller.render400Rest("invalid json input." + e.getMessage());
            return;
        }
        ai.invoke();
    }

}
