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
package com.jfeat.ext.plugin.zbus.sender;

import java.io.IOException;

import com.jfeat.ext.plugin.zbus.ZbusPlugin;
import com.jfeat.ext.plugin.zbus.coder.Coder;
import com.jfeat.ext.plugin.zbus.coder.JsonCoder;
import org.zbus.mq.Producer;
import org.zbus.mq.Protocol.MqMode;
import org.zbus.net.http.Message;


/**  
 * @ClassName: AbstractSender  
 * @Description: 发送对象到MQ／topic 抽象基类  
 * @author 李飞 (lifei@wellbole.com)   
 * @date 2015年9月5日 下午5:27:58
 * @since V1.0.0  
 */
abstract class AbstractSender<T> implements Sender<T>{
	/**
	 * 生产者
	 */
	private Producer producer;
	
	/**
	 * mq名称
	 */
	private final String mq;
	
	/**
	 * mq类型
	 */
	private final MqMode mqMode;
	
	/**
	 * 编码解码器
	 */
	private static final Coder coder = new JsonCoder();
	
	/**
	 * <p>Title: AbstractSender</p>  
	 * <p>Description: 默认构造函数</p>  
	 * @param mq MQ队列名
	 * @param mqMode MQ队列类型
	 * @since V1.0.0
	 */
	public AbstractSender(String mq, MqMode mqMode){
		this.mq = mq;
		this.mqMode = mqMode;
	}
	
	/**
	 * @Title: ensureProducer  
	 * @Description: 确保生产者使用前被创建  
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @since V1.0.0
	 */
	private void ensureProducer() throws IOException, InterruptedException{
		if(this.producer == null){
    		synchronized (this) {
				if(this.producer == null){
					//创建生产者
					producer = ZbusPlugin.createProducer(this, mq, mqMode);
				}
			} 
    	}
	}
	
	@Override
	public void close() throws IOException {
		//将producer重新设定为null，重新获取producer对象
		producer = null;
	}
	
	/**
	 * @Title: sendSync
	 * @Description: 发送对象到MQ／topic（同步方式）
	 * @param obj
	 *            发送对象
	 * @throws IOException
	 * @throws InterruptedException 
	 * @since V1.0.0
	 */
	@Override
	public void sendSync(T obj) throws IOException, InterruptedException{
		ensureProducer();
		producer.sendSync(encode(obj));
	}
	
	/**
	 * @Title: sendAsync  
	 * @Description: 发送对象到MQ／topic（异步方式）
	 * @param obj
	 *            发送对象
	 * @throws IOException
	 * @throws InterruptedException 
	 * @since V1.0.0
	 */
	@Override
	public void sendAsync(T obj) throws IOException, InterruptedException{
		ensureProducer();
		producer.sendAsync(encode(obj));
	}
	
	/**
	 * @Title: encode  
	 * @Description: 默认编码，子类可重载  
	 * @param obj
	 * @return 
	 * @since V1.0.0
	 */
	protected Message encode(T obj){
		return coder.encode(obj);
	}
}
