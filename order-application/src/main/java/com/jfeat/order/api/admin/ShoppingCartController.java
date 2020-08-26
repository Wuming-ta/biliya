package com.jfeat.order.api.admin;

import com.jfeat.core.BaseController;
import com.jfeat.core.RestController;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.ShoppingCart;
import com.jfeat.order.service.OrderService;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * 管理人员查看会员的购物车信息
 * @author jackyhuang
 * @date 2018/10/25
 */
@ControllerBind(controllerKey = "/rest/admin/shopping_cart")
public class ShoppingCartController extends RestController {

    private OrderService orderService = new OrderService();

    @Override
    @RequiresPermissions("order.view")
    public void index() {
        String phone = getPara("phone");
        if (StrKit.isBlank(phone)) {
            renderFailure("phone.is.required");
            return;
        }
        User user = User.dao.findByPhone(phone);
        if (user == null) {
            renderFailure("user.not.found");
            return;
        }

        renderSuccess(orderService.queryShoppingCart(user.getId()));
    }
}
