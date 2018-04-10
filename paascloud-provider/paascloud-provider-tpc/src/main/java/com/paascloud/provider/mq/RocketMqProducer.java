package com.paascloud.provider.mq;


import com.paascloud.base.constant.GlobalConstant;
import com.paascloud.base.enums.ErrorCodeEnum;
import com.paascloud.core.mq.MqMessage;
import com.paascloud.provider.exceptions.TpcBizException;
import com.paascloud.provider.service.MqProducerBeanFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * The class Rocket mq producer.
 *
 * @author paascloud.net @gmail.com
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RocketMqProducer {

    private static final int PRODUCER_RETRY_TIMES = 3;

    /**
     * @param body
     * @param topic
     * @param tag
     * @param key
     * @param pid        ProducerGroup
     * @param delayLevel
     * @return
     */
    public static SendResult sendSimpleMessage(String body, String topic, String tag, String key, String pid, Integer delayLevel) {
        if (delayLevel == null) {
            delayLevel = 0;
        }
        if (delayLevel < 0 || delayLevel > GlobalConstant.Number.EIGHTEEN_INT) {
            throw new TpcBizException(ErrorCodeEnum.TPC100500013, topic, key);
        }
        Message message = MqMessage.checkMessage(body, topic, tag, key);
        message.setDelayTimeLevel(delayLevel);
        return retrySendMessage(pid, message);
    }

    /**
     * 真正发mq在这里
     */
    private static SendResult retrySendMessage(String pid, Message msg) {
        int iniCount = 1;
        SendResult result;
        while (true) {
            try {
                result = MqProducerBeanFactory.getBean(pid).send(msg);
                break;
            } catch (Exception e) {
                log.error("发送消息失败:", e);
                if (iniCount++ >= PRODUCER_RETRY_TIMES) {
                    throw new TpcBizException(ErrorCodeEnum.TPC100500014, msg.getTopic(), msg.getKeys());
                }
            }
        }
        log.info("<== 发送MQ SendResult={}", result);
        return result;
    }

}
