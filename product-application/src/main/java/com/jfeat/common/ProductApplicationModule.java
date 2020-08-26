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
package com.jfeat.common;

import com.jfeat.config.model.Config;
import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.WmsPlugin;
import com.jfeat.ext.plugin.rabbitmq.RabbitMQPlugin;
import com.jfeat.ext.plugin.wms.WmsType;
import com.jfeat.ext.plugin.wms.WmsUpdatedHandler;
import com.jfeat.observer.ObserverKit;
import com.jfeat.product.handler.PromotedProductCarouselUpdatedHandler;
import com.jfeat.product.handler.WmsSkuUpdatedHandler;
import com.jfeat.product.util.CategoryPromotedCarousel;
import com.jfinal.config.Constants;
import com.jfinal.config.Plugins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductApplicationModule extends Module {

    private static Logger logger = LoggerFactory.getLogger(ProductApplicationModule.class);

    public ProductApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        ProductApplicationModelMapping.mapping(this);

        addXssExcluded("/product");

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(com.jfeat.product.controller.ProductCategoryController.class);
        addController(com.jfeat.product.controller.ProductController.class);
        addController(com.jfeat.product.api.ProductController.class);
        addController(com.jfeat.product.api.ProductCategoryController.class);
        addController(com.jfeat.product.api.ProductSearchController.class);
        addController(com.jfeat.product.api.ProductFavoriteController.class);
        addController(com.jfeat.product.api.ProductHitWordController.class);
        addController(com.jfeat.product.controller.FareTemplateController.class);
        addController(com.jfeat.product.controller.PurchaseStrategyController.class);
        addController(com.jfeat.product.controller.ProductHitWordController.class);
        addController(com.jfeat.product.api.ProductPurchaseStrategyController.class);
        addController(com.jfeat.product.controller.ProductBrandController.class);
        addController(com.jfeat.product.controller.SkuSelectController.class);
        addController(com.jfeat.product.controller.ProductTagController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new PcdDomainModule(jfeatConfig);
        new ConfigDomainModule(jfeatConfig);
        new ProductDomainModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);
        new MerchantApplicationModule(jfeatConfig);

        ObserverKit.me().register(Config.class, Config.EVENT_UPDATE, PromotedProductCarouselUpdatedHandler.class);
    }

    @Override
    public void configPlugin(Plugins me) {
        super.configPlugin(me);

        boolean wmsUpdatedSyncEnabled = getJFeatConfig().getPropertyToBoolean("ext.wms.updated.sync.enabled", false);
        if (wmsUpdatedSyncEnabled) {
            String rabbitmqHost = getJFeatConfig().getProperty("ext.wms.updated.rabbitmq.host", "localhost");
            Integer rabbitmqPort = getJFeatConfig().getPropertyToInt("ext.wms.updated.rabbitmq.port", 5672);
            String rabbitmqUsername = getJFeatConfig().getProperty("ext.wms.updated.rabbitmq.username", "guest");
            String rabbitmqPassword = getJFeatConfig().getProperty("ext.wms.updated.rabbitmq.password", "guest");
            RabbitMQPlugin rabbitMQPlugin = new RabbitMQPlugin(rabbitmqHost, rabbitmqPort, rabbitmqUsername, rabbitmqPassword);
            me.add(rabbitMQPlugin);
        }
    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        Config config = Config.dao.findByKey("mall.promoted_product_carousel");
        if (config != null && config.getValueToInt() != null) {
            int timeout = config.getValueToInt() > 30 ? config.getValueToInt() : 30;
            CategoryPromotedCarousel.me().setCarouselTimeout(timeout * 60 * 1000);
            logger.info("Category Promoted Carousel timeout set as {} minutes.", timeout);
        }

        boolean wmsUpdatedSyncEnabled = getJFeatConfig().getPropertyToBoolean("ext.wms.updated.sync.enabled", false);
        String rabbitmqQueue = getJFeatConfig().getProperty("ext.wms.updated.rabbitmq.queue", "wms-update-queue");
        if (wmsUpdatedSyncEnabled) {
            WmsUpdatedHandler.me()
                    .setEndpointName(rabbitmqQueue)
                    .registerHandler(WmsType.SKU, new WmsSkuUpdatedHandler())
                    .init();
        }
    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);
        WmsPlugin wmsPlugin = new WmsPlugin(
                getJFeatConfig().getPropertyToBoolean("ext.wms.enabled", false),
                getJFeatConfig().getProperty("ext.wms.api.host", getJFeatConfig().getProperty("ext.api.host")),
                getJFeatConfig().getProperty("ext.wms.jwt.key", getJFeatConfig().getProperty("ext.jwt.key")));
        ExtPluginHolder.me().start(WmsPlugin.class, wmsPlugin);
    }
}
