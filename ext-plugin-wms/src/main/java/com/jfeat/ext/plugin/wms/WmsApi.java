package com.jfeat.ext.plugin.wms;

import com.jfeat.ext.plugin.*;
import com.jfeat.ext.plugin.wms.services.domain.model.*;
import com.jfeat.http.utils.HttpUtils;
import com.jfeat.http.utils.StrKit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/6/14
 */
public class WmsApi extends BaseApi {

    private static final String querySkusUrl = "/api/wms/skus?";
    private static final String getSkuUrl = "/api/wms/skus/%s?";
    private static final String increaseStockBalanceUrl = "/api/wms/storages/in/mall?";
    private static final String decreaseStockBalanceUrl = "/api/wms/storages/out/mall?";
    private static final String deliveredNotifyUrl = "/api/wms/storages/out/mall/update?";
    private static final String queryInventoriesUrl = "/api/wms/inventories?";
    private static final String queryWarehousesUrl = "/api/wms/warehouses?all=true";

    private static final String TRANSACTION_TYPE_SALES_IN = "SalesIn";
    private static final String TRANSACTION_TYPE_SALES_OUT = "SalesOut";
    private static final String TRANSACTION_CODE_IN = "IN";
    private static final String TRANSACTION_CODE_OUT = "OUT";

    public WmsApi() {
        BasePlugin wmsPlugin = ExtPluginHolder.me().get(WmsPlugin.class);
        init(wmsPlugin);
    }

    public QueryWarehouseResult queryWarehouse() {
        String url = getBaseUrl() + queryWarehousesUrl;
        String result = HttpUtils.get(url, null, getAuthorizationHeader());
        QueryWarehouseResult apiResult = QueryWarehouseResult.create(result);
        return apiResult;
    }

    public QuerySkusApiResult querySkus(Long userId, String account,
                                               Integer pageNum,
                                               Integer pageSize,
                                               String skuCode,
                                               String skuName,
                                               String barCode) {
        Map<String, String> queryParams = new HashMap<>();
        if (pageNum != null && pageNum > 0) {
            queryParams.put("pageNum", Integer.toString(pageNum));
        }
        if (pageSize != null && pageSize > 0) {
            queryParams.put("pageSize", Integer.toString(pageSize));
        }
        if (StrKit.notBlank(skuCode)) {
            queryParams.put("skuCode", skuCode);
        }
        if (StrKit.notBlank(skuName)) {
            queryParams.put("skuName", skuName);
        }
        if (StrKit.notBlank(barCode)) {
            queryParams.put("barCode", barCode);
        }

        String url = getBaseUrl() + querySkusUrl;
        String result = HttpUtils.get(url, queryParams, getAuthorizationHeader(null, userId, account));
        QuerySkusApiResult apiResult = QuerySkusApiResult.create(result);
        return apiResult;
    }

    public GetSkuApiResult getSku(String skuId) {
        String url = String.format(getBaseUrl() + getSkuUrl, skuId);
        String result = HttpUtils.get(url, null, getAuthorizationHeader());
        GetSkuApiResult apiResult = GetSkuApiResult.create(result);
        return apiResult;
    }

    /**
     * 退货入库
     * @param skuIds
     * @param amounts
     * @return
     */
    public AffectedApiResult increaseStockBalance(Long userId, String account,
                                                         String userName,
                                                         String orderNumber,
                                                         Long[] skuIds, Integer[] amounts, String note,
                                                         Long warehouseId) {
        if (skuIds == null || amounts == null || skuIds.length == 0 || skuIds.length != amounts.length) {
            throw new RuntimeException("invalid.skuids");
        }
        String url = getBaseUrl() + increaseStockBalanceUrl;

        StorageIn storageIn = new StorageIn();
        storageIn.setTransactionType(TRANSACTION_TYPE_SALES_IN);
        storageIn.setTransactionCode(TRANSACTION_CODE_IN + orderNumber);
        storageIn.setOutOrderNum(orderNumber);
        storageIn.setDistributorCustomer(userName);
        storageIn.setOriginatorName(userName);
        storageIn.setWarehouseId(warehouseId);
        storageIn.setNote(note);
        for (int i = 0; i < skuIds.length; i++) {
            StorageItem item = new StorageItem();
            item.setSkuId(skuIds[i]);
            item.setTransactionQuantities(amounts[i]);
            storageIn.getStorageInItems().add(item);
        }

        String dataJsonStr = JsonKit.toJson(storageIn);
        String result = HttpUtils.post(url, dataJsonStr, getAuthorizationHeader(null, userId, account));
        AffectedApiResult apiResult = AffectedApiResult.create(result);
        return apiResult;
    }

    /**
     * 销售出库
     * @param skuIds
     * @param amounts
     * @return
     */
    public AffectedApiResult decreaseStockBalance(Long userId, String account,
                                                         String userName,
                                                         String orderNumber,
                                                         Long[] skuIds,
                                                         Integer[] amounts,
                                                         BigDecimal[] skuPrices,
                                                         String note, Long warehouseId) {
        if (skuIds == null || amounts == null || skuIds.length == 0 || skuIds.length != amounts.length) {
            throw new RuntimeException("invalid.skuids");
        }
        String url = getBaseUrl() + decreaseStockBalanceUrl;

        StorageOut storageOut = new StorageOut();
        storageOut.setTransactionType(TRANSACTION_TYPE_SALES_OUT);
        storageOut.setTransactionCode(TRANSACTION_CODE_OUT + orderNumber);
        storageOut.setOutOrderNum(orderNumber);
        storageOut.setDistributorCustomer(userName);
        storageOut.setOriginatorName(userName);
        storageOut.setNote(note);
        storageOut.setWarehouseId(warehouseId);
        for (int i = 0; i < skuIds.length; i++) {
            StorageItem item = new StorageItem();
            item.setSkuId(skuIds[i]);
            item.setTransactionQuantities(amounts[i]);
            item.setTransactionSkuPrice(skuPrices[i]);
            storageOut.getStorageOutItems().add(item);
        }
        String dataJsonStr = JsonKit.toJson(storageOut);
        String result = HttpUtils.post(url, dataJsonStr, getAuthorizationHeader(null, userId, account));
        AffectedApiResult apiResult = AffectedApiResult.create(result);
        return apiResult;
    }

    /**
     * [{
     *     "skuId":"1234",
     *     "warehouseId":"4567",
     *     "orderCount":"100"
     *
     * }]
     * @param userId
     * @param account
     * @param userName
     * @param orderNumber
     * @param skuIds
     * @param amounts
     * @param warehouseIds
     * @param note
     * @return
     */
    public AffectedApiResult deliveredNotify(Long userId, String account,
                                                    String userName,
                                                    String orderNumber,
                                                    Long[] skuIds,
                                                    Integer[] amounts,
                                                    Long[] warehouseIds,
                                                    String note) {
        if (skuIds == null || amounts == null || skuIds.length == 0 || skuIds.length != amounts.length) {
            throw new RuntimeException("invalid.skuids");
        }

        String url = getBaseUrl() + deliveredNotifyUrl;
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < skuIds.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("skuId", skuIds[i]);
            map.put("orderCount", amounts[i]);
            map.put("warehouseId", warehouseIds[i]);
            list.add(map);
        }
        data.put("items", list);
        data.put("outOrderNum", orderNumber);
        String dataJsonStr = JsonKit.toJson(data);
        String result = HttpUtils.post(url, dataJsonStr, getAuthorizationHeader(null, userId, account));
        AffectedApiResult apiResult = AffectedApiResult.create(result);
        return apiResult;
    }

    public GetInventoryApiResult getInventory(Long userId, String account, Long skuId, Long warehouseId) {
        if (skuId == null || skuId <= 0) {
            throw new RuntimeException("sku id is required");
        }
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("skuId", Long.toString(skuId));

        if (warehouseId != null) {
            queryParams.put("warehouseId", Long.toString(warehouseId));
        }

        String url = getBaseUrl() + queryInventoriesUrl;

        String result = HttpUtils.get(url, queryParams, getAuthorizationHeader(null, userId, account));
        GetInventoryApiResult apiResult = GetInventoryApiResult.create(result);
        return apiResult;
    }

}
