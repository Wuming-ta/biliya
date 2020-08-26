/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.pcd.api;

import com.jfeat.core.RestController;
import com.jfeat.kit.lbs.baidu.GeocodingApi;
import com.jfeat.kit.lbs.baidu.model.GeoAddressResult;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ehngjen on 1/22/2016.
 */
@ControllerBind(controllerKey = "/rest/geo")
public class GeoController extends RestController {

    private static Logger logger = LoggerFactory.getLogger(GeoController.class);

    /**
     * GET /address?lng=113.24788673459&lat=23.126990536009
     */
    public void index() {
        String lngStr = getPara("lng");
        String latStr = getPara("lat");
        if (StrKit.isBlank(lngStr) || StrKit.isBlank(latStr)) {
            render400Rest("lng or lat is null");
            return;
        }

        Double lng = Double.parseDouble(lngStr);
        Double lat = Double.parseDouble(latStr);

        try {
            GeoAddressResult result = (GeoAddressResult) GeocodingApi.location(lat, lng);
            if (result.isSucceed()) {
                Map<String, Object> addressMap = new HashMap<>();
                addressMap.put("formatted_address", result.getFormattedAddressObject());
                addressMap.put("address_component", result.getAddressComponentObject());
                renderSuccess(addressMap);
                return;
            }
            renderFailure("cannot.get.address");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            renderError(500);
        }
    }
}
