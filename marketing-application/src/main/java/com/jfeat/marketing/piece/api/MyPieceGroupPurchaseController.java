package com.jfeat.marketing.piece.api;

import com.google.common.collect.Maps;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.marketing.piece.api.validator.PieceGroupPurchaseConfigValidator;
import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;
import com.jfeat.order.model.Order;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import org.apache.shiro.SecurityUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/11.
 */
@ControllerBind(controllerKey = "/rest/my_piece_group_purchase")
public class MyPieceGroupPurchaseController extends RestController {

    @Before(PieceGroupPurchaseConfigValidator.class)
    @Validation(rules = {
            "pageNumber=number",
            "pageSize=number",
    })
    public void index() {
        ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String status = getPara("status");
        Page<PieceGroupPurchaseMember> pieceGroupPurchaseMemberPagePage = PieceGroupPurchaseMember.dao.paginateByUserIdAndMasterStatus(pageNumber, pageSize, user.id, status);
        List<PieceGroupPurchaseMember> pieceGroupPurchaseMembers = pieceGroupPurchaseMemberPagePage.getList();
        for (PieceGroupPurchaseMember pieceGroupPurchaseMember : pieceGroupPurchaseMembers) {
            pieceGroupPurchaseMember.put("total_members_count", pieceGroupPurchaseMember.getPieceGroupPurchaseMaster().getMembersCount());
            pieceGroupPurchaseMember.put("paid_members_count", PieceGroupPurchaseMember.dao.findByMasterIdAndStatus(pieceGroupPurchaseMember.getMasterId(),
                    PieceGroupPurchaseMember.Status.PAID.toString())
                    .size());
            pieceGroupPurchaseMember.remove(PieceGroupPurchaseMember.Fields.ID.toString());
            pieceGroupPurchaseMember.remove(PieceGroupPurchaseMember.Fields.MASTER_ID.toString());
            pieceGroupPurchaseMember.put("payment_type", Order.dao.findByOrderNumber(pieceGroupPurchaseMember.getOrderNumber()).getPaymentType());
        }
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("pageNumber", pieceGroupPurchaseMemberPagePage.getPageNumber());
        map.put("pageSize", pieceGroupPurchaseMemberPagePage.getPageSize());
        map.put("totalPage", pieceGroupPurchaseMemberPagePage.getTotalPage());
        map.put("totalRow", pieceGroupPurchaseMemberPagePage.getTotalRow());
        map.put("list", pieceGroupPurchaseMemberPagePage.getList());
        renderSuccess(map);
    }

    @Before({PieceGroupPurchaseConfigValidator.class, CurrentUserInterceptor.class})
    public void show() {
        //validate
        User user = getAttr("currentUser");
        Integer pieceGroupPurchaseId = getParaToInt();
        if (pieceGroupPurchaseId == null) {
            renderFailure("pieceGroupPurchaseId.is.null");
            return;
        }
        PieceGroupPurchase pieceGroupPurchase = PieceGroupPurchase.dao.findById(pieceGroupPurchaseId);
        if (pieceGroupPurchase == null) {
            renderFailure("pieceGroupPurchase.not.found");
            return;
        }
        PieceGroupPurchaseMember pieceGroupPurchaseMember = PieceGroupPurchaseMember.dao.findByUserIdAndPieceGroupPurchaseIdAndMasterStatus(user.getId(), pieceGroupPurchaseId, null);
        if (pieceGroupPurchaseMember == null) {
            renderFailure("not.your.pieceGroupPurchase");
            return;
        }

        pieceGroupPurchaseMember.put("total_members_count", pieceGroupPurchaseMember.getPieceGroupPurchaseMaster().getMembersCount());
        pieceGroupPurchaseMember.put("paid_members_count", PieceGroupPurchaseMember.dao.findByMasterIdAndStatus(pieceGroupPurchaseMember.getMasterId(),
                PieceGroupPurchaseMember.Status.PAID.toString())
                .size());
        pieceGroupPurchaseMember.remove(PieceGroupPurchaseMember.Fields.ID.toString());
        pieceGroupPurchaseMember.remove(PieceGroupPurchaseMember.Fields.MASTER_ID.toString());
        pieceGroupPurchaseMember.put("payment_type", Order.dao.findByOrderNumber(pieceGroupPurchaseMember.getOrderNumber()).getPaymentType());
        pieceGroupPurchaseMember.put("order", Order.dao.findByOrderNumber(pieceGroupPurchaseMember.getOrderNumber()));
        renderSuccess(pieceGroupPurchaseMember);
    }
}
