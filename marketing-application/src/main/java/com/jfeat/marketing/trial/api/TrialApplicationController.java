package com.jfeat.marketing.trial.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.marketing.trial.model.Trial;
import com.jfeat.marketing.trial.model.TrialApplication;
import com.jfeat.marketing.trial.service.TrialService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.Map;

/**
 * Created by kang on 2017/4/19.
 */
@ControllerBind(controllerKey = "/rest/trial/application")
public class TrialApplicationController extends RestController {

    private TrialService trialService = Enhancer.enhance(TrialService.class);

    @Before({CurrentUserInterceptor.class})
    public void index() {
        User currentUser = getAttr("currentUser");
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        renderSuccess(TrialApplication.dao.paginate(pageNumber, pageSize, currentUser.getId()));
    }

    @Before({CurrentUserInterceptor.class})
    public void show() {
        User currentUser = getAttr("currentUser");
        Integer id = getParaToInt();
        if (id == null) {
            renderFailure("id.is.required");
            return;
        }
        TrialApplication trialApplication = TrialApplication.dao.findById(id);
        if (trialApplication == null) {
            renderFailure("trialApplication.not.found");
            return;
        }
        if (!trialApplication.getUserId().equals(currentUser.getId())) {
            renderFailure("not your trial application");
            return;
        }
        renderSuccess(trialApplication);
    }

}
