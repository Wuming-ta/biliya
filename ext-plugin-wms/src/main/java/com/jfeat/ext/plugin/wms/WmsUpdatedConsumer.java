package com.jfeat.ext.plugin.wms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfeat.ext.plugin.rabbitmq.QueueConsumer;
import com.jfinal.kit.StrKit;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * WMS 数据更新处理
 *
 * message:
 * {
 *     "type": "SKU",
 *     "data": {
 *         "id": 1,
 *         "skuCode": "2344",
 *         "barCode": "2233",
 *         "skuName": "xxff"
 *     }
 * }
 *
 * @author jackyhuang
 * @date 2018/12/18
 */
public class WmsUpdatedConsumer extends QueueConsumer {

    private Map<WmsType, WmsHandler> handlers = new HashMap<>();

    public WmsUpdatedConsumer(String endPointName) throws IOException, TimeoutException {
        super(endPointName);
    }

    public void addHandler(WmsType type, WmsHandler handler) {
        handlers.put(type, handler);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope env, AMQP.BasicProperties props, byte[] body) {
        try {
            String message = SerializationUtils.deserialize(body);
            if (StrKit.isBlank(message)) {
                logger.debug("message is empty.");
                return;
            }

            logger.debug("message is {}", message);
            JSONObject jsonObject = JSON.parseObject(message);
            String type = jsonObject.getString("type");
            WmsType wmsType = WmsType.valueOf(type);
            JSONObject data = jsonObject.getJSONObject("data");
            WmsHandler handler = handlers.get(wmsType);
            if (handler != null) {
                logger.debug("handling type = {} data = {}", wmsType, data);
                handler.handle(data);
            }
        } catch (Exception ex) {
            logger.error("consumer error. {}", ex.getMessage());
        }
    }
}
