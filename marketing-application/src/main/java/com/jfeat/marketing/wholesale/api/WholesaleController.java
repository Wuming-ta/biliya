package com.jfeat.marketing.wholesale.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.core.RestController;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.marketing.exception.WholesalePricingException;
import com.jfeat.marketing.wholesale.api.validator.PhysicalCrownAuthorityValidator;
import com.jfeat.marketing.wholesale.api.validator.WholesaleConfigValidator;
import com.jfeat.marketing.wholesale.model.Wholesale;
import com.jfeat.marketing.wholesale.service.WholesaleService;
import com.jfeat.partner.model.SettlementProportion;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductImage;
import com.jfeat.product.model.ProductSpecification;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/25.
 */
@ControllerBind(controllerKey = "/rest/wholesale")
public class WholesaleController extends RestController {

    private WholesaleService wholesaleService = Enhancer.enhance(WholesaleService.class);

    @Before({WholesaleConfigValidator.class, PhysicalCrownAuthorityValidator.class})
    public void index() {
        Subject subject = SecurityUtils.getSubject();
        ShiroUser user = (ShiroUser) subject.getPrincipal();
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        Integer categoryId = getParaToInt("categoryId");
        Page<Wholesale> wholesalePage = Wholesale.dao.paginate(pageNumber, pageSize, categoryId, null, Wholesale.Status.ONSELL.toString());
        ListIterator<Wholesale> listIterator = wholesalePage.getList().listIterator();
        while (listIterator.hasNext()) {
            Wholesale wholesale = listIterator.next();
            try {
                wholesale.put("pricing", wholesaleService.getWholesalePricing(wholesale, wholesaleService.getRegion(user.id)));
            } catch (WholesalePricingException e) {
                listIterator.remove();
                continue;
            }
            Product product = wholesale.getProduct();
            List<ProductImage> covers = product.getCovers();
            List<String> coverUrls = Lists.newLinkedList();
            for (ProductImage productImage : covers) {
                coverUrls.add(productImage.getUrl());
            }
            product.put("covers", coverUrls);
            List<ProductSpecification> specifications = product.getProductSpecifications();
            product.put("specifications", specifications);
            wholesale.put("product", product);
        }

        Map<String, Object> resultMap = Maps.newLinkedHashMap();
        resultMap.put("totalRow", wholesalePage.getTotalRow());
        resultMap.put("totalPage", wholesalePage.getTotalPage());
        resultMap.put("pageNumber", wholesalePage.getPageNumber());
        resultMap.put("pageSize", wholesalePage.getPageSize());
        resultMap.put("firstPage", wholesalePage.isFirstPage());
        resultMap.put("lastPage", wholesalePage.isLastPage());
        resultMap.put("list", wholesalePage.getList());
        renderSuccess(resultMap);
    }

    @Before({WholesaleConfigValidator.class, PhysicalCrownAuthorityValidator.class})
    public void show() {
        Subject subject = SecurityUtils.getSubject();
        ShiroUser user = (ShiroUser) subject.getPrincipal();
        Wholesale wholesale = Wholesale.dao.findById(getParaToInt());
        if (wholesale == null) {
            renderFailure("wholesale.not.found");
            return;
        }
        Product product = wholesale.getProduct();
        List<ProductImage> covers = product.getCovers();
        List<String> coverUrls = Lists.newLinkedList();
        for (ProductImage productImage : covers) {
            coverUrls.add(productImage.getUrl());
        }
        product.put("covers", coverUrls);
        List<ProductSpecification> specifications = product.getProductSpecifications();
        product.put("specifications", specifications);
        wholesale.put("product", product);
        try {
            wholesale.put("pricing", wholesaleService.getWholesalePricing(wholesale, wholesaleService.getRegion(user.id)));
        } catch (WholesalePricingException e) {
            renderFailure(e.getMessage());
            return;
        }
        List<SettlementProportion> settlementProportions= SettlementProportion.dao.findByType(SettlementProportion.Type.PHYSICAL_CROWN);
        Double proportionLv1 = null;
        Double proportionLv2=null;
        if(settlementProportions.size()>0 && wholesale.getSettlementProportion()!=null) {
            proportionLv1=settlementProportions.get(0).getProportionObject().getValue() * wholesale.getSettlementProportion()/100;
            proportionLv2=settlementProportions.get(1).getProportionObject().getValue() * wholesale.getSettlementProportion()/100;
        }
        wholesale.put("proportionLv1",proportionLv1);
        wholesale.put("proportionLv2",proportionLv2);
        renderSuccess(wholesale);
    }
}

