package xyz.funnyboy.yygh.order.service;

import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 20:23:09
 */
public interface WeixinService
{
    /**
     * 生成微信支付二维码
     *
     * @param orderId 订单编号
     * @return {@link Map}<{@link String},{@link Object}>
     */
    Map<String, Object> createNative(Long orderId);

    /**
     * 查询支付状态
     *
     * @param orderId 订单编号
     * @return {@link Map}
     */
    Map<String, String> queryPayStatus(Long orderId);

    /**
     * 退款
     *
     * @param orderId 订单编号
     * @return {@link Boolean}
     */
    Boolean refund(Long orderId);
}
