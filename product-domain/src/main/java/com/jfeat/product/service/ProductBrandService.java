package com.jfeat.product.service;

import com.jfeat.core.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kang on 2017/4/5.
 */
public class ProductBrandService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(ProductBrandService.class);
    private String uploadDir = "pb";

    public ProductBrandService() {
    }

    public String getUploadDir() {
        return this.uploadDir;
    }
}


