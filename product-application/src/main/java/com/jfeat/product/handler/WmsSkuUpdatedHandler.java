package com.jfeat.product.handler;

import com.alibaba.fastjson.JSONObject;
import com.jfeat.ext.plugin.wms.WmsHandler;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;

import java.util.List;

/**
 * 更新产品关联的SKU信息
 *  {
 *  *         "id": 1,
 *  *         "skuCode": "2344",
 *  *         "barCode": "2233",
 *  *         "skuName": "xxff"
 *  }
 *
 * @author jackyhuang
 * @date 2018/12/18
 */
public class WmsSkuUpdatedHandler implements WmsHandler {

    @Override
    public void handle(JSONObject data) {
        if (data == null) {
            return;
        }

        Long id = data.getLong("id");
        String skuCode = data.getString("skuCode");
        String skuName = data.getString("skuName");
        String barCode = data.getString("barCode");

        List<Product> products = Product.dao.findByField(Product.Fields.SKU_ID.toString(), id);
        products.forEach(product -> {
            product.setSkuCode(skuCode);
            product.setSkuName(skuName);
            product.setBarCode(barCode);
            product.update();
        });

        List<ProductSpecification> productSpecifications = ProductSpecification.dao.findByField(ProductSpecification.Fields.SKU_ID.toString(), id);
        productSpecifications.forEach(productSpecification -> {
            productSpecification.setSkuCode(skuCode);
            productSpecification.setSkuName(skuName);
            productSpecification.setBarCode(barCode);
            productSpecification.update();
        });
    }
}
