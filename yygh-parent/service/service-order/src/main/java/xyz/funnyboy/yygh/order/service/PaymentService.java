package xyz.funnyboy.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.yygh.model.order.OrderInfo;
import xyz.funnyboy.yygh.model.order.PaymentInfo;

import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 20:16:23
 */
public interface PaymentService extends IService<PaymentInfo>
{
    /**
     * 保存交易记录
     *
     * @param order       订单信息
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    void savePaymentInfo(OrderInfo order, Integer paymentType);

    /**
     * 更新订单状态
     *
     * @param outTradeNo 交易编号
     * @param resultMap  结果
     */
    void paySuccess(String outTradeNo, Map<String, String> resultMap);

    /**
     * 获取支付记录
     *
     * @param orderId     订单编号
     * @param paymentType 付款方式
     * @return {@link PaymentInfo}
     */
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);
}
