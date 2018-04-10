package com.paascloud.config.properties;

import lombok.Data;

/**
 * @author haze
 * @date created at 2018/4/9 下午9:36
 */
@Data
public class ActiveMqProperties {
    private String consumerGroup;
    private String producerGroup;
    private String namesrvAddr;
    /**
     * 生产者是否使用可靠消息, 默认不使用 @MqConsumerStore
     */
    private boolean reliableMessageProducer;
    /**
     * 消费者是否使用可靠消息, 默认不使用 @MqProducerStore
     */
    private boolean reliableMessageConsumer;
}
