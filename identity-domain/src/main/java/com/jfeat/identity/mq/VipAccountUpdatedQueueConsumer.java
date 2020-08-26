package com.jfeat.identity.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfeat.ext.plugin.rabbitmq.QueueConsumer;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfinal.kit.StrKit;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Vip account 更新会员信息时，同步商城的user数据。
 *
 * @author jackyhuang
 * @date 2019/1/22
 */
public class VipAccountUpdatedQueueConsumer extends QueueConsumer {

    public VipAccountUpdatedQueueConsumer(String endPointName) throws IOException, TimeoutException {
        super(endPointName);
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
            VipAccount account = JSON.parseObject(message, VipAccount.class);
            User user = User.dao.findByLoginName(account.getAccount());
            logger.debug("vipaccount={}, originuser={}", account, user);
            if (user != null) {
                if (StrKit.notBlank(account.getRealName())) {
                    user.setRealName(account.getRealName());
                }
                if (StrKit.notBlank(account.getVipName())) {
                    user.setName(account.getVipName());
                }
                if (StrKit.notBlank(account.getDob())) {
                    user.setBirthday(DateKit.toDate(account.getDob()));
                }
                if (account.getSex() != null) {
                    user.setSex(account.getSex());
                }
                if (StrKit.notBlank(account.getInviterAccount())) {
                    User inviter = User.dao.findByLoginName(account.getInviterAccount());
                    if (inviter != null) {
                        user.setInviterId(inviter.getId());
                    }
                }
                if ("".equals(account.getInviterAccount())) {
                    user.setInviterId(null);
                }
                user.updateWithoutNotify();
                logger.debug("user updated. {}", user);
            }

        } catch (Exception ex) {
            logger.error("consumer error. {}", ex.getMessage());
            logger.error(ex.getMessage());
            logger.error(ex.toString());
            for (StackTraceElement element : ex.getStackTrace()) {
                logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
            }
        }
    }
}

