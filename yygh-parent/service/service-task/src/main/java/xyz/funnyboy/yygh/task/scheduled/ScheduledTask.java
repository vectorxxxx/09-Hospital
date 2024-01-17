package xyz.funnyboy.yygh.task.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.funnyboy.common.rabbit.constant.MQConst;
import xyz.funnyboy.common.rabbit.service.RabbitService;

/**
 * 定时任务
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-17 19:11:32
 */
@Component
@EnableScheduling
public class ScheduledTask
{
    @Autowired
    private RabbitService rabbitService;

    @Scheduled(cron = "0/30 * * * * ?")
    public void taskPatient() {
        rabbitService.sendMessage(MQConst.EXCHANGE_DIRECT_TASK, MQConst.ROUTING_TASK_8, "");
    }
}
