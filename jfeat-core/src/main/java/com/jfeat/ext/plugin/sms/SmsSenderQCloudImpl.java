package com.jfeat.ext.plugin.sms;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author jackyhuang
 * @date 2018/5/18
 */
public class SmsSenderQCloudImpl extends SmsSender {

    private static final Logger logger = LoggerFactory.getLogger(SmsSenderQCloudImpl.class);

    public SmsSenderQCloudImpl(ConfigData configData) {
        super(configData);
    }

    @Override
    public void send() {
        SmsSingleSender sender = new SmsSingleSender(Integer.parseInt(configData.getAppid()), configData.getAppkey());
        try {
            String[] params = null;
            if (this.params != null && !this.params.isEmpty()) {
                params = this.params.toArray(new String[0]);
            }
            else if (this.map != null && !this.map.isEmpty()) {
                params = this.map.values().toArray(new String[0]);
            }
            if (params == null) {
                logger.warn("no param set, will not send sms.");
                return;
            }
            SmsSingleSenderResult result = sender.sendWithParam("86", phone,
                    Integer.parseInt(configData.getDefaultTemplateId()),
                    params, configData.getSignName(), "", "");
            logger.debug("send result = {}", result);
        } catch (HTTPException | IOException e) {
            logger.error("Error sending sms. error = {}", e.getMessage());
            throw new SmsException(e.getMessage());
        }
    }
}
