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

import com.jfeat.ext.plugin.zbus.Topic;
import org.zbus.mq.Protocol.MqMode;
import org.zbus.net.http.Message;


/**
 * @ClassName: TopicSender
 * @Description: Topic泛型发送器
 * @author 李飞 (lifei@wellbole.com)
 * @date 2015年8月2日 下午6:46:51
 * @since V1.0.0
 */
public class TopicSender<T> extends AbstractSender<T>{
	
	/**
	 * 主题
	 */
	private final String topic;
	
	/**
	 * 
	 * <p>
	 * Title: TopicSender
	 * </p>
	 * <p>
	 * Description: 构建一个Topic发送器
	 * </p>
	 * 
	 * @param topic
	 *            Topic对象
	 * @since V1.0.0
	 */
	public TopicSender(final Topic topic) {
		this(topic.getMqName(),topic.getTopicName());
	}

	/**
	 * 
	 * <p>
	 * Title: TopicSender
	 * </p>
	 * <p>
	 * Description: 构建一个Topic发送器
	 * </p>
	 * 
	 * @param mq
	 *            MQ队列名
	 * @param topic
	 *            主题名
	 * @since V1.0.0
	 */
	public TopicSender(String mq, String topic) {
		super(mq, MqMode.PubSub);
		this.topic = topic;
	}

	
	@Override
	protected Message encode(T obj) {
		//设定topic
		return super.encode(obj).setTopic(this.topic);
	}
}
