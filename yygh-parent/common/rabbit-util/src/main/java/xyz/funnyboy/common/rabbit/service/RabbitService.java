package xyz.funnyboy.common.rabbit.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 10:58:12
 */
@Service
public class RabbitService
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param exchange   交换
     * @param routingKey 路由密钥
     * @param message    消息
     * @return boolean
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }
}
