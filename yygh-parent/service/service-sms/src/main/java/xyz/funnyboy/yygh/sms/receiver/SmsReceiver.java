package xyz.funnyboy.yygh.sms.receiver;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.funnyboy.common.rabbit.constant.MQConst;
import xyz.funnyboy.yygh.sms.service.SmsService;
import xyz.funnyboy.yygh.vo.sms.SmsVo;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 11:11:53
 */
@Component
@Slf4j
public class SmsReceiver
{
    @Autowired
    private SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MQConst.QUEUE_SMS_ITEM,
                                                            durable = "true"),
                                             exchange = @Exchange(value = MQConst.EXCHANGE_DIRECT_SMS),
                                             key = {MQConst.ROUTING_SMS_ITEM}))
    public void send(SmsVo msmVo, Message message, Channel channel) {
        log.info("接收消息：{} from {}", message, channel);
        smsService.send(msmVo);
    }
}
