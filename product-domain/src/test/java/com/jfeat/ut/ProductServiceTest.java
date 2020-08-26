package com.jfeat.ut;

import com.jfeat.AbstractTestCase;
import com.jfeat.config.model.Config;
import com.jfeat.product.exception.StockBalanceException;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfeat.product.service.ProductService;
import com.jfinal.aop.Enhancer;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by kang on 2016/11/3.
 */
public class ProductServiceTest extends AbstractTestCase {
    private ProductCategory category;
    private ProductCategory subCategory;
    private ProductService productService = Enhancer.enhance(ProductService.class);
    private List<Integer> ids = new ArrayList<>();
    private static String MALL_AUTO_OFFSELL = "mall.auto_offsell";

    @Before
    public void setup() {
        category = new ProductCategory();
        category.setName("c1");
        category.save();
        subCategory = new ProductCategory();
        subCategory.setParentId(category.getId());
        subCategory.setName("subc1");
        subCategory.save();
        for (int i = 0; i < 2; i++) {
            Product product = new Product();
            product.setName("p" + i);
            product.setShortName("p" + i);
            product.setCategoryId(subCategory.getId());
            product.setPrice(new BigDecimal(50));
            product.setStatus(Product.Status.ONSELL.toString());
            if (i == 0) {
                product.setStockBalance(100);
            } else {
                product.setStockBalance(0);
            }
            product.save();
            ids.add(product.getId());
        }
    }

    @Test
    public void testOffSellProduct() {
        //没有Config 或者 有Config但库存校验未开启
        Config config = Config.dao.findByKey(MALL_AUTO_OFFSELL);
        if (config == null) {
            config = new Config();
            config.setKeyName(MALL_AUTO_OFFSELL);
            config.setValueType(Config.ValueType.BOOLEAN.toString());
            config.save();
        }
        config.setValue("false");
        config.update();
        productService.offSellProduct(ids);
        assertEquals(Product.dao.findById(ids.get(0)).getStatus(), Product.Status.ONSELL.toString());
        assertEquals(Product.dao.findById(ids.get(1)).getStatus(), Product.Status.ONSELL.toString());
        //有Config且库存校验已开启
        config.setValue("true");
        config.update();
        productService.offSellProduct(ids);
        assertEquals(Product.dao.findById(ids.get(0)).getStatus(), Product.Status.ONSELL.toString());
        assertEquals(Product.dao.findById(ids.get(1)).getStatus(), Product.Status.OFFSELL.toString());
    }

    @Test(expected = RuntimeException.class)
    public void testIncreaseProductSalesFailure() throws StockBalanceException {
        List<Integer> productIds = new ArrayList<>();
        List<Integer> specificationIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        for (Integer id : ids) {
            productIds.add(id);
            specificationIds.add(null);
            quantities.add(1);
        }
        productService.increaseProductSales(null, null, null, productIds, specificationIds, quantities, null, null);
        Product product = Product.dao.findById(ids.get(0));
        assertNotNull(product);
        assertEquals(100, product.getStockBalance().longValue());
        assertEquals(0, product.getSales().longValue());
    }

    @Test
    public void testIncreaseProductSales() throws StockBalanceException {
        List<Integer> productIds = new ArrayList<>();
        List<Integer> specificationIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        for (Integer id : ids) {
            productIds.add(id);
            specificationIds.add(null);
            quantities.add(1);
            break;
        }

        productService.increaseProductSales(null, null, null, productIds, specificationIds, quantities, null, null);
        Product product = Product.dao.findById(ids.get(0));
        assertNotNull(product);
        assertEquals(99, product.getStockBalance().longValue());
        assertEquals(1, product.getSales().longValue());

        productService.decreaseProductSales(null, null,null, productIds, specificationIds, quantities, null, null);
        product = Product.dao.findById(ids.get(0));
        assertNotNull(product);
        assertEquals(100, product.getStockBalance().longValue());
        assertEquals(0, product.getSales().longValue());
    }
}
