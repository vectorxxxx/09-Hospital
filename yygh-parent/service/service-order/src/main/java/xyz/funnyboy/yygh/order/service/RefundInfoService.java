package xyz.funnyboy.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.yygh.model.order.PaymentInfo;
import xyz.funnyboy.yygh.model.order.RefundInfo;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 21:37:47
 */
public interface RefundInfoService extends IService<RefundInfo>
{
    /**
     * 保存退款信息
     *
     * @param paymentInfo 付款信息
     * @return {@link RefundInfo}
     */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
