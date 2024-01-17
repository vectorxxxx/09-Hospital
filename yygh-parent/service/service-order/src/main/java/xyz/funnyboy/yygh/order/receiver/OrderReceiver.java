package xyz.funnyboy.yygh.order.receiver;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.funnyboy.common.rabbit.constant.MQConst;
import xyz.funnyboy.yygh.order.service.OrderService;

import java.io.IOException;

/**
 * 订单MQ监听
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-17 19:35:23
 */
@Component
public class OrderReceiver
{
    @Autowired
    private OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MQConst.QUEUE_TASK_8,
                                                            durable = "true"),
                                             exchange = @Exchange(value = MQConst.EXCHANGE_DIRECT_TASK),
                                             key = {MQConst.ROUTING_TASK_8}))
    public void patientTips(Message message, Channel channel) throws IOException {
        orderService.patientTips();
    }
}
