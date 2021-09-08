/**
 * Copyright (c) 2015, 玛雅牛［李飞］ (lifei@wellbole.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jfeat.ext.plugin.zbus;

import com.jfeat.ext.plugin.zbus.annotation.MqHandler;
import com.jfeat.ext.plugin.zbus.annotation.TopicHandler;
import com.jfeat.ext.plugin.zbus.handler.TMsgHandler;
import com.jfeat.ext.plugin.zbus.sender.Sender;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zbus.broker.Broker;
import org.zbus.broker.BrokerConfig;
import org.zbus.broker.JvmBroker;
import org.zbus.broker.SingleBroker;
import org.zbus.mq.Consumer;
import org.zbus.mq.Producer;
import org.zbus.mq.Protocol.MqMode;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * @ClassName: ZbusPlugin
 * @Description: JFinal的Zbus插件实现
 * @author 李飞 (lifei@wellbole.com)
 * @date 2015年7月29日 下午12:46:32
 * @since V1.0.0
 */
public class ZbusPlugin implements IPlugin {
	
	/**
	 * 日志
	 */
	private static final Logger LOG = LoggerFactory.getLogger("ZbusPlugin");

	/**
	 * MQ消费者配置Map
	 */
	private final Map<String, TMsgHandler<?>> mqNameTMsgHandlerMap = new HashMap<>();

	/**
	 * Topic消费者配置Map mp - topic - TMsgHandler
	 */
	private final Map<String, Map<String, TMsgHandler<?>>> mqNamePubSubTMsgHandlerMap = new HashMap<>();

	/**
	 * 消费者列表
	 */
	private final List<Consumer> consumerList = new ArrayList<>();
	
	/**
	 * 发送器列表
	 */
	private final static List<Sender<?>> senderList =  new ArrayList<>();

	/**
	 * zbusServer地址
	 */
	private String brokerAddress = null;

	private boolean isJvmBroker = false;

	/**
	 * broker对象
	 */
	private static Broker broker = null;

	/**
	 * 简单Broker配置
	 */
	private BrokerConfig brokerConfig = null;
	
	private final String scanRootPackage;

	/**
	 * 默认构造函数，使用 jvm broker
	 */
	public ZbusPlugin() {
		this(true, null);
	}

	public ZbusPlugin(boolean isJvmBroker, String scanRootPackage) {
		this.scanRootPackage = scanRootPackage;
		this.isJvmBroker = isJvmBroker;
		//创建broker
		ensureBroker();
		//加载注解
		autoLoadByAnnotation();
	}
	
	/**
	 * @Title: createProducer  
	 * @Description: 创建一个Producer  
	 * @return Producer
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @since V1.0.0
	 */
	public static Producer createProducer(Sender<?> sender, String mq, MqMode mqMode) throws IOException, InterruptedException{
		Producer producer = new Producer(broker, mq, mqMode);
		producer.createMQ();
		//这册这个发送器
		senderList.add(sender);
		return producer;
	}

	/**
	 * 默认构造函数,可指定brokerAddress
	 */
	public ZbusPlugin(String brokerAddress) {
		this(brokerAddress, null);
	}

	public ZbusPlugin(String brokerAddress, String scanRootPackage) {
		this.brokerAddress = brokerAddress;
		this.scanRootPackage = scanRootPackage;
		//创建broker
		ensureBroker();
		//加载注解
		autoLoadByAnnotation();
	}

	/**
	 * 构造函数 使用BrokerConfig config构建
	 */
	public ZbusPlugin(BrokerConfig config) {
		this(config, null);
	}
	
	public ZbusPlugin(BrokerConfig config, String scanRootPackage) {
		this.brokerConfig = config;
		this.scanRootPackage = scanRootPackage;
		//创建broker
		ensureBroker();
		//加载注解
		autoLoadByAnnotation();
	}
	

	/**
	 * @Title: registerMqMsgHandler
	 * @Description: 注册Mq的消息回调接口
	 * @param mq
	 *            MQ名
	 * @param msgHandler
	 *            消息到达回调接口
	 * @since V1.0.0
	 */
	public void registerMqMsgHandler(String mq, TMsgHandler<?> msgHandler) {
		if (mqNameTMsgHandlerMap.containsKey(mq)) {
			LOG.warn("(mq=" + mq + ")对应的消息处理器已存在!");
		}
		mqNameTMsgHandlerMap.put(mq, msgHandler);
	}
	
	/**
	 * @Title: registerTopicMsgHandler
	 * @Description: 注册Topic的消息回调接口
	 * @param topic
	 *            Topic对象
	 * @param msgHandler
	 *            消息到达回调接口
	 * @since V1.0.0
	 */
	public void registerTopicMsgHandler(Topic topic, TMsgHandler<?> msgHandler) {
		this.registerTopicMsgHandler(topic.getMqName(), topic.getTopicName(), msgHandler);
	}

	/**
	 * @Title: registerTopicMsgHandler
	 * @Description: 注册Topic的消息回调接口
	 * @param mq
	 *            MQ名
	 * @param topic
	 *            主题名
	 * @param msgHandler
	 *            消息到达回调接口
	 * @since V1.0.0
	 */
	public void registerTopicMsgHandler(String mq, String topic, TMsgHandler<?> msgHandler) {
		// 依据mq获得 topic－TMsgHandler映射map
		Map<String, TMsgHandler<?>> tmc = this.mqNamePubSubTMsgHandlerMap.get(mq);
		if (null == tmc) {
			tmc = new HashMap<String, TMsgHandler<?>>();
		}
		if(tmc.containsKey(topic)){
			LOG.warn("(mq=" + mq + ",topic=" + topic + ")对应的消息处理器已存在!");
		}
		tmc.put(topic, msgHandler);
		this.mqNamePubSubTMsgHandlerMap.put(mq, tmc);
	}
	
	/**
	 * @Title: ensureBroker  
	 * @Description: 确保broker可用  
	 * @throws Exception 
	 * @since V1.0.0
	 */
	private  void ensureBroker(){
		if(broker == null){
    		synchronized (this) {
				if(broker == null){
					if (isJvmBroker) {
						broker = new JvmBroker();
						LOG.info("创建jvm broker成功");
						return;
					}

					if(this.brokerAddress != null){
						this.brokerConfig = new BrokerConfig();
						this.brokerConfig.setBrokerAddress(brokerAddress);
					}
					try {
						broker = new SingleBroker(this.brokerConfig);
						LOG.info("创建broker成功(brokerAddress=" + this.brokerAddress + ")");
					} catch (IOException e) {
						throw new RuntimeException(e.getMessage(),e);
					}
				}
			} 
    	}
	}

	@Override
	public boolean start() {
		try {
			//确保创建
			ensureBroker();
			// 创建Mq消费者
			for (Entry<String, TMsgHandler<?>> entry : this.mqNameTMsgHandlerMap.entrySet()) {
				String mq = entry.getKey();
				Consumer c = new Consumer(broker, mq, MqMode.MQ);
				c.onMessage(entry.getValue());
				c.start();
				consumerList.add(c);
				LOG.info("创建MQ消费者成功(mq=" + mq + ")");
			}
			// 创建topic消费者
			for (Entry<String, Map<String, TMsgHandler<?>>> mqConfig : this.mqNamePubSubTMsgHandlerMap
					.entrySet()) {
				String mq = mqConfig.getKey();
				// topic <－> TMsgHandler 映射map
				Map<String, TMsgHandler<?>> tmt = mqConfig.getValue();
				for (Entry<String, TMsgHandler<?>> topicConfig : tmt.entrySet()) {
					String topic = topicConfig.getKey();
					Consumer c = new Consumer(broker, mq, MqMode.PubSub);
					c.setTopic(topic);
					c.onMessage(topicConfig.getValue());
					c.start();
					consumerList.add(c);
					LOG.info("创建Topic消费者成功 (mq=" + mq + ",topic=" + topic + ")");
				}
			}
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public boolean stop() {
		try {
			// 关闭消费者
			for (Consumer c : consumerList) {
				c.close();
				c = null;
			}
			consumerList.clear();
			
			//关闭所有发送器
			for(Sender<?> sender : senderList){
				sender.close();
			}
			senderList.clear();
			
			// 关闭broker
			if (broker != null) {
				broker.close();
				broker = null;
			}
			return true;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
	}
	
	private void autoLoadByAnnotation(){
		if(StrKit.isBlank(this.scanRootPackage)){
			return;
		}
		try {
			//自动扫描相关类库检查
			Class.forName("org.reflections.Reflections");
			Class.forName("com.google.common.collect.Sets");
			Class.forName("javassist.bytecode.annotation.Annotation");
		} catch (Exception e) { 
			throw new RuntimeException("Zbus开启自动扫描加载消息处理器需要Reflections、Guava、Javassist类库，请导入相应的jar包");
		}
		//首先检查是否具有相关的库
		Reflections reflections = new Reflections(this.scanRootPackage);
		Set<Class<?>> mqHandlerClasses = reflections.getTypesAnnotatedWith(MqHandler.class);
		for (Class<?> mc : mqHandlerClasses) {
			if(!TMsgHandler.class.isAssignableFrom(mc)){
				throw new RuntimeException(mc.getName() + " 必须继承自 TMsgHandler<T>");
			}
			MqHandler mh = mc.getAnnotation(MqHandler.class);
			try {
				TMsgHandler<?> hander = (TMsgHandler<?>) mc.newInstance();
				this.registerMqMsgHandler(mh.value(), hander);
				LOG.info("通过注解自动加载MQ消息处理器( mq=" + mh.value()  + ",handler=" + mc.getName() + " )");
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			} 
		}
		
		Set<Class<?>> topicHandlerClasses = reflections.getTypesAnnotatedWith(TopicHandler.class);
		for (Class<?> mc : topicHandlerClasses) {
			if(!TMsgHandler.class.isAssignableFrom(mc)){
				throw new RuntimeException(mc.getName() + " 必须继承自 TMsgHandler<T>");
			}
			TopicHandler th = mc.getAnnotation(TopicHandler.class);
			try {
				TMsgHandler<?> hander = (TMsgHandler<?>) mc.newInstance();
				this.registerTopicMsgHandler(th.mq(), th.topic(), hander);
				LOG.info("通过注解自动加载Topic消息处理器( mq=" + th.mq()  + ",topic=" + th.topic() + ",handler=" + mc.getName() + " )");
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			} 
		}
	}
}
