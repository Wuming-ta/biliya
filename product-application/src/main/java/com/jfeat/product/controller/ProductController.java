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
package com.jfeat.product.controller;

import com.github.abel533.echarts.code.Orient;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.code.Y;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Pie;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.config.model.Config;
import com.jfeat.config.utils.ConfigUtils;
import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.UploadedFile;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.WmsPlugin;
import com.jfeat.flash.Flash;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfeat.product.model.*;
import com.jfeat.product.model.base.ProductSettlementProportionBase;
import com.jfeat.product.model.param.ProductParam;
import com.jfeat.product.service.ProductService;
import com.jfeat.product.util.UploadFileComparator;
import com.jfeat.ui.model.Widget;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ProductController extends BaseController {

    private ProductService productService = Enhancer.enhance(ProductService.class);

    private static final String PARTNER_LEVEL_ZONE_PREFIX = "product.partner_level_zone_";
    private static final Integer[] PARTNER_LEVEL_ZONE_LIST = {1, 2, 3};

    private static final String SHOW_SETTLEMENT_SETTING = "product.show_settlement_setting";
    private static final String SHOW_SPECIFICATION_SETTING = "product.show_specification_setting";

    private List<Map<String, String>> getPartnerLevelZones() {
        List<Map<String, String>> zones = Lists.newArrayList();
        for (Integer i : PARTNER_LEVEL_ZONE_LIST) {
            Config config = Config.dao.findByKey(PARTNER_LEVEL_ZONE_PREFIX + i);
            String name = "Zone" + i;
            if (config != null) {
                name = config.getValueToStr();
            }
            Map<String, String> zone = Maps.newHashMap();
            zone.put("key", String.valueOf(i));
            zone.put("name", name);
            zones.add(zone);
        }
        return zones;
    }

    private boolean showSpecificationSetting() {
        Config config = Config.dao.findByKey(SHOW_SPECIFICATION_SETTING);
        if (config != null) {
            return config.getValueToBoolean();
        }
        return false;
    }

    private boolean showSettlementSetting() {
        Config config = Config.dao.findByKey(SHOW_SETTLEMENT_SETTING);
        if (config != null) {
            return config.getValueToBoolean();
        }
        return false;
    }

    @Before({Flash.class})
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String name = getPara("name");
        String status = getPara("status");
        Integer categoryId = getParaToInt("categoryId");
        Integer promoted = getParaToInt("promoted");
        Integer isVirtual = getParaToInt("isVirtual");
        Integer isPresale = getParaToInt("isPresale");
        Integer zone = getParaToInt("zone");
        Integer purchaseStrategyId = getParaToInt("purchaseStrategyId");
        String barCode = getPara("barCode");
        String storeLocation = getPara("storeLocation");
        Integer mid = getParaToInt("mid");

        ProductParam param = new ProductParam(pageNumber, pageSize);
        param.setName(name).setStatus(status).setCategoryId(categoryId)
                .setIsVirtual(isVirtual)
                .setIsPresale(isPresale)
                .setPromoted(promoted).setZone(zone).setPurchaseStrategyId(purchaseStrategyId)
                .setBarCode(barCode).setStoreLocation(storeLocation).setMid(mid);

        setAttr("products", Product.dao.paginate(param));
        setAttr("statuses", Product.Status.values());
        List<ProductCategory> productCategories = productService.getProductCategories();
        setAttr("productCategories", productCategories);
        setAttr("purchaseStrategies", ProductPurchaseStrategy.dao.findAll());
        setAttr("partnerLevelZones", getPartnerLevelZones());
        setAttr("wmsPluginEnabled", ExtPluginHolder.me().get(WmsPlugin.class).isEnabled());
        setAttr("merchantList", SettledMerchant.dao.findByStatus(SettledMerchant.Status.APPROVED));
        keepPara();
    }

    public void stockBalanceLimited() {
        String status = getPara("status");
        Integer limit = getParaToInt("limit", 0);
        setAttr("products", Product.dao.findStockBalanceLimited(status, limit));
        setAttr("statuses", Product.Status.values());
        setAttr("limit", limit);
        keepPara();
    }

    @Override
    @RequiresPermissions("product.edit")
    public void edit() {
        Product product = Product.dao.findById(getParaToInt());
        if (product == null) {
            renderError(404);
            return;
        }

        setAttr("merchantList", SettledMerchant.dao.findByStatus(SettledMerchant.Status.APPROVED));
        setAttr("productTags", ProductTag.dao.findAll());
        setAttr("product", product);
        setAttr("statuses", Product.Status.values());
        List<ProductCategory> productCategories = productService.getProductCategories();
        setAttr("productCategories", productCategories);
        Config pointExchangeRate = Config.dao.findByKey("mall.point_exchange_rate");
        if (pointExchangeRate != null) {
            setAttr("pointExchangeRate", pointExchangeRate.getValueToInt());
        }

        setAttr("fareTemplates", FareTemplate.dao.findAll());
        setAttr("purchaseStrategies", ProductPurchaseStrategy.dao.findAll());
        setAttr("productBrands", ProductBrand.dao.findAll());
        setAttr("partnerLevelZones", getPartnerLevelZones());
        setAttr("showSettlementSetting", showSettlementSetting());
        setAttr("showSpecificationSetting", showSpecificationSetting());

        //系统已经在运行，所以之前的产品都没有7级提成设置，此处当用户编辑产品的时候，如果没有，则自动创建7级提成设置
        List<ProductSettlementProportion> productSettlementProportions = ProductSettlementProportion.dao.findByProductId(product.getId());
        List<ProductSettlementProportion> defaultProportions = productService.getProductSettlementProportions();

        List<ProductSettlementProportion> res = defaultProportions.stream()
                .filter(d -> productSettlementProportions.stream().noneMatch(p -> p.getType().equals(d.getType())))
                .collect(Collectors.toList());
        if (res.size() > 0) {
            productSettlementProportions.addAll(res);
        }

        setAttr("productSettlementProportions", productSettlementProportions);

        setAttr("wmsPluginEnabled", ExtPluginHolder.me().get(WmsPlugin.class).isEnabled());
        keepPara();
    }

    @Override
    @RequiresPermissions("product.edit")
    @Before({Tx.class})
    public void update() {
        List<UploadFile> uploadFiles = getUploadFiles();

        Integer[] unchangedVideoIds = getParaValuesToInt("video-id");
        List<UploadFile> videoFiles = uploadFiles.stream().filter(item -> item.getContentType().startsWith("video/")).collect(Collectors.toList());
        List<UploadFile> newVideoFiles = new ArrayList<>();
        Map<Integer, String> updatedVideos = new HashMap<>();
        for (UploadFile uploadFile : videoFiles) {
            if (uploadFile.getParameterName().startsWith("new-video-")) {
                newVideoFiles.add(uploadFile);
            } else if (uploadFile.getParameterName().startsWith("update-video-")) {
                Integer id = Integer.parseInt(uploadFile.getParameterName().split("update-video-")[1]);
                String url = getUploadFileUrl(uploadFile);
                if (url != null) {
                    updatedVideos.put(id, url);
                }
            }
        }
        newVideoFiles.sort(new UploadFileComparator("new-video-"));
        List<String> newVideos = new ArrayList<>();
        for (UploadFile uploadFile : newVideoFiles) {
            String url = getUploadFileUrl(uploadFile);
            if (url != null) {
                newVideos.add(url);
            }
        }

        List<UploadFile> coverFiles = uploadFiles.stream().filter(item -> item.getContentType().startsWith("image/")).collect(Collectors.toList());
        List<UploadFile> newCoverFiles = new ArrayList<>();
        Map<Integer, String> updatedCovers = new HashMap<>();
        for (UploadFile uploadFile : coverFiles) {
            if (uploadFile.getParameterName().startsWith("new-cover-")) {
                newCoverFiles.add(uploadFile);
            } else if (uploadFile.getParameterName().startsWith("update-cover-")) {
                Integer id = Integer.parseInt(uploadFile.getParameterName().split("update-cover-")[1]);
                String url = getUploadFileUrl(uploadFile);
                if (url != null) {
                    updatedCovers.put(id, url);
                }
            }
        }
        newCoverFiles.sort(new UploadFileComparator("new-cover-"));
        List<String> newCovers = new ArrayList<>();
        for (UploadFile uploadFile : newCoverFiles) {
            String url = getUploadFileUrl(uploadFile);
            if (url != null) {
                newCovers.add(url);
            }
        }

        Integer[] unchangedCoverIds = getParaValuesToInt("cover-id");

        List<UploadFile> banners = uploadFiles.stream().filter(item -> item.getContentType().startsWith("image/") && item.getParameterName().startsWith("update-banner"))
                .collect(Collectors.toList());
        String banner = getPara("banner");
        if (!banners.isEmpty()) {
            String url = getUploadFileUrl(banners.get(0));
            if (url != null) {
                banner = url;
            }
        }

        String description = getPara("description");
        Product product = getModel(Product.class);
        if (product.getBrandId() == null) {
            product.setBrandId(null);
        }
        product.setBanner(banner);

        //若产品关联了sku id，则使用库存系统的库存，不使用这里的库存
        if (product.getSkuId() != null) {
            product.setStockBalance(0);
            product.setBarcode(null);
            product.setStoreLocation(null);
        }
        List<ProductSpecification> specifications = getModels(ProductSpecification.class);
        if (specifications != null && !specifications.isEmpty()) {
            product.setSkuId(null);
            product.setSkuCode(null);
            product.setSkuName(null);
            product.setBarCode(null);

            //若某个规格关联了sku id，则使用库存系统的库存，不使用这里的库存
            for (ProductSpecification specification : specifications) {
                if (specification.getSkuId() != null) {
                    specification.setStockBalance(0);
                }
            }
        }
        Ret ret = productService.updateProduct(product,
                newCovers, updatedCovers, unchangedCoverIds,
                newVideos, updatedVideos, unchangedVideoIds,
                description, specifications);

        List<ProductProperty> productProperties = getModels(ProductProperty.class);
        if (productProperties.isEmpty()) {
            ProductProperty.dao.deleteByProductId(product.getId());
        }
        boolean oldPropertiesRemoved = false;
        for (ProductProperty p : productProperties) {
            if (p.getId() != null) {
                p.update();
            } else {
                //更换了产品的类别, 对应的属性也要变
                if (!oldPropertiesRemoved) {
                    ProductProperty.dao.deleteByProductId(product.getId());
                }
                p.setProductId(product.getId());
                p.save();
                oldPropertiesRemoved = true;
            }
        }

        //update purchase strategy
        Integer strategyId = getParaToInt("purchase_strategy_id");
        if (strategyId != null) {
            ProductPurchaseStrategy.dao.updateProductStrategy(product.getId(), strategyId);
        }

        List<ProductSettlementProportion> productSettlementProportions = getModels(ProductSettlementProportion.class);
        updateProductSettlementProportion(product, productSettlementProportions);


        // update tags
        Integer[] tags = getParaValuesToInt("tags");
        ProductTag.dao.deleteByProductId(product.getId());
        if (tags != null && tags.length > 0) {
            for (Integer tagId : tags) {
                ProductTag.dao.addProduct(tagId, product.getId());
            }
        }

        setFlash("message", getRes().get(ret.get(ProductService.MESSAGE).toString()));

        String returnUrl = getPara("returnUrl", "/product");
        redirect(urlDecode(returnUrl));
    }

    @RequiresPermissions("product.add")
    public void add() {
        List<ProductCategory> productCategories = productService.getProductCategories();
        setAttr("productCategories", productCategories);
        if (productCategories.size() == 0) {
            setFlash("message", getRes().get("product.category_is_empty"));
            redirect("/product");
            return;
        }
        Config pointExchangeRate = Config.dao.findByKey("mall.point_exchange_rate");
        if (pointExchangeRate != null) {
            setAttr("pointExchangeRate", pointExchangeRate.getValueToInt());
        }

        setAttr("merchantList", SettledMerchant.dao.findByStatus(SettledMerchant.Status.APPROVED));
        setAttr("productTags", ProductTag.dao.findAll());
        setAttr("fareTemplates", FareTemplate.dao.findAll());
        setAttr("purchaseStrategies", ProductPurchaseStrategy.dao.findAll());
        setAttr("productBrands", ProductBrand.dao.findAll());
        setAttr("partnerLevelZones", getPartnerLevelZones());
        setAttr("showSettlementSetting", showSettlementSetting());
        setAttr("showSpecificationSetting", showSpecificationSetting());
        setAttr("productSettlementProportions", productService.getProductSettlementProportions());

        setAttr("wmsPluginEnabled", ExtPluginHolder.me().get(WmsPlugin.class).isEnabled());

        keepPara();
    }


    private void updateProductSettlementProportion(Product product, List<ProductSettlementProportion> productSettlementProportions) {
        for (int i = 0; i < productSettlementProportions.size(); i++) {
            ProductSettlementProportion productSettlementProportion = productSettlementProportions.get(i);
            String paraNamePrefix = "proportion[" + productSettlementProportion.getId() + "].";
            ProductSettlementProportion.Proportion p = new ProductSettlementProportion.Proportion();
            p.setFixedvalue(getParaToBoolean(paraNamePrefix + ProductSettlementProportion.Proportion.FIXEDVALUE));
            p.setPercentage(getParaToBoolean(paraNamePrefix + ProductSettlementProportion.Proportion.PERCENTAGE));
            if (StrKit.notBlank(getPara(paraNamePrefix + ProductSettlementProportion.Proportion.VALUE))) {
                p.setValue(Double.parseDouble(getPara(paraNamePrefix + ProductSettlementProportion.Proportion.VALUE)));
            }
            productSettlementProportion.setProductId(product.getId());
            productSettlementProportion.setProportion(p.toString());
            productSettlementProportion.remove(ProductSettlementProportion.Fields.ID.toString());
        }
        productService.updateSettlementProportions(productSettlementProportions);
    }

    @Override
    @Before({Tx.class})
    @RequiresPermissions("product.add")
    public void save() {
        List<UploadFile> uploadFiles = getUploadFiles();
        List<UploadFile> videoFiles = uploadFiles.stream().filter(item -> item.getContentType().startsWith("video/"))
                .sorted( new UploadFileComparator("video-")).collect(Collectors.toList());
        List<String> videos = new ArrayList<>();
        for (UploadFile uploadFile : videoFiles) {
            String url = getUploadFileUrl(uploadFile);
            if (url != null) {
                videos.add(url);
            }
        }

        List<UploadFile> coverFiles = uploadFiles.stream().filter(item -> item.getContentType().startsWith("image/") && item.getParameterName().startsWith("cover-"))
                .sorted( new UploadFileComparator("cover-")).collect(Collectors.toList());
        List<String> covers = new ArrayList<>();
        for (UploadFile uploadFile : coverFiles) {
            String url = getUploadFileUrl(uploadFile);
            if (url != null) {
                covers.add(url);
            }
        }

        List<UploadFile> banners = uploadFiles.stream().filter(item -> item.getContentType().startsWith("image/") && item.getParameterName().startsWith("banner"))
                .collect(Collectors.toList());
        String banner = null;
        if (!banners.isEmpty()) {
            String url = getUploadFileUrl(banners.get(0));
            if (url != null) {
                banner = url;
            }
        }

        Product product = getModel(Product.class);
        product.setBanner(banner);
        //若产品关联了sku id，则使用库存系统的库存，不使用这里的库存
        if (product.getSkuId() != null) {
            product.setStockBalance(null);
            product.setBarcode(null);
            product.setStoreLocation(null);
        }
        String description = getPara("description");
        List<ProductSpecification> specifications = getModels(ProductSpecification.class);
        if (specifications != null && !specifications.isEmpty()) {
            product.setSkuId(null);
            product.setSkuCode(null);
            product.setSkuName(null);
            product.setBarCode(null);

            //若某个规格关联了sku id，则使用库存系统的库存，不使用这里的库存
            for (ProductSpecification specification : specifications) {
                if (specification.getSkuId() != null) {
                    specification.setStockBalance(null);
                }
            }
        }
        Ret ret = productService.createProduct(product, covers, videos, description, specifications);

        boolean publish = getParaToBoolean("publish");
        if (publish && ShiroMethod.hasPermission("product.direct_publish")) {
            product.setStatus(Product.Status.ONSELL.toString());
            product.update();
        }

        List<ProductProperty> productProperties = getModels(ProductProperty.class);
        for (ProductProperty p : productProperties) {
            p.setProductId(product.getId());
            p.save();
        }

        //update purchase stategy
        Integer strategyId = getParaToInt("purchase_strategy_id");
        if (strategyId != null) {
            ProductPurchaseStrategy.dao.updateProductStrategy(product.getId(), strategyId);
        }

        if (BaseService.isSucceed(ret)) {
            List<ProductSettlementProportion> productSettlementProportions = getModels(ProductSettlementProportion.class);
            updateProductSettlementProportion(product, productSettlementProportions);
        }

        // update tags
        Integer[] tags = getParaValuesToInt("tags");
        if (tags != null && tags.length > 0) {
            for (Integer tagId : tags) {
                ProductTag.dao.addProduct(tagId, product.getId());
            }
        }

        setFlash("message", getRes().get(ret.get(ProductService.MESSAGE).toString()));
        String returnUrl = getPara("returnUrl", "/product");
        redirect(urlDecode(returnUrl));
    }

    @RequiresPermissions("product.delete")
    public void delete() {
        Ret ret = productService.deleteProduct(getParaToInt());
        setFlash("message", getRes().get(ret.get(ProductService.MESSAGE).toString()));
        String returnUrl = getPara("returnUrl", "/product");
        redirect(urlDecode(returnUrl));
    }

    @RequiresPermissions("product.approve")
    public void approve() {
        changeStatus(new Product.Status[]{Product.Status.PENDING_APPROVAL}, Product.Status.APPROVED);
        String returnUrl = getPara("returnUrl", "/product");
        redirect(urlDecode(returnUrl));
    }

    @RequiresPermissions("product.edit")
    public void publish() {
        Product.Status targetStatus = Product.Status.PENDING_APPROVAL;
        if (ShiroMethod.hasPermission("product.direct_publish")) {
            targetStatus = Product.Status.ONSELL;
        }
        changeStatus(new Product.Status[]{Product.Status.DRAFT}, targetStatus);
        String returnUrl = getPara("returnUrl", "/product");
        redirect(urlDecode(returnUrl));
    }

    @RequiresPermissions("product.edit")
    public void onsell() {
        changeStatus(new Product.Status[]{Product.Status.APPROVED, Product.Status.OFFSELL}, Product.Status.ONSELL);
        String returnUrl = getPara("returnUrl", "/product");
        redirect(returnUrl);
    }

    @RequiresPermissions("product.edit")
    public void offsell() {
        changeStatus(new Product.Status[]{Product.Status.ONSELL}, Product.Status.OFFSELL);
        String returnUrl = getPara("returnUrl", "/product");
        redirect(urlDecode(returnUrl));
    }

    public void widget() {
        //record : status, count
        List<Record> productStatuses = Product.dao.calcStatusCount();
        setAttr("productStatuses", productStatuses);
        List<String> statuses = new LinkedList<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Product.Status status : Product.Status.values()) {
            String statusName = getRes().get("product.status." + status.toString().toLowerCase());
            statuses.add(statusName);

            long value = 0;
            for (Record record : productStatuses) {
                if (status.toString().equals(record.getStr("status"))) {
                    value = record.getLong("count");
                    break;
                }
            }

            Map<String, Object> map = new HashMap<>();
            map.put("value", value);
            map.put("name", statusName);
            dataList.add(map);
        }

        GsonOption option = new GsonOption();
        //option.title(getRes().get("product.widget.title"));
        option.tooltip().trigger(Trigger.item).formatter("{a} <br/>{b} : {c} ({d}%)");
        option.legend().orient(Orient.horizontal).x(X.center).y(Y.bottom).data(statuses);
        Pie pie = new Pie();
        pie.name(getRes().get("product.widget.title"));
        pie.radius("50%", "70%");
        pie.setData(dataList);
        pie.label().normal().show(false);
        pie.label().emphasis().show(true);
        option.series(pie);

        setAttr("option", option);
        Widget widget = Widget.dao.findFirstByField(Widget.Fields.NAME.toString(), "product.overview");
        setAttr("productWidgetDisplayName", widget.getDisplayName());
        setAttr("productWidgetName", widget.getName());
    }

    /**
     * ajax
     */
    public void getProductCategoryProperties() {
        Integer categoryId = getParaToInt();
        ProductCategory category = ProductCategory.dao.findById(categoryId);
        setAttr("category", category);
        render("_category_properties.html");
    }

    private void changeStatus(Product.Status[] fromStatus, Product.Status toStatus) {
        Product product = Product.dao.findById(getParaToInt());
        if (product == null) {
            renderError(404);
            return;
        }

        for (Product.Status status : fromStatus) {
            if (Product.Status.valueOf(product.getStatus()) == status) {
                product.setStatus(toStatus.toString());
                product.update();
                setFlash("message", getRes().get("product.status.success"));
                return;
            }
        }
    }

    private String saveToQiniu(UploadFile uploadFile) {
        String url = QiniuKit.me().upload(uploadFile.getFile().getAbsolutePath());
        if (url != null) {
            logger.debug("deleted after saved to qiniu.");
            uploadFile.getFile().delete();
            return QiniuKit.me().getFullUrl(url);
        }
        return null;
    }

    private List<UploadFile> getUploadFiles() {
        if (QiniuKit.me().isInited()) {
            return getFiles(QiniuKit.me().getTmpdir());
        }
        String subDir = productService.getProductUploadDir();
        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        return getFiles(PhotoGalleryConstants.me().getUploadPath(), policy);
    }

    private String getUploadFileUrl(UploadFile uploadFile) {
        if (QiniuKit.me().isInited()) {
            return saveToQiniu(uploadFile);
        }
        return UploadedFile.buildUrl(uploadFile, productService.getProductUploadDir());
    }

    private String urlDecode(String url) {
        return StringEscapeUtils.unescapeHtml4(url);
    }


}
