package xyz.funnyboy.yygh.hosp.receiver;

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
import xyz.funnyboy.common.rabbit.service.RabbitService;
import xyz.funnyboy.yygh.hosp.service.ScheduleService;
import xyz.funnyboy.yygh.model.hosp.Schedule;
import xyz.funnyboy.yygh.vo.order.OrderMqVo;
import xyz.funnyboy.yygh.vo.sms.SmsVo;

import java.io.IOException;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 11:33:58
 */
@Component
@Slf4j
public class HospitalReceiver
{
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MQConst.QUEUE_ORDER,
                                                            durable = "true"),
                                             exchange = @Exchange(value = MQConst.EXCHANGE_DIRECT_ORDER),
                                             key = {MQConst.ROUTING_ORDER}))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
        log.info("接收消息：{} from {}", message, channel);
        if (orderMqVo.getAvailableNumber() != null) {
            // 下单成功更新预约数
            final Schedule schedule = scheduleService.getById(orderMqVo.getScheduleId());
            schedule.setReservedNumber(orderMqVo.getReservedNumber());
            schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
            scheduleService.update(schedule);
        }
        else {
            // 取消更新预约数
            final Schedule schedule = scheduleService.getById(orderMqVo.getScheduleId());
            schedule.setAvailableNumber(schedule.getAvailableNumber() + 1);
            scheduleService.update(schedule);
        }

        // 发送短信
        final SmsVo smsVo = orderMqVo.getSmsVo();
        if (smsVo != null) {
            rabbitService.sendMessage(MQConst.EXCHANGE_DIRECT_SMS, MQConst.ROUTING_SMS_ITEM, smsVo);
        }
    }
}
