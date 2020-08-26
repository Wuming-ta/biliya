package com.jfeat.order;

import com.google.common.collect.Lists;
import com.jfeat.ext.plugin.BasePlugin;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.WmsPlugin;
import com.jfeat.ext.plugin.wms.WmsApi;
import com.jfeat.ext.plugin.wms.services.domain.model.Inventory;
import com.jfeat.identity.model.User;
import com.jfeat.order.api.model.OrderCustomerServiceItemEntity;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderCustomerServiceItem;
import com.jfeat.order.model.OrderItem;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/9/3
 */
public class OrderCustomerServiceUtil {


    /**
     * 根据returns构建要保存的List<OrderCustomerServiceItem>（无论是有关联订单的退货单，还是没有关联订单的退货单，还是必须关联订单的换货单都可使用此方法）
     *
     * @param order                               可以为null，当不为null时，会检查returns中的项是否是此order的原有项
     * @param returns
     * @return
     */
    public static List<OrderCustomerServiceItem> buildReturns(Order order, List<OrderCustomerServiceItemEntity> returns) {
        List<OrderCustomerServiceItem> orderCustomerServiceItems = Lists.newArrayList();
        if (order != null) { //有指定关联order的售后单
            for (OrderCustomerServiceItemEntity refundItemEntity : returns) {
                //是产品还是产品规格(没指定规格id，即为产品；有指定，即为规格）
                boolean isProduct = refundItemEntity.getProduct_specification_id() == null;
                Product product = Product.dao.findById(refundItemEntity.getProduct_id());
                ProductSpecification productSpecification = null;
                OrderItem orderItem = null;
                if (product == null) {
                    throw new RuntimeException(String.format("售后单创建失败 - 产品(id:%s)不存在", refundItemEntity.getProduct_id()));
                }
                if (isProduct) {
//                    if (product.getSkuId() == null) {
//                        throw new RuntimeException(String.format("售后单创建失败 - 产品(%s)没有相关的sku id", product.getName()));
//                    }
                    orderItem = OrderItem.dao.findFirstByOrderIdProductIdProductSpecificationId(order.getId(),product.getId(),refundItemEntity.getProduct_specification_id());
                    if (orderItem == null) {
                        throw new RuntimeException(String.format("售后单创建失败 - 您先前的订单中并没此产品(%s)", product.getName()));
                    }
                } else {
                    productSpecification = ProductSpecification.dao.findById(refundItemEntity.getProduct_specification_id());
                    if (productSpecification == null) {
                        throw new RuntimeException(String.format("售后单创建失败 - 规格（id:%s)不存在", refundItemEntity.getProduct_specification_id()));
                    }
                    if (!productSpecification.getProductId().equals(product.getId())) {
                        throw new RuntimeException(String.format("售后单创建失败 - 产品(%s)没有(%s)这种规格", product.getName(), productSpecification.getName()));
                    }
//                    if (productSpecification.getSkuId() == null) {
//                        throw new RuntimeException(String.format("售后单创建失败 - 产品规格(%s)没有相关的sku id", productSpecification.getName()));
//                    }
                    orderItem = OrderItem.dao.findFirstByOrderIdProductIdProductSpecificationId(order.getId(),product.getId(),refundItemEntity.getProduct_specification_id());
                    if (orderItem == null) {
                        throw new RuntimeException(String.format("售后单创建失败 - 您先前的订单中并没有购买规格为(%s)的产品(%s)", productSpecification.getName(), product.getName()));
                    }
                    if (orderItem.getStatus().equals(OrderItem.Status.REFUNDED.toString())
                            || orderItem.equals(OrderItem.Status.REFUNDING.toString())) {
                        throw new RuntimeException(String.format("产品 %s 正在退货中，不能重复申请", product.getName()));
                    }
                }

                OrderCustomerServiceItem orderCustomerServiceItem = new OrderCustomerServiceItem();
                orderCustomerServiceItem.setPrice(orderItem.getPrice());
                orderCustomerServiceItem.setCostPrice(orderItem.getCostPrice());
                orderCustomerServiceItem.setFinalPrice(orderItem.getFinalPrice());
                orderCustomerServiceItem.setQuantity(orderItem.getQuantity());
                orderCustomerServiceItem.setRefundFee(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));

                if (refundItemEntity.getQuantity() != null && refundItemEntity.getQuantity().compareTo(orderItem.getQuantity()) <= 0) {
                    orderCustomerServiceItem.setQuantity(refundItemEntity.getQuantity());
                    orderCustomerServiceItem.setRefundFee(orderItem.getPrice().multiply(BigDecimal.valueOf(refundItemEntity.getQuantity())));
                }

                if (refundItemEntity.getRefund_fee() != null && refundItemEntity.getRefund_fee().compareTo(orderCustomerServiceItem.getRefundFee()) <= 0) {
                    orderCustomerServiceItem.setRefundFee(refundItemEntity.getRefund_fee());
                }


                orderCustomerServiceItem.setCover(orderItem.getCover());
                orderCustomerServiceItem.setMarketingId(orderItem.getMarketingId());
                orderCustomerServiceItem.setMarketing(orderItem.getMarketing());
                orderCustomerServiceItem.setMarketingDescription(orderItem.getMarketingDescription());
                orderCustomerServiceItem.setProductId(orderItem.getProductId());
                orderCustomerServiceItem.setProductName(orderItem.getProductName());
                orderCustomerServiceItem.setProductSpecificationId(orderItem.getProductSpecificationId());
                orderCustomerServiceItem.setProductSpecificationName(orderItem.getProductSpecificationName());
                orderCustomerServiceItem.setWeight(orderItem.getWeight());
                orderCustomerServiceItems.add(orderCustomerServiceItem);
            }

        } else {
            for (OrderCustomerServiceItemEntity refundItemEntity : returns) {
                boolean isProduct = refundItemEntity.getProduct_specification_id() == null;
                Product product = Product.dao.findById(refundItemEntity.getProduct_id());
                ProductSpecification productSpecification = null;
                if (product == null) {
                    throw new RuntimeException(String.format("售后单创建失败 - 产品(id:%s)不存在", refundItemEntity.getProduct_id()));
                }
                if (isProduct) {
//                    if (product.getSkuId() == null) {
//                        throw new RuntimeException("售后单创建失败 - 部分产品没有相关的sku id");
//                    }
                } else {
                    productSpecification = ProductSpecification.dao.findById(refundItemEntity.getProduct_specification_id());
                    if (productSpecification == null) {
                        throw new RuntimeException(String.format("售后单创建失败 - 规格（id:%s)不存在", refundItemEntity.getProduct_specification_id()));
                    }
                    if (!productSpecification.getProductId().equals(product.getId())) {
                        throw new RuntimeException(String.format("售后单创建失败 - 产品(%s)没有(%s)这种规格", product.getName(), productSpecification.getName()));
                    }
//                    if (productSpecification.getSkuId() == null) {
//                        throw new RuntimeException(String.format("售后单创建失败 - 产品规格(%s)没有相关的sku id", productSpecification.getName()));
//                    }
                }

                OrderCustomerServiceItem orderCustomerServiceItem = new OrderCustomerServiceItem();
                orderCustomerServiceItem.setPrice(product.getPrice());
                orderCustomerServiceItem.setCostPrice(product.getCostPrice());
                orderCustomerServiceItem.setFinalPrice(product.getPrice().multiply(BigDecimal.valueOf(refundItemEntity.getQuantity())));
                orderCustomerServiceItem.setQuantity(refundItemEntity.getQuantity());
                orderCustomerServiceItem.setRefundFee(refundItemEntity.getRefund_fee());
                orderCustomerServiceItem.setCover(product.getCover());

                orderCustomerServiceItem.setProductId(product.getId());
                orderCustomerServiceItem.setProductName(product.getName());

                if (productSpecification != null) {
                    orderCustomerServiceItem.setProductSpecificationName(productSpecification.getName());
                }
                orderCustomerServiceItem.setWeight(product.getWeight());
                orderCustomerServiceItems.add(orderCustomerServiceItem);
            }
        }
        return orderCustomerServiceItems;
    }

    /**
     * 根据exchanges构建要保存的List<OrderCustomerServiceItem>（换货单使用此方法）
     *
     * @param exchanges
     * @return
     */
    public static List<OrderCustomerServiceItem> buildExchanges(User user, List<OrderCustomerServiceItemEntity> exchanges) {

        List<OrderCustomerServiceItem> orderCustomerServiceItems = Lists.newArrayList();
        for (OrderCustomerServiceItemEntity exchangeItemEntity : exchanges) {
            if (exchangeItemEntity.getQuantity() == null || exchangeItemEntity.getQuantity() <= 0) {
                throw new RuntimeException("售后单创建失败 - 置换项必须指定要置换的数量");
            }

            //是产品还是产品规格(没指定规格id，即为产品；有指定，即为规格）
            boolean isProduct = exchangeItemEntity.getProduct_specification_id() == null;
            Product product = Product.dao.findById(exchangeItemEntity.getProduct_id());
            ProductSpecification productSpecification = null;
            if (product == null) {
                throw new RuntimeException(String.format("售后单创建失败 - 产品(id:%s)不存在", exchangeItemEntity.getProduct_id()));
            }
            if (isProduct) {
//                if (product.getSkuId() == null) {
//                    throw new RuntimeException(String.format("售后单创建失败 - 产品(%s)没有相关的sku id", product.getName()));
//                }

                //检查库存
//                Inventory inventory = WmsApi.getInventory(user.getId().longValue(), user.getLoginName(), Long.parseLong(product.getSkuId()), null).getData();
//                if (inventory.getValidSku() == null || inventory.getValidSku().compareTo(exchangeItemEntity.getQuantity()) < 0) {
//                    throw new RuntimeException(String.format("售后单创建失败 - 产品(%s)库存不足", product.getName()));
//                }
            } else {
                productSpecification = ProductSpecification.dao.findById(exchangeItemEntity.getProduct_specification_id());
                if (productSpecification == null) {
                    throw new RuntimeException(String.format("售后单创建失败 - 规格（id:%s)不存在", exchangeItemEntity.getProduct_specification_id()));
                }
                if (!productSpecification.getProductId().equals(product.getId())) {
                    throw new RuntimeException(String.format("售后单创建失败 - 产品(%s)没有(%s)这种规格", product.getName(), productSpecification.getName()));
                }
//                if (productSpecification.getSkuId() == null) {
//                    throw new RuntimeException(String.format("售后单创建失败 - 产品规格(%s)没有相关的sku id", productSpecification.getName()));
//                }

                //检查库存
//                Inventory inventory = WmsApi.getInventory(user.getId().longValue(), user.getLoginName(), Long.parseLong(productSpecification.getSkuId()), null).getData();
//                if (inventory.getValidSku() == null || inventory.getValidSku().compareTo(exchangeItemEntity.getQuantity()) < 0) {
//                    throw new RuntimeException(String.format("售后单创建失败 - 产品(%s)的规格(%s)库存不足", product.getName(), productSpecification.getName()));
//                }
            }


            OrderCustomerServiceItem orderCustomerServiceItem = new OrderCustomerServiceItem();
            orderCustomerServiceItem.setRefundFee(BigDecimal.ZERO); //置换项无需设置退回金额
            orderCustomerServiceItem.setMarketingId(null);
            orderCustomerServiceItem.setMarketing(null);
            orderCustomerServiceItem.setMarketingDescription(null);

            orderCustomerServiceItem.setPrice(isProduct ? product.getPrice() : productSpecification.getPrice());
            orderCustomerServiceItem.setCostPrice(isProduct ? product.getCostPrice() : productSpecification.getCostPrice());
            orderCustomerServiceItem.setQuantity(exchangeItemEntity.getQuantity());
            orderCustomerServiceItem.setFinalPrice(orderCustomerServiceItem.getPrice().multiply(BigDecimal.valueOf(orderCustomerServiceItem.getQuantity()))); //置换项金额
            orderCustomerServiceItem.setCover(product.getCover());
            orderCustomerServiceItem.setProductId(product.getId());
            orderCustomerServiceItem.setProductName(product.getName());
            orderCustomerServiceItem.setProductSpecificationId(isProduct ? null : productSpecification.getId());
            orderCustomerServiceItem.setProductSpecificationName(isProduct ? null : productSpecification.getName());
            orderCustomerServiceItem.setWeight(isProduct ? product.getWeight() : productSpecification.getWeight());
            orderCustomerServiceItems.add(orderCustomerServiceItem);
        }

        return orderCustomerServiceItems;
    }

}
