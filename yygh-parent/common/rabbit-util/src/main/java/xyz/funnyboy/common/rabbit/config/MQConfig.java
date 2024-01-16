package xyz.funnyboy.common.rabbit.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ配置
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 11:00:40
 */
@Configuration
public class MQConfig
{
    /**
     * 消息转换器
     *
     * @return {@link MessageConverter}
     */
    @Bean
    public MessageConverter messageConverter() {
        // 默认是字符串转换器，需要修改为json转换器
        return new Jackson2JsonMessageConverter();
    }
}
