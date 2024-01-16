package xyz.funnyboy.yygh.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.yygh.model.order.OrderInfo;
import xyz.funnyboy.yygh.vo.order.OrderQueryVo;

import java.util.Map;

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

    /**
     * 选择页面
     *
     * @param pageParam    页面参数
     * @param orderQueryVo 订单查询 VO
     * @return {@link IPage}<{@link OrderInfo}>
     */
    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    /**
     * 根据订单ID获取订单信息
     *
     * @param orderId 订单ID
     * @return {@link OrderInfo}
     */
    OrderInfo getOrderInfo(String orderId);

    /**
     * 订单详情
     *
     * @param orderId 订单编号
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> show(Long orderId);

    /**
     * 取消订单
     *
     * @param orderId 订单编号
     * @return {@link Boolean}
     */
    Boolean cancelOrder(Long orderId);
}
