package com.jfeat.marketing.trial.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.marketing.trial.model.Trial;
import com.jfeat.marketing.trial.service.TrialService;
import com.jfeat.product.model.Product;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;

import java.util.List;

/**
 * Created by kang on 2017/4/19.
 */
@ControllerBind(controllerKey = "/rest/trial")
public class TrialController extends RestController {

    TrialService trialService = Enhancer.enhance(TrialService.class);

    @Before({CurrentUserInterceptor.class})
    @Override
    public void index() {
        User currentUser = getAttr("currentUser");
        Integer userId = null;
        if (currentUser != null) {
            userId = currentUser.getId();
        }
        List<Trial> trials = trialService.available(userId);
        renderSuccess(trials);
    }

    @Before({CurrentUserInterceptor.class})
    @Override
    public void show() {
        User currentUser = getAttr("currentUser");
        Trial trial = Trial.dao.findById(getParaToInt());
        if (trial == null) {
            renderFailure("trial.not.found");
            return;
        }
        if (trial.getEnabled().equals(Trial.Enabled.NO.getValue())) {
            renderFailure("trial.is.disabled");
            return;
        }

        if (currentUser == null) {
            renderError(401);
            return;
        }

        Product product = trial.getProduct();
        trial.put("partaken", trialService.isPartaken(trial, currentUser.getId()));
        trial.put("product", product);
        trial.put("covers", trial.getCovers());
        renderSuccess(trial);
    }
}
