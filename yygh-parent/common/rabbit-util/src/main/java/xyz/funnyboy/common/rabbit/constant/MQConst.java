package xyz.funnyboy.common.rabbit.constant;

/**
 * MQ 常量
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/16
 */
public class MQConst
{
    /**
     * 预约下单
     */
    public static final String EXCHANGE_DIRECT_ORDER = "exchange.direct.order";
    public static final String ROUTING_ORDER = "order";
    public static final String QUEUE_ORDER = "queue.order";

    /**
     * 短信
     */
    public static final String EXCHANGE_DIRECT_SMS = "exchange.direct.sms";
    public static final String ROUTING_SMS_ITEM = "sms.item";
    public static final String QUEUE_SMS_ITEM = "queue.sms.item";

    /**
     * 定时任务
     */
    public static final String EXCHANGE_DIRECT_TASK = "exchange.direct.task";
    public static final String ROUTING_TASK_8 = "task.8";
    public static final String QUEUE_TASK_8 = "queue.task.8";

}

