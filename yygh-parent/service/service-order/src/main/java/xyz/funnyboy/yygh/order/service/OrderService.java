package xyz.funnyboy.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.yygh.model.order.OrderInfo;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-15 23:27:26
 */
public interface OrderService extends IService<OrderInfo>
{
    /**
     * 保存订单
     *
     * @param scheduleId 排班 ID
     * @param patientId  就诊人 ID
     * @return {@link Long}
     */
    Long saveOrder(String scheduleId, Long patientId);
}
