package com.jfeat.ext.plugin.rabbitmq;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by jackyhuang on 2017/8/16.
 */
public class RabbitMQPlugin implements IPlugin {

    private static final Logger LOG = LoggerFactory.getLogger("RabbitMQPlugin");


    private Channel channel;
    private Connection connection;

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String virtualHost;

    public RabbitMQPlugin() {
        this(null, null, null, null, null);
    }

    public RabbitMQPlugin(String host, Integer port, String username, String password) {
        this(host, port, username, password, null);
    }

    public RabbitMQPlugin(String host, Integer port, String username, String password, String virtualHost) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.virtualHost = virtualHost;
    }

    @Override
    public boolean start() {
        if (RabbitMQKit.isInited()) {
            LOG.warn("RabbitMQ already inited. ");
            return true;
        }
        ConnectionFactory factory = new ConnectionFactory();
        if (StrKit.notBlank(host)) {
            factory.setHost(host);
        }
        if (port != null) {
            factory.setPort(port);
        }
        if (StrKit.notBlank(username)) {
            factory.setUsername(username);
        }
        if (StrKit.notBlank(password)) {
            factory.setPassword(password);
        }
        if (StrKit.notBlank(virtualHost)) {
            factory.setVirtualHost(virtualHost);
        }
        try {
            this.connection = factory.newConnection();
            this.channel = this.connection.createChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            LOG.error("start rabbitmq error. {}", e.getMessage());
            return false;
        }

        RabbitMQKit.init(channel);
        LOG.info("RabbitMQ Plugin started.");
        return true;
    }

    @Override
    public boolean stop() {
        try {
            this.channel.close();
            this.connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
